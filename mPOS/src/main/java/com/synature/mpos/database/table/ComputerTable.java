package com.synature.mpos.database.table;

import android.database.sqlite.SQLiteDatabase;

public class ComputerTable{
	
	public static final String TABLE_COMPUTER = "Computer";
	public static final String COLUMN_COMPUTER_ID = "computer_id";
	public static final String COLUMN_COMPUTER_NAME = "computer_name";
	public static final String COLUMN_DEVICE_CODE = "device_code";
	public static final String COLUMN_REGISTER_NUMBER = "register_number";
	public static final String COLUMN_IS_MAIN_COMPUTER = "ismain_computer";
	public static final String COLUMN_DOC_TYPE_HEADER = "document_type_header";
	public static final String COLUMN_PRINT_RECEIPT_HAS_COPY = "print_receipt_has_copy";
	
	private static final String SQL_CREATE =
			" create table " + TABLE_COMPUTER + " ( " 
			+ COLUMN_COMPUTER_ID + " integer not null, " 
			+ COLUMN_COMPUTER_NAME + " text, " 
			+ COLUMN_DEVICE_CODE + " text, "
			+ COLUMN_REGISTER_NUMBER + " text, " 
			+ COLUMN_IS_MAIN_COMPUTER + " integer default 0, " 
			+ COLUMN_DOC_TYPE_HEADER + " text not null, "
			+ COLUMN_PRINT_RECEIPT_HAS_COPY + " integer default 1, "
			+ "primary key (" + COLUMN_COMPUTER_ID + ") );";
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE_COMPUTER);
		onCreate(db);
	}
}
