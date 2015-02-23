package com.synature.mpos.database.table;

import android.database.sqlite.SQLiteDatabase;

public class PaymentDetailTable{
	
	public static final String TABLE_PAYMENT_DETAIL = "PaymentDetail";
	public static final String TEMP_PAYMENT_DETAIL = "PaymentDetailTemp";
	public static final String COLUMN_PAY_ID = "pay_detail_id";
	public static final String COLUMN_PAY_AMOUNT = "pay_amount";
	public static final String COLUMN_TOTAL_PAY_AMOUNT = "total_pay_amount";
	public static final String COLUMN_REMARK = "remark";
	
	private static final String SQL_CREATE =
			" create table " + TABLE_PAYMENT_DETAIL + " ( " 
			+ COLUMN_PAY_ID + " integer not null, "
			+ OrderTransTable.COLUMN_TRANS_ID + " integer not null, "
			+ ComputerTable.COLUMN_COMPUTER_ID + " integer not null, "
			+ PayTypeTable.COLUMN_PAY_TYPE_ID + " integer not null, "
			+ COLUMN_PAY_AMOUNT + " real not null, " 
			+ COLUMN_TOTAL_PAY_AMOUNT + " real not null, " 
			+ CreditCardTable.COLUMN_CREDITCARD_NO + " text, " 
			+ CreditCardTable.COLUMN_EXP_MONTH + " integer, "
			+ CreditCardTable.COLUMN_EXP_YEAR + " integer, "
			+ BankTable.COLUMN_BANK_ID + " integer, "
			+ CreditCardTable.COLUMN_CREDITCARD_TYPE_ID + " integer, "
			+ COLUMN_REMARK + " text, " 
			+ " primary key  (" + COLUMN_PAY_ID + "));";
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);
		db.execSQL("create table " + TEMP_PAYMENT_DETAIL + " as select * from " + TABLE_PAYMENT_DETAIL + " where 0;");
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(oldVersion < 8){
			db.execSQL("create table " + TEMP_PAYMENT_DETAIL + " as select * from " + TABLE_PAYMENT_DETAIL + " where 0;");
			db.execSQL("create index pay_idx on " + TABLE_PAYMENT_DETAIL + "(" + COLUMN_PAY_ID + ");");
			db.execSQL("reindex pay_idx;");
		}
	}
}
