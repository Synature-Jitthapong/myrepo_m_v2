package com.synature.mpos.database.table;

import android.database.sqlite.SQLiteDatabase;

/**
 * @author j1tth4
 * MenuComment Table
 */
public class MenuFixCommentTable{
	
	public static final String TABLE_MENU_FIX_COMMENT = "MenuFixComment";
	public static final String COLUMN_COMMENT_ID = "menu_comment_id";
	
	private static final String SQL_CREATE = 
			" create table " + TABLE_MENU_FIX_COMMENT + " ( "
			+ ProductTable.COLUMN_PRODUCT_ID + " integer not null, "
			+ COLUMN_COMMENT_ID + " integer not null);";
	
	public static void onCreate(SQLiteDatabase db){
		db.execSQL(SQL_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENU_FIX_COMMENT);
		onCreate(db);
	}
}