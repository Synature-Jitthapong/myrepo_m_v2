package com.synature.mpos.database;

import com.synature.mpos.database.table.OrderDetailTable;
import com.synature.mpos.database.table.OrderTransTable;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class OrderTransWasteDao{

	private SQLiteDatabase mDatabase;
	
	public OrderTransWasteDao(SQLiteDatabase db){
		mDatabase = db;
	}
	
	protected void moveToRealTable(int transactionId) throws SQLException{
		String where = OrderTransTable.COLUMN_TRANS_ID + "=" + transactionId;
		mDatabase.execSQL("insert into " + OrderTransTable.TABLE_ORDER_TRANS_WASTE
				+ " select * from " + OrderTransTable.TEMP_ORDER_TRANS
				+ " where " + where);
		mDatabase.execSQL("insert into " + OrderDetailTable.TABLE_ORDER_WASTE
				+ " select * from " + OrderDetailTable.TEMP_ORDER
				+ " where " + where);
		mDatabase.execSQL("delete from " + OrderTransTable.TEMP_ORDER_TRANS
				+ " where " + where);
		mDatabase.execSQL("delete from " + OrderDetailTable.TEMP_ORDER
				+ " where " + where);
	}
}
