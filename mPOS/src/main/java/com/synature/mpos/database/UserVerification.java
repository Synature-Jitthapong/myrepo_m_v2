package com.synature.mpos.database;

import com.synature.mpos.database.table.StaffPermissionTable;
import com.synature.mpos.database.table.StaffTable;
import com.synature.util.EncryptSHA1;

import android.content.Context;
import android.database.Cursor;

public class UserVerification extends MPOSDatabase{
	
	private String mUser;
	private String mPassEncrypt;
	
	public UserVerification(Context context, String user, String pass) {
		super(context);
		mUser = user;
		EncryptSHA1 encrypt = new EncryptSHA1();
		mPassEncrypt = encrypt.sha1(pass);
	}
	
	public boolean checkUser(){
		boolean isFound = false;
		Cursor cursor = getReadableDatabase().query(StaffTable.TABLE_STAFF, 
				new String[]{StaffTable.COLUMN_STAFF_CODE}, 
				StaffTable.COLUMN_STAFF_CODE + "=?", 
				new String[]{mUser}, null, null, null);
		if(cursor.moveToFirst()){
			isFound = true;
		}
		cursor.close();
		return isFound;
	}
	
	public com.synature.pos.Staff checkLogin() {
		com.synature.pos.Staff s = null;
		Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " 
				+ StaffTable.TABLE_STAFF
				+ " WHERE " + StaffTable.COLUMN_STAFF_CODE + "=?" 
				+ " AND " + StaffTable.COLUMN_STAFF_PASS + "=?", 
				new String[]{mUser, mPassEncrypt});
		if(cursor.moveToFirst()){
			s = new com.synature.pos.Staff();
			s.setStaffID(cursor.getInt(cursor.getColumnIndex(StaffTable.COLUMN_STAFF_ID)));
			s.setStaffCode(cursor.getString(cursor.getColumnIndex(StaffTable.COLUMN_STAFF_CODE)));
			s.setStaffName(cursor.getString(cursor.getColumnIndex(StaffTable.COLUMN_STAFF_NAME)));
			s.setStaffRoleID(cursor.getInt(cursor.getColumnIndex(StaffPermissionTable.COLUMN_STAFF_ROLE_ID)));
			cursor.moveToNext();
		}
		cursor.close();
		return s;
	}
}
