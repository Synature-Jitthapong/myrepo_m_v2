package com.synature.mpos.database.table;

import android.database.sqlite.SQLiteDatabase;

public class PromotionPriceGroupTable {

	public static final String TABLE_PROMOTION_PRICE_GROUP = "PromotionPriceGroup";
	public static final String COLUMN_PRICE_GROUP_ID = "price_group_id";
	public static final String COLUMN_PROMOTION_TYPE_ID = "promotion_type_id";
	public static final String COLUMN_PROMOTION_CODE = "promotion_code";
	public static final String COLUMN_PROMOTION_NAME = "promotion_name";
	public static final String COLUMN_BUTTON_NAME = "button_name";
	public static final String COLUMN_COUPON_HEADER = "coupon_header";
	public static final String COLUMN_PRICE_FROM_DATE = "price_from_date";
	public static final String COLUMN_PRICE_FROM_TIME = "price_from_time";
	public static final String COLUMN_PRICE_TO_DATE = "price_to_date";
	public static final String COLUMN_PRICE_TO_TIME = "price_to_time";
	public static final String COLUMN_PROMOTION_WEEKLY = "promotion_weekly";
	public static final String COLUMN_PROMOTION_MONTHLY = "promotion_monthly";
	public static final String COLUMN_IS_ALLOW_USE_OTHER_PROMOTION = "is_allow_use_other_promotion";
	public static final String COLUMN_VOUCHER_AMOUNT = "voucher_amount";
	public static final String COLUMN_OVER_PRICE = "over_price";
	public static final String COLUMN_PROMOTION_AMOUNT_TYPE = "promotion_amount_type";
	
	public static final String SQL_CREATE = 
			" create table " + TABLE_PROMOTION_PRICE_GROUP + "("
			+ COLUMN_PRICE_GROUP_ID + " integer not null, "
			+ COLUMN_PROMOTION_TYPE_ID + " integer not null, "
			+ COLUMN_PROMOTION_CODE + " text, "
			+ COLUMN_PROMOTION_NAME + " text, "
			+ COLUMN_BUTTON_NAME + " text, "
			+ COLUMN_COUPON_HEADER + " text, "
			+ COLUMN_PRICE_FROM_DATE + " text, "
			+ COLUMN_PRICE_FROM_TIME + " text, "
			+ COLUMN_PRICE_TO_DATE + " text, "
			+ COLUMN_PRICE_TO_TIME + " text, "
			+ COLUMN_PROMOTION_WEEKLY + " text, "
			+ COLUMN_PROMOTION_MONTHLY + " text, "
			+ COLUMN_IS_ALLOW_USE_OTHER_PROMOTION + " integer, "
			+ COLUMN_VOUCHER_AMOUNT + " real, "
			+ COLUMN_OVER_PRICE + " real, "
			+ COLUMN_PROMOTION_AMOUNT_TYPE + " integer);";
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE_PROMOTION_PRICE_GROUP);
		onCreate(db);
	}
}
