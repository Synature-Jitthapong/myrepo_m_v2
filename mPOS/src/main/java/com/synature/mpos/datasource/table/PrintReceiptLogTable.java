package com.synature.mpos.datasource.table;

import android.database.sqlite.SQLiteDatabase;

public class PrintReceiptLogTable{
	
	public static final String TABLE_PRINT_LOG = "PrintReceiptLog";
	public static final String COLUMN_PRINT_RECEIPT_LOG_ID = "print_receipt_log_id";
	public static final String COLUMN_PRINT_RECEIPT_LOG_TIME = "print_receipt_log_time";
	public static final String COLUMN_PRINT_RECEIPT_LOG_STATUS = "print_receipt_log_status";
	public static final String COLUMN_IS_COPY = "is_copy";
	
	private static final String SQL_CREATE = 
			" create table " + TABLE_PRINT_LOG + "( " 
			+ COLUMN_PRINT_RECEIPT_LOG_ID + " integer primary key autoincrement, "
			+ OrderTransTable.COLUMN_TRANS_ID + " integer not null, "
			+ StaffTable.COLUMN_STAFF_ID + " integer not null, "
			+ COLUMN_PRINT_RECEIPT_LOG_TIME + " text, "
			+ COLUMN_PRINT_RECEIPT_LOG_STATUS + " integer default 0, "
			+ COLUMN_IS_COPY + " integer default 0);";
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE_PRINT_LOG);
		onCreate(db);
	}
}