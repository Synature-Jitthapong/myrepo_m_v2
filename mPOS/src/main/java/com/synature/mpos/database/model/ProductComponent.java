package com.synature.mpos.database.model;

public class ProductComponent extends Product{
    private int childProductId;
    private double childProductAmount;
    private double flexibleProductPrice;
    private int flexibleIncludePrice;
	
    public int getChildProductId() {
		return childProductId;
	}
	public void setChildProductId(int childProductId) {
		this.childProductId = childProductId;
	}
	public double getChildProductAmount() {
		return childProductAmount;
	}
	public void setChildProductAmount(double childProductAmount) {
		this.childProductAmount = childProductAmount;
	}
	public double getFlexibleProductPrice() {
		return flexibleProductPrice;
	}
	public void setFlexibleProductPrice(double flexibleProductPrice) {
		this.flexibleProductPrice = flexibleProductPrice;
	}
	public int getFlexibleIncludePrice() {
		return flexibleIncludePrice;
	}
	public void setFlexibleIncludePrice(int flexibleIncludePrice) {
		this.flexibleIncludePrice = flexibleIncludePrice;
	}
}