package com.synature.mpos.database.model;

public class ProductComponentGroup extends ProductComponent{
	private int groupNo;
	private String groupName;
	private double requireAmount;
	private double requireMinAmount;
	
	public double getRequireMinAmount() {
		return requireMinAmount;
	}
	public void setRequireMinAmount(double requireMinAmount) {
		this.requireMinAmount = requireMinAmount;
	}
	public int getGroupNo() {
		return groupNo;
	}
	public void setGroupNo(int groupNo) {
		this.groupNo = groupNo;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public double getRequireAmount() {
		return requireAmount;
	}
	public void setRequireAmount(double requireAmount) {
		this.requireAmount = requireAmount;
	}
}
