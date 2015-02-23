package com.synature.mpos.database;

import com.synature.mpos.Utils;
import com.synature.mpos.database.table.SyncHistoryTable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class SyncHistoryDao extends MPOSDatabase{
	
	public static final int SYNC_STATUS_SUCCESS = 1;
	
	public static final int SYNC_STATUS_FAIL = 0;
	
	public SyncHistoryDao(Context context) {
		super(context);
	}

	/**
	 * Get last success sync time
	 * @return time in millisecond
	 */
	public String getLastSyncTime(){
		String time = "";
		Cursor cursor = getReadableDatabase().query(
				SyncHistoryTable.TABLE_SYNC_HISTORY, 
				new String[]{
					SyncHistoryTable.COLUMN_SYNC_TIME	
				}, SyncHistoryTable.COLUMN_SYNC_STATUS + "=?", 
				new String[]{
					String.valueOf(SYNC_STATUS_SUCCESS)
				}, null, null, SyncHistoryTable.COLUMN_SYNC_TIME + " desc ", "1");
		if(cursor.moveToFirst()){
			time = cursor.getString(0);
		}
		cursor.close();
		return time;
	}
	
	/**
	 * @return true if all sync_status = 1
	 */
	public boolean IsAlreadySync(){
		boolean isSync = false;
		Cursor cursor = getReadableDatabase().rawQuery(
				"SELECT COUNT(*) "
				+ " FROM " + SyncHistoryTable.TABLE_SYNC_HISTORY
				+ " WHERE " + SyncHistoryTable.COLUMN_SYNC_DATE + "=?"
				+ " AND " + SyncHistoryTable.COLUMN_SYNC_STATUS + "=?",
				new String[]{
						String.valueOf(Utils.getDate().getTimeInMillis()),
						String.valueOf(SYNC_STATUS_SUCCESS)
				});
		if(cursor.moveToFirst()){
			if(cursor.getInt(0) > 0)
				isSync = true;
		}
		cursor.close();
		return isSync;
	}
	
	/**
	 * @param status
	 */
	public void updateSyncStatus(int status){
		ContentValues cv = new ContentValues();
		cv.put(SyncHistoryTable.COLUMN_SYNC_STATUS, status);
		cv.put(SyncHistoryTable.COLUMN_SYNC_TIME, Utils.getCalendar().getTimeInMillis());
		getWritableDatabase().update(SyncHistoryTable.TABLE_SYNC_HISTORY, 
				cv, SyncHistoryTable.COLUMN_SYNC_DATE + "=?", 
				new String[]{
					String.valueOf(Utils.getDate().getTimeInMillis())
				});
	}
	
	public void insertSyncLog(){
		if(!IsAlreadySync()){
			deleteSyncLog();
			ContentValues cv = new ContentValues();
			cv.put(SyncHistoryTable.COLUMN_SYNC_DATE, Utils.getDate().getTimeInMillis());
			cv.put(SyncHistoryTable.COLUMN_SYNC_TIME, Utils.getCalendar().getTimeInMillis());
			getWritableDatabase().insert(SyncHistoryTable.TABLE_SYNC_HISTORY, null, cv);
		}
	}
	
	public void deleteSyncLog(){
		getWritableDatabase().delete(SyncHistoryTable.TABLE_SYNC_HISTORY, null, null);
	}
}
