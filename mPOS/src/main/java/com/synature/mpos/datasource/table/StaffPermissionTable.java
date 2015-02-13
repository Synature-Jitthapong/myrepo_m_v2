package com.synature.mpos.datasource.table;

import android.database.sqlite.SQLiteDatabase;

public class StaffPermissionTable {

	public static final String TABLE_STAFF_PERMISSION = "StaffPermission";
	public static final String COLUMN_STAFF_ROLE_ID = "staff_role_id";
	public static final String COLUMN_PERMMISSION_ITEM_ID = "permission_item_id";
	
	private static final String SQL_CREATE =
			" create table " + TABLE_STAFF_PERMISSION + " ( " 
			+ COLUMN_STAFF_ROLE_ID + " integer not null, " 
			+ COLUMN_PERMMISSION_ITEM_ID + " integer not null);";
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE_STAFF_PERMISSION);
		onCreate(db);
	}
}