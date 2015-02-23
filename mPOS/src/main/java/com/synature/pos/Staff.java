package com.synature.pos;

public class Staff {	
	private int StaffID;
	private int StaffRoleID;
	private String StaffCode;
	private String StaffName;
	private String StaffPassword;
	
	public int getStaffID() {
		return StaffID;
	}

	public void setStaffID(int staffID) {
		StaffID = staffID;
	}

	public String getStaffCode() {
		return StaffCode;
	}

	public void setStaffCode(String staffCode) {
		StaffCode = staffCode;
	}

	public String getStaffName() {
		return StaffName;
	}

	public void setStaffName(String staffName) {
		StaffName = staffName;
	}

	public String getStaffPassword() {
		return StaffPassword;
	}

	public void setStaffPassword(String staffPassword) {
		StaffPassword = staffPassword;
	}

	public int getStaffRoleID() {
		return StaffRoleID;
	}

	public void setStaffRoleID(int staffRoleID) {
		StaffRoleID = staffRoleID;
	}	
}
