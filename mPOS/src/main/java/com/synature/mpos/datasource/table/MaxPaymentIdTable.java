package com.synature.mpos.datasource.table;

import com.synature.mpos.datasource.MPOSDatabase;

import android.database.sqlite.SQLiteDatabase;

public class MaxPaymentIdTable {
	public static final String TABLE_MAX_PAY_ID = "MaxPayId";
	public static final String COLUMN_MAX_PAY_ID = "max_pay_id";

	private static final String SQL_CREATE = 
			"create table " + TABLE_MAX_PAY_ID + "("
			+ COLUMN_MAX_PAY_ID + " integer default 0);";
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		if(!MPOSDatabase.checkTableExists(db, TABLE_MAX_PAY_ID)){
			onCreate(db);
		}
	}
}
