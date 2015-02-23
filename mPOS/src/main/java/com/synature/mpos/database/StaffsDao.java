package com.synature.mpos.database;

import java.util.List;

import com.synature.mpos.database.table.StaffPermissionTable;
import com.synature.mpos.database.table.StaffTable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

public class StaffsDao extends MPOSDatabase{
	
	public static final int VOID_PERMISSION = 30;
	public static final int ACCESS_POS_PERMISSION = 36;
	public static final int OTHER_DISCOUNT_PERMISSION = 134; 
	public static final int VIEW_REPORT_PERMISSION = 180;
			
	public StaffsDao(Context context) {
		super(context);
	}

	public int countStaffs(){
		int totalStaff = 0;
		Cursor cursor = getReadableDatabase().rawQuery(
				"SELECT COUNT(*) FROM " + StaffTable.TABLE_STAFF, 
				null);
		if(cursor.moveToFirst()){
			totalStaff = cursor.getInt(0);
		}
		return totalStaff;
	}
	
	/**
	 * @param roleId
	 * @return true if have permits
	 */
	public boolean checkOtherDiscountPermission(int roleId){
		boolean isPermis = false;
		String selection = StaffPermissionTable.COLUMN_STAFF_ROLE_ID + "=?";
		String[] selectionArgs = {
				String.valueOf(roleId)
		};
		Cursor cursor = queryStaffPermission(selection, selectionArgs);
		if(cursor.moveToFirst()){
			do{
				int itemId = cursor.getInt(cursor.getColumnIndex(StaffPermissionTable.COLUMN_PERMMISSION_ITEM_ID));
				if(itemId == OTHER_DISCOUNT_PERMISSION)
					isPermis = true;
			}while(cursor.moveToNext());
		}
		cursor.close();
		return isPermis;
	}
	
	/**
	 * @param roleId
	 * @return true if permits
	 */
	public boolean checkAccessPOSPermission(int roleId){
		boolean isPermis = false;
		String selection = StaffPermissionTable.COLUMN_STAFF_ROLE_ID + "=?";
		String[] selectionArgs = {
				String.valueOf(roleId)
		};
		Cursor cursor = queryStaffPermission(selection, selectionArgs);
		if(cursor.moveToFirst()){
			do{
				int itemId = cursor.getInt(cursor.getColumnIndex(StaffPermissionTable.COLUMN_PERMMISSION_ITEM_ID));
				if(itemId == ACCESS_POS_PERMISSION)
					isPermis = true;
			}while(cursor.moveToNext());
		}
		cursor.close();
		return isPermis;
	}
	
	/**
	 * Check void permission
	 * @param roleId
	 * @return true if have permits
	 */
	public boolean checkVoidPermission(int roleId){
		boolean isPermis = false;
		String selection = StaffPermissionTable.COLUMN_STAFF_ROLE_ID + "=?";
		String[] selectionArgs = {
				String.valueOf(roleId)
		};
		Cursor cursor = queryStaffPermission(selection, selectionArgs);
		if(cursor.moveToFirst()){
			do{
				int itemId = cursor.getInt(cursor.getColumnIndex(StaffPermissionTable.COLUMN_PERMMISSION_ITEM_ID));
				if(itemId == VOID_PERMISSION)
					isPermis = true;
			}while(cursor.moveToNext());
		}
		cursor.close();
		return isPermis;
	}
	
	private Cursor queryStaffPermission(String selection, String[] selectionArgs){
		return getReadableDatabase().query(
				StaffPermissionTable.TABLE_STAFF_PERMISSION, 
				new String[]{
						StaffPermissionTable.COLUMN_PERMMISSION_ITEM_ID
				}, selection, selectionArgs, null, null, null);
	}
	
	/**
	 * @param staffId
	 * @return com.synature.pos.Staff
	 */
	public com.synature.pos.Staff getStaff(int staffId){
		com.synature.pos.Staff s = null;
		Cursor cursor = getReadableDatabase().query(StaffTable.TABLE_STAFF, 
				new String[]{
					StaffTable.COLUMN_STAFF_CODE, 
					StaffTable.COLUMN_STAFF_NAME,
					StaffPermissionTable.COLUMN_STAFF_ROLE_ID
				}, 
				StaffTable.COLUMN_STAFF_ID + "=?", 
				new String[]{
					String.valueOf(staffId)
				}, null, null, null);
		if(cursor.moveToFirst()){
			s = new com.synature.pos.Staff();
			s.setStaffCode(cursor.getString(cursor.getColumnIndex(StaffTable.COLUMN_STAFF_CODE)));
			s.setStaffName(cursor.getString(cursor.getColumnIndex(StaffTable.COLUMN_STAFF_NAME)));
			s.setStaffRoleID(cursor.getInt(cursor.getColumnIndex(StaffPermissionTable.COLUMN_STAFF_ROLE_ID)));
		}
		cursor.close();
		return s;
	}
	
	/**
	 * @param spl
	 * @throws SQLException
	 */
	public void insertStaffPermission(List<com.synature.pos.StaffPermission> spl) throws SQLException{
		getWritableDatabase().beginTransaction();
		try{
			getWritableDatabase().delete(StaffPermissionTable.TABLE_STAFF_PERMISSION, null, null);
			for(com.synature.pos.StaffPermission sp : spl){
				ContentValues cv = new ContentValues();
				cv.put(StaffPermissionTable.COLUMN_STAFF_ROLE_ID, sp.getStaffRoleID());
				cv.put(StaffPermissionTable.COLUMN_PERMMISSION_ITEM_ID, sp.getPermissionItemID());
				getWritableDatabase().insertOrThrow(StaffPermissionTable.TABLE_STAFF_PERMISSION, null, cv);
			}
			getWritableDatabase().setTransactionSuccessful();
		}finally{
			getWritableDatabase().endTransaction();
		}
	}
	
	/**
	 * @param staffLst
	 * @throws SQLException
	 */
	public void insertStaff(List<com.synature.pos.Staff> staffLst) throws SQLException{
		getWritableDatabase().beginTransaction();
		try {
			getWritableDatabase().delete(StaffTable.TABLE_STAFF, null, null);
			for(com.synature.pos.Staff staff : staffLst){
				ContentValues cv = new ContentValues();
				cv.put(StaffTable.COLUMN_STAFF_ID, staff.getStaffID());
				cv.put(StaffTable.COLUMN_STAFF_CODE, staff.getStaffCode());
				cv.put(StaffTable.COLUMN_STAFF_NAME, staff.getStaffName());
				cv.put(StaffTable.COLUMN_STAFF_PASS, staff.getStaffPassword());
				cv.put(StaffPermissionTable.COLUMN_STAFF_ROLE_ID, staff.getStaffRoleID());
				getWritableDatabase().insertOrThrow(StaffTable.TABLE_STAFF, null, cv);
			}
			getWritableDatabase().setTransactionSuccessful();
		} finally{
			getWritableDatabase().endTransaction();
		}
	}
}
