package com.synature.mpos.database;

import java.util.List;

import com.synature.mpos.database.table.ShopTable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

public class ShopDao extends MPOSDatabase{
	
	public static final int SHOP_TYPE_FOOD_COURT = 3;
	
	public static final String[] ALL_SHOP_COLUMNS = {
		ShopTable.COLUMN_SHOP_ID,
		ShopTable.COLUMN_SHOP_CODE,
		ShopTable.COLUMN_SHOP_NAME,
		ShopTable.COLUMN_SHOP_TYPE,
		ShopTable.COLUMN_FAST_FOOD_TYPE,
		ShopTable.COLUMN_OPEN_HOUR,
		ShopTable.COLUMN_CLOSE_HOUR,
		ShopTable.COLUMN_COMPANY_VAT_TYPE,
		ShopTable.COLUMN_COMPANY_NAME,
		ShopTable.COLUMN_ADDR1,
		ShopTable.COLUMN_ADDR2,
		ShopTable.COLUMN_CITY,
		ShopTable.COLUMN_PROVINCE_ID,
		ShopTable.COLUMN_ZIPCODE,
		ShopTable.COLUMN_TELEPHONE,
		ShopTable.COLUMN_FAX,
		ShopTable.COLUMN_TAX_ID,
		ShopTable.COLUMN_REGISTER_ID,
		ShopTable.COLUMN_COMPANY_VAT_RATE,
		ShopTable.COLUMN_PRINT_VAT_IN_RECEIPT
	};
	
	public ShopDao(Context context){
		super(context);
	}
	
	/**
	 * @return Shop Name
	 */
	public String getShopName(){
		return getShopProperty().getShopName();
	}
	
	/**
	 * @return Type of fast food
	 */
	public int getFastFoodType(){
		return getShopProperty().getFastFoodType();
	}
	
	/**
	 * @return Type of shop
	 */
	public int getShopType(){
		return getShopProperty().getShopType();
	}
	
	/**
	 * @return Company Vat Type
	 */
	public int getCompanyVatType(){
		return getShopProperty().getVatType();
	}
	
	/**
	 * @return company vat rate
	 */
	public double getCompanyVatRate(){
		return getShopProperty().getCompanyVat();
	}

	/**
	 * @return shop id
	 */
	public int getShopId(){
		return getShopProperty().getShopID();
	}
	
	/**
	 * @return ShopData.ShopProperty
	 */
	public com.synature.pos.ShopProperty getShopProperty(){
		com.synature.pos.ShopProperty sp = new com.synature.pos.ShopProperty();
		Cursor cursor = getReadableDatabase().query(ShopTable.TABLE_SHOP, 
				ALL_SHOP_COLUMNS, null, null, null, null, null);
		if(cursor.moveToFirst()){
			sp.setShopID(cursor.getInt(cursor.getColumnIndex(ShopTable.COLUMN_SHOP_ID)));
			sp.setShopCode(cursor.getString(cursor.getColumnIndex(ShopTable.COLUMN_SHOP_CODE)));
			sp.setShopName(cursor.getString(cursor.getColumnIndex(ShopTable.COLUMN_SHOP_NAME)));
			sp.setShopType(cursor.getInt(cursor.getColumnIndex(ShopTable.COLUMN_SHOP_TYPE)));
			sp.setFastFoodType(cursor.getInt(cursor.getColumnIndex(ShopTable.COLUMN_FAST_FOOD_TYPE)));
			sp.setOpenHour(cursor.getString(cursor.getColumnIndex(ShopTable.COLUMN_OPEN_HOUR)));
			sp.setCloseHour(cursor.getString(cursor.getColumnIndex(ShopTable.COLUMN_CLOSE_HOUR)));
			sp.setVatType(cursor.getInt(cursor.getColumnIndex(ShopTable.COLUMN_COMPANY_VAT_TYPE)));
			sp.setCompanyName(cursor.getString(cursor.getColumnIndex(ShopTable.COLUMN_COMPANY_NAME)));
			sp.setCompanyAddress1(cursor.getString(cursor.getColumnIndex(ShopTable.COLUMN_ADDR1)));
			sp.setCompanyAddress2(cursor.getString(cursor.getColumnIndex(ShopTable.COLUMN_ADDR2)));
			sp.setCompanyCity(cursor.getString(cursor.getColumnIndex(ShopTable.COLUMN_CITY)));
			sp.setCompanyProvince(cursor.getString(cursor.getColumnIndex(ShopTable.COLUMN_PROVINCE_ID)));
			sp.setCompanyZipCode(cursor.getString(cursor.getColumnIndex(ShopTable.COLUMN_ZIPCODE)));
			sp.setCompanyTelephone(cursor.getString(cursor.getColumnIndex(ShopTable.COLUMN_TELEPHONE)));
			sp.setCompanyFax(cursor.getString(cursor.getColumnIndex(ShopTable.COLUMN_FAX)));
			sp.setCompanyTaxID(cursor.getString(cursor.getColumnIndex(ShopTable.COLUMN_TAX_ID)));
			sp.setCompanyRegisterID(cursor.getString(cursor.getColumnIndex(ShopTable.COLUMN_REGISTER_ID)));
			sp.setCompanyVat(cursor.getFloat(cursor.getColumnIndex(ShopTable.COLUMN_COMPANY_VAT_RATE)));
			sp.setPrintVatInReceipt(cursor.getInt(cursor.getColumnIndex(ShopTable.COLUMN_PRINT_VAT_IN_RECEIPT)));
			cursor.moveToNext();
		}
		cursor.close();		
		return sp;
	}

	/**
	 * @param shopPropLst
	 * @throws SQLException
	 */
	public void insertShopProperty(List<com.synature.pos.ShopProperty> shopPropLst) throws SQLException{
		getWritableDatabase().beginTransaction();
		try {
			getWritableDatabase().delete(ShopTable.TABLE_SHOP, null, null);
			for(com.synature.pos.ShopProperty shop : shopPropLst){
				ContentValues cv = new ContentValues();
				cv.put(ShopTable.COLUMN_SHOP_ID, shop.getShopID());
				cv.put(ShopTable.COLUMN_SHOP_CODE, shop.getShopCode());
				cv.put(ShopTable.COLUMN_SHOP_NAME, shop.getShopName());
				cv.put(ShopTable.COLUMN_SHOP_TYPE, shop.getShopType());
				cv.put(ShopTable.COLUMN_FAST_FOOD_TYPE, shop.getFastFoodType());
				cv.put(ShopTable.COLUMN_COMPANY_VAT_TYPE, shop.getVatType());
				cv.put(ShopTable.COLUMN_OPEN_HOUR, shop.getOpenHour());
				cv.put(ShopTable.COLUMN_CLOSE_HOUR, shop.getCloseHour());
				cv.put(ShopTable.COLUMN_COMPANY_NAME, shop.getCompanyName());
				cv.put(ShopTable.COLUMN_ADDR1, shop.getCompanyAddress1());
				cv.put(ShopTable.COLUMN_ADDR2, shop.getCompanyAddress2());
				cv.put(ShopTable.COLUMN_CITY, shop.getCompanyCity());
				cv.put(ShopTable.COLUMN_PROVINCE_ID, shop.getCompanyProvince());
				cv.put(ShopTable.COLUMN_ZIPCODE, shop.getCompanyZipCode());
				cv.put(ShopTable.COLUMN_TELEPHONE, shop.getCompanyTelephone());
				cv.put(ShopTable.COLUMN_FAX, shop.getCompanyFax());
				cv.put(ShopTable.COLUMN_TAX_ID, shop.getCompanyTaxID());
				cv.put(ShopTable.COLUMN_REGISTER_ID, shop.getCompanyRegisterID());
				cv.put(ShopTable.COLUMN_COMPANY_VAT_RATE, shop.getCompanyVat());
				cv.put(ShopTable.COLUMN_PRINT_VAT_IN_RECEIPT, shop.getPrintVatInReceipt());
				getWritableDatabase().insertOrThrow(ShopTable.TABLE_SHOP, null, cv);
			}
			getWritableDatabase().setTransactionSuccessful();
		} finally {
			getWritableDatabase().endTransaction();
		}
	}
}
