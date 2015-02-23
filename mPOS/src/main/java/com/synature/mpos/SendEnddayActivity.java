package com.synature.mpos;

import java.util.Calendar;
import java.util.List;

import com.synature.mpos.datasource.GlobalPropertyDataSource;
import com.synature.mpos.datasource.SessionDataSource;
import com.synature.mpos.datasource.model.Session;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class SendEnddayActivity extends Activity {

	private GlobalPropertyDataSource mFormat;
	
	private int mStaffId;
	private int mShopId;
	private int mComputerId;
	
	private SessionDataSource mSession;
	private List<Session> mSessLst;
	private EnddayListAdapter mEnddayAdapter;
	
	private ListView mLvEndday;
	
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
		getActionBar().setDisplayHomeAsUpEnabled(true);
	    setFinishOnTouchOutside(false);
		setContentView(R.layout.activity_send_endday);
		
		mLvEndday = (ListView) findViewById(R.id.lvEndday);

		Intent intent = getIntent();
		mStaffId = intent.getIntExtra("staffId", 0);
		mShopId = intent.getIntExtra("shopId", 0);
		mComputerId = intent.getIntExtra("computerId", 0);
		
		mFormat = new GlobalPropertyDataSource(this);
		mSession = new SessionDataSource(this);
		setupAdapter();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			return false;
		}else{
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case android.R.id.home:
				setResult(RESULT_OK);
				finish();
			return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	private void setupAdapter(){
		mSessLst = mSession.listSessionEnddayNotSend();
		if(mSessLst != null){
			if(mEnddayAdapter == null){
				mEnddayAdapter = new EnddayListAdapter();
				mLvEndday.setAdapter(mEnddayAdapter);
			}
			mEnddayAdapter.notifyDataSetChanged();
		}
	}
	
	private class EnddayReceiver extends ResultReceiver{

		private ProgressDialog progress;
		
		public EnddayReceiver(Handler handler) {
			super(handler);
			progress = new ProgressDialog(SendEnddayActivity.this);
			progress.setCanceledOnTouchOutside(false);
			progress.setMessage(getString(R.string.please_wait));
			progress.show();
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			super.onReceiveResult(resultCode, resultData);
			switch(resultCode){
			case EnddaySenderService.RESULT_SUCCESS:
				break;
			case EnddaySenderService.RESULT_ERROR:
				new AlertDialog.Builder(SendEnddayActivity.this)
				.setMessage(resultData.getString("msg"))
				.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {}
				}).show();
				break;
			}
			progress.dismiss();
			setupAdapter();
		}
		
	}
	
	private class SendEnddayClickListener implements OnClickListener{

		private String sessionDate;
		
		public SendEnddayClickListener(String sessionDate){
			this.sessionDate = sessionDate;
		}
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(SendEnddayActivity.this, EnddaySenderService.class);
			intent.putExtra(EnddaySenderService.WHAT_TO_DO_PARAM, EnddaySenderService.SEND_CURRENT);
			intent.putExtra(EnddaySenderService.SESSION_DATE_PARAM, sessionDate);
			intent.putExtra(EnddaySenderService.SHOP_ID_PARAM, mShopId);
			intent.putExtra(EnddaySenderService.COMPUTER_ID_PARAM, mComputerId);
			intent.putExtra(EnddaySenderService.STAFF_ID_PARAM, mStaffId);
			intent.putExtra(EnddaySenderService.RECEIVER_NAME, new EnddayReceiver(new Handler()));
			startService(intent);
		}
		
	}
	
	private class EnddayListAdapter extends BaseAdapter{
		
		private LayoutInflater mInflater = getLayoutInflater();
		
		@Override
		public int getCount() {
			return mSessLst != null ? mSessLst.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return mSessLst.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView == null){
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.endday_list_template, parent, false);
				holder.tvSessionDate = (TextView) convertView.findViewById(R.id.tvSaleDate);
				holder.tvSummary = (TextView) convertView.findViewById(R.id.tvSummary);
				holder.btnSend = (ImageButton) convertView.findViewById(R.id.btnSendEndday);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			Session session = mSessLst.get(position); 
			String sessionDate = session.getSessionDate();
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(Long.parseLong(sessionDate));
			holder.tvSessionDate.setText(mFormat.dateFormat(cal.getTime()));
			holder.tvSummary.setText("#Bill " + mFormat.qtyFormat(session.getTotalQtyReceipt()) 
					+ " Total " + mFormat.currencyFormat(session.getTotalAmountReceipt()));
			holder.btnSend.setOnClickListener(new SendEnddayClickListener(sessionDate));
			return convertView;
		}
		
		class ViewHolder{
			TextView tvSessionDate;
			TextView tvSummary;
			ImageButton btnSend;
		}
	}
}
