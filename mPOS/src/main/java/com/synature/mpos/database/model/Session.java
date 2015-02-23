package com.synature.mpos.database.model;

public class Session {
	private int sessionId;
	private String sessionDate;
	private String openDate;
	private String closeDate;
	private double openAmount;
	private double closeAmount;
	private int openStaff;
	private int closeStaff;
	private int isEndday;
	private int totalQtyReceipt;
	private double totalAmountReceipt;
	private String sessNumber;
	public int getSessionId() {
		return sessionId;
	}
	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}
	public String getSessionDate() {
		return sessionDate;
	}
	public void setSessionDate(String sessionDate) {
		this.sessionDate = sessionDate;
	}
	public String getOpenDate() {
		return openDate;
	}
	public void setOpenDate(String openDate) {
		this.openDate = openDate;
	}
	public int getOpenStaff() {
		return openStaff;
	}
	public void setOpenStaff(int openStaff) {
		this.openStaff = openStaff;
	}
	public int getCloseStaff() {
		return closeStaff;
	}
	public void setCloseStaff(int closeStaff) {
		this.closeStaff = closeStaff;
	}
	public String getCloseDate() {
		return closeDate;
	}
	public void setCloseDate(String closeDate) {
		this.closeDate = closeDate;
	}
	public double getOpenAmount() {
		return openAmount;
	}
	public void setOpenAmount(double openAmount) {
		this.openAmount = openAmount;
	}
	public double getCloseAmount() {
		return closeAmount;
	}
	public void setCloseAmount(double closeAmount) {
		this.closeAmount = closeAmount;
	}
	public int getIsEndday() {
		return isEndday;
	}
	public void setIsEndday(int isEndday) {
		this.isEndday = isEndday;
	}
	public int getTotalQtyReceipt() {
		return totalQtyReceipt;
	}
	public void setTotalQtyReceipt(int totalQtyReceipt) {
		this.totalQtyReceipt = totalQtyReceipt;
	}
	public double getTotalAmountReceipt() {
		return totalAmountReceipt;
	}
	public void setTotalAmountReceipt(double totalAmountReceipt) {
		this.totalAmountReceipt = totalAmountReceipt;
	}
	public String getSessNumber() {
		return sessNumber;
	}
	public void setSessNumber(String sessNumber) {
		this.sessNumber = sessNumber;
	}
}
