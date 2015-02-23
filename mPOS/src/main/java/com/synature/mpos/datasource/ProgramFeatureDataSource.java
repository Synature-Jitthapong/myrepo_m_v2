package com.synature.mpos.datasource;

import java.util.List;

import com.synature.mpos.datasource.table.ProgramFeatureTable;
import com.synature.pos.ProgramFeature;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

public class ProgramFeatureDataSource extends MPOSDatabase{

	public static final String[] PROGRAM_FEATURE_COLUMNS = {
		ProgramFeatureTable.COLUMN_FEATURE_ID,
		ProgramFeatureTable.COLUMN_FEATURE_NAME,
		ProgramFeatureTable.COLUMN_FEATURE_VALUE,
		ProgramFeatureTable.COLUMN_FEATURE_TEXT,
		ProgramFeatureTable.COLUMN_FEATURE_DESC
	};
	
	public ProgramFeatureDataSource(Context context) {
		super(context);
	}
	
	/**
	 * @param featureId
	 * @return ProgramFeature Object null if not have records.
	 */
	public ProgramFeature getProgramFeature(int featureId){
		ProgramFeature feature = null;
		Cursor cursor = null;
		try {
			cursor = getReadableDatabase().query(
					ProgramFeatureTable.PROGRAM_FEATURE_TABLE, 
					PROGRAM_FEATURE_COLUMNS, 
					ProgramFeatureTable.COLUMN_FEATURE_ID + "=?", 
					new String[]{
						String.valueOf(featureId)
					}, null, null, null);
			if(cursor.moveToFirst()){
				feature = new ProgramFeature();
				feature.setFeatureID(cursor.getInt(cursor.getColumnIndex(ProgramFeatureTable.COLUMN_FEATURE_ID)));
				feature.setFeatureName(cursor.getString(cursor.getColumnIndex(ProgramFeatureTable.COLUMN_FEATURE_NAME)));
				feature.setFeatureValue(cursor.getInt(cursor.getColumnIndex(ProgramFeatureTable.COLUMN_FEATURE_VALUE)));
				feature.setFeatureText(cursor.getString(cursor.getColumnIndex(ProgramFeatureTable.COLUMN_FEATURE_TEXT)));
				feature.setFeatureDesc(cursor.getString(cursor.getColumnIndex(ProgramFeatureTable.COLUMN_FEATURE_DESC)));
			}
		} finally {
			if(cursor != null)
				cursor.close();
		}
		return feature;
	}
	
	/**
	 * @param featureLst
	 * @throws SQLException
	 */
	public void insertProgramFeature(List<ProgramFeature> featureLst) throws SQLException{
		getWritableDatabase().beginTransaction();
		try{
			getWritableDatabase().delete(ProgramFeatureTable.PROGRAM_FEATURE_TABLE, null, null);
			for(ProgramFeature feature : featureLst){
				ContentValues cv = new ContentValues();
				cv.put(ProgramFeatureTable.COLUMN_FEATURE_ID, feature.getFeatureID());
				cv.put(ProgramFeatureTable.COLUMN_FEATURE_NAME, feature.getFeatureName());
				cv.put(ProgramFeatureTable.COLUMN_FEATURE_VALUE, feature.getFeatureValue());
				cv.put(ProgramFeatureTable.COLUMN_FEATURE_TEXT, feature.getFeatureText());
				cv.put(ProgramFeatureTable.COLUMN_FEATURE_DESC, feature.getFeatureDesc());
				getWritableDatabase().insertOrThrow(ProgramFeatureTable.PROGRAM_FEATURE_TABLE, null, cv);
			}
			getWritableDatabase().setTransactionSuccessful();
		}finally{
			getWritableDatabase().endTransaction();
		}
	}
}
