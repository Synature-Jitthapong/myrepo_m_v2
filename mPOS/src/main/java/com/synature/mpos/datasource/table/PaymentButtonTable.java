package com.synature.mpos.datasource.table;

import android.database.sqlite.SQLiteDatabase;

public class PaymentButtonTable{

	public static final String TABLE_PAYMENT_BUTTON = "PaymentAmountButton";
	public static final String COLUMN_PAYMENT_AMOUNT_ID = "payment_amount_id";
	public static final String COLUMN_PAYMENT_AMOUNT = "payment_amount";
	public static final String COLUMN_ORDERING = "ordering";

	private static final String SQL_CREATE = 
			" create table " + TABLE_PAYMENT_BUTTON + "( " 
			+ COLUMN_PAYMENT_AMOUNT_ID + " integer not null, " 
			+ COLUMN_PAYMENT_AMOUNT + " real not null, "
			+ COLUMN_ORDERING + " integer default 0 );";
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE_PAYMENT_BUTTON);
		onCreate(db);
	}
}