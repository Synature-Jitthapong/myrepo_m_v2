package com.synature.mpos.database.model;

public class OrderComment extends Comment{
	private int orderCommentId;

	public int getOrderCommentId() {
		return orderCommentId;
	}

	public void setOrderCommentId(int orderCommentId) {
		this.orderCommentId = orderCommentId;
	}
}
