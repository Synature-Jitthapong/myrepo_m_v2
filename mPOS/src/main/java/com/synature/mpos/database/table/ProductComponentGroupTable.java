package com.synature.mpos.database.table;

import android.database.sqlite.SQLiteDatabase;

public class ProductComponentGroupTable {
	
	public static final String TABLE_PCOMPONENT_GROUP = "PComponentGroup";
	public static final String COLUMN_SET_GROUP_NO = "set_group_no";
	public static final String COLUMN_SET_GROUP_NAME = "set_group_name";
	public static final String COLUMN_REQ_AMOUNT = "req_amount";
	public static final String COLUMN_REQ_MIN_AMOUNT = "req_min_amount";
	
	private static final String SQL_CREATE =
			" create table " + TABLE_PCOMPONENT_GROUP + " ( "
			+ ProductComponentTable.COLUMN_PGROUP_ID + " integer not null, "
			+ ProductTable.COLUMN_PRODUCT_ID + " integer not null, "
			+ ProductTable.COLUMN_SALE_MODE + " integer default 0, "
			+ COLUMN_SET_GROUP_NO + " integer, " 
			+ COLUMN_SET_GROUP_NAME + " text, " 
			+ COLUMN_REQ_AMOUNT + " real default 0,"
			+ COLUMN_REQ_MIN_AMOUNT + " real default 0 );";
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE_PCOMPONENT_GROUP);
		onCreate(db);
	}
}
