package com.synature.mpos;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;

import com.synature.mpos.database.GlobalPropertyDao;
import com.synature.mpos.database.MPOSDatabase;
import com.synature.mpos.database.PaymentDetailDao;
import com.synature.mpos.database.ProductsDao;
import com.synature.mpos.database.Reporting;
import com.synature.mpos.database.SessionDao;
import com.synature.mpos.database.ShopDao;
import com.synature.mpos.database.StaffsDao;
import com.synature.mpos.database.TransactionDao;
import com.synature.mpos.database.Reporting.SimpleProductData;
import com.synature.mpos.database.model.MPOSPaymentDetail;
import com.synature.mpos.database.model.OrderDetail;
import com.synature.mpos.database.model.OrderTransaction;
import com.synature.pos.Report;
import com.synature.pos.Staff;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class SaleReportActivity extends Activity{

	public static final int REPORT_BY_BILL = 0;
	public static final int REPORT_BY_PRODUCT = 1;
	public static final int REPORT_ENDDAY = 2;
	
	private ShopDao mShop;
	private GlobalPropertyDao mFormat;
	private Reporting mReporting;

	private int mStaffId;
	
	private String mDateFrom;
	private String mDateTo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sale_report);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle(null);
		
		mShop = new ShopDao(this);
		mFormat = new GlobalPropertyDao(SaleReportActivity.this);
		mDateFrom = String.valueOf(Utils.getDate().getTimeInMillis());
		mDateTo = String.valueOf(Utils.getDate().getTimeInMillis());

		mReporting = new Reporting(SaleReportActivity.this, mDateFrom, mDateTo);
		
		mStaffId = getIntent().getIntExtra("staffId", 0);
		
		if(savedInstanceState == null){
			getFragmentManager().beginTransaction()
				.add(R.id.reportContent, BillReportFragment.getInstance()).commit();
		}
		setupSpRpType();
	}
	
	private void setupSpRpType(){
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		LayoutInflater inflater = getLayoutInflater();
		View rpTypeView = inflater.inflate(R.layout.spinner_view, null);
		Spinner spRpType = (Spinner) rpTypeView.findViewById(R.id.spinner1);
		spRpType.setAdapter(new ArrayAdapter<String>(this, 
				android.R.layout.simple_spinner_dropdown_item, 
				new String[] {
					getString(R.string.sale_report_by_bill),
					getString(R.string.sale_report_by_product),
					getString(R.string.summary_sale_report)
				}
		));
		spRpType.setOnItemSelectedListener(new ReportTypeSwitcher());
		spRpType.setSelection(0);
		actionBar.setCustomView(rpTypeView);
	}
	
	private class ReportTypeSwitcher implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View v, int position,
				long id) {
            mDateTo = String.valueOf(Utils.getDate().getTimeInMillis());
            mReporting.setDateTo(mDateTo);
			switch(position){
			case REPORT_BY_BILL:
				getFragmentManager().beginTransaction()
				.replace(R.id.reportContent, BillReportFragment.getInstance()).commit();
				break;
			case REPORT_BY_PRODUCT:
				getFragmentManager().beginTransaction()
				.replace(R.id.reportContent, ProductReportFragment.getInstance()).commit();
				break;
			case REPORT_ENDDAY:
				getFragmentManager().beginTransaction()
				.replace(R.id.reportContent, SummarySaleReportFragment.getInstance()).commit();
				break;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_sale_report, menu);
		MenuItem itemCondition = (MenuItem) menu.findItem(R.id.itemDateCondition);
		Button btnDateFrom = (Button) itemCondition.getActionView().findViewById(R.id.btnDateFrom);
		Button btnDateTo = (Button) itemCondition.getActionView().findViewById(R.id.btnDateTo);
		btnDateFrom.setText(mFormat.dateFormat(Utils.getDate().getTime()));
		btnDateTo.setText(mFormat.dateFormat(Utils.getDate().getTime()));
		btnDateFrom.setOnClickListener(new OnDateClickListener());
		btnDateTo.setOnClickListener(new OnDateClickListener());
		return super.onCreateOptionsMenu(menu);
	}
	
	private class OnDateClickListener implements OnClickListener{

		private SummarySaleReportFragment mEdf = null;
		
		public OnDateClickListener(){
			Fragment f = getFragmentManager().findFragmentById(R.id.reportContent);
			if(f instanceof SummarySaleReportFragment){
				mEdf = (SummarySaleReportFragment) f;
			}
		}
		
		@Override
		public void onClick(final View v) {
			DialogFragment dialogFragment;
			
			switch(v.getId()){
			case R.id.btnDateFrom:
				dialogFragment = new DatePickerFragment(new DatePickerFragment.OnSetDateListener() {
					
					@Override
					public void onSetDate(long date) {
						Calendar cal = Utils.getDate();
						cal.setTimeInMillis(date);
						mDateFrom = String.valueOf(cal.getTimeInMillis());
						
						((Button) v).setText(mFormat.dateFormat(cal.getTime()));
						mReporting.setDateFrom(mDateFrom);
						if(mEdf != null){
							mEdf.setupSpSession();
						}
					}
				});
				dialogFragment.show(getFragmentManager(), "Condition");
				break;
			case R.id.btnDateTo:
				dialogFragment = new DatePickerFragment(new DatePickerFragment.OnSetDateListener() {
					
					@Override
					public void onSetDate(long date) {
						Calendar cal = Utils.getDate();
						cal.setTimeInMillis(date);
						mDateTo = String.valueOf(cal.getTimeInMillis());
						
						((Button) v).setText(mFormat.dateFormat(cal.getTime()));
						mReporting.setDateTo(mDateTo);
						if(mEdf != null){
							mEdf.setupSpSession();
						}
					}
				});
				dialogFragment.show(getFragmentManager(), "Condition");
				break;
			}
		}	
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public static LinearLayout createRowSummary(Context context, TextView[] tvs){
		LinearLayout row = new LinearLayout(context);
		for(TextView tvSummary : tvs){
			row.addView(tvSummary);
		}
		return row;
	}
	
	private static TextView createTextViewItem(Context context, 
			String content, LinearLayout.LayoutParams params){
		TextView tvItem = new TextView(context);
		tvItem.setText(content);
		tvItem.setLayoutParams(params);
		tvItem.setGravity(Gravity.END);
		tvItem.setTextAppearance(context, R.style.BodyText);
		return tvItem;
	}
	
	private static TextView createTextViewHeader(Context context, 
			String content, LinearLayout.LayoutParams params, int gravity){
		TextView tvHeader = new TextView(context);
		tvHeader.setText(content);
		tvHeader.setLayoutParams(params);
		tvHeader.setGravity(gravity == 0 ? Gravity.CENTER : gravity);
		tvHeader.setTextAppearance(context, R.style.HeaderText);
		tvHeader.setPadding(4, 4, 4, 4);
		return tvHeader;
	}
	
	public static TextView createTextViewSummary(Context context, 
			String content, LinearLayout.LayoutParams params){
		TextView tvSummary = new TextView(context);
		tvSummary.setText(content);
		tvSummary.setLayoutParams(params);
		tvSummary.setGravity(Gravity.END);
		tvSummary.setTextAppearance(context, R.style.TextSummary);
		tvSummary.setTypeface(null, Typeface.BOLD);
		tvSummary.setPadding(4, 4, 4, 4);
		return tvSummary;
	}

	/*
	 * Payment detail dialog
	 */
	public static class PaymentDetailFragment extends DialogFragment{
		
		private SaleReportActivity mHost;
		private PaymentDetailDao mPayment;
		private List<MPOSPaymentDetail> mPaymentLst;
		private PaymentDetailAdapter mPaymentAdapter;
		
		private int mTransactionId;
		
		private LayoutInflater mInflater;
		
		public static PaymentDetailFragment newInstance(int transactionId){
			PaymentDetailFragment f = new PaymentDetailFragment();
			Bundle b = new Bundle();
			b.putInt("transactionId", transactionId);
			f.setArguments(b);
			return f;
		}
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			mHost = (SaleReportActivity) getActivity();
			mTransactionId = getArguments().getInt("transactionId");
			
			mPayment = new PaymentDetailDao(getActivity());
			mPaymentLst = mPayment.listPaymentGroupByType(mTransactionId);
			mPaymentAdapter = new PaymentDetailAdapter();
			
			mInflater = (LayoutInflater) 
					getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			super.onCreate(savedInstanceState);
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final ListView lv = new ListView(getActivity());
			lv.setAdapter(mPaymentAdapter);
			
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.payment);
			builder.setView(lv);
			builder.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					getDialog().dismiss();
				}
			});

			return builder.create();
		}

		private class PaymentDetailAdapter extends BaseAdapter{

			@Override
			public int getCount() {
				return mPaymentLst != null ? mPaymentLst.size() : 0;
			}

			@Override
			public MPOSPaymentDetail getItem(int position) {
				return mPaymentLst.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				convertView = mInflater.inflate(R.layout.template_flex_left_right, parent, false);
				TextView tvLeft = (TextView) convertView.findViewById(R.id.textView1);
				TextView tvRight = (TextView) convertView.findViewById(R.id.textView2);
				
				MPOSPaymentDetail payment = mPaymentLst.get(position);
				
				tvLeft.setText(payment.getPayTypeName());
				tvRight.setText(mHost.mFormat.currencyFormat(payment.getPayAmount()));
				
				return convertView;
			}
		}
	}
	
	/**
	 * @author j1tth4
	 * Summary Sale Report Fragment
	 */
	public static class SummarySaleReportFragment extends Fragment{

		private SaleReportActivity mHost;
		
		private static SummarySaleReportFragment sInstance;
		
		private TransactionDao mTrans;
		private SessionDao mSession;
		private PaymentDetailDao mPayment;
		private int mSessionId;
	
		private LinearLayout mEnddaySumContent;
		private ListView mLvEnddayReport;
		private Spinner mSpSession;
		
		public static SummarySaleReportFragment getInstance(){
			if(sInstance == null){
				sInstance = new SummarySaleReportFragment();
			}
			return sInstance;
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mHost = (SaleReportActivity) getActivity();
			mTrans = new TransactionDao(getActivity());
			mSession = new SessionDao(getActivity());
			mPayment = new PaymentDetailDao(getActivity());
			setHasOptionsMenu(true);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_endday_report, container, false);
			mEnddaySumContent = (LinearLayout) rootView.findViewById(R.id.enddayReportFooterContainer);
			mLvEnddayReport = (ListView) rootView.findViewById(R.id.lvEnddayReport);
			return rootView;
		}

		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			inflater.inflate(R.menu.fragment_sale_report, menu);
			MenuItem itemCondition = (MenuItem) menu.findItem(R.id.itemDateCondition);
			MenuItem itemSession = (MenuItem) menu.findItem(R.id.itemSession);
			itemSession.setVisible(true);
			((Button) itemCondition.getActionView().findViewById(R.id.btnDateFrom)).setVisibility(View.GONE);
			((TextView) itemCondition.getActionView().findViewById(R.id.tvFrom)).setVisibility(View.GONE);
			((TextView) itemCondition.getActionView().findViewById(R.id.tvTo)).setText(R.string.sale_date);
			mSpSession = (Spinner) itemSession.getActionView().findViewById(R.id.spinner1);
			setupSpSession();
			mSpSession.setOnItemSelectedListener(new OnSessionSelectedListener());
			super.onCreateOptionsMenu(menu, inflater);
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			SaleReportActivity activity = (SaleReportActivity) getActivity();
			switch(item.getItemId()){
			case R.id.itemCreateReport:
				createReport();
				return true;
			case R.id.itemPrint:
				new PrintReport(getActivity(), 
						PrintReport.WhatPrint.SUMMARY_SALE, mSessionId, activity.mStaffId, activity.mDateTo).execute();
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		}
		
		// setup spinner session
		private void setupSpSession(){
			List<com.synature.mpos.database.model.Session> sl = 
					mSession.listSession(mHost.mDateTo);
			com.synature.mpos.database.model.Session s = 
					new com.synature.mpos.database.model.Session();
			s.setSessionId(0);
			s.setSessNumber(getString(R.string.all_session));
			sl.add(0, s);
			mSpSession.setAdapter(new SessionAdapter(sl));
		}
		
		private class OnSessionSelectedListener implements OnItemSelectedListener{

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long id) {
				com.synature.mpos.database.model.Session sess = 
						(com.synature.mpos.database.model.Session) parent.getItemAtPosition(position);
				mSessionId = sess.getSessionId();
				createReport();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
			
		}
		
		private class SessionAdapter extends BaseAdapter{

			private List<com.synature.mpos.database.model.Session> mSl;
			
			public SessionAdapter(List<com.synature.mpos.database.model.Session> sl){
				mSl = sl;
			}
			
			@Override
			public int getCount() {
				return mSl != null ? mSl.size() : 0;
			}

			@Override
			public Object getItem(int position) {
				return mSl.get(position);
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
					convertView = getActivity().getLayoutInflater().inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
					holder.tvSession = (CheckedTextView) convertView.findViewById(android.R.id.text1);
					convertView.setTag(holder);
				}else{
					holder = (ViewHolder) convertView.getTag();
				}
				com.synature.mpos.database.model.Session sess = mSl.get(position);
				if(position > 0)
					sess.setSessNumber(String.valueOf(position));
				holder.tvSession.setText(sess.getSessNumber());
				return convertView;
			}
			
			private class ViewHolder{
				CheckedTextView tvSession;
			}
		}
		
		private void createReport(){
			LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			ShopDao shop = new ShopDao(getActivity());
			OrderTransaction trans = null;
			OrderDetail sumOrder = null;
			sumOrder = mTrans.getSummaryOrder(mSessionId, mHost.mDateTo, mHost.mDateTo);
			if(mSessionId != 0){
				trans = mTrans.getSummaryTransaction(mSessionId, mHost.mDateTo);
			}else{
				trans = mTrans.getSummaryTransaction(mHost.mDateTo);
			}
			
			mEnddaySumContent.removeAllViews();
			TextView tvSumTxt = new TextView(getActivity());
			tvSumTxt.setText(R.string.summary);
			tvSumTxt.setTextAppearance(getActivity(), R.style.HeaderText);
			tvSumTxt.setGravity(Gravity.CENTER);
			tvSumTxt.setPaintFlags(tvSumTxt.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
			mEnddaySumContent.addView(tvSumTxt);
			
			if(mSessionId != 0){
				com.synature.mpos.database.model.Session s = mSession.getSession(mSessionId);
				StaffsDao st = new StaffsDao(getActivity());
				Staff std = st.getStaff(s.getOpenStaff());
				if(std != null){
					View opStView = inflater.inflate(R.layout.left_mid_right_template, null);
					((TextView) opStView.findViewById(R.id.tvLeft)).setText(getString(R.string.open_by) + ": ");
					((TextView) opStView.findViewById(R.id.tvLeft)).append(std.getStaffName());
					((TextView) opStView.findViewById(R.id.tvMid)).setVisibility(View.GONE);
					((TextView) opStView.findViewById(R.id.tvRight)).setText(mHost.mFormat.timeFormat(s.getOpenDate()));
					mEnddaySumContent.addView(opStView);
				}
				
				std = st.getStaff(s.getCloseStaff());
				if(std != null){
					View clStView = inflater.inflate(R.layout.left_mid_right_template, null);
					((TextView) clStView.findViewById(R.id.tvLeft)).setText(getString(R.string.close_by) + ": ");
					((TextView) clStView.findViewById(R.id.tvLeft)).append(std != null ? std.getStaffName(): "-");
					((TextView) clStView.findViewById(R.id.tvMid)).setVisibility(View.GONE);
					((TextView) clStView.findViewById(R.id.tvRight)).setText(std != null ? mHost.mFormat.timeFormat(s.getCloseDate()) : "-");
					mEnddaySumContent.addView(clStView);
				}
			}
			
			View lmrView = inflater.inflate(R.layout.left_mid_right_template, null);
			((TextView) lmrView.findViewById(R.id.tvLeft)).setText(getString(R.string.sub_total));
			((TextView) lmrView.findViewById(R.id.tvMid)).setText(mHost.mFormat.qtyFormat(sumOrder.getOrderQty()));
			((TextView) lmrView.findViewById(R.id.tvMid)).setTypeface(null, Typeface.BOLD);
			((TextView) lmrView.findViewById(R.id.tvRight)).setText(mHost.mFormat.currencyFormat(sumOrder.getTotalRetailPrice()));
			((TextView) lmrView.findViewById(R.id.tvRight)).setTypeface(null, Typeface.BOLD);
			mEnddaySumContent.addView(lmrView);
			
			if(sumOrder.getPriceDiscount() > 0){
				View discountView = inflater.inflate(R.layout.left_mid_right_template, null);
				((TextView) discountView.findViewById(R.id.tvLeft)).setText(getString(R.string.discount));
				((TextView) discountView.findViewById(R.id.tvRight)).setText(mHost.mFormat.currencyFormat(sumOrder.getPriceDiscount()));
				((TextView) discountView.findViewById(R.id.tvRight)).setTypeface(null, Typeface.BOLD);
				mEnddaySumContent.addView(discountView);
			}
			
			View totalView = inflater.inflate(R.layout.left_mid_right_template, null);
			((TextView) totalView.findViewById(R.id.tvLeft)).setText(getString(R.string.total));
			((TextView) totalView.findViewById(R.id.tvRight)).setText(mHost.mFormat.currencyFormat(sumOrder.getTotalSalePrice()));
			((TextView) totalView.findViewById(R.id.tvRight)).setTypeface(null, Typeface.BOLD);
			mEnddaySumContent.addView(totalView);
			
			if(sumOrder.getVatExclude() > 0){
				View vatExcludeView = inflater.inflate(R.layout.left_mid_right_template, null);
				((TextView) vatExcludeView.findViewById(R.id.tvLeft)).setText(getString(R.string.vat_exclude) 
						+ " " + NumberFormat.getInstance().format(mHost.mShop.getCompanyVatRate()) + "%");
				((TextView) vatExcludeView.findViewById(R.id.tvRight)).setText(mHost.mFormat.currencyFormat(trans.getTransactionVatExclude()));
				((TextView) vatExcludeView.findViewById(R.id.tvRight)).setTypeface(null, Typeface.BOLD);
				mEnddaySumContent.addView(vatExcludeView);
			}
			
			double totalPaymentReceipt = mPayment.getTotalPaymentReceipt(mHost.mDateTo);
			if(mSessionId != 0)
				totalPaymentReceipt = mPayment.getTotalPaymentReceipt(mSessionId);
			if( totalPaymentReceipt != (sumOrder.getTotalSalePrice() + sumOrder.getVatExclude())){
				double totalRounding = totalPaymentReceipt - (sumOrder.getTotalSalePrice() + sumOrder.getVatExclude());
				View rounding = inflater.inflate(R.layout.left_mid_right_template, null);
				((TextView) rounding.findViewById(R.id.tvLeft)).setText(getString(R.string.rounding));
				((TextView) rounding.findViewById(R.id.tvRight)).setText(mHost.mFormat.currencyFormat(totalRounding));
				((TextView) rounding.findViewById(R.id.tvRight)).setTypeface(null, Typeface.BOLD);
				mEnddaySumContent.addView(rounding);
				
				View grandTotal = inflater.inflate(R.layout.left_mid_right_template, null);
				((TextView) grandTotal.findViewById(R.id.tvLeft)).setText(getString(R.string.grand_total));
				((TextView) grandTotal.findViewById(R.id.tvLeft)).setPaintFlags(
						((TextView) grandTotal.findViewById(R.id.tvLeft)).getPaintFlags() |Paint.UNDERLINE_TEXT_FLAG);
				((TextView) grandTotal.findViewById(R.id.tvRight)).setText(mHost.mFormat.currencyFormat(totalPaymentReceipt));
				((TextView) grandTotal.findViewById(R.id.tvRight)).setTypeface(null, Typeface.BOLD);
				mEnddaySumContent.addView(grandTotal);
			}
			
			if(shop.getCompanyVatType() == ProductsDao.VAT_TYPE_INCLUDED){
				View vatView = inflater.inflate(R.layout.left_mid_right_template, null);
				((TextView) vatView.findViewById(R.id.tvLeft)).setText(getString(R.string.before_vat));
				((TextView) vatView.findViewById(R.id.tvRight)).setText(mHost.mFormat.currencyFormat(
								trans.getTransactionVatable() - trans.getTransactionVat()));
				((TextView) vatView.findViewById(R.id.tvRight)).setTypeface(null, Typeface.BOLD);
				mEnddaySumContent.addView(vatView);
				
				vatView = inflater.inflate(R.layout.left_mid_right_template, null);
				((TextView) vatView.findViewById(R.id.tvLeft)).setText(getString(R.string.total_vat)
						+ " " + NumberFormat.getInstance().format(mHost.mShop.getCompanyVatRate()) + "%");
				((TextView) vatView.findViewById(R.id.tvRight)).setText(mHost.mFormat.currencyFormat(trans.getTransactionVat()));
				((TextView) vatView.findViewById(R.id.tvRight)).setTypeface(null, Typeface.BOLD);
				mEnddaySumContent.addView(vatView);
			}
			
			List<MPOSPaymentDetail> summaryPaymentLst = mPayment.listSummaryPayment(mSessionId, mHost.mDateTo);
			if(summaryPaymentLst != null){
				View paymentView = inflater.inflate(R.layout.left_mid_right_template, null);
				((TextView) paymentView.findViewById(R.id.tvLeft)).setText(getString(R.string.payment_detail));
				((TextView) paymentView.findViewById(R.id.tvLeft)).setPaintFlags(
						((TextView) paymentView.findViewById(R.id.tvLeft)).getPaintFlags() |Paint.UNDERLINE_TEXT_FLAG);
				((TextView) paymentView.findViewById(R.id.tvRight)).setText(null);
				mEnddaySumContent.addView(paymentView);
				for(MPOSPaymentDetail payment : summaryPaymentLst){
					paymentView = inflater.inflate(R.layout.left_mid_right_template, null);
					((TextView) paymentView.findViewById(R.id.tvLeft)).setText(payment.getPayTypeName());
					((TextView) paymentView.findViewById(R.id.tvRight)).setText(mHost.mFormat.currencyFormat(payment.getPayAmount()));
					((TextView) paymentView.findViewById(R.id.tvRight)).setTypeface(null, Typeface.BOLD);
					mEnddaySumContent.addView(paymentView);
				}
			}
			
			View totalReceiptView = inflater.inflate(R.layout.left_mid_right_template, null);
			((TextView) totalReceiptView.findViewById(R.id.tvLeft)).setText(getString(R.string.total_receipt));
			((TextView) totalReceiptView.findViewById(R.id.tvRight)).setText(String.valueOf(mTrans.getTotalReceipt(mSessionId, mHost.mDateTo)));
			((TextView) totalReceiptView.findViewById(R.id.tvRight)).setTypeface(null, Typeface.BOLD);
			mEnddaySumContent.addView(totalReceiptView);
			
			OrderDetail sumVoidOrder = mTrans.getSummaryVoidOrderInDay(mSessionId, mHost.mDateTo);
			View totalVoidView = inflater.inflate(R.layout.left_mid_right_template, null);
			((TextView) totalVoidView.findViewById(R.id.tvLeft)).setText(getString(R.string.void_bill));
			((TextView) totalVoidView.findViewById(R.id.tvLeft)).setPaintFlags(
					((TextView) totalVoidView.findViewById(R.id.tvLeft)).getPaintFlags() |Paint.UNDERLINE_TEXT_FLAG);
			((TextView) totalVoidView.findViewById(R.id.tvRight)).setText(null);
			mEnddaySumContent.addView(totalVoidView);
			totalVoidView = inflater.inflate(R.layout.left_mid_right_template, null);
			((TextView) totalVoidView.findViewById(R.id.tvLeft)).setText(getString(R.string.void_bill_after_paid));
			((TextView) totalVoidView.findViewById(R.id.tvMid)).setText(mHost.mFormat.qtyFormat(sumVoidOrder.getOrderQty()));
			((TextView) totalVoidView.findViewById(R.id.tvMid)).setTypeface(null, Typeface.BOLD);
			((TextView) totalVoidView.findViewById(R.id.tvRight)).setText(mHost.mFormat.currencyFormat(sumVoidOrder.getTotalSalePrice()));
			((TextView) totalVoidView.findViewById(R.id.tvRight)).setTypeface(null, Typeface.BOLD);
			mEnddaySumContent.addView(totalVoidView);

			Reporting report = new Reporting(getActivity(), 
					mHost.mDateTo, 
					mHost.mDateTo);
			List<Reporting.WasteReportData> wasteLst = report.listWasteReport();
			if(wasteLst != null){
				View wasteReportView = inflater.inflate(R.layout.left_mid_right_template, null);
				((TextView) wasteReportView.findViewById(R.id.tvLeft)).setText(getString(R.string.finish_waste));
				((TextView) wasteReportView.findViewById(R.id.tvLeft)).setPaintFlags(
						((TextView) wasteReportView.findViewById(R.id.tvLeft)).getPaintFlags() |Paint.UNDERLINE_TEXT_FLAG);
				mEnddaySumContent.addView(wasteReportView);
				for(Reporting.WasteReportData wasteData : wasteLst){
					String wasteName = wasteData.getWasteName();
					wasteReportView = inflater.inflate(R.layout.left_mid_right_template, null);
					((TextView) wasteReportView.findViewById(R.id.tvLeft)).setText(wasteName);
					mEnddaySumContent.addView(wasteReportView);
					if(wasteData.getSimpleProductData() != null){
						for(SimpleProductData sp : wasteData.getSimpleProductData()){
							wasteReportView = inflater.inflate(R.layout.left_mid_right_template, null);
							((TextView) wasteReportView.findViewById(R.id.tvLeft)).setText(" " + sp.getDeptName());
							((TextView) wasteReportView.findViewById(R.id.tvMid)).setText(mHost.mFormat.qtyFormat(sp.getDeptTotalQty()));
							((TextView) wasteReportView.findViewById(R.id.tvMid)).setTypeface(null, Typeface.BOLD);
							((TextView) wasteReportView.findViewById(R.id.tvRight)).setText(mHost.mFormat.currencyFormat(sp.getDeptTotalPrice()));
							((TextView) wasteReportView.findViewById(R.id.tvRight)).setTypeface(null, Typeface.BOLD);
							mEnddaySumContent.addView(wasteReportView);
							if(sp.getItemLst() != null){
								for(SimpleProductData.Item item : sp.getItemLst()){
									wasteReportView = inflater.inflate(R.layout.left_mid_right_template, null);
									((TextView) wasteReportView.findViewById(R.id.tvLeft)).setText("   " + item.getItemName());
									((TextView) wasteReportView.findViewById(R.id.tvMid)).setText(mHost.mFormat.qtyFormat(item.getTotalQty()));
									((TextView) wasteReportView.findViewById(R.id.tvRight)).setText(mHost.mFormat.currencyFormat(item.getTotalPrice()));
									mEnddaySumContent.addView(wasteReportView);
								}
							}
						}
					}
					SimpleProductData.Item sumWaste = report.getTotalStockOnly(wasteData.getPayTypeId());
					wasteReportView = inflater.inflate(R.layout.left_mid_right_template, null);
					((TextView) wasteReportView.findViewById(R.id.tvLeft)).setText(" " + getString(R.string.summary) + " " + wasteName);
					((TextView) wasteReportView.findViewById(R.id.tvMid)).setText(mHost.mFormat.qtyFormat(sumWaste.getTotalQty()));
					((TextView) wasteReportView.findViewById(R.id.tvMid)).setTypeface(null, Typeface.BOLD);
					((TextView) wasteReportView.findViewById(R.id.tvRight)).setText(mHost.mFormat.currencyFormat(sumWaste.getTotalPrice()));
					((TextView) wasteReportView.findViewById(R.id.tvRight)).setTypeface(null, Typeface.BOLD);
					mEnddaySumContent.addView(wasteReportView);
				}
				SimpleProductData.Item sumTotalWaste = report.getTotalStockOnly();
				wasteReportView = inflater.inflate(R.layout.left_mid_right_template, null);
				((TextView) wasteReportView.findViewById(R.id.tvLeft)).setText(" " + getString(R.string.total) + " " + getString(R.string.finish_waste));
				((TextView) wasteReportView.findViewById(R.id.tvMid)).setText(mHost.mFormat.qtyFormat(sumTotalWaste.getTotalQty()));
				((TextView) wasteReportView.findViewById(R.id.tvMid)).setTypeface(null, Typeface.BOLD);
				((TextView) wasteReportView.findViewById(R.id.tvRight)).setText(mHost.mFormat.currencyFormat(sumTotalWaste.getTotalPrice()));
				((TextView) wasteReportView.findViewById(R.id.tvRight)).setTypeface(null, Typeface.BOLD);
				mEnddaySumContent.addView(wasteReportView);
			}
			loadReportDetail();
		}
		
		private void loadReportDetail(){
			Reporting reporting = new Reporting(getActivity(), 
					mHost.mDateTo, 
					mHost.mDateTo);
			List<SimpleProductData> simpleLst = reporting.listSummaryProductGroupInDay(mSessionId);
			mLvEnddayReport.setAdapter(new EnddayReportAdapter(simpleLst));
		}
		
		private class EnddayReportAdapter extends BaseAdapter{

			private LayoutInflater mInflater;
			private List<SimpleProductData> mSimpleLst;
			
			public EnddayReportAdapter(List<SimpleProductData> simpleLst){
				mInflater = (LayoutInflater) getActivity()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				mSimpleLst = simpleLst;
			}
			
			@Override
			public int getCount() {
				return mSimpleLst != null ? mSimpleLst.size() : 0;
			}

			@Override
			public SimpleProductData getItem(int position) {
				return mSimpleLst.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolder holder;
				if(convertView == null){
					convertView = mInflater.inflate(R.layout.endday_report_template, null);
					holder = new ViewHolder();
					holder.tvGroupDept = (TextView) convertView.findViewById(R.id.tvGroupDept);
					holder.tvGroupTotalQty = (TextView) convertView.findViewById(R.id.tvGroupTotalQty);
					holder.tvGroupTotalPrice= (TextView) convertView.findViewById(R.id.tvGroupTotalPrice);
					holder.itemContainer = (LinearLayout) convertView.findViewById(R.id.itemContainer);
					convertView.setTag(holder);
				}else{
					holder = (ViewHolder) convertView.getTag();
				}
				
				SimpleProductData simple = mSimpleLst.get(position);
				holder.tvGroupDept.setText(simple.getDeptName());
				holder.tvGroupTotalQty.setText(
						mHost.mFormat.qtyFormat(simple.getDeptTotalQty()));
				holder.tvGroupTotalPrice.setText(
						mHost.mFormat.currencyFormat(simple.getDeptTotalPrice()));
				if(simple.getItemLst() != null){
					holder.itemContainer.removeAllViews();
					for(SimpleProductData.Item item : simple.getItemLst()){
						View bill = mInflater.inflate(R.layout.left_mid_right_template, null);
						((TextView) bill.findViewById(R.id.tvLeft)).setText(item.getItemName());
						((TextView) bill.findViewById(R.id.tvMid)).setText(
								mHost.mFormat.qtyFormat(item.getTotalQty()));
						((TextView) bill.findViewById(R.id.tvRight)).setText(
								mHost.mFormat.currencyFormat(item.getTotalPrice()));
						holder.itemContainer.addView(bill);
					}
				}
				return convertView;
			}
			
			class ViewHolder{
				TextView tvGroupDept;
				TextView tvGroupTotalQty;
				TextView tvGroupTotalPrice;
				LinearLayout itemContainer;
			}
		}
	}
	
	public static class BillReportFragment extends Fragment{

		private SaleReportActivity mHost;
		
		private static BillReportFragment sInstance;

		private Report mBillReport;
		private BillReportAdapter mBillReportAdapter;
		
		private ListView mLvReport;
		private LinearLayout mBillHeader;
		private LinearLayout mBillSumContent;
		private ProgressDialog mProgress;
		
		public static BillReportFragment getInstance(){
			if(sInstance == null){
				sInstance = new BillReportFragment();
			}
			return sInstance;
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch(item.getItemId()){
			case R.id.itemCreateReport:
				//new LoadBillReportTask().execute();

				mBillReport = mHost.mReporting.getSaleReportByBill();
				mBillReportAdapter.notifyDataSetChanged();
				return true;
			case R.id.itemPrint:
				new PrintReport(getActivity(), PrintReport.WhatPrint.BILL_REPORT, 
					mHost.mDateFrom, mHost.mDateTo).execute();
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		}

		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			inflater.inflate(R.menu.fragment_sale_report, menu);
			MenuItem itemCondition = (MenuItem) menu.findItem(R.id.itemDateCondition);
			((Button) itemCondition.getActionView().findViewById(R.id.btnDateFrom)).setVisibility(View.VISIBLE);
			((TextView) itemCondition.getActionView().findViewById(R.id.tvFrom)).setVisibility(View.VISIBLE);
			((TextView) itemCondition.getActionView().findViewById(R.id.tvTo)).setVisibility(View.VISIBLE);
			super.onCreateOptionsMenu(menu, inflater);
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mHost = (SaleReportActivity) getActivity();
			setHasOptionsMenu(true);
			mBillReport = new Report();
			mBillReportAdapter = new BillReportAdapter();
			mProgress = new ProgressDialog(getActivity());
			mProgress.setMessage(getString(R.string.loading));
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_bill_report, container, false);

			mBillHeader = (LinearLayout) rootView.findViewById(R.id.billHeader);
			mBillSumContent = (LinearLayout) rootView.findViewById(R.id.billSummaryContent);
			mLvReport = (ListView) rootView.findViewById(R.id.lvReport);
			mLvReport.setAdapter(mBillReportAdapter);
			mLvReport.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					Report.ReportDetail report = (Report.ReportDetail) arg0.getItemAtPosition(arg2);
					BillViewerFragment bf = BillViewerFragment.newInstance(report.getTransactionId(), 
							BillViewerFragment.REPORT_VIEW, BillViewerFragment.RECEIPT, false);
						bf.show(getFragmentManager(), "BillDetailFragment");
				}
			});
			
			createHeader();
			return rootView;
		}
		
		private void createHeader(){
			boolean isExcVat = mHost.mShop.getCompanyVatType() == ProductsDao.VAT_TYPE_EXCLUDE;
			mBillHeader.removeAllViews();
			TextView[] tvHeaders = {
					createTextViewHeader(getActivity(), "", Utils.getLinHorParams(0.2f), 0),
					createTextViewHeader(getActivity(), getActivity().getString(R.string.receipt_no), 
							Utils.getLinHorParams(1.0f), Gravity.START),
					createTextViewHeader(getActivity(), getActivity().getString(R.string.total_price), 
							Utils.getLinHorParams(0.7f), Gravity.END),
					createTextViewHeader(getActivity(), getActivity().getString(R.string.discount), 
							Utils.getLinHorParams(0.7f), Gravity.END)
			};
			for(TextView tv : tvHeaders){
				mBillHeader.addView(tv);
			}
			mBillHeader.addView(createTextViewHeader(getActivity(), getString(R.string.sub_total), 
					Utils.getLinHorParams(0.7f), Gravity.END));
//			mBillHeader.addView(createTextViewHeader(getActivity(), getString(R.string.total_sale), 
//				Utils.getLinHorParams(0.7f), Gravity.END));
			if(!isExcVat){
				mBillHeader.addView(createTextViewHeader(getActivity(), getString(R.string.vatable), 
						Utils.getLinHorParams(0.7f), Gravity.END));
			}
			mBillHeader.addView(
					createTextViewHeader(getActivity(), getString(R.string.vat) + " "
							+ NumberFormat.getInstance().format(mHost.mShop.getCompanyVatRate())
							+ " " + getString(R.string.percent), Utils.getLinHorParams(0.7f), Gravity.END));
			if(isExcVat){
				mBillHeader.addView(
						createTextViewHeader(getActivity(), getActivity().getString(R.string.total_sale_include_vat),
						Utils.getLinHorParams(0.7f), Gravity.END));
			}
			mBillHeader.addView(
					createTextViewHeader(getActivity(), getString(R.string.total_payment),
					Utils.getLinHorParams(0.7f), Gravity.END));
			mBillHeader.addView(
					createTextViewHeader(getActivity(), getString(R.string.diff),
					Utils.getLinHorParams(0.5f), Gravity.END));
		}
		
		public class BillReportAdapter extends BaseAdapter{
			
			private LayoutInflater mInflater;
			
			public BillReportAdapter(){
				mInflater = (LayoutInflater)
						getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			}
			
			@Override
			public int getCount() {
				return mBillReport != null ? mBillReport.getReportDetail().size() : 0;
			}

			@Override
			public Report.ReportDetail getItem(int position) {
				return mBillReport.getReportDetail().get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public void notifyDataSetChanged() {
				super.notifyDataSetChanged();

				summaryBill();
			}
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if(convertView == null){
					convertView = mInflater.inflate(R.layout.bill_report_template, null);
				}
				final Report.ReportDetail report = mBillReport.getReportDetail().get(position);
				double totalVat = report.getTotalVat();
				double totalVatExcl = report.getVatExclude();
				double vatable = report.getVatable();
				double totalPrice = report.getTotalPrice();
				double totalDiscount = report.getDiscount();
				double subTotal = report.getSubTotal();
				double totalPay = report.getTotalPayment();
				boolean isExcVat = mHost.mShop.getCompanyVatType() == ProductsDao.VAT_TYPE_EXCLUDE;
					
				LinearLayout container = (LinearLayout) convertView;
				if(container.getChildCount() > 0)
					container.removeAllViews();
				TextView tvBill = createTextViewItem(getActivity(), report.getReceiptNo(), 
						Utils.getLinHorParams(1f));
				tvBill.setGravity(Gravity.LEFT);		
				if(report.getTransStatus() == TransactionDao.TRANS_STATUS_VOID){
					tvBill.setTextColor(Color.RED);
					tvBill.setPaintFlags(tvBill.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
				}else{
					tvBill.setTextColor(Color.BLACK);
					tvBill.setPaintFlags(tvBill.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
				}
				TextView tvs[] = new TextView[]{
						tvBill,
						createTextViewItem(getActivity(), mHost.mFormat.currencyFormat(totalPrice),
								Utils.getLinHorParams(0.7f)),
						createTextViewItem(getActivity(), mHost.mFormat.currencyFormat(totalDiscount),
								Utils.getLinHorParams(0.7f))
				};
				ImageView imgSendStatus = new ImageView(getActivity());
				imgSendStatus.setLayoutParams(Utils.getLinHorParams(0.2f));
				container.addView(imgSendStatus);
				for(TextView tv : tvs){
					container.addView(tv);
				}
				container.addView(createTextViewItem(getActivity(), mHost.mFormat.currencyFormat(subTotal),  
								Utils.getLinHorParams(0.7f)));
//				container.addView(createTextViewItem(getActivity(), mHost.mFormat.currencyFormat(totalPay - totalVatExcl),  
//								Utils.getLinHorParams(0.7f)));
				if(!isExcVat){
					container.addView(createTextViewItem(getActivity(), mHost.mFormat.currencyFormat(vatable - totalVatExcl),  
							Utils.getLinHorParams(0.7f)));
				}
				container.addView(createTextViewItem(getActivity(), mHost.mFormat.currencyFormat(totalVat),  
								Utils.getLinHorParams(0.7f)));
				if(isExcVat){
					container.addView(createTextViewItem(getActivity(), mHost.mFormat.currencyFormat(vatable),  
							Utils.getLinHorParams(0.7f)));
				}
				TextView tvTotalPay = new TextView(getActivity());
				tvTotalPay = createTextViewItem(getActivity(), mHost.mFormat.currencyFormat(totalPay),  
						Utils.getLinHorParams(0.7f));
//				tvTotalPay.setFocusable(false);
//				tvTotalPay.setFocusableInTouchMode(false);
//				tvTotalPay.setTextAppearance(getActivity(), android.R.style.TextAppearance_Holo_Large);
//				tvTotalPay.setTextColor(Color.BLUE);
//				tvTotalPay.setPaintFlags(tvTotalPay.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
//				tvTotalPay.setOnClickListener(new OnClickListener(){
//
//					@Override
//					public void onClick(View v) {
////						PaymentDetailFragment f = 
////								PaymentDetailFragment.newInstance(report.getTransactionId());
////						f.show(getFragmentManager(), "PaymentDialogFragment");
//
//						// bill detail
//						BillViewerFragment bf = BillViewerFragment.newInstance(report.getTransactionId(), false);
//						bf.show(getFragmentManager(), "BillDetailFragment");
//					}
//					
//				});
				container.addView(tvTotalPay);
				container.addView(createTextViewItem(getActivity(), 
						mHost.mFormat.currencyFormat(totalPay - (subTotal + totalVatExcl)), Utils.getLinHorParams(0.5f)));
				if(report.getSendStatus() == MPOSDatabase.ALREADY_SEND){
					imgSendStatus.setImageResource(R.drawable.ic_action_accept);
				}else{
					imgSendStatus.setImageResource(R.drawable.ic_action_warning);
				}
				return convertView;
			}
		}
		
		private void summaryBill(){
			boolean isExcVat = mHost.mShop.getCompanyVatType() == ProductsDao.VAT_TYPE_EXCLUDE;
			Report.ReportDetail summary = mHost.mReporting.getBillSummary();
			mBillSumContent.removeAllViews();
			TextView[] tvSummary = {
					createTextViewSummary(getActivity(), getString(R.string.summary),  
							Utils.getLinHorParams(1.2f)),
					createTextViewSummary(getActivity(), mHost.mFormat.currencyFormat(summary.getTotalPrice()),  
							Utils.getLinHorParams(0.7f)),
					createTextViewSummary(getActivity(), mHost.mFormat.currencyFormat(summary.getDiscount()),  
							Utils.getLinHorParams(0.7f))
			};
			for(TextView tv : tvSummary){
				mBillSumContent.addView(tv);	
			}
			mBillSumContent.addView(createTextViewSummary(getActivity(), mHost.mFormat.currencyFormat(summary.getSubTotal()),  
					Utils.getLinHorParams(0.7f)));
//			mBillSumContent.addView(createTextViewSummary(getActivity(), mHost.mFormat.currencyFormat(summary.getTotalPayment() - summary.getVatExclude()),  
//					Utils.getLinHorParams(0.7f)));
			if(!isExcVat){
				mBillSumContent.addView(createTextViewSummary(getActivity(), mHost.mFormat.currencyFormat(summary.getVatable() - summary.getVatExclude()),  
						Utils.getLinHorParams(0.7f)));
			}
			mBillSumContent.addView(createTextViewSummary(getActivity(), mHost.mFormat.currencyFormat(summary.getTotalVat()),  
						Utils.getLinHorParams(0.7f)));
			if(isExcVat){
				mBillSumContent.addView(createTextViewSummary(getActivity(), mHost.mFormat.currencyFormat(summary.getVatable()),  
						Utils.getLinHorParams(0.7f)));
			}
			mBillSumContent.addView(createTextViewSummary(getActivity(), mHost.mFormat.currencyFormat(summary.getTotalPayment()),  
						Utils.getLinHorParams(0.7f)));
			mBillSumContent.addView(createTextViewSummary(getActivity(), 
					mHost.mFormat.currencyFormat(summary.getTotalPayment() - (summary.getSubTotal() + summary.getVatExclude())), Utils.getLinHorParams(0.5f)));
			
		}
	}
	
	public static class ProductReportFragment extends Fragment{

		private SaleReportActivity mHost;
		
		private static ProductReportFragment sInstance;

		private Report mReportProduct;
		private ProductReportAdapter mProductReportAdapter;
		
		private LinearLayout mProductSumContent;
		private ExpandableListView mLvReportProduct;
		private ProgressDialog mProgress;
		
		public static ProductReportFragment getInstance(){
			if(sInstance == null){
				sInstance = new ProductReportFragment();
			}
			return sInstance;
		}
		
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			inflater.inflate(R.menu.fragment_sale_report, menu);
			MenuItem itemCondition = (MenuItem) menu.findItem(R.id.itemDateCondition);
			((Button) itemCondition.getActionView().findViewById(R.id.btnDateFrom)).setVisibility(View.VISIBLE);
			((TextView) itemCondition.getActionView().findViewById(R.id.tvFrom)).setVisibility(View.VISIBLE);
			((TextView) itemCondition.getActionView().findViewById(R.id.tvTo)).setVisibility(View.VISIBLE);
			super.onCreateOptionsMenu(menu, inflater);
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch(item.getItemId()){
			case R.id.itemCreateReport:
				//new LoadProductReportTask().execute();

				mReportProduct = mHost.mReporting.getProductDataReport();
				mProductReportAdapter.notifyDataSetChanged();
				return true;
			case R.id.itemPrint:
				new PrintReport(getActivity(), PrintReport.WhatPrint.PRODUCT_REPORT, 
						mHost.mDateFrom, mHost.mDateTo).execute();
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mHost = (SaleReportActivity) getActivity();
			setHasOptionsMenu(true);
			mReportProduct = new Report();
			mProductReportAdapter = new ProductReportAdapter();
			mProgress = new ProgressDialog(getActivity());
			mProgress.setMessage(getString(R.string.loading));
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_product_report, container, false);

			mProductSumContent = (LinearLayout) rootView.findViewById(R.id.productSummaryContent);
			mLvReportProduct = (ExpandableListView) rootView.findViewById(R.id.lvReportProduct);

			mLvReportProduct.setAdapter(mProductReportAdapter);
			mLvReportProduct.setGroupIndicator(null);
			return rootView;
		}
		
		public class ProductReportAdapter extends BaseExpandableListAdapter{
			
			private LayoutInflater mInflater;
			
			public ProductReportAdapter(){
				mInflater = getActivity().getLayoutInflater();
			}
			
			@Override
			public Report.ReportDetail getChild(int groupPosition, int childPosition) {
				try {
					return mReportProduct.getGroupOfProductLst().get(groupPosition).
							getReportDetail().get(childPosition);
				} catch (Exception e) {
					return null;
				}
			}

			@Override
			public long getChildId(int groupPosition, int childPosition) {
				return childPosition;
			}

			@Override
			public void notifyDataSetChanged() {
				super.notifyDataSetChanged();
				
				for(int i = 0; i < mProductReportAdapter.getGroupCount(); i++){
					mLvReportProduct.expandGroup(i);
				}
				summaryProduct();
			}

			@Override
			public View getChildView(int groupPosition, int childPosition,
					boolean isLastChild, View convertView, ViewGroup parent) {	
				ProductReportViewHolder holder;
				if(convertView == null){
					convertView = mInflater.inflate(R.layout.product_report_template, parent, false);
					holder = new ProductReportViewHolder();
					holder.tvNo = (TextView) convertView.findViewById(R.id.tvNo);
					holder.tvProductCode = (TextView) convertView.findViewById(R.id.tvProCode);
					holder.tvProductName = (TextView) convertView.findViewById(R.id.tvProName);
					holder.tvProductPrice = (TextView) convertView.findViewById(R.id.tvProPrice);
					holder.tvQty = (TextView) convertView.findViewById(R.id.tvQty);
					holder.tvQtyPercent = (TextView) convertView.findViewById(R.id.tvQtyPercent);
					holder.tvSubTotal = (TextView) convertView.findViewById(R.id.tvSubTotal);
					holder.tvSubTotalPercent = (TextView) convertView.findViewById(R.id.tvSubTotalPercent);
					holder.tvDiscount = (TextView) convertView.findViewById(R.id.tvDiscount);
					holder.tvTotalPrice = (TextView) convertView.findViewById(R.id.tvTotalPrice);
					holder.tvTotalPricePercent = (TextView) convertView.findViewById(R.id.tvTotalPricePercent);
					holder.tvVatType = (TextView) convertView.findViewById(R.id.tvVatType);
					convertView.setTag(holder);
				}else{
					holder = (ProductReportViewHolder) convertView.getTag();
				}

				Report.ReportDetail reportDetail = 
						mReportProduct.getGroupOfProductLst().get(groupPosition).getReportDetail().get(childPosition);
				setText(convertView, holder, childPosition, reportDetail);
				
				if(reportDetail.getProductName().equals(Reporting.SUMM_DEPT)){
					setSummary(convertView, holder, mReportProduct.getGroupOfProductLst().get(groupPosition).getProductDeptName(), Reporting.SUMM_DEPT);
				}else if(reportDetail.getProductName().equals(Reporting.SUMM_GROUP)){
					setSummary(convertView, holder, mReportProduct.getGroupOfProductLst().get(groupPosition).getProductGroupName(), Reporting.SUMM_GROUP);
				}
				return convertView;
			}
			
			private void setSummary(View convertView, ProductReportViewHolder holder, String text, String sumType){
				holder.tvNo.setVisibility(View.GONE);
				holder.tvProductName.setVisibility(View.GONE);
				holder.tvProductCode.setVisibility(View.GONE);
				//holder.tvProductPrice.setTextAppearance(getActivity(), R.style.HeaderText);
				holder.tvProductPrice.setText(getActivity().getString(R.string.summary) + 
						" " + text);
				holder.tvProductPrice.setLayoutParams(
						new LinearLayout.LayoutParams(0, 
								LayoutParams.WRAP_CONTENT, 2.8f));
				if(sumType.equals(Reporting.SUMM_GROUP)){
					LinearLayout row = (LinearLayout) convertView;
					for(int i = 0; i < row.getChildCount(); i++){
						View v = row.getChildAt(i);
						TextView tv = (TextView) v;
						tv.setTypeface(null, Typeface.BOLD);
						//v.setBackgroundResource(R.color.gray_light);
					}
				}
			}
			
			private void setText(View convertView, ProductReportViewHolder holder, int position, 
					Report.ReportDetail reportDetail){
				holder.tvNo.setVisibility(View.VISIBLE);
				holder.tvProductCode.setVisibility(View.VISIBLE);
				holder.tvProductPrice.setVisibility(View.VISIBLE);
				holder.tvProductPrice.setTextAppearance(getActivity(), R.style.BodyText);
				holder.tvProductName.setVisibility(View.VISIBLE);
				holder.tvNo.setLayoutParams(
						new LinearLayout.LayoutParams(0, 
								LayoutParams.WRAP_CONTENT, 0.2f));
				holder.tvProductCode.setLayoutParams(
						new LinearLayout.LayoutParams(0, 
								LayoutParams.WRAP_CONTENT, 0.8f));
				holder.tvProductName.setLayoutParams(
						new LinearLayout.LayoutParams(0, 
								LayoutParams.WRAP_CONTENT, 1f));
				holder.tvProductPrice.setLayoutParams(
						new LinearLayout.LayoutParams(0, 
								LayoutParams.WRAP_CONTENT, 0.8f));
				holder.tvNo.setText(String.valueOf(position + 1) + ".");
				holder.tvProductCode.setText(reportDetail.getProductCode());
				holder.tvProductName.setText(reportDetail.getProductName());
				holder.tvProductPrice.setText(mHost.mFormat.currencyFormat(
						reportDetail.getPricePerUnit()));
				holder.tvQty.setText(mHost.mFormat.qtyFormat(
						reportDetail.getQty()));
				holder.tvQtyPercent.setText(mHost.mFormat.currencyFormat(
						reportDetail.getQtyPercent()));
				holder.tvSubTotal.setText(mHost.mFormat.currencyFormat(
						reportDetail.getSubTotal()));
				holder.tvSubTotalPercent.setText(mHost.mFormat.currencyFormat(
						reportDetail.getSubTotalPercent()));
				holder.tvDiscount.setText(mHost.mFormat.currencyFormat(
						reportDetail.getDiscount()));
				holder.tvTotalPrice.setText(mHost.mFormat.currencyFormat(
						reportDetail.getTotalPrice()));
				holder.tvTotalPricePercent.setText(mHost.mFormat.currencyFormat(
						reportDetail.getTotalPricePercent()));
				holder.tvVatType.setText(reportDetail.getVat());
				
				LinearLayout row = (LinearLayout) convertView;
				for(int i = 0; i < row.getChildCount(); i++){
					View v = row.getChildAt(i);
					TextView tv = (TextView) v;
					tv.setTypeface(null, Typeface.NORMAL);
					//v.setBackgroundResource(android.R.color.transparent);
				}
			}
			
			@Override
			public int getChildrenCount(int groupPosition) {
				int count = 0;
				if(mReportProduct != null)
				{
					if(mReportProduct.getGroupOfProductLst() != null){
						if(mReportProduct.getGroupOfProductLst().get(groupPosition).getReportDetail() != null){
							count = mReportProduct.getGroupOfProductLst().get(groupPosition).getReportDetail().size();
						}
					}
				}
				return count;
			}

			@Override
			public Report.GroupOfProduct getGroup(int groupPosition) {
				try {
					return mReportProduct.getGroupOfProductLst().get(groupPosition);
				} catch (Exception e) {
					return null;
				}
			}

			@Override
			public int getGroupCount() {
				int count = 0;
				if(mReportProduct != null){
					if(mReportProduct.getGroupOfProductLst() != null){
						count = mReportProduct.getGroupOfProductLst().size();
					}
				}
				return count;
			}

			@Override
			public long getGroupId(int groupPosition) {
				return groupPosition;
			}

			@Override
			public View getGroupView(int groupPosition, boolean isExpanded,
					View convertView, ViewGroup parent) {
				ProductReportHeaderHolder groupHolder;
				if(convertView == null){
					groupHolder = new ProductReportHeaderHolder();
					groupHolder.tvHeader = new TextView(getActivity());
					groupHolder.tvHeader.setTextAppearance(getActivity(), R.style.HeaderText);
					groupHolder.tvHeader.setPadding(8, 4, 4, 4);
					groupHolder.tvHeader.setTextSize(20);
					convertView = groupHolder.tvHeader;
					convertView.setTag(groupHolder);
				}else{
					groupHolder = (ProductReportHeaderHolder) convertView.getTag();
				}
				groupHolder.tvHeader.setText(mReportProduct.getGroupOfProductLst().get(groupPosition).getProductGroupName() + ":" +
						mReportProduct.getGroupOfProductLst().get(groupPosition).getProductDeptName());
				return convertView;
			}

			@Override
			public boolean hasStableIds() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isChildSelectable(int groupPosition, int childPosition) {
				// TODO Auto-generated method stub
				return false;
			}
			
			class ProductReportHeaderHolder{
				TextView tvHeader;
			}
			
			class ProductReportViewHolder{
				TextView tvNo;
				TextView tvProductCode;
				TextView tvProductName;
				TextView tvProductPrice;
				TextView tvQty;
				TextView tvQtyPercent;
				TextView tvSubTotal;
				TextView tvSubTotalPercent;
				TextView tvDiscount;
				TextView tvTotalPrice;
				TextView tvTotalPricePercent;
				TextView tvVatType;
			}
		}
		
		private void summaryProduct(){
			Report.ReportDetail summProduct = 
					mHost.mReporting.getProductSummary();
			TextView[] tvGrandTotal = {
					createTextViewSummary(getActivity(), getString(R.string.grand_total), Utils.getLinHorParams(2.8f)),
					createTextViewSummary(getActivity(), mHost.mFormat.qtyFormat(summProduct.getQty()), 
							Utils.getLinHorParams(0.5f)),
					createTextViewSummary(getActivity(), mHost.mFormat.qtyFormat(summProduct.getQty() / summProduct.getQty() * 100), 
							Utils.getLinHorParams(0.5f)),
					createTextViewSummary(getActivity(), mHost.mFormat.currencyFormat(summProduct.getSubTotal()), 
							Utils.getLinHorParams(0.8f)),
					createTextViewSummary(getActivity(), mHost.mFormat.qtyFormat(summProduct.getSubTotal() / summProduct.getSubTotal() * 100), 
							Utils.getLinHorParams(0.5f)),
					createTextViewSummary(getActivity(), mHost.mFormat.currencyFormat(summProduct.getDiscount()), 
							Utils.getLinHorParams(0.8f)),
					createTextViewSummary(getActivity(), mHost.mFormat.currencyFormat(summProduct.getTotalPrice()), 
							Utils.getLinHorParams(0.8f)),
					createTextViewSummary(getActivity(), mHost.mFormat.qtyFormat(summProduct.getTotalPrice() / summProduct.getTotalPrice() * 100), 
							Utils.getLinHorParams(0.5f)),
					createTextViewSummary(getActivity(), "", Utils.getLinHorParams(0.2f))
			};
			mProductSumContent.removeAllViews();
			mProductSumContent.addView(createRowSummary(getActivity(), tvGrandTotal));
			
			TransactionDao trans = new TransactionDao(getActivity());
			OrderTransaction sumTrans = trans.getSummaryTransaction(mHost.mDateFrom, mHost.mDateTo);
			
			if(sumTrans.getTransactionVatExclude() > 0){
				ShopDao shop = new ShopDao(getActivity());
				// total vatExclude
				TextView[] tvTotalVatExclude = {
						createTextViewSummary(getActivity(), getString(R.string.vat_exclude) + " " +
								NumberFormat.getInstance().format(shop.getCompanyVatRate()) + "%", Utils.getLinHorParams(5.9f)),
						createTextViewSummary(getActivity(), mHost.mFormat.currencyFormat(sumTrans.getTransactionVatExclude()), 
								Utils.getLinHorParams(0.8f)),
						createTextViewSummary(getActivity(), "", Utils.getLinHorParams(0.5f)),
						createTextViewSummary(getActivity(), "", Utils.getLinHorParams(0.2f))
				};
				mProductSumContent.addView(createRowSummary(getActivity(), tvTotalVatExclude));
			}
			
			// total sale
			TextView[] tvTotalSale = {
					createTextViewSummary(getActivity(), getString(R.string.total_sale), Utils.getLinHorParams(5.9f)),
					createTextViewSummary(getActivity(), mHost.mFormat.currencyFormat(sumTrans.getTransactionVatable()), 
							Utils.getLinHorParams(0.8f)),
					createTextViewSummary(getActivity(), "", Utils.getLinHorParams(0.5f)),
					createTextViewSummary(getActivity(), "", Utils.getLinHorParams(0.2f))
			};
			mProductSumContent.addView(createRowSummary(getActivity(), tvTotalSale));
		
		}
	}
}
