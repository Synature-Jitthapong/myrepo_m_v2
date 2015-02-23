package com.synature.mpos.database;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.synature.mpos.Utils;
import com.synature.mpos.database.table.GlobalPropertyTable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

public class GlobalPropertyDao extends MPOSDatabase{
	
	public static final String[] COLUMNS = {
		GlobalPropertyTable.COLUMN_CURRENCY_SYMBOL,
		GlobalPropertyTable.COLUMN_CURRENCY_CODE,
		GlobalPropertyTable.COLUMN_CURRENCY_NAME,
		GlobalPropertyTable.COLUMN_CURRENCY_FORMAT,
		GlobalPropertyTable.COLUMN_QTY_FORMAT,
		GlobalPropertyTable.COLUMN_DATE_FORMAT,
		GlobalPropertyTable.COLUMN_TIME_FORMAT,
		GlobalPropertyTable.COLUMN_TOTAL_DISCOUNT_ROUND_TYPE
	};
	
	public GlobalPropertyDao(Context context) {
		super(context);
	}
	
	/**
	 * Get rounding type 
	 * 1 - 6 up/down not follow by function
	 * 7 0, 1
	 * 8 0, 0.5, 1
	 * 9 0, 0.25, 0.5, 0.75, 1
	 * @return rounding type
	 */
	public int getRoundingType(){
		int roundingType = 0;
		com.synature.pos.GlobalProperty gb = getGlobalProperty();
		roundingType = gb.getTotalDiscountRoundType();
		return roundingType;
	}
	
	/**
	 * @param d
	 * @param pattern
	 * @return String format date
	 */
	public String dateFormat(Date d, String pattern){
		SimpleDateFormat dateFormat = getSimpleDateFormat();
		dateFormat.applyPattern(pattern);
		return dateFormat.format(d);	
	}
	
	/**
	 * @param d
	 * @return String format date
	 */
	public String dateFormat(Date d){
		SimpleDateFormat dateFormat = getSimpleDateFormat();
		if(!getGlobalProperty().getDateFormat().equals(""))
			dateFormat.applyPattern(getGlobalProperty().getDateFormat());
		return dateFormat.format(d);	
	}
	
	/**
	 * @param date
	 * @return String format date
	 */
	public String dateFormat(String date){
		Calendar calendar = Utils.convertStringToCalendar(date);
		SimpleDateFormat dateFormat = getSimpleDateFormat();
		if(!getGlobalProperty().getDateFormat().equals(""))
			dateFormat.applyPattern(getGlobalProperty().getDateFormat());
		return dateFormat.format(calendar.getTime());
	}
	
	/**
	 * @param dateTime
	 * @return String format date time
	 */
	public String dateTimeFormat(String dateTime){
		Calendar calendar = Utils.convertStringToCalendar(dateTime);
		SimpleDateFormat dateTimeFormat = getSimpleDateTimeFormat();
		if(!getGlobalProperty().getDateFormat().equals("") && 
				!getGlobalProperty().getTimeFormat().equals(""))
			dateTimeFormat.applyPattern(getGlobalProperty().getDateFormat() + " " +
					getGlobalProperty().getTimeFormat());
		return dateTimeFormat.format(calendar.getTime());
	}
	
	/**
	 * @param time
	 * @return String time format
	 */
	public String timeFormat(String time){
		Calendar calendar = Utils.convertStringToCalendar(time);
		SimpleDateFormat timeFormat = getSimpleTimeFormat();
		if(!getGlobalProperty().getTimeFormat().equals(""))
			timeFormat.applyPattern(getGlobalProperty().getTimeFormat());
		return timeFormat.format(calendar.getTime());
	}
	
	/**
	 * @param dateTime
	 * @param pattern
	 * @return String format date time
	 */
	public String dateTimeFormat(String dateTime, String pattern){
		Calendar calendar = Utils.convertStringToCalendar(dateTime);
		SimpleDateFormat dateTimeFormat = getSimpleDateTimeFormat();
		dateTimeFormat.applyPattern(pattern);
		return dateTimeFormat.format(calendar.getTime());
	}
	
	/**
	 * @param d
	 * @param pattern
	 * @return String format date time
	 */
	public String dateTimeFormat(Date d, String pattern){
		SimpleDateFormat dateTimeFormat = getSimpleDateTimeFormat();
		dateTimeFormat.applyPattern(pattern);
		return dateTimeFormat.format(d);
	}
	
	/**
	 * @param d
	 * @return String format date time
	 */
	public String dateTimeFormat(Date d){
		SimpleDateFormat dateTimeFormat = getSimpleDateTimeFormat();
		if(!getGlobalProperty().getDateFormat().equals("") && 
				!getGlobalProperty().getTimeFormat().equals(""))
			dateTimeFormat.applyPattern(getGlobalProperty().getDateFormat() + " " +
					getGlobalProperty().getTimeFormat());
		return dateTimeFormat.format(d);
	}
	
	/**
	 * @param d
	 * @return String format time
	 */
	public String timeFormat(Date d){
		SimpleDateFormat timeFormat = getSimpleTimeFormat();
		if(!getGlobalProperty().getTimeFormat().equals(""))
			timeFormat.applyPattern(getGlobalProperty().getTimeFormat());
		return timeFormat.format(d);
	}
	
	/**
	 * @param qty
	 * @param pattern
	 * @return qty format
	 */
	public String qtyFormat(double qty, String pattern){
		DecimalFormat qtyFormat = getDecimalFormat();
		qtyFormat.applyPattern(pattern);
		return qtyFormat.format(qty);
	}
	
	/**
	 * @param qty
	 * @return qty format
	 */
	public String qtyFormat(double qty){
		DecimalFormat qtyFormat = getDecimalFormat();
		if(!getGlobalProperty().getQtyFormat().equals("")){
			qtyFormat.applyPattern(getGlobalProperty().getQtyFormat());
			return qtyFormat.format(qty);
		}else{
			NumberFormat numFormat = getNumberFormat();
			return numFormat.format(qty);
		}
	}
	
	/**
	 * @param currency
	 * @param pattern
	 * @return currency format
	 */
	public String currencyFormat(double currency, String pattern){
		DecimalFormat currencyFormat = getDecimalFormat();
		currencyFormat.applyPattern(pattern);
		return currencyFormat.format(currency);
	}
	
	/**
	 * @param currency
	 * @return currency format
	 */
	public String currencyFormat(double currency){
		DecimalFormat currencyFormat = getDecimalFormat();
		if(!getGlobalProperty().getCurrencyFormat().equals("")){
			currencyFormat.applyPattern(getGlobalProperty().getCurrencyFormat());
			return currencyFormat.format(currency);
		}else{
			NumberFormat numFormat = getNumberFormat();
			return numFormat.format(currency);
		}
	}
	
	public com.synature.pos.GlobalProperty getGlobalProperty() {
		com.synature.pos.GlobalProperty gb = new com.synature.pos.GlobalProperty();
		Cursor cursor = getReadableDatabase().query(GlobalPropertyTable.TABLE_GLOBAL_PROPERTY, COLUMNS, 
				null, null, null, null, null);
		if (cursor.moveToFirst()) {
			gb.setCurrencyCode(cursor.getString(cursor.getColumnIndex(GlobalPropertyTable.COLUMN_CURRENCY_CODE)));
			gb.setCurrencySymbol(cursor.getString(cursor.getColumnIndex(GlobalPropertyTable.COLUMN_CURRENCY_SYMBOL)));
			gb.setCurrencyName(cursor.getString(cursor.getColumnIndex(GlobalPropertyTable.COLUMN_CURRENCY_NAME)));
			gb.setCurrencyFormat(cursor.getString(cursor.getColumnIndex(GlobalPropertyTable.COLUMN_CURRENCY_FORMAT)));
			gb.setDateFormat(cursor.getString(cursor.getColumnIndex(GlobalPropertyTable.COLUMN_DATE_FORMAT)));
			gb.setTimeFormat(cursor.getString(cursor.getColumnIndex(GlobalPropertyTable.COLUMN_TIME_FORMAT)));
			gb.setQtyFormat(cursor.getString(cursor.getColumnIndex(GlobalPropertyTable.COLUMN_QTY_FORMAT)));
			gb.setTotalDiscountRoundType(cursor.getInt(cursor.getColumnIndex(GlobalPropertyTable.COLUMN_TOTAL_DISCOUNT_ROUND_TYPE)));
			cursor.moveToNext();
		}
		cursor.close();
		return gb;
	}

	public void insertProperty(List<com.synature.pos.GlobalProperty> globalLst) throws SQLException{
		getWritableDatabase().beginTransaction();
		try {
			getWritableDatabase().delete(GlobalPropertyTable.TABLE_GLOBAL_PROPERTY, null, null);
			for (com.synature.pos.GlobalProperty global : globalLst) {
				ContentValues cv = new ContentValues();
				cv.put(GlobalPropertyTable.COLUMN_CURRENCY_SYMBOL, global.getCurrencySymbol());
				cv.put(GlobalPropertyTable.COLUMN_CURRENCY_CODE, global.getCurrencyCode());
				cv.put(GlobalPropertyTable.COLUMN_CURRENCY_NAME, global.getCurrencyName());
				cv.put(GlobalPropertyTable.COLUMN_CURRENCY_FORMAT, global.getCurrencyFormat());
				cv.put(GlobalPropertyTable.COLUMN_DATE_FORMAT, global.getDateFormat());
				cv.put(GlobalPropertyTable.COLUMN_TIME_FORMAT, global.getTimeFormat());
				cv.put(GlobalPropertyTable.COLUMN_QTY_FORMAT, global.getQtyFormat());
				cv.put(GlobalPropertyTable.COLUMN_TOTAL_DISCOUNT_ROUND_TYPE, global.getTotalDiscountRoundType());
				getWritableDatabase().insertOrThrow(GlobalPropertyTable.TABLE_GLOBAL_PROPERTY, null, cv);
			}
			getWritableDatabase().setTransactionSuccessful();
		} finally {
			getWritableDatabase().endTransaction();
		}
	}
	
	private SimpleDateFormat getSimpleDateFormat(){
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Utils.getLocale(mContext));
		return dateFormat;
	}
	
	private SimpleDateFormat getSimpleDateTimeFormat(){
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Utils.getLocale(mContext));
		return dateFormat;
	}
	
	private SimpleDateFormat getSimpleTimeFormat(){
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Utils.getLocale(mContext));
		return dateFormat;
	}
	
	private NumberFormat getNumberFormat(){
		NumberFormat numFormat = NumberFormat.getInstance(Utils.getLocale(mContext));
		return numFormat;
	}
	
	private DecimalFormat getDecimalFormat(){
		DecimalFormat decFormat = new DecimalFormat();
		return decFormat;
	}
}
