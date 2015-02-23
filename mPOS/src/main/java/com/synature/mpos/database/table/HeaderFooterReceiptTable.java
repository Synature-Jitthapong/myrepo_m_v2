package com.synature.mpos.database.table;

import android.database.sqlite.SQLiteDatabase;

public class HeaderFooterReceiptTable{
	
	public static final String TABLE_HEADER_FOOTER_RECEIPT = "HeaderFooterReceipt";
	public static final String COLUMN_TEXT_IN_LINE = "text_inline";
	public static final String COLUMN_LINE_TYPE = "line_type";
	public static final String COLUMN_LINE_ORDER = "line_order";
	
	private static final String SQL_CREATE =
			" create table " + TABLE_HEADER_FOOTER_RECEIPT + " ( " 
			+ COLUMN_TEXT_IN_LINE + " text not null, " 
			+ COLUMN_LINE_TYPE + " integer not null, " 
			+ COLUMN_LINE_ORDER + " integer default 0 );";
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE_HEADER_FOOTER_RECEIPT);
		onCreate(db);
	}
}