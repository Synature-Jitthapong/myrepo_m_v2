package com.synature.mpos.datasource.table;

import android.database.sqlite.SQLiteDatabase;

public class ShopTable{

	public static final String TABLE_SHOP = "Shop";
	public static final String COLUMN_SHOP_ID = "shop_id";
	public static final String COLUMN_SHOP_CODE = "shop_code";
	public static final String COLUMN_SHOP_NAME = "shop_name";
	public static final String COLUMN_SHOP_TYPE = "shop_type";
	public static final String COLUMN_FAST_FOOD_TYPE = "fast_food_type";
	public static final String COLUMN_COMPANY_VAT_TYPE = "company_vat_type";
	public static final String COLUMN_OPEN_HOUR = "open_hour";
	public static final String COLUMN_CLOSE_HOUR = "close_hour";
	public static final String COLUMN_COMPANY_NAME = "company_name";
	public static final String COLUMN_ADDR1 = "addr1";
	public static final String COLUMN_ADDR2 = "addr2";
	public static final String COLUMN_CITY = "city";
	public static final String COLUMN_PROVINCE_ID = "province_id";
	public static final String COLUMN_ZIPCODE = "zip_code";
	public static final String COLUMN_TELEPHONE = "telephone";
	public static final String COLUMN_FAX = "fax";
	public static final String COLUMN_TAX_ID = "tax_id";
	public static final String COLUMN_REGISTER_ID = "register_id";
	public static final String COLUMN_COMPANY_VAT_RATE = "company_vat_rate";
	public static final String COLUMN_PRINT_VAT_IN_RECEIPT = "print_vat_in_receipt";
	
	private static final String SQL_CREATE =
			" create table " + TABLE_SHOP + " ( " 
			+ COLUMN_SHOP_ID + " integer not null, " 
			+ COLUMN_SHOP_CODE + " text, " 
			+ COLUMN_SHOP_NAME + " text, " 
			+ COLUMN_SHOP_TYPE + " integer not null, " 
			+ COLUMN_FAST_FOOD_TYPE + " integer not null, "
			+ COLUMN_COMPANY_VAT_TYPE + " integer not null, " 
			+ COLUMN_OPEN_HOUR + " text, "
			+ COLUMN_CLOSE_HOUR + " text, " 
			+ COLUMN_COMPANY_NAME + " text, "
			+ COLUMN_ADDR1 + " text, " 
			+ COLUMN_ADDR2 + " text, "
			+ COLUMN_CITY + " text, " 
			+ COLUMN_PROVINCE_ID + " integer, "
			+ COLUMN_ZIPCODE + " text, " 
			+ COLUMN_TELEPHONE + " text, "
			+ COLUMN_FAX + " text, " 
			+ COLUMN_TAX_ID + " text, "
			+ COLUMN_REGISTER_ID + " text, " 
			+ COLUMN_PRINT_VAT_IN_RECEIPT + " integer default 0, "
			+ COLUMN_COMPANY_VAT_RATE + " real not null );";
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE_SHOP);
		onCreate(db);
	}
}
