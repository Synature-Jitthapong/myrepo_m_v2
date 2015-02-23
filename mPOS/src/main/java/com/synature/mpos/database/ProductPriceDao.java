package com.synature.mpos.database;

import java.util.List;

import com.synature.mpos.database.table.ProductPriceTable;
import com.synature.mpos.database.table.ProductTable;

import android.content.ContentValues;
import android.content.Context;

public class ProductPriceDao extends MPOSDatabase{

	public ProductPriceDao(Context context) {
		super(context);
	}

	public void insertProductPrice(List<com.synature.pos.ProductPrice> priceLst){
		getWritableDatabase().beginTransaction();
		try{
			getWritableDatabase().delete(ProductPriceTable.TABLE_PRODUCT_PRICE, null, null);
			for(com.synature.pos.ProductPrice price : priceLst){
				ContentValues cv = new ContentValues();
				cv.put(ProductPriceTable.COLUMN_PRODUCT_PRICE_ID, price.getPPID());
				cv.put(ProductTable.COLUMN_PRODUCT_ID, price.getPID());
				cv.put(ProductTable.COLUMN_PRODUCT_PRICE, price.getPRICE());
				cv.put(ProductTable.COLUMN_SALE_MODE, price.getSMODE());
				cv.put(ProductPriceTable.COLUMN_PRICE_FROM_DATE, price.getFDATE());
				cv.put(ProductPriceTable.COLUMN_PRICE_TO_DATE, price.getTDATE());
				getWritableDatabase().insert(ProductPriceTable.TABLE_PRODUCT_PRICE, null, cv);
			}
			getWritableDatabase().setTransactionSuccessful();
		}finally{
			getWritableDatabase().endTransaction();
		}
	}
}
