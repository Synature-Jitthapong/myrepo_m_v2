package com.synature.pos;

public class ComputerProperty{
	private int ComputerID;
	private String ComputerName;
	private String DeviceCode;
	private String RegistrationNumber;
	private int IsMainComputer;
	private int IsQueueOrder;
	private String DocumentTypeHeader;
	private int PrintReceiptHasCopy;
	
	public int getPrintReceiptHasCopy() {
		return PrintReceiptHasCopy;
	}
	public void setPrintReceiptHasCopy(int printReceiptHasCopy) {
		PrintReceiptHasCopy = printReceiptHasCopy;
	}
	public String getDocumentTypeHeader() {
		return DocumentTypeHeader;
	}
	public void setDocumentTypeHeader(String documentTypeHeader) {
		DocumentTypeHeader = documentTypeHeader;
	}
	public int getComputerID() {
		return ComputerID;
	}
	public void setComputerID(int computerID) {
		ComputerID = computerID;
	}
	public String getComputerName() {
		return ComputerName;
	}
	public void setComputerName(String computerName) {
		ComputerName = computerName;
	}
	public String getDeviceCode() {
		return DeviceCode;
	}
	public void setDeviceCode(String deviceCode) {
		DeviceCode = deviceCode;
	}
	public String getRegistrationNumber() {
		return RegistrationNumber;
	}
	public void setRegistrationNumber(String registrationNumber) {
		RegistrationNumber = registrationNumber;
	}
	public int getIsMainComputer() {
		return IsMainComputer;
	}
	public void setIsMainComputer(int isMainComputer) {
		IsMainComputer = isMainComputer;
	}
	public int getIsQueueOrder() {
		return IsQueueOrder;
	}
	public void setIsQueueOrder(int isQueueOrder) {
		IsQueueOrder = isQueueOrder;
	}
}
