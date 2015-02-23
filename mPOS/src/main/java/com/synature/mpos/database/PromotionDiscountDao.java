package com.synature.mpos.database;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.synature.mpos.Utils;
import com.synature.mpos.database.table.ProductTable;
import com.synature.mpos.database.table.PromotionPriceGroupTable;
import com.synature.mpos.database.table.PromotionProductDiscountTable;
import com.synature.pos.PromotionPriceGroup;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

public class PromotionDiscountDao extends MPOSDatabase{

	public static final int PROMOTION_TYPE_COUPON = 4;
	public static final int PROMOTION_TYPE_VOUCHER = 5;
	
	public PromotionDiscountDao(Context context) {
		super(context);
	}

	/**
	 * List product discount
	 * @param priceGroupId
	 * @return List<com.synature.pos.PromotionProduct>
	 */
	public List<com.synature.pos.PromotionProductDiscount> listPromotionProductDiscount(int priceGroupId){
		List<com.synature.pos.PromotionProductDiscount> productLst = 
				new ArrayList<com.synature.pos.PromotionProductDiscount>();
		Cursor cursor = getReadableDatabase().query(PromotionProductDiscountTable.TABLE_PROMOTION_PRODUCT_DISCOUNT, 
				new String[]{
					ProductTable.COLUMN_PRODUCT_ID,
					PromotionProductDiscountTable.COLUMN_DISCOUNT_AMOUNT,
					PromotionProductDiscountTable.COLUMN_DISCOUNT_PERCENT,
					PromotionProductDiscountTable.COLUMN_AMOUNT_OR_PERCENT
				}, 
				PromotionPriceGroupTable.COLUMN_PRICE_GROUP_ID + "=?", 
				new String[]{
					String.valueOf(priceGroupId)
				}, null, null, null);
		if(cursor.moveToFirst()){
			do{
				com.synature.pos.PromotionProductDiscount product = new com.synature.pos.PromotionProductDiscount();
				product.setProductID(cursor.getInt(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_ID)));
				product.setDiscountAmount(cursor.getDouble(cursor.getColumnIndex(PromotionProductDiscountTable.COLUMN_DISCOUNT_AMOUNT)));
				product.setDiscountPercent(cursor.getDouble(cursor.getColumnIndex(PromotionProductDiscountTable.COLUMN_DISCOUNT_PERCENT)));
				product.setAmountOrPercent(cursor.getInt(cursor.getColumnIndex(PromotionProductDiscountTable.COLUMN_AMOUNT_OR_PERCENT)));
				productLst.add(product);
			}while(cursor.moveToNext());
		}
		cursor.close();
		return productLst;
	}
	
	/**
	 * List PromotionPriceGroup
	 * @return t<com.synature.pos.PromotionPrice>
	 */
	public List<com.synature.pos.PromotionPriceGroup> listPromotionPriceGroup(){
		List<com.synature.pos.PromotionPriceGroup> promoLst = 
				new ArrayList<com.synature.pos.PromotionPriceGroup>();
		String selection = PromotionPriceGroupTable.COLUMN_PROMOTION_TYPE_ID + "=?"
				+ " AND " + PromotionPriceGroupTable.COLUMN_PRICE_FROM_DATE + "<=?";
		String[] selectionArgs = new String[]{
				String.valueOf(PROMOTION_TYPE_COUPON),
				String.valueOf(Utils.getDate().getTimeInMillis())
			};
		Cursor cursor = getReadableDatabase().query(
				PromotionPriceGroupTable.TABLE_PROMOTION_PRICE_GROUP, 
				new String[]{
				 	PromotionPriceGroupTable.COLUMN_PRICE_GROUP_ID,
				 	PromotionPriceGroupTable.COLUMN_PROMOTION_TYPE_ID,
				 	PromotionPriceGroupTable.COLUMN_PROMOTION_CODE,
				 	PromotionPriceGroupTable.COLUMN_PROMOTION_NAME,
				 	PromotionPriceGroupTable.COLUMN_BUTTON_NAME,
				 	PromotionPriceGroupTable.COLUMN_COUPON_HEADER,
				 	PromotionPriceGroupTable.COLUMN_PRICE_FROM_DATE,
				 	PromotionPriceGroupTable.COLUMN_PRICE_FROM_TIME,
				 	PromotionPriceGroupTable.COLUMN_PRICE_TO_DATE,
				 	PromotionPriceGroupTable.COLUMN_PRICE_TO_TIME,
				 	PromotionPriceGroupTable.COLUMN_PROMOTION_WEEKLY,
				 	PromotionPriceGroupTable.COLUMN_PROMOTION_MONTHLY,
				 	PromotionPriceGroupTable.COLUMN_IS_ALLOW_USE_OTHER_PROMOTION,
				 	PromotionPriceGroupTable.COLUMN_VOUCHER_AMOUNT,
				 	PromotionPriceGroupTable.COLUMN_OVER_PRICE,
				 	PromotionPriceGroupTable.COLUMN_PROMOTION_AMOUNT_TYPE
				},selection, selectionArgs, null, null, PromotionPriceGroupTable.COLUMN_PRICE_GROUP_ID);
		if(cursor.moveToFirst()){
			do{
				boolean isActive = true;
				int pgId = cursor.getInt(cursor.getColumnIndex(PromotionPriceGroupTable.COLUMN_PRICE_GROUP_ID));
				String toDate = getToDateCondition(pgId);
				if(!TextUtils.isEmpty(toDate)){
					isActive = false;
					if(Utils.getCalendar().getTimeInMillis() <= Long.parseLong(toDate)){
						isActive = true;
					}
				}
				String weekly = getWeeklyCondition(pgId);
				if(!TextUtils.isEmpty(weekly)){
					isActive = false;
					String[] days = weekly.split(",");
					if(days.length > 0){
						for(String day : days){
							int dayOfWeek = Utils.getCalendar().get(Calendar.DAY_OF_WEEK);
							if(dayOfWeek == Integer.parseInt(day)){
								isActive = true;
								break;
							}
						}
					}
				}
				String monthly = getMonthlyCondition(pgId);
				if(!TextUtils.isEmpty(monthly)){
					isActive = false;
					String[] days = monthly.split(",");
					if(days.length > 0){
						for(String day : days){
							if(Utils.getCalendar().get(Calendar.DAY_OF_MONTH) == Integer.parseInt(day)){
								isActive = true;
								break;
							}
						}
					}
				}
				if(isActive){
					com.synature.pos.PromotionPriceGroup promoPriceGroup = new com.synature.pos.PromotionPriceGroup();
					promoPriceGroup.setPriceGroupID(pgId);
					promoPriceGroup.setPromotionTypeID(cursor.getInt(cursor.getColumnIndex(PromotionPriceGroupTable.COLUMN_PROMOTION_TYPE_ID)));
					promoPriceGroup.setPromotionCode(cursor.getString(cursor.getColumnIndex(PromotionPriceGroupTable.COLUMN_PROMOTION_CODE)));
					promoPriceGroup.setPromotionName(cursor.getString(cursor.getColumnIndex(PromotionPriceGroupTable.COLUMN_PROMOTION_NAME)));
					promoPriceGroup.setButtonName(cursor.getString(cursor.getColumnIndex(PromotionPriceGroupTable.COLUMN_BUTTON_NAME)));
					promoPriceGroup.setCouponHeader(cursor.getString(cursor.getColumnIndex(PromotionPriceGroupTable.COLUMN_COUPON_HEADER)));
					promoPriceGroup.setPriceFromDate(cursor.getString(cursor.getColumnIndex(PromotionPriceGroupTable.COLUMN_PRICE_FROM_DATE)));
					promoPriceGroup.setPriceFromTime(cursor.getString(cursor.getColumnIndex(PromotionPriceGroupTable.COLUMN_PRICE_FROM_TIME)));
					promoPriceGroup.setPriceToDate(cursor.getString(cursor.getColumnIndex(PromotionPriceGroupTable.COLUMN_PRICE_TO_DATE)));
					promoPriceGroup.setPriceToTime(cursor.getString(cursor.getColumnIndex(PromotionPriceGroupTable.COLUMN_PRICE_TO_TIME)));
					promoPriceGroup.setPromotionWeekly(cursor.getString(cursor.getColumnIndex(PromotionPriceGroupTable.COLUMN_PROMOTION_WEEKLY)));
					promoPriceGroup.setPromotionMonthly(cursor.getString(cursor.getColumnIndex(PromotionPriceGroupTable.COLUMN_PROMOTION_MONTHLY)));
					promoPriceGroup.setIsAllowUseOtherPromotion(cursor.getInt(cursor.getColumnIndex(PromotionPriceGroupTable.COLUMN_IS_ALLOW_USE_OTHER_PROMOTION)));
					promoPriceGroup.setVoucherAmount(cursor.getDouble(cursor.getColumnIndex(PromotionPriceGroupTable.COLUMN_VOUCHER_AMOUNT)));
					promoPriceGroup.setOverPrice(cursor.getDouble(cursor.getColumnIndex(PromotionPriceGroupTable.COLUMN_OVER_PRICE)));
					promoPriceGroup.setPromotionAmountType(cursor.getInt(cursor.getColumnIndex(PromotionPriceGroupTable.COLUMN_PROMOTION_AMOUNT_TYPE)));
					promoLst.add(promoPriceGroup);
				}
			}while(cursor.moveToNext());
		}
		cursor.close();
		return promoLst;
	}
	
	private String getWeeklyCondition(int pgId){
		String weekly = "";
		Cursor cursor = getReadableDatabase().query(
				PromotionPriceGroupTable.TABLE_PROMOTION_PRICE_GROUP, 
				new String[]{
						PromotionPriceGroupTable.COLUMN_PROMOTION_WEEKLY
				}, 
				PromotionPriceGroupTable.COLUMN_PRICE_GROUP_ID + "=?", 
				new String[]{
						String.valueOf(pgId)
				}, null, null, null);
		if(cursor.moveToFirst()){
			weekly = cursor.getString(0);
		}
		cursor.close();
		return weekly;
	}
	
	private String getMonthlyCondition(int pgId){
		String monthly = "";
		Cursor cursor = getReadableDatabase().query(
				PromotionPriceGroupTable.TABLE_PROMOTION_PRICE_GROUP, 
				new String[]{
						PromotionPriceGroupTable.COLUMN_PROMOTION_MONTHLY
				}, 
				PromotionPriceGroupTable.COLUMN_PRICE_GROUP_ID + "=?",
				new String[]{
						String.valueOf(pgId)
				}, null, null, null);
		if(cursor.moveToFirst()){
			monthly = cursor.getString(0);
		}
		cursor.close();
		return monthly;
	}
	
	private String getToDateCondition(int pgId){
		String toDate = "";
		Cursor cursor = getReadableDatabase().query(
				PromotionPriceGroupTable.TABLE_PROMOTION_PRICE_GROUP, 
				new String[]{
						PromotionPriceGroupTable.COLUMN_PRICE_TO_DATE
				}, 
				PromotionPriceGroupTable.COLUMN_PRICE_GROUP_ID + "=?", 
				new String[]{
						String.valueOf(pgId)
				}, null, null, null);
		if(cursor.moveToFirst()){
			toDate = cursor.getString(0);
		}
		cursor.close();
		return toDate;
	}
	
	/**
	 * Insert PromotionProductDiscount
	 * @param promoProductLst
	 */
	public void insertPromotionProductDiscount(List<com.synature.pos.PromotionProductDiscount> promoProductLst){
		getWritableDatabase().beginTransaction();
		try {
			getWritableDatabase().delete(PromotionProductDiscountTable.TABLE_PROMOTION_PRODUCT_DISCOUNT, null, null);
			for(com.synature.pos.PromotionProductDiscount promoProduct : promoProductLst){
				ContentValues cv = new ContentValues();
				cv.put(PromotionPriceGroupTable.COLUMN_PRICE_GROUP_ID, promoProduct.getPriceGroupID());
				cv.put(ProductTable.COLUMN_PRODUCT_ID, promoProduct.getProductID());
				cv.put(ProductTable.COLUMN_SALE_MODE, promoProduct.getSaleMode());
				cv.put(PromotionProductDiscountTable.COLUMN_DISCOUNT_AMOUNT, promoProduct.getDiscountAmount());
				cv.put(PromotionProductDiscountTable.COLUMN_DISCOUNT_PERCENT, promoProduct.getDiscountPercent());
				cv.put(PromotionProductDiscountTable.COLUMN_AMOUNT_OR_PERCENT, promoProduct.getAmountOrPercent());
				getWritableDatabase().insert(PromotionProductDiscountTable.TABLE_PROMOTION_PRODUCT_DISCOUNT, null, cv);
			}
			getWritableDatabase().setTransactionSuccessful();
		} finally {
			getWritableDatabase().endTransaction();
		}
		
	}
	
	/**
	 * Insert PromotionPriceGroup
	 * @param promoLst
	 */
	public void insertPromotionPriceGroup(List<com.synature.pos.PromotionPriceGroup> promoLst){
		getWritableDatabase().beginTransaction();
		try {
			getWritableDatabase().delete(PromotionPriceGroupTable.TABLE_PROMOTION_PRICE_GROUP, null, null);
			for(com.synature.pos.PromotionPriceGroup promoPriceGroup : promoLst){
				String strDf = promoPriceGroup.getPriceFromDate();
				String strDt = promoPriceGroup.getPriceToDate();
				try {
					Calendar c = Utils.getCalendar();
					DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
					c.setTime(df.parse(promoPriceGroup.getPriceFromDate()));
					strDf = String.valueOf(c.getTimeInMillis());
					df = new SimpleDateFormat("yyyy-MM-dd");
					c.setTime(df.parse(promoPriceGroup.getPriceToDate()));
					strDt = String.valueOf(c.getTimeInMillis());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ContentValues cv = new ContentValues();
				cv.put(PromotionPriceGroupTable.COLUMN_PRICE_GROUP_ID, promoPriceGroup.getPriceGroupID());
				cv.put(PromotionPriceGroupTable.COLUMN_PROMOTION_TYPE_ID, promoPriceGroup.getPromotionTypeID());
				cv.put(PromotionPriceGroupTable.COLUMN_PROMOTION_CODE, promoPriceGroup.getPromotionCode());
				cv.put(PromotionPriceGroupTable.COLUMN_PROMOTION_NAME, promoPriceGroup.getPromotionName());
				cv.put(PromotionPriceGroupTable.COLUMN_BUTTON_NAME, promoPriceGroup.getButtonName());
				cv.put(PromotionPriceGroupTable.COLUMN_COUPON_HEADER, promoPriceGroup.getCouponHeader());
				cv.put(PromotionPriceGroupTable.COLUMN_PRICE_FROM_DATE, strDf);
				cv.put(PromotionPriceGroupTable.COLUMN_PRICE_FROM_TIME, promoPriceGroup.getPriceFromTime());
				cv.put(PromotionPriceGroupTable.COLUMN_PRICE_TO_DATE, strDt);
				cv.put(PromotionPriceGroupTable.COLUMN_PRICE_TO_TIME, promoPriceGroup.getPriceToTime());
				cv.put(PromotionPriceGroupTable.COLUMN_PROMOTION_WEEKLY, promoPriceGroup.getPromotionWeekly());
				cv.put(PromotionPriceGroupTable.COLUMN_PROMOTION_MONTHLY, promoPriceGroup.getPromotionMonthly());
				cv.put(PromotionPriceGroupTable.COLUMN_IS_ALLOW_USE_OTHER_PROMOTION, promoPriceGroup.getIsAllowUseOtherPromotion());
				cv.put(PromotionPriceGroupTable.COLUMN_VOUCHER_AMOUNT, promoPriceGroup.getVoucherAmount());
				cv.put(PromotionPriceGroupTable.COLUMN_OVER_PRICE, promoPriceGroup.getOverPrice());
				cv.put(PromotionPriceGroupTable.COLUMN_PROMOTION_AMOUNT_TYPE, promoPriceGroup.getPromotionAmountType());
				getWritableDatabase().insert(PromotionPriceGroupTable.TABLE_PROMOTION_PRICE_GROUP, null, cv);
			}
			getWritableDatabase().setTransactionSuccessful();
		} finally{
			getWritableDatabase().endTransaction();
		}
	}
}
