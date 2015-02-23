package com.synature.mpos.database.table;

import android.database.sqlite.SQLiteDatabase;

public class LanguageTable{
	
	public static final String TABLE_LANGUAGE = "Language";
	public static final String COLUMN_LANG_ID = "lang_id";
	public static final String COLUMN_LANG_NAME = "lang_name";
	public static final String COLUMN_LANG_CODE = "lang_code";
	
	private static final String SQL_CREATE =
			" create table " + TABLE_LANGUAGE + " ( " 
			+ COLUMN_LANG_ID + " integer default 1, "
			+ COLUMN_LANG_NAME + " text, " 
			+ COLUMN_LANG_CODE + " text default 'en', " 
			+ " primary key (" + COLUMN_LANG_ID + ") );";
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE_LANGUAGE);
		onCreate(db);
	}
}
