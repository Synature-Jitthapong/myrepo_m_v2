package com.synature.pos;

public class PayType {
	private int PayTypeID;
	private String PayTypeCode;
	private String PayTypeName;
	private int DocumentTypeID;
	private String DocumentTypeHeader;
	public int getDocumentTypeID() {
		return DocumentTypeID;
	}
	public void setDocumentTypeID(int documentTypeID) {
		DocumentTypeID = documentTypeID;
	}
	public String getDocumentTypeHeader() {
		return DocumentTypeHeader;
	}
	public void setDocumentTypeHeader(String documentTypeHeader) {
		DocumentTypeHeader = documentTypeHeader;
	}
	private int Ordering;
	public int getPayTypeID() {
		return PayTypeID;
	}
	public void setPayTypeID(int payTypeID) {
		PayTypeID = payTypeID;
	}
	public String getPayTypeCode() {
		return PayTypeCode;
	}
	public void setPayTypeCode(String payTypeCode) {
		PayTypeCode = payTypeCode;
	}
	public String getPayTypeName() {
		return PayTypeName;
	}
	public void setPayTypeName(String payTypeName) {
		PayTypeName = payTypeName;
	}
	public int getOrdering() {
		return Ordering;
	}
	public void setOrdering(int ordering) {
		Ordering = ordering;
	}
}
