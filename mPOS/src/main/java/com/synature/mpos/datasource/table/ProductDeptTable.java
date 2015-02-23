package com.synature.mpos.datasource.table;

import android.database.sqlite.SQLiteDatabase;

public class ProductDeptTable extends BaseColumn{
	
	public static final String TABLE_PRODUCT_DEPT = "ProductDept";
	public static final String COLUMN_PRODUCT_DEPT_ID = "product_dept_id";
	public static final String COLUMN_PRODUCT_DEPT_CODE = "product_dept_code";
	public static final String COLUMN_PRODUCT_DEPT_NAME = "product_dept_name";
	public static final String COLUMN_PRODUCT_DEPT_NAME1 = "product_dept_name_1";
	
	private static final String SQL_CREATE =
			" create table " + TABLE_PRODUCT_DEPT + " ( " 
			+ COLUMN_PRODUCT_DEPT_ID + " integer not null, " 
			+ ProductGroupTable.COLUMN_PRODUCT_GROUP_ID + " integer not null, " 
			+ COLUMN_PRODUCT_DEPT_CODE + " text, "
			+ COLUMN_PRODUCT_DEPT_NAME + " text, "
			+ COLUMN_PRODUCT_DEPT_NAME1 + " text, "
			+ ProductTable.COLUMN_ACTIVATE + " integer default 0, "
			+ COLUMN_DELETED + " integer default 0, "
			+ COLUMN_ORDERING + " integer default 0, " 
			+ " primary key (" + ProductDeptTable.COLUMN_PRODUCT_DEPT_ID + "));";
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE_PRODUCT_DEPT);
		onCreate(db);
	}
}
