package com.synature.mpos.datasource.model;

public class Comment{
	private int commentId;
	private String commentName;
	private String commentName1;
	private String commentName2;
	private double commentQty;
	private double commentPrice;
	private double commentTotalPrice;
	private boolean isSelected;
	
	public double getCommentTotalPrice() {
		return commentTotalPrice;
	}
	public void setCommentTotalPrice(double commentTotalPrice) {
		this.commentTotalPrice = commentTotalPrice;
	}
	public double getCommentQty() {
		return commentQty;
	}
	public void setCommentQty(double commentQty) {
		this.commentQty = commentQty;
	}
	public double getCommentPrice() {
		return commentPrice;
	}
	public void setCommentPrice(double commentPrice) {
		this.commentPrice = commentPrice;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	public int getCommentId() {
		return commentId;
	}
	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}
	public String getCommentName() {
		return commentName;
	}
	public void setCommentName(String commentName) {
		this.commentName = commentName;
	}
	public String getCommentName1() {
		return commentName1;
	}
	public void setCommentName1(String commentName1) {
		this.commentName1 = commentName1;
	}
	public String getCommentName2() {
		return commentName2;
	}
	public void setCommentName2(String commentName2) {
		this.commentName2 = commentName2;
	}
}
