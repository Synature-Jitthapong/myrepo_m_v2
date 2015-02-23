package com.synature.mpos.database.table;

import android.database.sqlite.SQLiteDatabase;

public class ProductPriceTable extends BaseColumn{
	
	public static final String TABLE_PRODUCT_PRICE = "ProductPrice";
	public static final String COLUMN_PRODUCT_PRICE_ID = "product_price_id";
	public static final String COLUMN_PRICE_FROM_DATE = "price_from_date";
	public static final String COLUMN_PRICE_TO_DATE = "price_to_date";
	
	private static final String SQL_CREATE =
			" create table " + TABLE_PRODUCT_PRICE + " ( "
			+ COLUMN_PRODUCT_PRICE_ID + " integer,"
			+ ProductTable.COLUMN_PRODUCT_ID + " integer, " 
			+ ProductTable.COLUMN_PRODUCT_PRICE + " real, "
			+ ProductTable.COLUMN_SALE_MODE + " integer default 1,"
			+ COLUMN_PRICE_FROM_DATE + " text, "
			+ COLUMN_PRICE_TO_DATE + " text);";
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE_PRODUCT_PRICE);
		onCreate(db);
	}
}
