package com.synature.mpos.database.table;

import android.database.sqlite.SQLiteDatabase;

public class ProgramFeatureTable {
	
	public static final String PROGRAM_FEATURE_TABLE = "ProgramFeature";
	public static final String COLUMN_FEATURE_ID = "feature_id";
	public static final String COLUMN_FEATURE_NAME = "feature_name";
	public static final String COLUMN_FEATURE_VALUE = "feature_value";
	public static final String COLUMN_FEATURE_TEXT = "feature_text";
	public static final String COLUMN_FEATURE_DESC = "feature_desc";	

	private static final String SQL_CREATE = 
			"create table " + PROGRAM_FEATURE_TABLE + " ("
			+ COLUMN_FEATURE_ID + " integer, "
			+ COLUMN_FEATURE_NAME + " text, "
			+ COLUMN_FEATURE_VALUE + " integer default 0, "
			+ COLUMN_FEATURE_TEXT + " text, "
			+ COLUMN_FEATURE_DESC + " text);";
			
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + PROGRAM_FEATURE_TABLE);
		onCreate(db);
	}
}
