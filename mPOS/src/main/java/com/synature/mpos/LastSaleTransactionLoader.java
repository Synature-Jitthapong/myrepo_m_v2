package com.synature.mpos;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.synature.mpos.database.MPOSDatabase;
import com.synature.mpos.database.SaleTransaction;
import com.synature.mpos.database.TransactionDao;
import com.synature.mpos.database.table.ComputerTable;
import com.synature.mpos.database.table.OrderDetailTable;
import com.synature.mpos.database.table.OrderTransTable;
import com.synature.mpos.database.table.PaymentDetailTable;
import com.synature.mpos.database.table.ProductComponentGroupTable;
import com.synature.mpos.database.table.ProductComponentTable;
import com.synature.mpos.database.table.ProductGroupTable;
import com.synature.mpos.database.table.ProductTable;
import com.synature.pos.PComponentGroup;
import com.synature.pos.WebServiceResult;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.ResultReceiver;

public class LastSaleTransactionLoader extends MPOSServiceBase{

	public static final String METHOD = "WSmPOS_GenerateAllSaleTransBackToMPos";
	
	public LastSaleTransactionLoader(Context context, ResultReceiver receiver) {
		super(context, METHOD, receiver);
	}

	@Override
	protected void onPostExecute(String result) {
		WebServiceResult ws;
		try {
			ws = toServiceObject(result);
			if(ws.getiResultID() == WebServiceResult.SUCCESS_STATUS){
				Gson gson = new Gson();
				BackSaleTransaction saleTrans = 
						gson.fromJson(ws.getSzResultData(), BackSaleTransaction.class);
				
			}
		} catch (JsonSyntaxException e) {
			
		}
	}
	
	private void rollbackSale(BackSaleTransaction backSaleTrans){
		MPOSDatabase helper = new MPOSDatabase(mContext);
		SQLiteDatabase db = helper.getWritableDatabase();
		db.beginTransaction();
		try {
			createTempTable(db);
			for(SaleTransaction.SaleData_SaleTransaction saleTrans : backSaleTrans.getxArySaleTransaction()){
				for(SaleTransaction.SaleTable_OrderDetail orderDetail : saleTrans.getxAryOrderDetail()){
					ContentValues cv = createOrderDetailContentValues(orderDetail);
					
					
					for(SaleTransaction.SaleTable_CommentInfo comment : orderDetail.getxListCommentInfo()){
						
					}
					for(SaleTransaction.SaleTable_ChildOrderType7 orderSet : orderDetail.getxListChildOrderSetLinkType7()){
						
					}
				}
				for(SaleTransaction.SaleTable_OrderPromotion promotion : saleTrans.getxAryOrderPromotion()){
					
				}
			}
			db.setTransactionSuccessful();
		} finally{
			db.endTransaction();
		}
	}
	
	private ContentValues createOrderDetailContentValues(Object obj){
		SaleTransaction.SaleTable_OrderDetail orderDetail = 
				(SaleTransaction.SaleTable_OrderDetail) obj;
		ContentValues cv = new ContentValues();
		cv.put(OrderDetailTable.COLUMN_ORDER_ID, orderDetail.getiOrderDetailID());
		cv.put(OrderTransTable.COLUMN_TRANS_ID, orderDetail.getiTransactionID());
		cv.put(ComputerTable.COLUMN_COMPUTER_ID, orderDetail.getiComputerID());
		cv.put(ProductTable.COLUMN_PRODUCT_ID, orderDetail.getiProductID());
		cv.put(ProductTable.COLUMN_PRODUCT_TYPE_ID, orderDetail.getiProductTypeID());
		cv.put(OrderDetailTable.COLUMN_ORDER_QTY, orderDetail.getfQty());
		cv.put(ProductTable.COLUMN_PRODUCT_PRICE, orderDetail.getfPricePerUnit());
		cv.put(OrderDetailTable.COLUMN_PRICE_OR_PERCENT, 2);
		cv.put(ProductTable.COLUMN_VAT_TYPE, orderDetail.getiVatType());
		cv.put(OrderDetailTable.COLUMN_TOTAL_VAT, orderDetail.getfTotalVatAmount());
		cv.put(OrderDetailTable.COLUMN_TOTAL_VAT_EXCLUDE, orderDetail.getfTotalVatAmount());
		cv.put(OrderDetailTable.COLUMN_MEMBER_DISCOUNT, orderDetail.getfMemberDiscountAmount());
		cv.put(OrderDetailTable.COLUMN_PRICE_DISCOUNT, orderDetail.getfPriceDiscountAmount());
		cv.put(OrderDetailTable.COLUMN_TOTAL_RETAIL_PRICE, orderDetail.getfRetailPrice());
		cv.put(OrderDetailTable.COLUMN_TOTAL_SALE_PRICE, orderDetail.getfSalePrice());
		return cv;
	}
	
	private int countTransaction(){
		int total = 0;
		MPOSDatabase helper = new MPOSDatabase(mContext);
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(" + OrderTransTable.COLUMN_TRANS_ID + ")"
				+ " from " + OrderTransTable.TABLE_ORDER_TRANS, null);
		if(cursor.moveToFirst()){
			total = cursor.getInt(0);
		}
		cursor.close();
		return total;
	}
	
	private void createTempTable(SQLiteDatabase db){
		db.execSQL("create table RollbackSaleTransaction as select * from " + OrderTransTable.TABLE_ORDER_TRANS + " where 0;");
		db.execSQL("create table RollbackOrderDetail as select * from " + OrderDetailTable.TABLE_ORDER + " where 0;");
		db.execSQL("create table RollbackPaymentDetail as select * from " + PaymentDetailTable.TABLE_PAYMENT_DETAIL + " where 0;");
	}
	
	public static class BackSaleTransaction extends SaleTransaction.POSData_EndDaySaleTransaction implements Parcelable{

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
		}
		
	}
}
