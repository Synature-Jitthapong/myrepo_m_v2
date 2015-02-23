package com.synature.mpos.database;

import java.util.List;

import com.synature.mpos.database.table.ComputerTable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.text.TextUtils;

public class ComputerDao extends MPOSDatabase{

	public ComputerDao(Context context) {
		super(context);
	}

	public int getReceiptHasCopy(){
		return getComputerProperty().getPrintReceiptHasCopy();
	}
	
	public int getComputerId(){
		return getComputerProperty().getComputerID();
	}
	
	public boolean checkIsMainComputer(int computerId){
		boolean isMainComputer = false;
		if(getComputerProperty().getIsMainComputer() != 0)
			isMainComputer = true;
		return isMainComputer;
	}
	
	public String getReceiptHeader(){
		String receiptHeader = getComputerProperty().getDocumentTypeHeader();
		return TextUtils.isEmpty(receiptHeader) ? "" : receiptHeader;
	}
	
	public com.synature.pos.ComputerProperty getComputerProperty() {
		com.synature.pos.ComputerProperty comp = 
				new com.synature.pos.ComputerProperty();
		Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + ComputerTable.TABLE_COMPUTER, null);
		if (cursor.moveToFirst()) {
			comp.setComputerID(cursor.getInt(cursor.getColumnIndex(ComputerTable.COLUMN_COMPUTER_ID)));
			comp.setComputerName(cursor.getString(cursor.getColumnIndex(ComputerTable.COLUMN_COMPUTER_NAME)));
			comp.setDeviceCode(cursor.getString(cursor.getColumnIndex(ComputerTable.COLUMN_DEVICE_CODE)));
			comp.setIsMainComputer(cursor.getInt(cursor.getColumnIndex(ComputerTable.COLUMN_IS_MAIN_COMPUTER)));
			comp.setRegistrationNumber(cursor.getString(cursor.getColumnIndex(ComputerTable.COLUMN_REGISTER_NUMBER)));
			comp.setDocumentTypeHeader(cursor.getString(cursor.getColumnIndex(ComputerTable.COLUMN_DOC_TYPE_HEADER)));
			comp.setPrintReceiptHasCopy(cursor.getInt(cursor.getColumnIndex(ComputerTable.COLUMN_PRINT_RECEIPT_HAS_COPY)));
			cursor.moveToNext();
		}
		cursor.close();
		return comp;
	}

	public void insertComputer(List<com.synature.pos.ComputerProperty> compLst) throws SQLException{
		getWritableDatabase().beginTransaction();
		try {
			getWritableDatabase().delete(ComputerTable.TABLE_COMPUTER, null, null);
			for (com.synature.pos.ComputerProperty comp : compLst) {
				ContentValues cv = new ContentValues();
				cv.put(ComputerTable.COLUMN_COMPUTER_ID, comp.getComputerID());
				cv.put(ComputerTable.COLUMN_COMPUTER_NAME, comp.getComputerName());
				cv.put(ComputerTable.COLUMN_DEVICE_CODE, comp.getDeviceCode());
				cv.put(ComputerTable.COLUMN_REGISTER_NUMBER, comp.getRegistrationNumber());
				cv.put(ComputerTable.COLUMN_IS_MAIN_COMPUTER, comp.getIsMainComputer());
				cv.put(ComputerTable.COLUMN_DOC_TYPE_HEADER, comp.getDocumentTypeHeader());
				cv.put(ComputerTable.COLUMN_PRINT_RECEIPT_HAS_COPY, comp.getPrintReceiptHasCopy());
				getWritableDatabase().insertOrThrow(ComputerTable.TABLE_COMPUTER, null, cv);
			}
			getWritableDatabase().setTransactionSuccessful();
		} finally {
			getWritableDatabase().endTransaction();
		}
	}
}
