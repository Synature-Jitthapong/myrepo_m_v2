package com.synature.pos;

public class GlobalProperty{
	private String CurrencySymbol;
	private String CurrencyCode;
	private String CurrencyName;
	private String CurrencyFormat;
	private String DateFormat;
	private String TimeFormat;
	private String QtyFormat;
	private String PrefixTextTW;
	private int PositionPrefix;
	private int TotalDiscountRoundType;
	
	public String getPrefixTextTW() {
		return PrefixTextTW;
	}
	public void setPrefixTextTW(String prefixTextTW) {
		PrefixTextTW = prefixTextTW;
	}
	public int getPositionPrefix() {
		return PositionPrefix;
	}
	public void setPositionPrefix(int positionPrefix) {
		PositionPrefix = positionPrefix;
	}
	public String getCurrencySymbol() {
		return CurrencySymbol;
	}
	public void setCurrencySymbol(String currencySymbol) {
		CurrencySymbol = currencySymbol;
	}
	public String getCurrencyCode() {
		return CurrencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		CurrencyCode = currencyCode;
	}
	public String getCurrencyName() {
		return CurrencyName;
	}
	public void setCurrencyName(String currencyName) {
		CurrencyName = currencyName;
	}
	public String getCurrencyFormat() {
		return CurrencyFormat;
	}
	public void setCurrencyFormat(String currencyFormat) {
		CurrencyFormat = currencyFormat;
	}
	public String getDateFormat() {
		return DateFormat;
	}
	public void setDateFormat(String dateFormat) {
		DateFormat = dateFormat;
	}
	public String getTimeFormat() {
		return TimeFormat;
	}
	public void setTimeFormat(String timeFormat) {
		TimeFormat = timeFormat;
	}
	public String getQtyFormat() {
		return QtyFormat;
	}
	public void setQtyFormat(String qtyFormat) {
		QtyFormat = qtyFormat;
	}
	public int getTotalDiscountRoundType() {
		return TotalDiscountRoundType;
	}
	public void setTotalDiscountRoundType(int totalDiscountRoundType) {
		TotalDiscountRoundType = totalDiscountRoundType;
	}
	
}
