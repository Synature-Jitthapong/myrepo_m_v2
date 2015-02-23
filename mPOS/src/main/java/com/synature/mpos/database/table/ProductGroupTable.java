package com.synature.mpos.database.table;

import android.database.sqlite.SQLiteDatabase;

public class ProductGroupTable extends BaseColumn{
	
	public static final String TABLE_PRODUCT_GROUP = "ProductGroup";
	public static final String COLUMN_PRODUCT_GROUP_ID = "product_group_id";
	public static final String COLUMN_PRODUCT_GROUP_CODE = "product_group_code";
	public static final String COLUMN_PRODUCT_GROUP_NAME = "product_group_name";
	public static final String COLUMN_PRODUCT_GROUP_NAME1 = "product_group_name_1";
	public static final String COLUMN_PRODUCT_GROUP_NAME2 = "product_group_name_2";
	public static final String COLUMN_PRODUCT_GROUP_TYPE = "product_group_type";
	public static final String COLUMN_IS_COMMENT = "is_comment";
	
	private static final String SQL_CREATE =
			" create table " + TABLE_PRODUCT_GROUP + " ( "
			+ COLUMN_PRODUCT_GROUP_ID + " integer not null, "
			+ COLUMN_PRODUCT_GROUP_CODE + " text, " 
			+ COLUMN_PRODUCT_GROUP_NAME + " text, " 
			+ COLUMN_PRODUCT_GROUP_NAME1 + " text, " 
			+ COLUMN_PRODUCT_GROUP_NAME2 + " text, " 
			+ COLUMN_PRODUCT_GROUP_TYPE + " integer default 0, "
			+ COLUMN_IS_COMMENT + " integer default 0, "
			+ ProductTable.COLUMN_ACTIVATE + " integer default 0, "
			+ COLUMN_ORDERING + " integer default 0, " 
			+ COLUMN_DELETED + " integer default 0, "
			+ " primary key (" + ProductGroupTable.COLUMN_PRODUCT_GROUP_ID + "));";
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE_PRODUCT_GROUP);
		onCreate(db);
	}		
}
