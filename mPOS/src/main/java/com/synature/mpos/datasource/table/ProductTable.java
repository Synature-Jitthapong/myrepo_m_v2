package com.synature.mpos.datasource.table;

import android.database.sqlite.SQLiteDatabase;

public class ProductTable extends BaseColumn{
	
	public static final String TABLE_PRODUCT = "Product";
	public static final String COLUMN_PRODUCT_ID = "product_id";
	public static final String COLUMN_PRODUCT_CODE = "product_code";
	public static final String COLUMN_PRODUCT_BAR_CODE = "product_barcode";
	public static final String COLUMN_PRODUCT_NAME = "product_name";
	public static final String COLUMN_PRODUCT_NAME1 = "product_name_1";
	public static final String COLUMN_PRODUCT_NAME2 = "product_name_2";
	public static final String COLUMN_PRODUCT_DESC = "product_desc";
	public static final String COLUMN_PRODUCT_TYPE_ID = "product_type_id";
	public static final String COLUMN_PRODUCT_PRICE = "product_price";
	public static final String COLUMN_PRODUCT_UNIT_NAME = "product_unitname";
	public static final String COLUMN_DISCOUNT_ALLOW = "discount_allow";
	public static final String COLUMN_VAT_TYPE = "vat_type";
	public static final String COLUMN_VAT_RATE = "vat_rate";
	public static final String COLUMN_IS_OUTOF_STOCK = "is_outof_stock";
	public static final String COLUMN_IMG_FILE_NAME = "image_file_name";
	public static final String COLUMN_ACTIVATE = "activate";
	public static final String COLUMN_SALE_MODE = "sale_mode";
	
	private static final String SQL_CREATE =
			" create table " + TABLE_PRODUCT + " ( " 
			+ COLUMN_PRODUCT_ID + " integer not null, " 
			+ ProductDeptTable.COLUMN_PRODUCT_DEPT_ID + " integer not null, " 
			+ ProductGroupTable.COLUMN_PRODUCT_GROUP_ID + " integer not null, "
			+ COLUMN_PRODUCT_CODE + " text, "
			+ COLUMN_PRODUCT_BAR_CODE + " text, " 
			+ COLUMN_PRODUCT_NAME + " text, " 
			+ COLUMN_PRODUCT_NAME1 + " text, "
			+ COLUMN_PRODUCT_NAME2 + " text, "
			+ COLUMN_PRODUCT_DESC + " text, "
			+ COLUMN_PRODUCT_TYPE_ID + " integer default 0, "
			+ COLUMN_PRODUCT_PRICE + " real default 0, "
			+ COLUMN_PRODUCT_UNIT_NAME + " text, " 
			+ COLUMN_DISCOUNT_ALLOW + " integer default 1, " 
			+ COLUMN_VAT_TYPE + " integer not null default 1, "
			+ COLUMN_VAT_RATE + " real not null default 0, " 
			+ COLUMN_IS_OUTOF_STOCK + " integer default 0, " 
			+ COLUMN_IMG_FILE_NAME + " text, " 
			+ COLUMN_ACTIVATE + " integer default 0, " 
			+ COLUMN_DELETED + " integer default 0, "
			+ COLUMN_ORDERING + " integer default 0, " 
			+ " primary key (" + COLUMN_PRODUCT_ID + " ASC));";
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE_PRODUCT);
		onCreate(db);
	}
}
