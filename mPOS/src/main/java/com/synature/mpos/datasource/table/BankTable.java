package com.synature.mpos.datasource.table;

import android.database.sqlite.SQLiteDatabase;

public class BankTable {

	public static final String TABLE_BANK = "BankName";
	public static final String COLUMN_BANK_ID = "bank_id";
	public static final String COLUMN_BANK_NAME = "bank_name";
	
	private static final String SQL_CREATE =
			" create table " + TABLE_BANK + " ( " 
			+ COLUMN_BANK_ID + " integer not null, " 
			+ COLUMN_BANK_NAME + " text not null, " 
			+ " primary key (" + COLUMN_BANK_ID + ") );";
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE_BANK);
		onCreate(db);
	}
	
}