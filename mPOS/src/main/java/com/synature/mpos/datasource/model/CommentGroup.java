package com.synature.mpos.datasource.model;

public class CommentGroup{
	private int commentGroupId;
	private String commentGroupName;
	
	public int getCommentGroupId() {
		return commentGroupId;
	}
	public void setCommentGroupId(int commentGroupId) {
		this.commentGroupId = commentGroupId;
	}
	public String getCommentGroupName() {
		return commentGroupName;
	}
	public void setCommentGroupName(String commentGroupName) {
		this.commentGroupName = commentGroupName;
	}
	
	@Override
	public String toString() {
		return commentGroupName;
	}
}
