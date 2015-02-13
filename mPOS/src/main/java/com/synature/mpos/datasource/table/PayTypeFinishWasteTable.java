package com.synature.mpos.datasource.table;

import android.database.sqlite.SQLiteDatabase;

public class PayTypeFinishWasteTable extends BaseColumn{
	
	public static final String TABLE_PAY_TYPE_FINISH_WASTE = "PayTypeFinishWaste";
	
	private static final String SQL_CREATE = 
			"create table " + TABLE_PAY_TYPE_FINISH_WASTE + " ("
					+ PayTypeTable.COLUMN_PAY_TYPE_ID + " integer,"
					+ PayTypeTable.COLUMN_PAY_TYPE_CODE + " text, "
					+ PayTypeTable.COLUMN_PAY_TYPE_NAME + " text, "
					+ COLUMN_DOCUMENT_TYPE_ID + " integer, "
					+ COLUMN_DOCUMENT_TYPE_HEADER + " text, "
					+ COLUMN_ORDERING + " integer default 0);";
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE_PAY_TYPE_FINISH_WASTE);
		onCreate(db);
	}
}
