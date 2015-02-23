package com.synature.mpos.datasource.table;

import android.database.sqlite.SQLiteDatabase;

public class SessionTable extends BaseColumn{
	
	public static final String TABLE_SESSION = "Session";
	public static final String COLUMN_SESS_ID = "session_id";
	public static final String COLUMN_SESS_DATE = "session_date";
	public static final String COLUMN_OPEN_DATE = "open_date_time";
	public static final String COLUMN_CLOSE_DATE = "close_date_time";
	public static final String COLUMN_OPEN_AMOUNT = "open_amount";
	public static final String COLUMN_CLOSE_AMOUNT = "close_amount";
	public static final String COLUMN_IS_ENDDAY = "is_endday";
	
	private static final String SQL_CREATE =
			" create table " + TABLE_SESSION + " ( " 
			+ BaseColumn.COLUMN_UUID + " text, "
			+ COLUMN_SESS_ID + " integer, "
			+ ComputerTable.COLUMN_COMPUTER_ID + " integer, "
			+ ShopTable.COLUMN_SHOP_ID + " integer, "
			+ COLUMN_OPEN_STAFF + " integer default 0, "
			+ COLUMN_CLOSE_STAFF + " integer default 0, "
			+ COLUMN_SESS_DATE + " text, " 
			+ COLUMN_OPEN_DATE + " text, "
			+ COLUMN_CLOSE_DATE + " text, " 
			+ COLUMN_OPEN_AMOUNT + " real default 0, " 
			+ COLUMN_CLOSE_AMOUNT + " real default 0, "
			+ COLUMN_IS_ENDDAY + " integer default 0, " 
			+ " primary key (" + COLUMN_SESS_ID + "));";
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}	
}
