package com.synature.mpos.database.model;

public class Product{
	private int productId;
	private int productGroupId;
	private int productDeptId;
	private String productCode;
	private String productBarCode;
	private String productName;
	private String productName1;
	private String productName2;
	private int productTypeId;
	private double productPrice;
	private String productUnitName;
	private String productDesc;
	private int discountAllow;
	private int vatType;
	private double vatRate;
	private int hasServiceCharge;
	private String imgName;
	private int saleMode;
	
	public int getSaleMode() {
		return saleMode;
	}
	public void setSaleMode(int saleMode) {
		this.saleMode = saleMode;
	}
	public int getProductGroupId() {
		return productGroupId;
	}
	public void setProductGroupId(int productGroupId) {
		this.productGroupId = productGroupId;
	}
	public int getProductDeptId() {
		return productDeptId;
	}
	public void setProductDeptId(int productDeptId) {
		this.productDeptId = productDeptId;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getProductBarCode() {
		return productBarCode;
	}
	public void setProductBarCode(String productBarCode) {
		this.productBarCode = productBarCode;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductName1() {
		return productName1;
	}
	public void setProductName1(String productName1) {
		this.productName1 = productName1;
	}
	public String getProductName2() {
		return productName2;
	}
	public void setProductName2(String productName2) {
		this.productName2 = productName2;
	}
	public int getProductTypeId() {
		return productTypeId;
	}
	public void setProductTypeId(int productTypeId) {
		this.productTypeId = productTypeId;
	}
	public double getProductPrice() {
		return productPrice;
	}
	public void setProductPrice(double productPrice) {
		this.productPrice = productPrice;
	}
	public String getProductUnitName() {
		return productUnitName;
	}
	public void setProductUnitName(String productUnitName) {
		this.productUnitName = productUnitName;
	}
	public String getProductDesc() {
		return productDesc;
	}
	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}
	public int getDiscountAllow() {
		return discountAllow;
	}
	public void setDiscountAllow(int discountAllow) {
		this.discountAllow = discountAllow;
	}
	public int getVatType() {
		return vatType;
	}
	public void setVatType(int vatType) {
		this.vatType = vatType;
	}
	public double getVatRate() {
		return vatRate;
	}
	public void setVatRate(double vatRate) {
		this.vatRate = vatRate;
	}
	public int getHasServiceCharge() {
		return hasServiceCharge;
	}
	public void setHasServiceCharge(int hasServiceCharge) {
		this.hasServiceCharge = hasServiceCharge;
	}
	public String getImgName() {
		return imgName;
	}
	public void setImgName(String imgName) {
		this.imgName = imgName;
	}
	@Override
	public String toString() {
		return productName;
	}
}
