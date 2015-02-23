package com.synature.mpos.database.table;

import com.synature.mpos.database.MPOSDatabase;

import android.database.sqlite.SQLiteDatabase;

public class MaxTransIdTable {
	public static final String TABLE_MAX_TRANS_ID = "MaxTransId";
	public static final String COLUMN_MAX_TRANS_ID = "max_trans_id";

	private static final String SQL_CREATE = 
			"create table " + TABLE_MAX_TRANS_ID + "("
			+ COLUMN_MAX_TRANS_ID + " integer default 0);";
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		if(!MPOSDatabase.checkTableExists(db, TABLE_MAX_TRANS_ID)){
			onCreate(db);
		}
	}
}
