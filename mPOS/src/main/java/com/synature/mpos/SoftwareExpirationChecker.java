package com.synature.mpos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class SoftwareExpirationChecker{

	private Context mContext;
	private SoftwareExpirationCheckerListener mListener;
	
	public SoftwareExpirationChecker(Context context, SoftwareExpirationCheckerListener listener){
		mContext = context;
		mListener = listener;
	}
	
	public void checkExpDate(){
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
		String expDate = sharedPref.getString(SettingsActivity.KEY_PREF_EXP_DATE, "");
		String lockDate = sharedPref.getString(SettingsActivity.KEY_PREF_LOCK_DATE, "");
		if(!TextUtils.isEmpty(expDate)){
			Calendar c = Calendar.getInstance();
			Calendar cExp = Calendar.getInstance();
			try {
				Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(expDate);
				cExp.setTimeInMillis(d.getTime());
			} catch (ParseException e) {}
			boolean isLocked = false;
			if(c.compareTo(cExp) >= 0){
				Calendar cLock = Calendar.getInstance();
				try {
					Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(lockDate);
					cLock.setTimeInMillis(d.getTime());
				} catch (ParseException e) {}
				if(c.compareTo(cLock) > 0){
					isLocked = true;
				}
				mListener.onExpire(cLock, isLocked);
			}else{
				mListener.onNotExpired();
			}
		}else{
			mListener.onNotExpired();
		}
	}
	
	public static interface SoftwareExpirationCheckerListener{
		void onExpire(Calendar lockDate, boolean isLocked);
		void onNotExpired();
	}
}
