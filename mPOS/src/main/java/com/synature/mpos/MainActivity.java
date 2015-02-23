package com.synature.mpos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.j1tth4.slidinglibs.SlidingTabLayout;
import com.synature.mpos.SwitchLangFragment.OnChangeLanguageListener;
import com.synature.mpos.database.ComputerDao;
import com.synature.mpos.database.GlobalPropertyDao;
import com.synature.mpos.database.PaymentDetailDao;
import com.synature.mpos.database.PrintReceiptLogDao;
import com.synature.mpos.database.ProductsDao;
import com.synature.mpos.database.SessionDao;
import com.synature.mpos.database.ShopDao;
import com.synature.mpos.database.StaffsDao;
import com.synature.mpos.database.TransactionDao;
import com.synature.mpos.database.UserVerification;
import com.synature.mpos.database.model.OrderComment;
import com.synature.mpos.database.model.OrderDetail;
import com.synature.mpos.database.model.OrderSet.OrderSetDetail;
import com.synature.mpos.database.model.OrderTransaction;
import com.synature.mpos.database.model.Product;
import com.synature.mpos.database.model.ProductDept;
import com.synature.mpos.seconddisplay.SecondDisplayJSON;
import com.synature.pos.SecondDisplayProperty.clsSecDisplay_TransSummary;
import com.synature.util.ImageLoader;
import com.synature.util.Logger;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView.OnEditorActionListener;

public class MainActivity extends FragmentActivity implements 
	MenuCommentDialogFragment.OnCommentDismissListener, ManageCashAmountFragment.OnManageCashAmountDismissListener, 
	UserVerifyDialogFragment.OnCheckPermissionListener, OnChangeLanguageListener{
	
	public static final String TAG = MainActivity.class.getSimpleName();
	
	/**
	 * payment request code
	 */
	public static final int PAYMENT_REQUEST = 1;
	
	/**
	 * food court payment request code
	 */
	public static final int FOOD_COURT_PAYMENT_REQUEST = 2;
	
	/**
	 * add product set type 7 request code
	 */
	public static final int SET_TYPE7_REQUEST = 3;
	
	/**
	 * number of menu grid column preference
	 */
	public static final String PREF_NUM_MENU_COLUMNS = "PrefNumMenuColums";
	
	/**
	 * the number of menu columns
	 */
	public static final String NUM_MENU_COLUMNS = "numMenuColumns";
	
	/**
	 * Wintec customer display
	 */
	private WintecCustomerDisplay mDsp;

	private ProductsDao mProducts;
	private ShopDao mShop;
	private GlobalPropertyDao mGlobal;
	
	private SessionDao mSession;
	private TransactionDao mTrans;
	private ComputerDao mComputer;
	
	private List<OrderDetail> mOrderDetailLst;
	private OrderDetailAdapter mOrderDetailAdapter;
	private List<ProductDept> mProductDeptLst;
	private MenuItemPagerAdapter mPageAdapter;

	private ImageLoader mImageLoader;
	
	private int mSessionId;
	private int mTransactionId;
	private int mStaffId;
	private int mStaffRoleId;
	private int mShopId;
	private int mComputerId;
	
	private ExpandableListView mLvOrderDetail;
	private EditText mTxtBarCode;
	private TableLayout mTbSummary;
	
	private Menu mMenu;
	
	private SlidingTabLayout mTabs;
	private ViewPager mPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        
		mTxtBarCode = (EditText) findViewById(R.id.txtBarCode);
		mTbSummary = (TableLayout) findViewById(R.id.tbLayoutSummary);
		mLvOrderDetail = (ExpandableListView) findViewById(R.id.lvOrder);
		mTabs = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
		mPager = (ViewPager) findViewById(R.id.pager);
		
		Intent intent = getIntent();
		mStaffId = intent.getIntExtra("staffId", 0);
		mStaffRoleId = intent.getIntExtra("staffRoleId", 0);
		
		mSession = new SessionDao(this);
		mTrans = new TransactionDao(this);
		mProducts = new ProductsDao(this);
		mShop = new ShopDao(this);
		mComputer = new ComputerDao(this);
		mGlobal = new GlobalPropertyDao(this);
		
		mShopId = mShop.getShopId();
		mComputerId = mComputer.getComputerId();
		
		mImageLoader = new ImageLoader(this, 0,
					MPOSApplication.IMG_DIR, ImageLoader.IMAGE_SIZE.MEDIUM);

		mDsp = new WintecCustomerDisplay(this);
		
		setupTitle();
		setupBarCodeEvent();
		setupMenuDeptPager();
		setupOrderingKeypadUtils();
	}

	private class MasterDataReceiver extends ResultReceiver{

		private ProgressDialog progress;
		
		public MasterDataReceiver(Handler handler) {
			super(handler);
			progress = new ProgressDialog(MainActivity.this);
			progress.setMessage(getString(R.string.load_master_progress));
			progress.setCancelable(false);
			progress.show();
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			super.onReceiveResult(resultCode, resultData);
			switch(resultCode){
			case MPOSServiceBase.RESULT_SUCCESS:
				if(progress.isShowing())
					progress.dismiss();
				refreshSelf();
				break;
			case MPOSServiceBase.RESULT_ERROR:
				if(progress.isShowing())
					progress.dismiss();
				String msg = resultData.getString("msg");
				new AlertDialog.Builder(MainActivity.this)
				.setMessage(msg)
				.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
					}
				}).show();
				break;
			}
		}
		
	}
	
	private void setupOrderingKeypadUtils(){
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.add(R.id.orderingCtrlContent, new OrderingKeypadFragment(), OrderingKeypadFragment.TAG);
		transaction.commit();
	}
	
	private void setupTitle(){
		StaffsDao staff = new StaffsDao(this);
		com.synature.pos.Staff s = staff.getStaff(mStaffId);
		setTitle(mShop.getShopName());
		getActionBar().setSubtitle(s.getStaffName());
	}
	
	private void setupMenuDeptPager(){
		SharedPreferences settings = getSharedPreferences(PREF_NUM_MENU_COLUMNS, 0);
		int numCols = settings.getInt(NUM_MENU_COLUMNS, 4);

		mProductDeptLst = mProducts.listProductDept();
		mPageAdapter = new MenuItemPagerAdapter(getSupportFragmentManager(), numCols);
		//mPager.setOffscreenPageLimit(8);
		mPager.setAdapter(mPageAdapter);
		final int pageMargin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
						.getDisplayMetrics());
		mPager.setPageMargin(pageMargin);
		mTabs.setViewPager(mPager);
	}
	
	private void setNumMenuColumnPref(int numCols){
		SharedPreferences settings = getSharedPreferences(PREF_NUM_MENU_COLUMNS, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putInt(NUM_MENU_COLUMNS, numCols);
	    editor.commit();
	}
	
	private void setupBarCodeEvent(){
		mTxtBarCode.setOnKeyListener(new OnKeyListener(){

			@Override
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				if(event.getAction() != KeyEvent.ACTION_DOWN)
					return true;
				
				if(keyCode == KeyEvent.KEYCODE_ENTER){
					String barCode = ((EditText) view).getText().toString();
					if(!barCode.isEmpty()){
						Product p = mProducts.getProduct(barCode);
						if(p != null){
							addOrder(p.getProductId(), p.getProductName(), 
									p.getProductTypeId(), p.getVatType(), p.getVatRate(), 
									getOrderingQty(), p.getProductPrice());
						}else{
							new AlertDialog.Builder(MainActivity.this)
							.setTitle(R.string.search)
							.setMessage(R.string.not_found_item)
							.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
								}
							}).show();
						}
					}
					((EditText) view).setText(null);
				}
				return false;
			}
			
		});
	}
	
	private void toggleKeyboard(){
		InputMethodManager imm = (InputMethodManager) getSystemService(
			      Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
	}
	
	private void clearBarCode(){
		mTxtBarCode.setText(null);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		mMenu = menu;
		
		countHoldOrder();
		countSaleDataNotSend();
		updateDisplayColumnMenu();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		switch (item.getItemId()) {
			case R.id.itemHoldBill:
				showHoldBill();
				return true;
			case R.id.itemSwUser:
				switchUser();
				return true;
			case R.id.itemLogout:
				logout();
				return true;
			case R.id.itemReport:
				intent = new Intent(this, SaleReportActivity.class);
				intent.putExtra("staffId", mStaffId);
				startActivity(intent);
				return true;
			case R.id.itemVoid:
				voidBill();
				return true;
			case R.id.itemEditCash:
				editCash();
				return true;
			case R.id.itemCloseShift:
				closeShift();
				return true;
			case R.id.itemEndday:
				endday();
				return true;
			case R.id.itemBackupDb:
				Utils.backupDatabase(this);
				return true;
			case R.id.itemSendEndday:
				intent = new Intent(this, SendEnddayActivity.class);
				intent.putExtra("staffId", mStaffId);
				intent.putExtra("shopId", mShopId);
				intent.putExtra("computerId", mComputerId);
				startActivity(intent);
				return true;
			case R.id.itemReprint:
				intent = new Intent(this, ReprintActivity.class);
				startActivity(intent);
				return true;
			case R.id.itemSendSale:
				intent = new Intent(this, SendSaleActivity.class);
				intent.putExtra("staffId", mStaffId);
				intent.putExtra("shopId", mShopId);
				intent.putExtra("computerId", mComputerId);
				startActivity(intent);
				return true;
			case R.id.itemSetting:
				intent = new Intent(this, SettingsActivity.class);
				startActivity(intent);
				return true;
			case R.id.itemUpdate:
				MasterDataLoader loader = new MasterDataLoader(this, mShopId, 
						new MasterDataReceiver(new Handler()));
				ExecutorService executor = Executors.newSingleThreadExecutor();
				executor.execute(loader);
				executor.shutdown();
				return true;
			case R.id.itemCheckUpdate:
				intent = new Intent(this, CheckUpdateActivity.class);
				startActivity(intent);
				return true;
			case R.id.itemLang:
				SwitchLangFragment swf = SwitchLangFragment.newInstance();
				swf.show(getFragmentManager(), "SwitchLangFragment");
				return true;
			case R.id.item2Cols:
				setNumMenuColumnPref(2);
				refreshSelf();
				return true;
			case R.id.item3Cols:
				setNumMenuColumnPref(3);
				refreshSelf();
				return true;
			case R.id.item4Cols:
				setNumMenuColumnPref(4);
				refreshSelf();
				return true;
				case R.id.item5Cols:
				setNumMenuColumnPref(5);
				refreshSelf();
				return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mDsp.close();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		String curDateMillisec = String.valueOf(Utils.getDate().getTimeInMillis());
		// check current day is already end day ?
		if(!mSession.checkEndday(curDateMillisec)){
			/*
			 * If resume when system date > session date || 
			 * session date > system date. It means the system date
			 * is not valid.
			 * It will be return to LoginActivity for new initial
			 */
			String lastSessDate = mSession.getLastSessionDate();
			if(!TextUtils.isEmpty(lastSessDate)){
				Calendar sessCal = Calendar.getInstance();
				sessCal.setTimeInMillis(Long.parseLong(lastSessDate));
				if(Utils.getDate().getTime().compareTo(sessCal.getTime()) > 0 || 
					sessCal.getTime().compareTo(Utils.getDate().getTime()) > 0){
					// check last session is already end day ?
					if(!mSession.checkEndday(mSession.getLastSessionDate())){
						startActivity(new Intent(MainActivity.this, LoginActivity.class));
						finish();
					}else{
						init();
					}
				}else{
					init();
				}
			}else{
				// not have any session
				init();
			}
		}else{
			startActivity(new Intent(MainActivity.this, LoginActivity.class));
			finish();
		}
		super.onResume();
	}

	/**
	 * summary transaction 
	 */
	public void summary(){
		if(mTbSummary.getChildCount() > 0)
			mTbSummary.removeAllViews();
		
		mTrans.summaryTransaction(mTransactionId);
		OrderDetail sumOrder = mTrans.getSummaryOrder(mTransactionId, true);
		double vatExclude = sumOrder.getVatExclude();
		double totalQty = sumOrder.getOrderQty();
		double totalDiscount = sumOrder.getPriceDiscount();
		double totalRetailPrice = sumOrder.getTotalRetailPrice();
		double totalSalePrice = sumOrder.getTotalSalePrice();
		double totalPriceInclVat = totalSalePrice + vatExclude;
		String disText = sumOrder.getPromotionName().equals("") ? getString(R.string.discount) : sumOrder.getPromotionName();

		mTbSummary.addView(createTableRowSummary(
				getString(R.string.items) + ": " + NumberFormat.getInstance().format(totalQty), 
				mGlobal.currencyFormat(sumOrder.getTotalRetailPrice()), 
				0, 0, 0, 0));
		
		if(totalDiscount > 0){ 
			mTbSummary.addView(createTableRowSummary(disText, 
					"-" + mGlobal.currencyFormat(totalDiscount), 0, 0, 0, 0));
			mTbSummary.addView(createTableRowSummary(getString(R.string.sub_total), 
					mGlobal.currencyFormat(totalSalePrice), 0, 0, 0, 0));
		}
		if(vatExclude > 0){
			mTbSummary.addView(createTableRowSummary(getString(R.string.vat_exclude) +
					" " + NumberFormat.getInstance().format(mShop.getCompanyVatRate()) + "%",
					mGlobal.currencyFormat(vatExclude), 0, 0, 0, 0));
		}
		double rounding = Utils.roundingPrice(mGlobal.getRoundingType(), totalPriceInclVat);
		if(rounding != totalPriceInclVat){
			if(totalDiscount == 0){
				mTbSummary.addView(createTableRowSummary(getString(R.string.sub_total), 
						mGlobal.currencyFormat(totalPriceInclVat), 0, 0, 0, 0));
			}
			mTbSummary.addView(createTableRowSummary(getString(R.string.rounding),
					mGlobal.currencyFormat(rounding - totalPriceInclVat), 0, 0, 0, 0));
		}
		mTbSummary.addView(createTableRowSummary(getString(R.string.total),
				mGlobal.currencyFormat(rounding),
				0, R.style.HeaderText, 0, getResources().getInteger(R.integer.large_text_size)));
		
		if(Utils.isEnableSecondDisplay(this)){
			List<clsSecDisplay_TransSummary> transSummLst = new ArrayList<clsSecDisplay_TransSummary>();
			clsSecDisplay_TransSummary transSumm = new clsSecDisplay_TransSummary();
			transSumm.szSumName = getString(R.string.sub_total); 
			transSumm.szSumAmount = mGlobal.currencyFormat(totalRetailPrice);
			transSummLst.add(transSumm);
			if(totalDiscount > 0){
				transSumm = new clsSecDisplay_TransSummary();
				transSumm.szSumName = disText;
				transSumm.szSumAmount = "-" + mGlobal.currencyFormat(totalDiscount);
				transSummLst.add(transSumm);
			}
			if(vatExclude > 0){
				transSumm = new clsSecDisplay_TransSummary();
				transSumm.szSumName = getString(R.string.vat_exclude) + 
						" " + NumberFormat.getInstance().format(mShop.getCompanyVatRate()) + "%";
				transSumm.szSumAmount = mGlobal.currencyFormat(vatExclude);
				transSummLst.add(transSumm);
			}
			transSumm = new clsSecDisplay_TransSummary();
			transSumm.szSumName = getString(R.string.total_qty); 
			transSumm.szSumAmount = mGlobal.qtyFormat(totalQty);
			transSummLst.add(transSumm);
			
			transSumm = new clsSecDisplay_TransSummary();
			transSumm.szSumName = getString(R.string.total); 
			transSumm.szSumAmount = mGlobal.currencyFormat(totalPriceInclVat);
			transSummLst.add(transSumm);
			secondDisplayItem(transSummLst, mGlobal.currencyFormat(totalPriceInclVat));
		}
		if(Utils.isEnableWintecCustomerDisplay(this)){
			// display only have item
			if(!TextUtils.isEmpty(mDsp.getItemName())){
				if(totalQty > 0){
					try {
						mDsp.setItemTotalQty(mGlobal.qtyFormat(totalQty));
						mDsp.setItemTotalAmount(mGlobal.currencyFormat(totalRetailPrice));
						mDsp.displayOrder();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					mDsp.displayWelcome();
				}
			}
		}
		resetOrdingQty();
	}
	
	private TableRow createTableRowSummary(String label, String value,
			int labelAppear, int valAppear, float labelSize, float valSize){
		TextView tvLabel = new TextView(this);
		TextView tvValue = new TextView(this);
		tvLabel.setTextAppearance(this, android.R.style.TextAppearance_Holo_Medium);
		tvValue.setTextAppearance(this, android.R.style.TextAppearance_Holo_Medium);
		if(labelAppear != 0)
			tvLabel.setTextAppearance(this, labelAppear);
		if(valAppear != 0)
			tvValue.setTextAppearance(this, valAppear);
		tvLabel.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 
				TableRow.LayoutParams.WRAP_CONTENT, 1f));
		tvValue.setGravity(Gravity.END);
		if(labelSize != 0)
			tvLabel.setTextSize(labelSize);
		if(valSize != 0)
			tvValue.setTextSize(valSize);
		tvLabel.setText(label);
		tvValue.setText(value);
		TableRow rowSummary = new TableRow(this);
		rowSummary.setPadding(4, 4, 4, 4);
		rowSummary.addView(tvLabel);
		rowSummary.addView(tvValue);
		return rowSummary;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if(requestCode == PAYMENT_REQUEST){
			if(resultCode == RESULT_OK){
				// request param from PaymentActivity for log to 
				// PrintReceiptLog
				double totalSalePrice = intent.getDoubleExtra("totalSalePrice", 0);
				double totalPaid = intent.getDoubleExtra("totalPaid", 0);
				double change = intent.getDoubleExtra("change", 0);
				int printType = intent.getIntExtra("printType", PrintReceipt.NORMAL);
				int transactionId = intent.getIntExtra("transactionId", 0);
				int staffId = intent.getIntExtra("staffId", 0);
				successTransaction(transactionId, staffId, totalSalePrice, 
						totalPaid, change, printType);
			}
		}
		if(requestCode == SET_TYPE7_REQUEST){
			if(resultCode == RESULT_OK){
				String setName = intent.getStringExtra("setName");
				String setPrice = intent.getStringExtra("setPrice");
				mDsp.setItemName(setName);
				mDsp.setItemAmount(setPrice);
			}
		}
	}
	
	/**
	 * @param transactionId
	 * @param staffId
	 * @param printType
	 */
	private void printReceipt(int transactionId, int staffId, int printType){
		// log receipt for print task
		PrintReceiptLogDao printLog = new PrintReceiptLogDao(MainActivity.this);
		int isCopy = 0;
		for(int i = 0; i < mComputer.getReceiptHasCopy(); i++){
			if(i > 0)
				isCopy = 1;
			printLog.insertLog(transactionId, staffId, isCopy);
		}
		if(printType == PrintReceipt.NORMAL){
			new PrintReceipt(MainActivity.this, mPrintReceiptListener).execute();
		}else if(printType == PrintReceipt.WASTE){
			new WastePrintReceipt(MainActivity.this, mPrintReceiptListener).execute();
		}
	}
	
	/**
	 * @param transactionId
	 * @param staffId
	 * @param totalSalePrice
	 * @param totalPaid
	 * @param change
	 * @param printType
	 */
	private void successTransaction(int transactionId, int staffId, double totalSalePrice, 
			double totalPaid, double change, int printType){
		// clear item that display on dsp
		if(Utils.isEnableWintecCustomerDisplay(this))
			mDsp.setItemName(null);
		
		printReceipt(transactionId, staffId, printType);
		
		if(change > 0){
			LinearLayout changeView = new LinearLayout(MainActivity.this);
			TextView tvChange = new TextView(MainActivity.this);
			tvChange.setTextSize(getResources().getDimension(R.dimen.larger_text_size));
			tvChange.setGravity(Gravity.CENTER);
			tvChange.setText(mGlobal.currencyFormat(change));
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 
					LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER;
			changeView.addView(tvChange, params);
			
			new AlertDialog.Builder(MainActivity.this)
			.setTitle(R.string.change)
			.setCancelable(false)
			.setView(changeView)
			.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(Utils.isEnableWintecCustomerDisplay(MainActivity.this)){
						mDsp.displayWelcome();
					}
					if(Utils.isEnableSecondDisplay(MainActivity.this)){
						clearSecondDisplay();
					}
				}
			}).show();
			if(Utils.isEnableSecondDisplay(this)){
				secondDisplayChangePayment(mGlobal.currencyFormat(totalSalePrice), 
						mGlobal.currencyFormat(totalPaid), mGlobal.currencyFormat(change));
				new Handler().postDelayed(new Runnable(){

					@Override
					public void run() {
						clearSecondDisplay();
					}
					
				}, 10000);
			}
		}
		if(Utils.isEnableWintecCustomerDisplay(this)){
			mDsp.displayTotalPay(mGlobal.currencyFormat(totalPaid), 
					mGlobal.currencyFormat(change));
			if(change == 0){
				new Handler().postDelayed(
						new Runnable(){
	
							@Override
							public void run() {
								runOnUiThread(new Runnable(){
	
									@Override
									public void run() {
										mDsp.displayWelcome();
									}
									
								});
							}
				}, 10000);
			}
		}
	}
	
	public class SendSaleResultReceiver extends ResultReceiver{

		public SendSaleResultReceiver(Handler handler) {
			super(handler);
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			super.onReceiveResult(resultCode, resultData);
			switch(resultCode){
				case SaleSenderService.RESULT_SUCCESS:
					countSaleDataNotSend();
					Toast.makeText(MainActivity.this, 
							getString(R.string.send_sale_data_success), Toast.LENGTH_SHORT).show();
					break;
				case SaleSenderService.RESULT_ERROR:
					String msg = resultData.getString("msg");
					Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
					break;
			}
		}
		
	}
	
	/**
	 * The listener for print receipt
	 */
	private PrintReceipt.OnPrintReceiptListener mPrintReceiptListener = 
		new PrintReceipt.OnPrintReceiptListener(){

			@Override
			public void onPrePrint() {}

			@Override
			public void onPostPrint() {
				Intent intent = new Intent(MainActivity.this, SaleSenderService.class);
				intent.putExtra(SaleSenderService.WHAT_TO_DO_PARAM, SaleSenderService.SEND_PARTIAL);
				intent.putExtra(SaleSenderService.SESSION_DATE_PARAM, mSession.getLastSessionDate());
				intent.putExtra(SaleSenderService.SHOP_ID_PARAM, mShopId);
				intent.putExtra(SaleSenderService.COMPUTER_ID_PARAM, mComputerId);
				intent.putExtra(SaleSenderService.STAFF_ID_PARAM, mStaffId);
				intent.putExtra(SaleSenderService.RECEIVER_NAME, new SendSaleResultReceiver(new Handler()));
				startService(intent);
			}
	};
	
	/**
	 * Paid equal total price
	 */
	private void cashPaid(){
		if(mOrderDetailLst.size() > 0){
			new AlertDialog.Builder(this)
			.setTitle(R.string.cash_paid)
			.setMessage(R.string.confirm_fast_payment)
			.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					OrderDetail sumOrder = mTrans.getSummaryOrder(mTransactionId, true);
					PaymentDetailDao payment = new PaymentDetailDao(MainActivity.this);
					double totalSalePrice = sumOrder.getTotalSalePrice() + sumOrder.getVatExclude();
					double totalPaid = Utils.roundingPrice(mGlobal.getRoundingType(), totalSalePrice);
					
					payment.deleteAllPaymentDetail(mTransactionId);
					payment.addPaymentDetail(mTransactionId, mComputerId, PaymentDetailDao.PAY_TYPE_CASH, 
							totalPaid, totalPaid, "", 0, 0, 0, 0, "");
					payment.confirmPayment(mTransactionId);
					
					// open cash drawer
					WintecCashDrawer drw = new WintecCashDrawer(MainActivity.this);
					drw.openCashDrawer();
					drw.close();
					
					mTrans.closeTransaction(mTransactionId, mStaffId, totalSalePrice);
					successTransaction(mTransactionId, mStaffId, totalSalePrice, 
							totalSalePrice, 0, PrintReceipt.NORMAL);
					
					init();
				}
			}).show();
		}
	}
	
	/**
	 * Go to PaymentActivity
	 */
	private void payment(){
		if(mOrderDetailLst.size() > 0){
			// food court type
			if(mShop.getFastFoodType() == ShopDao.SHOP_TYPE_FOOD_COURT){
				Intent intent = new Intent(MainActivity.this, FoodCourtCardPayActivity.class);
				intent.putExtra("transactionId", mTransactionId);
				intent.putExtra("shopId", mShopId);
				intent.putExtra("computerId", mComputerId);
				intent.putExtra("staffId", mStaffId);
				startActivityForResult(intent, FOOD_COURT_PAYMENT_REQUEST);
			}else{
				if(PaymentActivity.sIsRunning == false){
					Intent intent = new Intent(MainActivity.this, PaymentActivity.class);
					intent.putExtra("transactionId", mTransactionId);
					intent.putExtra("computerId", mComputerId);
					intent.putExtra("staffId", mStaffId);
					startActivityForResult(intent, PAYMENT_REQUEST);
				}
			}
		}
	}

	private void promotion() {
		if (mOrderDetailLst.size() > 0) {
			Intent intent = new Intent(MainActivity.this, PromotionActivity.class);
			intent.putExtra("transactionId", mTransactionId);
			startActivity(intent);
		}
	}
	
	/**
	 * Go to DiscountActivity
	 */
	private void discount(){
		if(mOrderDetailLst.size() > 0){
			StaffsDao st = new StaffsDao(MainActivity.this);
			if(!st.checkOtherDiscountPermission(mStaffRoleId)){
				UserVerifyDialogFragment uvf = UserVerifyDialogFragment.newInstance(StaffsDao.OTHER_DISCOUNT_PERMISSION);
				uvf.show(getFragmentManager(), "StaffPermissionDialog");
			}else{
				goToOtherDiscountActivity();
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			return false;
		}else{
			return super.onKeyDown(keyCode, event);
		}
	}

	/**
	 * @author j1tth4
	 *
	 */
	private class OrderDetailAdapter extends BaseExpandableListAdapter{
		
		@Override
		public int getGroupCount() {
			return mOrderDetailLst != null ? mOrderDetailLst.size() : 0;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return mOrderDetailLst.get(groupPosition).getOrdSetDetailLst() != null ? mOrderDetailLst.get(groupPosition).getOrdSetDetailLst().size() : 0;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return mOrderDetailLst.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return mOrderDetailLst.get(groupPosition).getOrdSetDetailLst().get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(final int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			ViewHolder holder;		
			if(convertView == null){
				convertView = getLayoutInflater().inflate(R.layout.order_list_item, parent, false);
				holder = new ViewHolder();
				holder.tvOrderNo = (TextView) convertView.findViewById(R.id.tvOrderNo);
				holder.tvOrderName = (CheckedTextView) convertView.findViewById(R.id.chkOrderName);
				holder.tvOrderPrice = (TextView) convertView.findViewById(R.id.tvOrderPrice);
				holder.tvComment = (TextView) convertView.findViewById(R.id.tvComment);
				holder.txtOrderQty = (EditText) convertView.findViewById(R.id.txtOrderQty);
				holder.btnComment = (ImageButton) convertView.findViewById(R.id.btnComment);
				holder.btnSetMod = (ImageButton) convertView.findViewById(R.id.btnSetModify);
				holder.btnMinus = (Button) convertView.findViewById(R.id.btnOrderMinus);
				holder.btnPlus = (Button) convertView.findViewById(R.id.btnOrderPlus);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			final OrderDetail orderDetail = mOrderDetailLst.get(groupPosition);
			holder.tvOrderName.setChecked(orderDetail.isChecked());
			holder.tvOrderNo.setText(Integer.toString(groupPosition + 1) + ".");
			holder.tvOrderName.setText(orderDetail.getProductName());
			holder.tvOrderPrice.setText(mGlobal.currencyFormat(orderDetail.getProductPrice()));
			holder.txtOrderQty.setText(mGlobal.qtyFormat(orderDetail.getOrderQty()));
			holder.tvComment.setText(null);
			if(orderDetail.getOrderCommentLst() != null){
				holder.tvComment.setVisibility(View.VISIBLE);
				for(int i = 0; i < orderDetail.getOrderCommentLst().size(); i++){
					final OrderComment comment = orderDetail.getOrderCommentLst().get(i);
					holder.tvComment.append("-" + comment.getCommentName());
					if(comment.getCommentPrice() > 0){
						double commentQty = comment.getCommentQty();
						double commentPrice = comment.getCommentPrice();
						holder.tvComment.append(" " + mGlobal.qtyFormat(commentQty));
						holder.tvComment.append("x" + mGlobal.currencyFormat(commentPrice));
						holder.tvComment.append("=" + mGlobal.currencyFormat(comment.getCommentTotalPrice()));
					}
					holder.tvComment.append("\n");
				}
			}else{
				holder.tvComment.setVisibility(View.GONE);
			}
			if(orderDetail.getOrderComment() != null)
				holder.tvComment.append(orderDetail.getOrderComment());
			holder.tvOrderName.setOnClickListener(new OnOrderClickListener(orderDetail));
			holder.btnComment.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					MenuCommentDialogFragment commentDialog = 
							MenuCommentDialogFragment.newInstance(groupPosition, mTransactionId, 
									mComputerId, orderDetail.getOrderDetailId(), 
									orderDetail.getVatType(), mProducts.getVatRate(orderDetail.getProductId()),
									orderDetail.getProductName(), orderDetail.getOrderComment());
					commentDialog.show(getFragmentManager(), "CommentDialog");
				}
				
			});
			holder.btnSetMod.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(MainActivity.this, ProductSetActivity.class);
					intent.putExtra("mode", ProductSetActivity.EDIT_MODE);
					intent.putExtra("transactionId", mTransactionId);
					intent.putExtra("computerId", mComputerId);
					intent.putExtra("orderDetailId", orderDetail.getOrderDetailId());
					intent.putExtra("productId", orderDetail.getProductId());
					startActivity(intent);
				}
				
			});
			holder.btnMinus.setOnClickListener(new OnClickListener(){
	
				@Override
				public synchronized void onClick(View v) {
					double qty = orderDetail.getOrderQty();
					double pricePerUnit = orderDetail.getProductPrice();
					if(--qty > 0){
						orderDetail.setOrderQty(qty);
                        orderDetail.setTotalRetailPrice(pricePerUnit * qty);
						updateOrder(orderDetail.getOrderDetailId(),
								qty, pricePerUnit,
								orderDetail.getVatType(),
								mProducts.getVatRate(orderDetail.getProductId()),
								orderDetail.getProductName(), orderDetail.getProductName1());
					}else{
						new AlertDialog.Builder(MainActivity.this)
						.setTitle(R.string.delete)
						.setMessage(R.string.confirm_delete_item)
						.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								
							}
						})
						.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								deleteOrder(orderDetail.getOrderDetailId());
								loadOrder();
							}
						}).show();
					}
					mOrderDetailAdapter.notifyDataSetChanged();
				}
			});
			
			holder.btnPlus.setOnClickListener(new OnClickListener(){
	
				@Override
				public void onClick(View v) {
					double qty = orderDetail.getOrderQty();
                    double pricePerUnit = orderDetail.getProductPrice();
					orderDetail.setOrderQty(++qty);
                    orderDetail.setTotalRetailPrice(pricePerUnit * qty);
					updateOrder(orderDetail.getOrderDetailId(),
							qty, pricePerUnit,
							orderDetail.getVatType(),
							mProducts.getVatRate(orderDetail.getProductId()),
							orderDetail.getProductName(), orderDetail.getProductName1());
					
					mOrderDetailAdapter.notifyDataSetChanged();
				}
				
			});
			holder.txtOrderQty.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if(hasFocus){
						EditText editText = (EditText) v;
						orderDetail.mTxtFocus = editText;
					}
				}
			});
			holder.txtOrderQty.setOnKeyListener(new OnKeyListener(){

				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if(event.getAction() != KeyEvent.ACTION_DOWN)
						return true;
					
					if(keyCode == KeyEvent.KEYCODE_ENTER){
						EditText editText = (EditText) v;
						orderDetail.mTxtFocus = editText;
						try {
							double qty = Utils.stringToDouble(editText.getText().toString());
							if(qty > 0){
								orderDetail.setOrderQty(qty);
								updateOrder(orderDetail.getOrderDetailId(),
										qty, orderDetail.getProductPrice(), 
										orderDetail.getVatType(),
										mProducts.getVatRate(orderDetail.getProductId()),
										orderDetail.getProductName(), orderDetail.getProductName1());
								
								mOrderDetailAdapter.notifyDataSetChanged();
							}
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						toggleKeyboard();
					}
					return false;
				}
				
			});
			if(orderDetail.mTxtFocus != null){
				holder.txtOrderQty.requestFocus();
				holder.txtOrderQty.selectAll();
				orderDetail.mTxtFocus = null;
			}else{
				holder.txtOrderQty.clearFocus();
			}
			if(orderDetail.getProductTypeId() == ProductsDao.SET_CAN_SELECT){
				holder.btnSetMod.setVisibility(View.VISIBLE);
				holder.btnComment.setVisibility(View.GONE);
			}else{
				holder.btnSetMod.setVisibility(View.GONE);
				holder.btnComment.setVisibility(View.VISIBLE);
			}
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			ChildViewHolder holder;
			if(convertView == null){
				convertView = getLayoutInflater().inflate(R.layout.order_detail_set_item, parent, false);
				holder = new ChildViewHolder();
				holder.tvSetNo = (TextView) convertView.findViewById(R.id.tvSetNo);
				holder.tvSetName = (TextView) convertView.findViewById(R.id.tvSetName);
				holder.tvSetPrice = (TextView) convertView.findViewById(R.id.tvSetPrice);
				holder.tvSetQty = (TextView) convertView.findViewById(R.id.tvSetQty);
				holder.tvSetNo.setTextAppearance(MainActivity.this, android.R.style.TextAppearance_Holo_Small);
				holder.tvSetName.setTextAppearance(MainActivity.this, android.R.style.TextAppearance_Holo_Small);
				holder.tvSetPrice.setTextAppearance(MainActivity.this, android.R.style.TextAppearance_Holo_Small);
				holder.tvSetQty.setTextAppearance(MainActivity.this, android.R.style.TextAppearance_Holo_Small);
				convertView.setTag(holder);
			}else{
				holder = (ChildViewHolder) convertView.getTag();
			}
			OrderSetDetail setDetail = mOrderDetailLst.get(groupPosition).getOrdSetDetailLst().get(childPosition);
			holder.tvSetNo.setText("-");
			holder.tvSetName.setText(setDetail.getProductName());
			holder.tvSetPrice.setText(setDetail.getProductPrice() > 0 ? mGlobal.currencyFormat(setDetail.getProductPrice()) : null);
			holder.tvSetQty.setText(mGlobal.qtyFormat(setDetail.getOrderSetQty()));
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void notifyDataSetChanged() {
			summary();
			super.notifyDataSetChanged();
		}
	}
	
	/**
	 * @author j1tth4
	 * OrderDetail selected event
	 */
	private class OnOrderClickListener implements OnClickListener{

		private OrderDetail mOrder;
		
		public OnOrderClickListener(OrderDetail order){
			mOrder = order;
		}
		
		@Override
		public void onClick(View v) {
			if(mOrder.isChecked()){
				mOrder.setChecked(false);
			}else{
				mOrder.setChecked(true);
			}
			mOrderDetailAdapter.notifyDataSetChanged();
		}
	}
		
	private class ChildViewHolder {
		TextView tvSetNo;
		TextView tvSetName;
		TextView tvSetPrice;
		TextView tvSetQty;
	}
		
	private class ViewHolder{
		TextView tvOrderNo;
		CheckedTextView tvOrderName;
		TextView tvOrderPrice;
		TextView tvComment;
		EditText txtOrderQty;
		ImageButton btnSetMod;
		ImageButton btnComment;
		Button btnMinus;
		Button btnPlus;
	}
		
	private class HoldBillAdapter extends BaseAdapter{
		LayoutInflater inflater;
		List<OrderTransaction> transLst;
		Calendar c;
		
		public HoldBillAdapter(List<OrderTransaction> transLst){
			inflater = getLayoutInflater();
			this.transLst = transLst;
			c = Calendar.getInstance();
		}
		
		@Override
		public int getCount() {
			return transLst != null ? transLst.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return transLst.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final OrderTransaction trans = transLst.get(position);
			ViewHolder holder;
			if(convertView == null){
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.hold_bill_template, parent, false);
				holder.tvNo = (TextView) convertView.findViewById(R.id.tvNo);
				holder.tvOpenTime = (TextView) convertView.findViewById(R.id.tvOpenTime);
				holder.tvOpenStaff = (TextView) convertView.findViewById(R.id.tvOpenStaff);
				holder.tvRemark = (TextView) convertView.findViewById(R.id.tvRemark);
				holder.btnBillDetail = (ImageButton) convertView.findViewById(R.id.btnBillDetail);	
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			c.setTimeInMillis(Long.parseLong(trans.getOpenTime()));
			holder.tvNo.setText(Integer.toString(position + 1) + ".");
			holder.tvOpenTime.setText(mGlobal.dateTimeFormat(c.getTime()));
			holder.tvOpenStaff.setText(trans.getStaffName());
			holder.tvRemark.setText(trans.getTransactionNote());
			holder.btnBillDetail.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					BillViewerFragment f = BillViewerFragment.newInstance(trans.getTransactionId(), 
							BillViewerFragment.CHECK_VIEW, BillViewerFragment.RECEIPT, false);
					f.show(getFragmentManager(), BillViewerFragment.TAG);
				}
			});
			return convertView;
		}
		
		private class ViewHolder{
			TextView tvNo;
			TextView tvOpenTime;
			TextView tvOpenStaff;
			TextView tvRemark;
			ImageButton btnBillDetail;
		}
	}
	
	/**
	 * @author j1tth4
	 * page
	 */
	private class MenuItemPagerAdapter extends FragmentPagerAdapter{
		private int mNumCols;
		
		public MenuItemPagerAdapter(FragmentManager fm, int numCols) {
			super(fm);
			mNumCols = numCols;
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			return mProductDeptLst.get(position).getProductDeptName();
		}
	
		@Override
		public android.support.v4.app.Fragment getItem(int position) {
			int deptId = mProductDeptLst.get(position).getProductDeptId();
			return MenuPageFragment.newInstance(deptId, mNumCols);
		}
	
		@Override
		public int getCount() {
			return mProductDeptLst.size();
		}		
	}
	
	public static class MenuPageFragment extends android.support.v4.app.Fragment {
		
		private List<Product> mProductLst;
		private MenuItemAdapter mMenuItemAdapter;
		
		private int mDeptId;
		private int mNumCols;
		
		private GridView mGvItem;
		private LayoutInflater mInflater;
		
		public static MenuPageFragment newInstance(int deptId, int numCols){
			MenuPageFragment f = new MenuPageFragment();
			Bundle b = new Bundle();
			b.putInt("deptId", deptId);
			b.putInt("numCols", numCols);
			f.setArguments(b);
			return f;
		}
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			mDeptId = getArguments().getInt("deptId");
			mNumCols = getArguments().getInt("numCols");
			mInflater = getActivity().getLayoutInflater();
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			loadMenuItem();
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			mGvItem = (GridView) inflater.inflate(R.layout.menu_grid_view, container, false);
			mGvItem.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View v, int position,
						long id) {
					Product p = (Product) parent.getItemAtPosition(position);
					((MainActivity) getActivity()).onMenuClick(p.getProductId(),
							p.getProductName(), p.getProductName1(), p.getProductTypeId(), 
							p.getVatType(), p.getVatRate(), p.getProductPrice());
				}
			});
			
			mGvItem.setOnItemLongClickListener(new OnItemLongClickListener(){
				
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View v,
						int position, long id) {
					Product p = (Product) parent.getItemAtPosition(position);
					ImageViewPinchZoom imgZoom = ImageViewPinchZoom.newInstance(p.getImgName(), p.getProductName(), 
							((MainActivity) getActivity()).mGlobal.currencyFormat(p.getProductPrice()));
					imgZoom.show(getFragmentManager(), "MenuImage");
					return true;
				}
				
			});
			setGridColumns();
			return mGvItem;
		}
		
		private void loadMenuItem(){
			mProductLst = ((MainActivity) getActivity()).mProducts.listProduct(mDeptId);
			mMenuItemAdapter = new MenuItemAdapter();
			mGvItem.setAdapter(mMenuItemAdapter);
		}

		private void setGridColumns(){
			mGvItem.setNumColumns(mNumCols);
		}
		
		/**
		 * @author j1tth4
		 * MenuItemAdapter
		 */
		private class MenuItemAdapter extends BaseAdapter{
		
			@Override
			public int getCount() {
				return mProductLst.size();
			}

			@Override
			public Product getItem(int position) {
				return mProductLst.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				final Product p = mProductLst.get(position);
				final MenuItemViewHolder holder;
				if(convertView == null){
					convertView = mInflater.inflate(R.layout.menu_template, parent, false);
					holder = new MenuItemViewHolder();
					holder.tvMenu = (TextView) convertView.findViewById(R.id.textViewMenuName);
					holder.tvPrice = (TextView) convertView.findViewById(R.id.textViewMenuPrice);
					holder.imgMenu = (ImageView) convertView.findViewById(R.id.imageViewMenu);
					convertView.setTag(holder);
				}else{
					holder = (MenuItemViewHolder) convertView.getTag();
				}
				
				holder.tvMenu.setText(p.getProductName());
				// open price fix price = -1
				if(p.getProductPrice() < 0){
					holder.tvPrice.setVisibility(View.INVISIBLE);
				}else{
					holder.tvPrice.setText(((MainActivity) 
							getActivity()).mGlobal.currencyFormat(p.getProductPrice()));
				}
				if(p.getProductTypeId() == ProductsDao.SIZE){
					holder.tvPrice.setText("(size)");
				}
				if(Utils.isShowMenuImage(getActivity())){
					holder.tvMenu.setLines(2);
					holder.tvMenu.setTextSize(14);
					holder.imgMenu.setVisibility(View.VISIBLE);
					holder.imgMenu.setImageBitmap(null);
					((MainActivity) getActivity()).mImageLoader.displayImage(
							Utils.getImageUrl(getActivity()) + 
							p.getImgName(), holder.imgMenu);
				}else{
					holder.tvMenu.setLines(4);
					holder.tvMenu.setTextSize(getResources().getDimension(R.dimen.menu_text_large));
					holder.imgMenu.setVisibility(View.GONE);
				}
				return convertView;
			}
		}
	}
	
	/**
	 * @author j1tth4
	 * ProductSizeAdapter
	 */
	private class ProductSizeAdapter extends BaseAdapter{
		
		private LayoutInflater mInflater;
		private List<Product> mProLst;
		
		public ProductSizeAdapter(List<Product> proLst){
			mInflater = getLayoutInflater();
			mProLst = proLst;
		}
		
		@Override
		public int getCount() {
			return mProLst != null ? mProLst.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return mProLst.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MenuItemViewHolder holder;
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.menu_template, parent, false);
				holder = new MenuItemViewHolder();
				holder.tvMenu = (TextView) convertView.findViewById(R.id.textViewMenuName);
				holder.tvPrice = (TextView) convertView.findViewById(R.id.textViewMenuPrice);
				holder.imgMenu = (ImageView) convertView.findViewById(R.id.imageViewMenu);
				convertView.setTag(holder);
			}else{
				holder = (MenuItemViewHolder) convertView.getTag();
			}
			Product p = mProLst.get(position);
			holder.tvMenu.setText(p.getProductName());
			if(p.getProductPrice() < 0)
				holder.tvPrice.setVisibility(View.INVISIBLE);
			else
				holder.tvPrice.setText(mGlobal.currencyFormat(p.getProductPrice()));

			if(Utils.isShowMenuImage(MainActivity.this)){
				holder.imgMenu.setVisibility(View.VISIBLE);
				mImageLoader.displayImage(
						Utils.getImageUrl(MainActivity.this) + 
						p.getImgName(), holder.imgMenu);
			}
			return convertView;
		}
	}
	
	/**
	 * @param productId
	 * @param productCode
	 * @param productName
	 * @param productName2
	 * @param productTypeId
	 * @param vatType
	 * @param vatRate
	 * @param productPrice
	 */
	public void onMenuClick(int productId, String productName, String productName2,
			int productTypeId, int vatType, double vatRate, double productPrice) {
		mDsp.setItemName(TextUtils.isEmpty(productName2) ? productName : productName2);
		mDsp.setItemQty(mGlobal.qtyFormat(1));
		if(productTypeId == ProductsDao.NORMAL_TYPE || 
				productTypeId == ProductsDao.SET){
			addOrder(productId, productName, productTypeId, 
					vatType, vatRate, getOrderingQty(), productPrice);
		}else if(productTypeId == ProductsDao.SIZE){
			productSizeDialog(productId, productName);
		}else if(productTypeId == ProductsDao.SET_CAN_SELECT){
			Intent intent = new Intent(MainActivity.this, ProductSetActivity.class);
			intent.putExtra("mode", ProductSetActivity.ADD_MODE);
			intent.putExtra("transactionId", mTransactionId);
			intent.putExtra("computerId", mComputerId);
			intent.putExtra("productId", productId);
			intent.putExtra("setGroupName", productName);
			startActivityForResult(intent, SET_TYPE7_REQUEST);
		}
	}
	
	private void resetOrdingQty(){
		OrderingKeypadFragment f = getOrderingKeypadFragment();
		if(f != null){
			f.resetTotalQty();
		}
	}
	
	private int getOrderingQty(){
		int qty = 1;
		OrderingKeypadFragment f = getOrderingKeypadFragment();
		if(f != null){
			qty = f.getTotalQty();
			if(qty == 0)
				qty = 1;
		}
		return qty;
	}
	
	private OrderingKeypadFragment getOrderingKeypadFragment(){
		OrderingKeypadFragment f = (OrderingKeypadFragment) 
				getFragmentManager().findFragmentByTag(OrderingKeypadFragment.TAG);
		return f;
	}

	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btnBillDetail:
			showBillDetail();
			break;
		case R.id.btnFree:
			setFreeProductPrice();
			break;
		case R.id.btnDelOrder:
			deleteSelectedOrder();
			break;
		case R.id.btnClearSelOrder:
			clearSelectedOrder();
			break;
		case R.id.btnCashPaid:
			cashPaid();
			break;
		case R.id.btnPayment:
			payment();
			break;
		case R.id.btnDiscount:
			discount();
			break;
		case R.id.btnPromotion:
			promotion();
			break;
		case R.id.btnHold:
			holdOrder();
			break;
		case R.id.btnCancelOrder:
			cancelOrder();
			break;
		case R.id.imgBtnToggleKey:
			toggleKeyboard();
			break;
		case R.id.imgBtnClearBarcode:
			clearBarCode();
			break;
		}
	}
	
	private void cancelOrder(){
		if(mOrderDetailLst.size() > 0){
			new AlertDialog.Builder(MainActivity.this)
			.setTitle(android.R.string.cancel)
			.setMessage(R.string.confirm_cancel_order)
			.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				
				}
			})
			.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					clearTransaction();
				}
			})
			.show();
		}
	}

	/**
	 * Hold order click
	 */
	private void holdOrder(){
		if(mOrderDetailLst.size() > 0){
			final EditText txtRemark = new EditText(MainActivity.this);
			txtRemark.setHint(R.string.remark);
			txtRemark.setTextSize(getResources().getInteger(R.integer.large_text_size));
			OrderTransaction trans = mTrans.getTransaction(mTransactionId, true);
			txtRemark.setText(trans.getTransactionNote());
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			builder.setTitle(R.string.hold);
			builder.setView(txtRemark);
			builder.setCancelable(false);
			builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String note = txtRemark.getText().toString();
					mTrans.holdTransaction(mTransactionId, note);
					init();
				}
			});
			final AlertDialog dialog = builder.create();
			dialog.show();
			dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, 
					WindowManager.LayoutParams.WRAP_CONTENT);
			txtRemark.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			    @Override
			    public void onFocusChange(View v, boolean hasFocus) {
			        if (hasFocus) {
			            dialog.getWindow().setSoftInputMode(
			            		WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			        }
			    }
			});
		}
	}

	public void switchUser() {
		LayoutInflater inflater = getLayoutInflater();
		View swUserView = inflater.inflate(R.layout.switch_user_popup, null);
		final EditText txtUser = (EditText) swUserView.findViewById(R.id.txtUser);
		final EditText txtPassword = (EditText) swUserView.findViewById(R.id.txtPassword);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle(R.string.switch_user);
		builder.setView(swUserView);
		builder.setCancelable(false);
		builder.setNeutralButton(android.R.string.ok, null);
		
		final AlertDialog d = builder.create();	
		d.show();
		Button btnOk = d.getButton(AlertDialog.BUTTON_NEUTRAL);
		btnOk.setOnClickListener(new OnClickListener(){
	
			@Override
			public void onClick(View v) {
				String user = txtUser.getText().toString();
				String pass = txtPassword.getText().toString();
				if(!TextUtils.isEmpty(user)){
					if(!TextUtils.isEmpty(pass)){
						pass = txtPassword.getText().toString();
						UserVerification login = new UserVerification(MainActivity.this, user, pass);
						if(login.checkUser()){
							com.synature.pos.Staff s = login.checkLogin();
							if(s != null){
								mStaffId = s.getStaffID();
								init();
								d.dismiss();
							}else{
								txtPassword.setError(getString(R.string.incorrect_password));
							}
						}else{
							txtUser.setError(getString(R.string.incorrect_staff_code));
						}
					}else{
						txtPassword.setError(getString(R.string.enter_password));
					}
				}else{
					txtUser.setError(getString(R.string.enter_staff_code));
				}
			}
			
		});
	}

	/**
	 * Logout
	 */
	public void logout() {
		StaffsDao staff = new StaffsDao(this);
		com.synature.pos.Staff s = staff.getStaff(mStaffId);
		new AlertDialog.Builder(MainActivity.this)
		.setTitle(R.string.logout)
		.setMessage(s.getStaffName() + "\n" + getString(R.string.confirm_logout))
		.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		})
		.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				startActivity(new Intent(MainActivity.this, LoginActivity.class));
				finish();
			}
		})
		.show();
	}

	/**
	 * Update order list
	 * @param position
	 * @param orderDetailId
	 */
	private void updateOrderLst(int position, int orderDetailId){
		OrderDetail orderDetail = mTrans.getOrder(mTransactionId, orderDetailId);
		if(orderDetail != null){
			mOrderDetailLst.set(position, orderDetail);
			mOrderDetailAdapter.notifyDataSetChanged();
			expandOrderLv(position);
		}
		//new UpdateOrderTask(position, orderDetailId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	/**
	 * Update order list
	 * @param orderDetailId
	 */
	private void updateOrderLst(int orderDetailId){
		OrderDetail orderDetail = mTrans.getOrder(mTransactionId, orderDetailId);
		if(orderDetail != null){
			mOrderDetailLst.add(orderDetail);
			mOrderDetailAdapter.notifyDataSetChanged();
			expandOrderLv(mOrderDetailAdapter.getGroupCount() - 1);
			scrollOrderLv(mOrderDetailAdapter.getGroupCount());
		}
		//new UpdateOrderTask(0, orderDetailId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	private class UpdateOrderTask extends AsyncTask<Void, Void, Void>{

		private OrderDetail orderDetail;
		private int position;
		private int orderId;
		
		public UpdateOrderTask(int position, int orderId){
			this.position = position;
			this.orderId = orderId;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			if(orderDetail != null){
				if(position > 0){
					mOrderDetailLst.set(position, orderDetail);
					mOrderDetailAdapter.notifyDataSetChanged();
					expandOrderLv(position);
				}else{
					mOrderDetailLst.add(orderDetail);
					mOrderDetailAdapter.notifyDataSetChanged();
					expandOrderLv(mOrderDetailAdapter.getGroupCount() - 1);
					scrollOrderLv(mOrderDetailAdapter.getGroupCount());
				}
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			orderDetail = mTrans.getOrder(mTransactionId, orderId);
			return null;
		}
		
	}
	
	/**
	 * load order
	 */
	private void loadOrder(){
		mOrderDetailLst = mTrans.listAllOrder(mTransactionId);
		if(mOrderDetailLst == null){
			mOrderDetailLst = new ArrayList<OrderDetail>();
		}
		if(mOrderDetailAdapter == null){
			mOrderDetailAdapter = new OrderDetailAdapter();
			mLvOrderDetail.setAdapter(mOrderDetailAdapter);
			mLvOrderDetail.setGroupIndicator(null);
		}
		mOrderDetailAdapter.notifyDataSetChanged();
		// expand all
		expandOrderLv(0);
		scrollOrderLv(mOrderDetailAdapter.getGroupCount());
	}
	
	private void scrollOrderLv(final int position){
		mLvOrderDetail.post(new Runnable(){

			@Override
			public void run() {
				mLvOrderDetail.setSelection(position);
			}
			
		});
	}
	
	private void expandOrderLv(int position){
		if(position == 0){
			for(int i = 0; i < mOrderDetailAdapter.getGroupCount(); i++){
				if(!mLvOrderDetail.isGroupExpanded(i))
					mLvOrderDetail.expandGroup(i);
			}
		}else{
			if(!mLvOrderDetail.isGroupExpanded(position))
				mLvOrderDetail.expandGroup(position);
		}
	}

	private void openTransaction(){
		openSession();	
		mTransactionId = mTrans.getCurrentTransactionId(mSessionId);
		if(mTransactionId == 0){
			mTransactionId = mTrans.openTransaction(mSession.getLastSessionDate(), 
					mShopId, mComputerId, mSessionId, mStaffId, mShop.getCompanyVatRate());
		}
	}

	private void openSession(){
		mSessionId = mSession.getCurrentSessionId(); 
		if(mSessionId == 0){
			mSessionId = mSession.openSession(mShopId, mComputerId, mStaffId, 0);
			
			ManageCashAmountFragment mf = ManageCashAmountFragment
					.newInstance(getString(R.string.open_shift), 0,
							ManageCashAmountFragment.OPEN_SHIFT_MODE);
			mf.show(getSupportFragmentManager(), "ManageCashAmount");
			WintecCashDrawer dsp = new WintecCashDrawer(MainActivity.this);
			dsp.openCashDrawer();
			dsp.close();
		}
	}

	private void init(){
		openTransaction();
		// update when changed user
		mTrans.updateTransaction(mTransactionId, mStaffId);
		countHoldOrder();
		countSaleDataNotSend();
		loadOrder();
		
		// init second display
		if(Utils.isEnableSecondDisplay(this)){
			initSecondDisplay();
		}
	}
	
	private void showHoldBill() {
		final OrderTransaction holdTrans = new OrderTransaction();
		LayoutInflater inflater = getLayoutInflater();
		View holdBillView = inflater.inflate(R.layout.hold_bill_layout, null, false);
		ListView lvHoldBill = (ListView) holdBillView.findViewById(R.id.listView1);
		List<OrderTransaction> billLst = mTrans.listHoldOrder(mSession.getLastSessionDate());
		HoldBillAdapter billAdapter = new HoldBillAdapter(billLst);
		lvHoldBill.setAdapter(billAdapter);
		lvHoldBill.setOnItemClickListener(new OnItemClickListener(){
	
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				OrderTransaction trans = (OrderTransaction) parent.getItemAtPosition(position);
				holdTrans.setTransactionId(trans.getTransactionId());
			}
			
		});
		
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle(R.string.hold_bill);
		builder.setView(holdBillView);
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.setPositiveButton(android.R.string.ok, null);
		
		final AlertDialog dialog = builder.create();
		dialog.show();
		dialog.getWindow().setLayout(690, 
				WindowManager.LayoutParams.WRAP_CONTENT);
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener(){
	
			@Override
			public void onClick(View v) {
				if(mOrderDetailLst.size() > 0){
					new AlertDialog.Builder(MainActivity.this)
					.setTitle(R.string.hold)
					.setMessage(R.string.hold_current_order)
					.setNeutralButton(R.string.close,
							new DialogInterface.OnClickListener() {
	
								@Override
								public void onClick(
										DialogInterface dialog,
										int which) {
								}
	
							}).show();
				}else{
					if(holdTrans.getTransactionId() != 0){
						mTrans.prepareTransaction(holdTrans.getTransactionId());
						// Delete current transaction because not have any orders.
						mTrans.deleteTransaction(mTransactionId);
						init();
						dialog.dismiss();
					}else{
						new AlertDialog.Builder(MainActivity.this)
						.setTitle(R.string.hold_bill)
						.setMessage(R.string.select_item)
						.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						}).show();
					}
				}
			}
		});
	}

	/**
	 * cancel order transaction
	 */
	private void clearTransaction(){
		mTrans.cancelTransaction(mTransactionId);
		init();
	}

	/**
	 * void bill
	 */
	private void voidBill(){
		StaffsDao st = new StaffsDao(MainActivity.this);
		if(!st.checkVoidPermission(mStaffRoleId)){
			UserVerifyDialogFragment uvf = UserVerifyDialogFragment.newInstance(StaffsDao.VOID_PERMISSION);
			uvf.show(getFragmentManager(), "StaffPermissionDialog");
		}else{
			goToVoidActivity(mStaffId);
		}
	}

	/**
	 * edit cash in drawer
	 */
	private void editCash(){
		ManageCashAmountFragment mf = ManageCashAmountFragment
				.newInstance(getString(R.string.edit_open_shift), mSession.getOpenAmount(mSessionId), 
						ManageCashAmountFragment.EDIT_CASH_MODE);
		mf.show(getSupportFragmentManager(), "ManageCashAmount");
	}
	
	/**
	 * close shift
	 */
	private void closeShift(){
		if(mTrans.countOrderStatusNotSuccess(mSession.getLastSessionDate()) == 0){
			new AlertDialog.Builder(MainActivity.this)
			.setCancelable(false)
			.setTitle(R.string.close_shift)
			.setMessage(R.string.confirm_close_shift)
			.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					ManageCashAmountFragment mf = ManageCashAmountFragment
							.newInstance(getString(R.string.close_shift), 0, 
									ManageCashAmountFragment.CLOSE_SHIFT_MODE);
					mf.show(getSupportFragmentManager(), "ManageCashAmount");
					WintecCashDrawer dsp = new WintecCashDrawer(MainActivity.this);
					dsp.openCashDrawer();
					dsp.close();
				}
			}).show();
		}else{
			new AlertDialog.Builder(MainActivity.this)
			.setTitle(R.string.close_shift)
			.setMessage(R.string.check_not_complete_orders)
			.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.show();
		}
	}

	/**
	 * endday
	 */
	private void endday(){
		if(mTrans.countOrderStatusNotSuccess(mSession.getLastSessionDate()) == 0){
			new AlertDialog.Builder(MainActivity.this)
			.setCancelable(false)
			.setTitle(R.string.endday)
			.setMessage(R.string.confirm_endday)
			.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					ManageCashAmountFragment mf = ManageCashAmountFragment
							.newInstance(getString(R.string.close_shift), 0, 
									ManageCashAmountFragment.END_DAY_MODE);
					mf.show(getSupportFragmentManager(), "ManageCashAmount");
					WintecCashDrawer dsp = new WintecCashDrawer(MainActivity.this);
					dsp.openCashDrawer();
					dsp.close();
				}
			}).show();
		}else{
			new AlertDialog.Builder(MainActivity.this)
			.setTitle(R.string.endday)
			.setMessage(R.string.check_not_complete_orders)
			.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.show();
		}
	}

	/**
	 * clear selected order
	 */
	private void clearSelectedOrder(){
		final List<OrderDetail> selectedOrderLst = listSelectedOrder();
		if(selectedOrderLst.size() > 0){
			for(OrderDetail order : selectedOrderLst){
				if(order.isChecked())
					order.setChecked(false);
			}
			mOrderDetailAdapter.notifyDataSetChanged();
		}
	}

	private void showBillDetail(){
		if(mOrderDetailLst.size() > 0){
			BillViewerFragment bf = BillViewerFragment.newInstance(mTransactionId, 
					BillViewerFragment.CHECK_VIEW, BillViewerFragment.RECEIPT, false);
			bf.show(getFragmentManager(), BillViewerFragment.TAG);
		}
	}
	
	private void setFreeProductPrice(){
		final List<OrderDetail> selectedOrderLst = listSelectedOrder();
		int size = selectedOrderLst.size();
		if(size > 0){
			for(OrderDetail detail : selectedOrderLst){
				mTrans.updateOrderDetailFreePrice(detail.getTransactionId(), detail.getOrderDetailId());
			}
			int idx = mOrderDetailLst.indexOf(selectedOrderLst.get(size - 1));
			loadOrder();
			scrollOrderLv(idx);
		}
	}
	
	/**
	 * delete multiple selected order
	 */
	private void deleteSelectedOrder(){
		final List<OrderDetail> selectedOrderLst = listSelectedOrder();
		if (selectedOrderLst.size() > 0) {
			new AlertDialog.Builder(MainActivity.this)
					.setTitle(R.string.delete)
					.setMessage(
							this.getString(R.string.confirm_delete) + " ("
									+ selectedOrderLst.size() + ")")
					.setNegativeButton(R.string.no,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							})
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									for (OrderDetail order : selectedOrderLst) {
										deleteOrder(order.getOrderDetailId());
									}
									loadOrder();
								}
							}).show();
		}
	}

	/**
	 * get selected order
	 * @return
	 */
	private List<OrderDetail> listSelectedOrder(){
		List<OrderDetail> orderSelectedLst = new ArrayList<OrderDetail>();
		for(OrderDetail order : mOrderDetailLst){
			if(order.isChecked())
				orderSelectedLst.add(order);
		}
		return orderSelectedLst;
	}

	/**
	 * Delete Order
	 * @param orderDetailId
	 */
	private void deleteOrder(int orderDetailId){
		mTrans.deleteOrder(mTransactionId, orderDetailId);
		mOrderDetailAdapter.notifyDataSetChanged();
	}
	
	/**
	 * Update Order
	 * @param orderDetailId
	 * @param qty
	 * @param price
	 * @param vatType
	 * @param vatRate
	 * @param productName
	 * @param productName2
	 */
	private void updateOrder(int orderDetailId, double qty, 
			double price, int vatType, double vatRate, 
			String productName, String productName2){
		mDsp.setItemName(TextUtils.isEmpty(productName2) ? productName : productName2);
		mDsp.setItemQty(mGlobal.qtyFormat(qty));
		mDsp.setItemAmount(mGlobal.currencyFormat(price));
		mTrans.updateOrderDetail(mTransactionId,
				orderDetailId, vatType, vatRate, qty, price);
	}
	
	/**
	 * Add Order
	 * @param productId
	 * @param productCode
	 * @param productName
	 * @param productTypeId
	 * @param vatType
	 * @param vatRate
	 * @param qty
	 * @param price
	 */
	private void addOrder(final int productId, final String productName, 
			final int productTypeId, final int vatType, final double vatRate, final double qty, double price){
		if(price > -1){
			mDsp.setItemAmount(mGlobal.currencyFormat(price));
			int ordId = mTrans.addOrderDetail(mTransactionId, mComputerId, 
					productId, productTypeId, vatType, vatRate, qty, price);
			updateOrderLst(ordId);
		}else{
			final EditText txtProductPrice = new EditText(this);
			txtProductPrice.setInputType(InputType.TYPE_CLASS_NUMBER);
			txtProductPrice.setTextSize(getResources().getInteger(R.integer.large_text_size));
			txtProductPrice.setOnEditorActionListener(new OnEditorActionListener(){
		
				@Override
				public boolean onEditorAction(TextView v, int actionId,
						KeyEvent event) {
					if(actionId == EditorInfo.IME_ACTION_DONE){
						return true;
					}
					return false;
				}
				
			});
			new AlertDialog.Builder(this)
			.setTitle(R.string.enter_price)
			.setCancelable(false)
			.setView(txtProductPrice)
			.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener(){
		
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
				
			})
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					double openPrice = 0.0f;
					try {
						openPrice = Utils.stringToDouble(txtProductPrice.getText().toString());
						mDsp.setItemAmount(mGlobal.currencyFormat(openPrice));
						int ordId = mTrans.addOrderDetail(mTransactionId, mComputerId, 
								productId, productTypeId, vatType, vatRate, qty, openPrice);
						updateOrderLst(ordId);
					} catch (ParseException e) {
						new AlertDialog.Builder(MainActivity.this)
						.setTitle(R.string.enter_price)
						.setMessage(R.string.enter_valid_numeric)
						.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {	
							}
						})
						.show();
						e.printStackTrace();
					}
				}
			})
			.show();
		}
	}

	/**
	 * create product size dialog
	 * @param proId
	 */
	private void productSizeDialog(int proId, String productName){
		List<Product> pSizeLst = mProducts.listProductSize(proId);
		LayoutInflater inflater = getLayoutInflater();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View sizeView = inflater.inflate(R.layout.product_size, null, false);
		GridView gvMenuSize = (GridView) sizeView.findViewById(R.id.gvMenuSize);
		builder.setView(sizeView);
		builder.setTitle(productName);
		final AlertDialog dialog = builder.create();
		gvMenuSize.setAdapter(new ProductSizeAdapter(pSizeLst));
		gvMenuSize.setOnItemClickListener(new OnItemClickListener(){
	
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long arg3) {
				Product p = (Product) parent.getItemAtPosition(position);
				addOrder(p.getProductId(), p.getProductName(), 
						p.getProductTypeId(), p.getVatType(), p.getVatRate(), 
						getOrderingQty(), p.getProductPrice());
				dialog.dismiss();
			}
			
		});
		dialog.show();
	}

	private void updateDisplayColumnMenu(){
		SharedPreferences settings = getSharedPreferences(PREF_NUM_MENU_COLUMNS, 0);
		int numCols = settings.getInt(NUM_MENU_COLUMNS, 4);
		if(mMenu != null){
			MenuItem itemMenuCols = mMenu.findItem(R.id.itemMenuCols);
			itemMenuCols.setTitle(getString(R.string.num_menu_columns) + "(" + numCols + ")");
		}	
	}
	
	/**
	 * count transaction that not send to server
	 */
	private void countSaleDataNotSend(){
		if(mMenu != null){
			MenuItem itemSendSale = mMenu.findItem(R.id.itemSendSale);
			MenuItem itemSendData = mMenu.findItem(R.id.itemSendData);
			MenuItem itemSendEndday = mMenu.findItem(R.id.itemSendEndday);
			int totalTrans = mTrans.countTransUnSend(mSession.getLastSessionDate());
			int totalSess = mSession.countSessionEnddayNotSend();
			int totalData = totalTrans + totalSess;
			if(totalData > 0){
				itemSendData.setTitle(getString(R.string.send_sale_data) + "(" + totalData + ")");
			}else{
				itemSendData.setTitle(getString(R.string.send_sale_data));
			}
			if(totalTrans > 0){
				itemSendSale.setTitle(getString(R.string.send_sale_data) + "(" + totalTrans + ")");
			}else{
				itemSendSale.setTitle(getString(R.string.send_sale_data));
			}
			if(totalSess > 0){
				itemSendEndday.setTitle(getString(R.string.send_endday_data) + "(" + totalSess + ")");
			}else{
				itemSendEndday.setTitle(getString(R.string.send_endday_data));
			}
		}
	}

	/**
	 * count order that hold
	 */
	private void countHoldOrder(){
		if(mMenu != null){
			MenuItem itemHoldBill = mMenu.findItem(R.id.itemHoldBill);
			int totalHold = mTrans.countHoldOrder(mSession.getLastSessionDate());
			if(totalHold > 0){
				itemHoldBill.setTitle(getString(R.string.hold_bill) + "(" + totalHold + ")");
			}else{
				itemHoldBill.setTitle(getString(R.string.hold_bill));
			}
		}
	}
	
	/**
	 * after MenuCommentFragment dismiss
	 * updateOrderDetailLst
	 */
	@Override
	public void onDismiss(int position, int orderDetailId) {
		updateOrderLst(position, orderDetailId);
	}
	
	private void secondDisplayChangePayment(String grandTotal, String totalPay, String change){
		final String paymentJson = SecondDisplayJSON.genChangePayment(grandTotal, totalPay, change);
		Logger.appendLog(this, MPOSApplication.LOG_PATH, MPOSApplication.LOG_FILE_NAME, paymentJson);
		new Thread(new Runnable(){

			@Override
			public void run() {
				try {
					InetAddress iNetAddr = InetAddress.getByName(Utils.getSecondDisplayIp(MainActivity.this));
					Socket socket = new Socket(iNetAddr, Utils.getSecondDisplayPort(MainActivity.this));
					PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
					BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//					while(reader.readLine() != null){
//					}
					writer.println(paymentJson);
					writer.flush();
				} catch (UnknownHostException e) {
					Log.d(TAG, e.getMessage());
				} catch (IOException e) {
					Log.d(TAG, e.getMessage());
				}
			}
			
		}).start();
	}
	
	private void secondDisplayItem(List<clsSecDisplay_TransSummary> transSummLst, String grandTotal){
		final String itemJson = SecondDisplayJSON.genDisplayItem(mGlobal, mOrderDetailLst, 
				transSummLst, grandTotal);
		Logger.appendLog(this, MPOSApplication.LOG_PATH, MPOSApplication.LOG_FILE_NAME, itemJson);
		new Thread(new Runnable(){

			@Override
			public void run() {
				try {
					InetAddress iNetAddr = InetAddress.getByName(Utils.getSecondDisplayIp(MainActivity.this));
					Socket socket = new Socket(iNetAddr, Utils.getSecondDisplayPort(MainActivity.this));
					PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
					BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					writer.println(itemJson);
					writer.flush();
//					while(reader.readLine() != null){
//					}
				} catch (UnknownHostException e) {
					Log.d(TAG, e.getMessage());
				} catch (IOException e) {
					Log.d(TAG, e.getMessage());
				}
			}
			
		}).start();
	}
	
	private void initSecondDisplay(){
		StaffsDao s = new StaffsDao(this);
		final String initJson = SecondDisplayJSON.genInitDisplay(mShop.getShopName(), 
				s.getStaff(mStaffId).getStaffName());
		new Thread(new Runnable(){

			@Override
			public void run() {
				try {
					InetAddress iNetAddr = InetAddress.getByName(Utils.getSecondDisplayIp(MainActivity.this));
					Socket socket = new Socket(iNetAddr, Utils.getSecondDisplayPort(MainActivity.this));
					PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
					BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//					while(reader.readLine() != null){
//					}
					writer.println(initJson);
					writer.flush();
				} catch (UnknownHostException e) {
					Log.d(TAG, e.getMessage());
				} catch (IOException e) {
					Log.d(TAG, e.getMessage());
				}
			}
			
		}).start();
	}
	
	private void clearSecondDisplay(){
		new Thread(new Runnable(){

			@Override
			public void run() {
				try {
					InetAddress iNetAddr = InetAddress.getByName(Utils.getSecondDisplayIp(MainActivity.this));
					Socket socket = new Socket(iNetAddr, Utils.getSecondDisplayPort(MainActivity.this));
					PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
					BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//					while(reader.readLine() != null){
//					}
					writer.println(SecondDisplayJSON.genClearDisplay());
					writer.flush();
				} catch (UnknownHostException e) {
					Log.d(TAG, e.getMessage());
				} catch (IOException e) {
					Log.d(TAG, e.getMessage());
				}
			}
			
		}).start();
	}

	@Override
	public void onOpenShift(double cashAmount) {
		mSession.updateOpenAmount(mSessionId, cashAmount);
	}

	@Override
	public void onCloseShift(double cashAmount) {
		mSession.closeSession(mSessionId, mStaffId, cashAmount, false);
		mTrans.cancelTransaction(mTransactionId);

		// print close shift
		new PrintReport(MainActivity.this, 
			PrintReport.WhatPrint.SUMMARY_SALE, mSessionId, mStaffId, null).execute();

		Intent intent = new Intent(MainActivity.this, SaleSenderService.class);
		intent.putExtra(SaleSenderService.WHAT_TO_DO_PARAM, SaleSenderService.SEND_LAST_SALE_TRANS);
		intent.putExtra(SaleSenderService.SESSION_DATE_PARAM, mSession.getLastSessionDate());
		intent.putExtra(SaleSenderService.SHOP_ID_PARAM, mShopId);
		intent.putExtra(SaleSenderService.COMPUTER_ID_PARAM, mComputerId);
		intent.putExtra(SaleSenderService.STAFF_ID_PARAM, mStaffId);
		intent.putExtra(SaleSenderService.RECEIVER_NAME, new SendSaleResultReceiver(new Handler()));
		startService(intent);
		startActivity(new Intent(MainActivity.this, LoginActivity.class));
		finish();
	}

	@Override
	public void onEditCashAmount(double cashAmount) {
		mSession.updateOpenAmount(mSessionId, cashAmount);
	}

	@Override
	public void onEndday(double cashAmount) {
		String lastSessDate = mSession.getLastSessionDate();
		boolean isEndday = false;
		try {
			mSession.addSessionEnddayDetail(lastSessDate,
					mTrans.getTotalReceipt(0, lastSessDate),
					mTrans.getTotalReceiptAmount(lastSessDate));
			mSession.closeSession(mSessionId, mStaffId, cashAmount, true);
			isEndday = true;
		} catch (SQLException e) {
			e.printStackTrace();
			isEndday = false;
		}
		if(isEndday){
			mTrans.cancelTransaction(mTransactionId);
			int totalSess = mSession.countSession(mSession.getLastSessionDate());
			if(totalSess > 1){
				new PrintReport(MainActivity.this, 
					PrintReport.WhatPrint.SUMMARY_SALE, mSessionId, mStaffId, null).execute();
			}
			// if parse sessionId = 0 will be print all summary in day
			new PrintReport(MainActivity.this, 
				PrintReport.WhatPrint.SUMMARY_SALE, 0, mStaffId, null).execute();

			// backup the database
			if(Utils.isEnableBackupDatabase(MainActivity.this)){
				Utils.backupDatabase(this);
			}
			sendEnddayData();
		}
	}

	/**
	 * on allow permission
	 */
	@Override
	public void onAllow(int staffId, int permissionId) {
		switch(permissionId){
		case StaffsDao.VOID_PERMISSION:
			goToVoidActivity(staffId);
			break;
		case StaffsDao.OTHER_DISCOUNT_PERMISSION:
			goToOtherDiscountActivity();
			break;
		}
	}
	
	private void goToVoidActivity(int staffId){
		Intent intent = new Intent(MainActivity.this, VoidBillActivity.class);
		intent.putExtra("staffId", staffId);
		intent.putExtra("shopId", mShopId);
		startActivity(intent);
	}
	
	private void goToOtherDiscountActivity(){
		Intent intent = new Intent(MainActivity.this, DiscountActivity.class);
		intent.putExtra("transactionId", mTransactionId);
		startActivity(intent);
	}
	
	/**
	 * @author j1tth4
	 * The receiver for send enddday
	 */
	private class EnddayReceiver extends ResultReceiver{
		
		private ProgressDialog progress;
		
		public EnddayReceiver(Handler handler) {
			super(handler);
			progress = new ProgressDialog(MainActivity.this);
			progress.setTitle(getString(R.string.endday_success));
			progress.setCancelable(false);
			progress.setMessage(getString(R.string.send_endday_data_progress));
			progress.show();
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			super.onReceiveResult(resultCode, resultData);
			switch(resultCode){
			case SaleSenderService.RESULT_SUCCESS:
				if (progress.isShowing())
					progress.dismiss();
				new AlertDialog.Builder(MainActivity.this)
				.setTitle(R.string.endday)
				.setMessage(R.string.send_endday_data_success)
				.setCancelable(false)
				.setNeutralButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}
						}).show();
				break;
			case SaleSenderService.RESULT_ERROR:
				new AlertDialog.Builder(MainActivity.this)
				.setTitle(R.string.endday)
				.setMessage(R.string.cannot_send_endday_data_on_this_time)
				.setCancelable(false)
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								finish();
							}
						})
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								sendEnddayData();
							}
						}).show();
				break;
			}
		}
		
	}
	
	private void sendEnddayData(){
		Intent intent = new Intent(this, EnddaySenderService.class);
		intent.putExtra(EnddaySenderService.WHAT_TO_DO_PARAM, EnddaySenderService.SEND_CURRENT);
		intent.putExtra(EnddaySenderService.SESSION_DATE_PARAM, mSession.getLastSessionDate());
		intent.putExtra(EnddaySenderService.SHOP_ID_PARAM, mShopId);
		intent.putExtra(EnddaySenderService.COMPUTER_ID_PARAM, mComputerId);
		intent.putExtra(EnddaySenderService.STAFF_ID_PARAM, mStaffId);
		intent.putExtra(EnddaySenderService.RECEIVER_NAME, new EnddayReceiver(new Handler()));
		startService(intent);
	}
	
	@Override
	public void onChangeLanguage() {
		refreshSelf();
	}
	
	private void refreshSelf(){
		startActivity(getIntent());
		finish();
	}
}
