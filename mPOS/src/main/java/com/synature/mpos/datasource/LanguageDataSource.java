package com.synature.mpos.datasource;

import java.util.ArrayList;
import java.util.List;

import com.synature.mpos.datasource.model.Language;
import com.synature.mpos.datasource.table.LanguageTable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

public  class LanguageDataSource extends MPOSDatabase{

	public LanguageDataSource(Context context){
		super(context);
	}
	
	public List<Language> listLanguage(){
		List<Language> langLst = null;
		Cursor cursor = getReadableDatabase().query(LanguageTable.TABLE_LANGUAGE, 
				new String[]{
					LanguageTable.COLUMN_LANG_ID,
					LanguageTable.COLUMN_LANG_CODE,
					LanguageTable.COLUMN_LANG_NAME,
				}, null, null, null, null, null);
		if(cursor.moveToFirst()){
			langLst = new ArrayList<Language>();
			do{
				Language lang = new Language();
				lang.setLangId(cursor.getInt(cursor.getColumnIndex(LanguageTable.COLUMN_LANG_ID)));
				lang.setLangCode(cursor.getString(cursor.getColumnIndex(LanguageTable.COLUMN_LANG_CODE)));
				lang.setLangName(cursor.getString(cursor.getColumnIndex(LanguageTable.COLUMN_LANG_NAME)));
				langLst.add(lang);
			}while(cursor.moveToNext());
		}
		cursor.close();
		return langLst;
	}
	
	public void insertLanguage(List<com.synature.pos.Language> langLst) throws SQLException{
		getWritableDatabase().beginTransaction();
		try {
			getWritableDatabase().delete(LanguageTable.TABLE_LANGUAGE, null, null);
			for(com.synature.pos.Language lang : langLst){
				ContentValues cv = new ContentValues();
				cv.put(LanguageTable.COLUMN_LANG_ID, lang.getLangID());
				cv.put(LanguageTable.COLUMN_LANG_NAME, lang.getLangName());
				cv.put(LanguageTable.COLUMN_LANG_CODE, lang.getLangCode());
				getWritableDatabase().insertOrThrow(LanguageTable.TABLE_LANGUAGE, null, cv);
			}
			getWritableDatabase().setTransactionSuccessful();
		} finally {
			getWritableDatabase().endTransaction();
		}
	}
}