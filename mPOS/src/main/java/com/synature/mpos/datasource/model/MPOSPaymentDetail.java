package com.synature.mpos.datasource.model;

public class MPOSPaymentDetail {
	private int transactionId;
	private int paymentDetailId;
	private int payTypeId;
	private int creditCardTypeId;
	private String payTypeCode;
	private String payTypeName;
	private String creditCardNo;
	private double totalPay;
	private double payAmount;
	private String remark;
	public int getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
	}
	public int getPaymentDetailId() {
		return paymentDetailId;
	}
	public void setPaymentDetailId(int paymentDetailId) {
		this.paymentDetailId = paymentDetailId;
	}
	public int getPayTypeId() {
		return payTypeId;
	}
	public void setPayTypeId(int payTypeId) {
		this.payTypeId = payTypeId;
	}
	public String getCreditCardNo() {
		return creditCardNo;
	}
	public void setCreditCardNo(String creditCardNo) {
		this.creditCardNo = creditCardNo;
	}
	public double getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(double payAmount) {
		this.payAmount = payAmount;
	}
	public int getCreditCardTypeId() {
		return creditCardTypeId;
	}
	public void setCreditCardTypeId(int creditCardTypeId) {
		this.creditCardTypeId = creditCardTypeId;
	}
	public String getPayTypeCode() {
		return payTypeCode;
	}
	public void setPayTypeCode(String payTypeCode) {
		this.payTypeCode = payTypeCode;
	}
	public String getPayTypeName() {
		return payTypeName;
	}
	public void setPayTypeName(String payTypeName) {
		this.payTypeName = payTypeName;
	}
	public double getTotalPay() {
		return totalPay;
	}
	public void setTotalPay(double totalPay) {
		this.totalPay = totalPay;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
}
