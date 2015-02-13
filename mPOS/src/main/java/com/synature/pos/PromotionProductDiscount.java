package com.synature.pos;

public class PromotionProductDiscount{
	private int PriceGroupID;
	private int ProductID;
	private int SaleMode;
    private double DiscountAmount;
    private double DiscountPercent;
    private int AmountOrPercent;
    
	public int getPriceGroupID() {
		return PriceGroupID;
	}
	public void setPriceGroupID(int priceGroupID) {
		PriceGroupID = priceGroupID;
	}
	public int getProductID() {
		return ProductID;
	}
	public void setProductID(int productID) {
		ProductID = productID;
	}
	public int getSaleMode() {
		return SaleMode;
	}
	public void setSaleMode(int saleMode) {
		SaleMode = saleMode;
	}
	public double getDiscountAmount() {
		return DiscountAmount;
	}
	public void setDiscountAmount(double discountAmount) {
		DiscountAmount = discountAmount;
	}
	public double getDiscountPercent() {
		return DiscountPercent;
	}
	public void setDiscountPercent(double discountPercent) {
		DiscountPercent = discountPercent;
	}
	public int getAmountOrPercent() {
		return AmountOrPercent;
	}
	public void setAmountOrPercent(int amountOrPercent) {
		AmountOrPercent = amountOrPercent;
	}
}
