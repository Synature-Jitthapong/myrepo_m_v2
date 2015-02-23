package com.synature.mpos.datasource.model;

public class ProductDept{
	private int productDeptId;
	private int productGroupId;
	private String productDeptCode;
	private String productDeptName;
	private String productDeptName1;
	private String productDeptName2;
	private String productDeptName3;
	
	public int getProductDeptId() {
		return productDeptId;
	}
	public void setProductDeptId(int productDeptId) {
		this.productDeptId = productDeptId;
	}
	public int getProductGroupId() {
		return productGroupId;
	}
	public void setProductGroupId(int productGroupId) {
		this.productGroupId = productGroupId;
	}
	public String getProductDeptCode() {
		return productDeptCode;
	}
	public void setProductDeptCode(String productDeptCode) {
		this.productDeptCode = productDeptCode;
	}
	public String getProductDeptName() {
		return productDeptName;
	}
	public void setProductDeptName(String productDeptName) {
		this.productDeptName = productDeptName;
	}
	public String getProductDeptName1() {
		return productDeptName1;
	}
	public void setProductDeptName1(String productDeptName1) {
		this.productDeptName1 = productDeptName1;
	}
	public String getProductDeptName2() {
		return productDeptName2;
	}
	public void setProductDeptName2(String productDeptName2) {
		this.productDeptName2 = productDeptName2;
	}
	public String getProductDeptName3() {
		return productDeptName3;
	}
	public void setProductDeptName3(String productDeptName3) {
		this.productDeptName3 = productDeptName3;
	}
	@Override
	public String toString() {
		return productDeptName;
	}
}
