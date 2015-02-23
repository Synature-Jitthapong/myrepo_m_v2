package com.synature.mpos;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.synature.mpos.datasource.ComputerDataSource;
import com.synature.mpos.datasource.PaymentDetailDataSource;
import com.synature.mpos.datasource.ProductsDataSource;
import com.synature.mpos.datasource.SessionDataSource;
import com.synature.mpos.datasource.ShopDataSource;
import com.synature.mpos.datasource.TransactionDataSource;
import com.synature.mpos.datasource.model.OrderDetail;
import com.synature.mpos.datasource.model.Product;
import com.synature.mpos.datasource.table.ProductTable;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class PerformTest extends DialogFragment{

	public static final int TRANS_LOOP = 1000;
	
	private SessionDataSource mSession;
	private TransactionDataSource mTrans;
	private PaymentDetailDataSource mPayment;
	
	public static PerformTest newInstance(){
		PerformTest f = new PerformTest();
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSession = new SessionDataSource(getActivity());
		mTrans = new TransactionDataSource(getActivity());
		mPayment = new PaymentDetailDataSource(getActivity());
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Perform Test");
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.setPositiveButton("Begin Test", null);
		

		final ProgressDialog progress = new ProgressDialog(getActivity());
		progress.setCancelable(false);
		
		final AlertDialog d = builder.create();
		d.show();
		d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				progress.setMessage("Testing...");
				progress.show();
				
				new Thread(new Runnable(){

					@Override
					public void run() {
						final String startTime = DateFormat.getInstance().format(Calendar.getInstance().getTime());
						Log.i("Begin", startTime);
						ShopDataSource shop = new ShopDataSource(getActivity());
						ComputerDataSource comp = new ComputerDataSource(getActivity());
						
						mSession.openSession(shop.getShopId(), comp.getComputerId(), 1, 100);
						for(int i = 0; i < TRANS_LOOP; i++){
							int transId = mTrans.openTransaction(mSession.getLastSessionDate(), 
									shop.getShopId(), comp.getComputerId(), 
									mSession.getCurrentSessionId(), 1, 7);

							Log.i("Loop ", String.valueOf(i));
							Log.i("Open transaction ", String.valueOf(transId));
							
							for(Product p : listAllProduct()){
								mTrans.addOrderDetail(transId, comp.getComputerId(), p.getProductId(), 
										p.getProductTypeId(), p.getVatType(), 
										p.getVatRate(), 1, p.getProductPrice());
							}
							OrderDetail sum = mTrans.getSummaryOrder(transId, true);
							
							mPayment.addPaymentDetail(transId, comp.getComputerId(), 
									1, sum.getTotalRetailPrice(), sum.getTotalRetailPrice(), 
									null, 0, 0, 0, 0, "Add from test perfomace");
							mTrans.closeTransaction(transId, 1, sum.getTotalRetailPrice());

							Log.i("Close transaction", String.valueOf(transId));
						}
						getActivity().runOnUiThread(new Runnable(){

							@Override
							public void run() {
								progress.dismiss();
								new AlertDialog.Builder(getActivity())
								.setTitle("Finish")
								.setMessage("Start time: " + startTime
										+ " Finish time: " + DateFormat.getInstance().format(Calendar.getInstance().getTime()))
								.setNeutralButton("Close", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										d.dismiss();
									}
								})
								.show();
							}
							
						});
					}
					
				}).start();
			}
			
		});
		return d;
	}

	private List<Product> listAllProduct(){
		List<Product> proLst = null;
		ProductsDataSource db = new ProductsDataSource(getActivity());
		Cursor cursor = db.getReadableDatabase().rawQuery(
				"SELECT * FROM " + ProductTable.TABLE_PRODUCT, null);
		if(cursor.moveToFirst()){
			proLst = new ArrayList<Product>();
			do{
				Product p = db.toProduct(cursor);
				proLst.add(p);
			}while(cursor.moveToNext());
		}
		cursor.close();
		return proLst;
	}
}
