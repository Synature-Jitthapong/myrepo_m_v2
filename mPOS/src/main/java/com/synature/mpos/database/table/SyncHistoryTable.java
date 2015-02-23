package com.synature.mpos.database.table;

import android.database.sqlite.SQLiteDatabase;

public class SyncHistoryTable {

	public static final String TABLE_SYNC_HISTORY = "SyncHistory";
	public static final String COLUMN_SYNC_ID = "id";
	public static final String COLUMN_SYNC_STATUS = "status";
	public static final String COLUMN_SYNC_DATE = "date";
	public static final String COLUMN_SYNC_TIME = "time";
	
	private static final String SQL_CREATE =
			" create table " + TABLE_SYNC_HISTORY + " ( " 
			+ COLUMN_SYNC_ID + " integer primary key autoincrement, " 
			+ COLUMN_SYNC_STATUS + " integer default 0, "
			+ COLUMN_SYNC_DATE + " text, "
			+ COLUMN_SYNC_TIME + " text);";
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE_SYNC_HISTORY);
		onCreate(db);
	}
	
}