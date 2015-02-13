package com.synature.pos;

public class PromotionPriceGroup{
	private int PriceGroupID;
    private int PromotionTypeID;
    private String PromotionCode;
    private String PromotionName;
    private String ButtonName;				// if this not set use PromotionName
    private String CouponHeader;
    private String PriceFromDate;
    private String PriceToDate;
    private String PriceFromTime;
    private String PriceToTime;
    private String PromotionWeekly;
    private String PromotionMonthly;
    private int IsAllowUseOtherPromotion;
    private double VoucherAmount;
    private double OverPrice; 				// if price over this allow discount
    private int PromotionAmountType;
    
	public int getPriceGroupID() {
		return PriceGroupID;
	}
	public void setPriceGroupID(int priceGroupID) {
		PriceGroupID = priceGroupID;
	}
	public int getPromotionTypeID() {
		return PromotionTypeID;
	}
	public void setPromotionTypeID(int promotionTypeID) {
		PromotionTypeID = promotionTypeID;
	}
	public String getPromotionCode() {
		return PromotionCode;
	}
	public void setPromotionCode(String promotionCode) {
		PromotionCode = promotionCode;
	}
	public String getPromotionName() {
		return PromotionName;
	}
	public void setPromotionName(String promotionName) {
		PromotionName = promotionName;
	}
	public String getButtonName() {
		return ButtonName;
	}
	public void setButtonName(String buttonName) {
		ButtonName = buttonName;
	}
	public String getCouponHeader() {
		return CouponHeader;
	}
	public void setCouponHeader(String couponHeader) {
		CouponHeader = couponHeader;
	}
	public String getPriceFromDate() {
		return PriceFromDate;
	}
	public void setPriceFromDate(String priceFromDate) {
		PriceFromDate = priceFromDate;
	}
	public String getPriceToDate() {
		return PriceToDate;
	}
	public void setPriceToDate(String priceToDate) {
		PriceToDate = priceToDate;
	}
	public String getPriceFromTime() {
		return PriceFromTime;
	}
	public void setPriceFromTime(String priceFromTime) {
		PriceFromTime = priceFromTime;
	}
	public String getPriceToTime() {
		return PriceToTime;
	}
	public void setPriceToTime(String priceToTime) {
		PriceToTime = priceToTime;
	}
	public String getPromotionWeekly() {
		return PromotionWeekly;
	}
	public void setPromotionWeekly(String promotionWeekly) {
		PromotionWeekly = promotionWeekly;
	}
	public String getPromotionMonthly() {
		return PromotionMonthly;
	}
	public void setPromotionMonthly(String promotionMonthly) {
		PromotionMonthly = promotionMonthly;
	}
	public int getIsAllowUseOtherPromotion() {
		return IsAllowUseOtherPromotion;
	}
	public void setIsAllowUseOtherPromotion(int isAllowUseOtherPromotion) {
		IsAllowUseOtherPromotion = isAllowUseOtherPromotion;
	}
	public double getVoucherAmount() {
		return VoucherAmount;
	}
	public void setVoucherAmount(double voucherAmount) {
		VoucherAmount = voucherAmount;
	}
	public double getOverPrice() {
		return OverPrice;
	}
	public void setOverPrice(double overPrice) {
		OverPrice = overPrice;
	}
	public int getPromotionAmountType() {
		return PromotionAmountType;
	}
	public void setPromotionAmountType(int promotionAmountType) {
		PromotionAmountType = promotionAmountType;
	}
}
