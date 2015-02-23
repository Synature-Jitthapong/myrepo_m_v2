package com.synature.mpos;

import org.ksoap2.serialization.PropertyInfo;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class SoftwareRegister extends RegisterServiceBase{

	public static final String TAG = SoftwareRegister.class.getSimpleName();
			
	public static final String REGIST_SERVICE_URL_METHOD = "WSmPOS_GetRegisterServiceUrl";
	
	public static final String SW_VERSION_PARAM = "szSwVersion";
	public static final String DB_VERSION_PARAM = "szDbVersion";
	
	public SoftwareRegister(Context context, ResultReceiver receiver) {
		super(context, REGIST_SERVICE_URL_METHOD, receiver);
		
		mProperty = new PropertyInfo();
		mProperty.setName(SW_VERSION_PARAM);
		mProperty.setValue(Utils.getSoftWareVersion(mContext));
		mProperty.setType(String.class);
		mSoapRequest.addProperty(mProperty);
		
		mProperty = new PropertyInfo();
		mProperty.setName(DB_VERSION_PARAM);
		mProperty.setValue(MPOSApplication.DB_VERSION);
		mProperty.setType(String.class);
		mSoapRequest.addProperty(mProperty);
	}

	@Override
	protected void onPostExecute(String result) {
		Gson gson = new Gson();
		try {
			MPOSSoftwareInfo info = gson.fromJson(result, MPOSSoftwareInfo.class);
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
			if(!TextUtils.isEmpty(info.getSzRegisterServiceUrl())){
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString(SettingsActivity.KEY_PREF_SERVER_URL, info.getSzRegisterServiceUrl());
				editor.putString(SettingsActivity.KEY_PREF_EXP_DATE, info.getSzSoftwareExpireDate());
				editor.putString(SettingsActivity.KEY_PREF_LOCK_DATE, info.getSzLockExpireDate());
				
				String version = info.getSzSoftwareVersion();
				String fileUrl = info.getSzSoftwareDownloadUrl();
				if(!TextUtils.isEmpty(version) && !TextUtils.isEmpty(fileUrl)){
					if(!TextUtils.equals(version, Utils.getSoftWareVersion(mContext))){
						editor.putString(SettingsActivity.KEY_PREF_NEED_TO_UPDATE, "1");
						editor.putString(SettingsActivity.KEY_PREF_NEW_VERSION, version);
						editor.putString(SettingsActivity.KEY_PREF_FILE_URL, fileUrl);
					}
				}else{
					editor.putString(SettingsActivity.KEY_PREF_NEED_TO_UPDATE, "0");
					editor.putString(SettingsActivity.KEY_PREF_NEW_VERSION, "");
					editor.putString(SettingsActivity.KEY_PREF_FILE_URL, "");
				}
				editor.commit();
				if(mReceiver != null){
					mReceiver.send(MPOSServiceBase.RESULT_SUCCESS, null);
				}
			}else{
				if(mReceiver != null){
					Bundle b = new Bundle();
					b.putString("msg", mContext.getString(R.string.device_not_register));
					mReceiver.send(MPOSServiceBase.RESULT_ERROR, b);
				}
			}
		} catch (JsonSyntaxException e1) {
			if(mReceiver != null){
				Bundle b = new Bundle();
				b.putString("msg", result);
				mReceiver.send(MPOSServiceBase.RESULT_ERROR, b);
			}
		} catch(Exception e){
			if(mReceiver != null){
				Bundle b = new Bundle();
				b.putString("msg", TextUtils.isEmpty(result) ? "Sorry unknown error." : result);
				mReceiver.send(MPOSServiceBase.RESULT_ERROR, b);
			}
		}
	}
}
