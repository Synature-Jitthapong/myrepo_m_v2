package com.synature.mpos.database;

import java.util.ArrayList;
import java.util.List;

import com.synature.mpos.database.table.PaymentButtonTable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class PaymentAmountButtonDao extends MPOSDatabase {
	
	public PaymentAmountButtonDao(Context context) {
		super(context);
	}
	
	/**
	 * @return List<Payment.PaymentAmountButton>
	 */
	public List<com.synature.pos.PaymentAmountButton> listPaymentButton(){
		List<com.synature.pos.PaymentAmountButton> paymentButtonLst = 
				new ArrayList<com.synature.pos.PaymentAmountButton>();
		Cursor cursor = getReadableDatabase().query(PaymentButtonTable.TABLE_PAYMENT_BUTTON, 
				new String[]{
				PaymentButtonTable.COLUMN_PAYMENT_AMOUNT_ID,
				PaymentButtonTable.COLUMN_PAYMENT_AMOUNT
				},
				null, null, null, null, 
				PaymentButtonTable.COLUMN_ORDERING);
		if(cursor.moveToFirst()){
			do{
				com.synature.pos.PaymentAmountButton payButton = 
						new com.synature.pos.PaymentAmountButton();
				payButton.setPaymentAmountID(cursor.getInt(cursor.getColumnIndex(PaymentButtonTable.COLUMN_PAYMENT_AMOUNT_ID)));
				payButton.setPaymentAmount(cursor.getDouble(cursor.getColumnIndex(PaymentButtonTable.COLUMN_PAYMENT_AMOUNT)));
				paymentButtonLst.add(payButton);
			}while(cursor.moveToNext());
		}
		cursor.close();
		return paymentButtonLst;
	}
	
	/**
	 * @param paymentAmountLst
	 */
	public void insertPaymentAmountButton(List<com.synature.pos.PaymentAmountButton> paymentAmountLst){
		getWritableDatabase().beginTransaction();
		try {
			getWritableDatabase().delete(PaymentButtonTable.TABLE_PAYMENT_BUTTON, null, null);
			for(com.synature.pos.PaymentAmountButton payButton : paymentAmountLst){
				ContentValues cv = new ContentValues();
				cv.put(PaymentButtonTable.COLUMN_PAYMENT_AMOUNT_ID, payButton.getPaymentAmountID());
				cv.put(PaymentButtonTable.COLUMN_PAYMENT_AMOUNT, payButton.getPaymentAmount());
				getWritableDatabase().insertOrThrow(PaymentButtonTable.TABLE_PAYMENT_BUTTON, null, cv);
			}
			getWritableDatabase().setTransactionSuccessful();
		} finally {
			getWritableDatabase().endTransaction();
		}
	}
}
