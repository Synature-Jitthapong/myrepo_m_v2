package com.synature.mpos.database.table;

import android.database.sqlite.SQLiteDatabase;

public class PayTypeTable{

	public static final String TABLE_PAY_TYPE = "PayType";
	public static final String COLUMN_PAY_TYPE_ID = "pay_type_id";
	public static final String COLUMN_PAY_TYPE_CODE = "pay_type_code";
	public static final String COLUMN_PAY_TYPE_NAME = "pay_type_name";
	
	private static final String SQL_CREATE =
			" create table " + TABLE_PAY_TYPE + " ( " 
			+ COLUMN_PAY_TYPE_ID + " integer not null, " 
			+ COLUMN_PAY_TYPE_CODE + " text, " 
			+ COLUMN_PAY_TYPE_NAME + " text, " 
			+ BaseColumn.COLUMN_ORDERING + " integer default 0, " 
			+ " primary key (" + COLUMN_PAY_TYPE_ID + ") );";
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE_PAY_TYPE);
		onCreate(db);
	}
}