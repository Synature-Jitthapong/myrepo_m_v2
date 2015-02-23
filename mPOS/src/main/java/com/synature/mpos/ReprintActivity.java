package com.synature.mpos;

import java.util.Calendar;
import java.util.List;

import com.synature.mpos.database.GlobalPropertyDao;
import com.synature.mpos.database.PaymentDetailDao;
import com.synature.mpos.database.SessionDao;
import com.synature.mpos.database.TransactionDao;
import com.synature.mpos.database.model.OrderTransaction;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class ReprintActivity extends Activity {
	
	public static final int RECEIPT = 1;
	public static final int WASTE = 2;
	
	private TransactionDao mTrans;
	private GlobalPropertyDao mFormat;
	private List<OrderTransaction> mTransLst;
	private int mBillType = RECEIPT;
	
	private ReprintTransAdapter mTransAdapter;
	private ListView mLvTrans;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
	            WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	    LayoutParams params = getWindow().getAttributes();
	    params.width = 500;
	    params.height= 500;
	    params.alpha = 1.0f;
	    params.dimAmount = 0.5f;
	    getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
		setContentView(R.layout.activity_reprint);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowCustomEnabled(true);
		
		mLvTrans = (ListView) findViewById(R.id.listView1);

		mTrans = new TransactionDao(this);
		mFormat = new GlobalPropertyDao(this);

		loadTransaction();
		setupCustomView();
	}
	
	private void loadTransaction(){
		SessionDao sess = new SessionDao(this);
		String sessionDate = sess.getLastSessionDate();
		if(mBillType == RECEIPT)
			mTransLst = mTrans.listSuccessTransaction(sessionDate);
		else if(mBillType == WASTE)
			mTransLst = mTrans.listSuccessTransactionWaste(sessionDate);
		mTransAdapter = new ReprintTransAdapter(
				ReprintActivity.this, mTransLst);
		mLvTrans.setAdapter(mTransAdapter);
	}
	
	private void setupCustomView(){
		PaymentDetailDao payment = new PaymentDetailDao(this);
		if(payment.countPayTypeWaste() > 0){
			View customView = getLayoutInflater().inflate(R.layout.spinner_view, null);
			Spinner sp = (Spinner) customView.findViewById(R.id.spinner1);
			String[] billTypes = getResources().getStringArray(R.array.bill_type);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
					android.R.layout.simple_spinner_dropdown_item, billTypes);
			sp.setAdapter(adapter);
			sp.setOnItemSelectedListener(new OnItemSelectedListener() {
	
				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					if(position == 0)
						mBillType = RECEIPT;
					else if(position == 1)
						mBillType = WASTE;
					loadTransaction();
				}
	
				@Override
				public void onNothingSelected(AdapterView<?> parent) {}
			});
			getActionBar().setCustomView(customView);
			getActionBar().setDisplayShowTitleEnabled(false);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			finish();
			return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	public class ReprintTransAdapter extends OrderTransactionAdapter{

		public ReprintTransAdapter(Context c, List<OrderTransaction> transLst) {
			super(c, transLst);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final OrderTransaction trans = mTransLst.get(position);
			final ViewHolder holder;
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.reprint_trans_item, parent, false);
				holder = new ViewHolder();
				holder.tvNo = (TextView) convertView.findViewById(R.id.tvNo);
				holder.tvReceiptNo = (TextView) convertView.findViewById(R.id.tvReceiptNo);
				holder.tvAd = (TextView) convertView.findViewById(R.id.tvAd);
				holder.btnPrint = (ImageButton) convertView.findViewById(R.id.btnPrint);
				holder.btnBillDetail = (ImageButton) convertView.findViewById(R.id.btnBillDetail);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tvNo.setText(String.valueOf(position + 1) + ".");
			holder.tvReceiptNo.setText(trans.getReceiptNo());
			if(!TextUtils.isEmpty(trans.getPaidTime())){
				Calendar c = Calendar.getInstance();
				try {
					c.setTimeInMillis(Long.parseLong(trans.getPaidTime()));
					holder.tvAd.setText(mFormat.timeFormat(c.getTime()));
				} catch (NumberFormatException e) {}
			}
			holder.btnPrint.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					holder.btnPrint.setEnabled(false);
					new Reprint(trans.getTransactionId(), holder.btnPrint).execute();
				}
				
			});
			holder.btnBillDetail.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					BillViewerFragment f = BillViewerFragment.newInstance(trans.getTransactionId(), 
							BillViewerFragment.REPORT_VIEW, BillViewerFragment.RECEIPT, true);
					if(mBillType == WASTE){
						f = BillViewerFragment.newInstance(trans.getTransactionId(), 
								BillViewerFragment.REPORT_VIEW, BillViewerFragment.WASTE, true);
					}
					f.show(getFragmentManager(), BillViewerFragment.TAG);
				}
			});
			return convertView;
		}
		
		public class ViewHolder {
			TextView tvNo;
			TextView tvReceiptNo;
			TextView tvAd;
			ImageButton btnPrint;
			ImageButton btnBillDetail;
		}
	}

	private class Reprint extends PrintReceipt{
		
		public int mTransactionId;
		private ImageButton mBtnPrint;
		
		public Reprint(int transactionId, ImageButton refBtnPrint) {
			super(ReprintActivity.this, null);
			mTransactionId = transactionId;
			mBtnPrint = refBtnPrint;
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			if(Utils.isInternalPrinterSetting(ReprintActivity.this)){
				WintecPrinter wtPrinter = new WintecPrinter(ReprintActivity.this);
				if(mBillType == RECEIPT)
					wtPrinter.createTextForPrintReceipt(mTransactionId, true, false);
				else if(mBillType == WASTE)
					wtPrinter.createTextForPrintWasteReceipt(mTransactionId, true, false);
				wtPrinter.print();
			}else{
				EPSONPrinter epPrinter = new EPSONPrinter(ReprintActivity.this);	
				if(mBillType == RECEIPT)
					epPrinter.createTextForPrintReceipt(mTransactionId, true, false);
				else if(mBillType == WASTE)
					epPrinter.createTextForPrintWasteReceipt(mTransactionId, true, false);
				epPrinter.print();
			}
			runOnUiThread(new Runnable(){

				@Override
				public void run() {
					mBtnPrint.setEnabled(true);
				}
				
			});
			return null;
		}
	}
}
