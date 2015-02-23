package com.synature.mpos.database.table;

import android.database.sqlite.SQLiteDatabase;

public class PaymentDetailWasteTable {
	public static final String TABLE_PAYMENT_DETAIL_WASTE = "PaymentDetailWaste";
	public static final String TEMP_PAYMENT_DETAIL_WASTE = "PaymentDetailWasteTemp";
	
	private static final String SQL_CREATE =
			" create table " + TABLE_PAYMENT_DETAIL_WASTE + " ( " 
			+ PaymentDetailTable.COLUMN_PAY_ID + " integer not null, "
			+ OrderTransTable.COLUMN_TRANS_ID + " integer not null, "
			+ ComputerTable.COLUMN_COMPUTER_ID + " integer not null, "
			+ PayTypeTable.COLUMN_PAY_TYPE_ID + " integer not null, "
			+ PaymentDetailTable.COLUMN_PAY_AMOUNT + " real not null, "
			+ CreditCardTable.COLUMN_CREDITCARD_NO + " text, " 
			+ CreditCardTable.COLUMN_EXP_MONTH + " integer, "
			+ CreditCardTable.COLUMN_EXP_YEAR + " integer, "
			+ BankTable.COLUMN_BANK_ID + " integer, "
			+ CreditCardTable.COLUMN_CREDITCARD_TYPE_ID + " integer, "
			+ BaseColumn.COLUMN_REMARK + " text, " 
			+ " primary key  (" + PaymentDetailTable.COLUMN_PAY_ID + "));";
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);
		db.execSQL("create table " + TEMP_PAYMENT_DETAIL_WASTE + " as select * from " + TABLE_PAYMENT_DETAIL_WASTE + " where 0;");
		db.execSQL("create index pay_waste_idx on " + TABLE_PAYMENT_DETAIL_WASTE + "(" + PaymentDetailTable.COLUMN_PAY_ID + ");");
		db.execSQL("reindex pay_waste_idx;");
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(oldVersion < 8){
			onCreate(db);
		}
	}
}
