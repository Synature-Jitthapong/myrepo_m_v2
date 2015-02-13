package com.synature.mpos.datasource.table;

import android.database.sqlite.SQLiteDatabase;

public class StaffTable {

	public static final String TABLE_STAFF = "Staffs";
	public static final String COLUMN_STAFF_ID = "staff_id";
	public static final String COLUMN_STAFF_CODE = "staff_code";
	public static final String COLUMN_STAFF_NAME = "staff_name";
	public static final String COLUMN_STAFF_PASS = "staff_password";

	private static final String SQL_CREATE =
			" create table " + TABLE_STAFF + " ( " 
			+ COLUMN_STAFF_ID + " integer not null, " 
			+ COLUMN_STAFF_CODE + " text not null, " 
			+ COLUMN_STAFF_NAME + " text not null, " 
			+ COLUMN_STAFF_PASS + " text not null, " 
			+ StaffPermissionTable.COLUMN_STAFF_ROLE_ID + " integer default 0, "
			+ " primary key (" + COLUMN_STAFF_ID + "));";

	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE_STAFF);
		onCreate(db);
	}
}