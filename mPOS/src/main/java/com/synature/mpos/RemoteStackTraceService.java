package com.synature.mpos;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

public class RemoteStackTraceService extends Service{

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		final String stackTrace = intent.getStringExtra("stackTrace");
		final String logUrl = MPOSApplication.STACK_TRACE_URL;
		SimpleDateFormat dateFormat = 
				new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		final String fileName = Utils.getDeviceCode(getApplicationContext())
				+ "_" + dateFormat.format(Calendar.getInstance().getTime());
		if(!TextUtils.isEmpty(logUrl) && !TextUtils.isEmpty(fileName) && !TextUtils.isEmpty(stackTrace)){
			new Thread(new Runnable() {
	            @Override
	            public void run() {
	                HttpPost httpPost = new HttpPost(logUrl);
	                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
	                nvps.add(new BasicNameValuePair("filename", fileName));
	                nvps.add(new BasicNameValuePair("stacktrace", stackTrace));
	                try {
	                    DefaultHttpClient httpClient = new DefaultHttpClient();
	                    httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
	                    httpClient.execute(httpPost);
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }finally{
	                	stopSelf();
	                }
	            }
	        }).start();
		}else{
        	stopSelf();
		}
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
