package com.synature.mpos.database.table;

import android.database.sqlite.SQLiteDatabase;

public class PromotionProductDiscountTable {
	
	public static final String TABLE_PROMOTION_PRODUCT_DISCOUNT = "PromotionProductDiscount";
	public static final String COLUMN_DISCOUNT_AMOUNT = "discount_amount";
	public static final String COLUMN_DISCOUNT_PERCENT = "discount_percent";
	public static final String COLUMN_AMOUNT_OR_PERCENT = "amount_or_percent";
	
	public static final String SQL_CREATE = 
			" create table " + TABLE_PROMOTION_PRODUCT_DISCOUNT + "("
			+ PromotionPriceGroupTable.COLUMN_PRICE_GROUP_ID + " integer not null, "
			+ ProductTable.COLUMN_PRODUCT_ID + " integer not null, "
			+ ProductTable.COLUMN_SALE_MODE + " integer, "
			+ COLUMN_DISCOUNT_AMOUNT + " real not null default 0, "
			+ COLUMN_DISCOUNT_PERCENT + " real not null default 0, "
			+ COLUMN_AMOUNT_OR_PERCENT + " integer default 0 );";
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE_PROMOTION_PRODUCT_DISCOUNT);
		onCreate(db);
	}
}
