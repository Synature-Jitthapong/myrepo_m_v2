package com.synature.mpos.datasource;

import java.util.ArrayList;
import java.util.List;

import com.synature.mpos.datasource.table.HeaderFooterReceiptTable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

public class HeaderFooterReceiptDataSource extends MPOSDatabase{
	
	public static final int HEADER_LINE_TYPE = 0;
	public static final int FOOTER_LINE_TYPE = 1;

	public HeaderFooterReceiptDataSource(Context context) {
		super(context);
	}
	
	public List<com.synature.pos.HeaderFooterReceipt> listHeaderFooter(int lineType){
		List<com.synature.pos.HeaderFooterReceipt> hfLst = 
				new ArrayList<com.synature.pos.HeaderFooterReceipt>();

		Cursor cursor = getReadableDatabase().query(HeaderFooterReceiptTable.TABLE_HEADER_FOOTER_RECEIPT, 
				new String[]{HeaderFooterReceiptTable.COLUMN_TEXT_IN_LINE, 
				HeaderFooterReceiptTable.COLUMN_LINE_TYPE, 
				HeaderFooterReceiptTable.COLUMN_LINE_ORDER}, 
				HeaderFooterReceiptTable.COLUMN_LINE_TYPE + "=?", 
				new String[]{String.valueOf(lineType)}, null, null, 
				HeaderFooterReceiptTable.COLUMN_LINE_ORDER);
		if(cursor.moveToFirst()){
			do{
				com.synature.pos.HeaderFooterReceipt hf = new com.synature.pos.HeaderFooterReceipt();
				hf.setTextInLine(cursor.getString(cursor.getColumnIndex(HeaderFooterReceiptTable.COLUMN_TEXT_IN_LINE)));
				hf.setLineType(cursor.getInt(cursor.getColumnIndex(HeaderFooterReceiptTable.COLUMN_LINE_TYPE)));
				hf.setLineOrder(cursor.getInt(cursor.getColumnIndex(HeaderFooterReceiptTable.COLUMN_LINE_ORDER)));
				hfLst.add(hf);
			}while(cursor.moveToNext());
		}
		cursor.close();

		return hfLst;
	}
	
	public void insertHeaderFooterReceipt(
		List<com.synature.pos.HeaderFooterReceipt> headerFooterLst) throws SQLException{
		getWritableDatabase().beginTransaction();
		try {
			getWritableDatabase().delete(HeaderFooterReceiptTable.TABLE_HEADER_FOOTER_RECEIPT, null, null);
			for(com.synature.pos.HeaderFooterReceipt hf : headerFooterLst){
				ContentValues cv = new ContentValues();
				cv.put(HeaderFooterReceiptTable.COLUMN_TEXT_IN_LINE, hf.getTextInLine());
				cv.put(HeaderFooterReceiptTable.COLUMN_LINE_TYPE, hf.getLineType());
				cv.put(HeaderFooterReceiptTable.COLUMN_LINE_ORDER, hf.getLineOrder());
				getWritableDatabase().insertOrThrow(HeaderFooterReceiptTable.TABLE_HEADER_FOOTER_RECEIPT, null, cv);
			}
			getWritableDatabase().setTransactionSuccessful();
		} finally {
			getWritableDatabase().endTransaction();
		}
	}
}
