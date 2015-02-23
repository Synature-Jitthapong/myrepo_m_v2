package com.synature.mpos.database.model;

import java.util.ArrayList;
import java.util.List;

public class OrderSet{
	private int transId;
	private int ordId;
	private int setGroupId;
	private int setGroupNo;
	private String setGroupName;
	private double reqAmount;
	private double reqMinAmount;
	private List<OrderSetDetail> orderSetDetail = new ArrayList<OrderSetDetail>();
	public int getTransId() {
		return transId;
	}
	public void setTransId(int transId) {
		this.transId = transId;
	}
	public int getOrdId() {
		return ordId;
	}
	public void setOrdId(int ordId) {
		this.ordId = ordId;
	}
	public int getSetGroupId() {
		return setGroupId;
	}
	public void setSetGroupId(int setGroupId) {
		this.setGroupId = setGroupId;
	}
	public int getSetGroupNo() {
		return setGroupNo;
	}
	public void setSetGroupNo(int setGroupNo) {
		this.setGroupNo = setGroupNo;
	}
	public String getSetGroupName() {
		return setGroupName;
	}
	public void setSetGroupName(String setGroupName) {
		this.setGroupName = setGroupName;
	}
	public double getReqAmount() {
		return reqAmount;
	}
	public void setReqAmount(double reqAmount) {
		this.reqAmount = reqAmount;
	}
	public double getReqMinAmount() {
		return reqMinAmount;
	}
	public void setReqMinAmount(double reqMinAmount) {
		this.reqMinAmount = reqMinAmount;
	}
	public List<OrderSetDetail> getOrderSetDetail() {
		return orderSetDetail;
	}
	public void setOrderSetDetail(List<OrderSetDetail> orderSetDetail) {
		this.orderSetDetail = orderSetDetail;
	}
	public static class OrderSetDetail extends Product{
		private int orderSetId;
		private double orderSetQty;
		private double deductAmount;
		public int getOrderSetId() {
			return orderSetId;
		}
		public void setOrderSetId(int orderSetId) {
			this.orderSetId = orderSetId;
		}
		public double getOrderSetQty() {
			return orderSetQty;
		}
		public void setOrderSetQty(double orderSetQty) {
			this.orderSetQty = orderSetQty;
		}
		public double getDeductAmount() {
			return deductAmount;
		}
		public void setDeductAmount(double deductAmount) {
			this.deductAmount = deductAmount;
		}
	}
}
