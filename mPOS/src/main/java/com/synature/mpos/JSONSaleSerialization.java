package com.synature.mpos;

import java.lang.reflect.Type;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.synature.mpos.datasource.SaleTransaction;
import com.synature.mpos.datasource.SaleTransaction.POSData_EndDaySaleTransaction;
import com.synature.mpos.datasource.SaleTransaction.POSData_SaleTransaction;
import com.synature.util.Logger;

public class JSONSaleSerialization{
	
	public static final String TAG = JSONSaleSerialization.class.getSimpleName();
	
	protected Context mContext;
	protected SaleTransaction mSaleTrans;
	
	public JSONSaleSerialization(Context context){
		mContext = context;
		mSaleTrans = new SaleTransaction(context);
	}
	
	public String generateSpecificSaleTransaction(int transactionId, String sessionDate){
		String json = null;
		try {
			Gson gson = new Gson();
			Type type = new TypeToken<POSData_SaleTransaction>() {}.getType();
			json = gson.toJson(mSaleTrans.getSaleTransaction(transactionId, sessionDate), type);
		} catch (Exception e) {
			Logger.appendLog(mContext, MPOSApplication.LOG_PATH, MPOSApplication.LOG_FILE_NAME,
					" Error at generate close shift json sale : " + e.getMessage());
		}
		return json;
	}
	
	public String generateLastSaleTransaction(String sessionDate){
		String json = null;
		try {
			Gson gson = new Gson();
			Type type = new TypeToken<POSData_SaleTransaction>() {}.getType();
			json = gson.toJson(mSaleTrans.getSaleTransaction(sessionDate), type);
		} catch (Exception e) {
			Logger.appendLog(mContext, MPOSApplication.LOG_PATH, MPOSApplication.LOG_FILE_NAME,
					" Error at generate close shift json sale : " + e.getMessage());
		}
		return json;
	}
	
	public String generateSale(String sessionDate){
		String json = null;
		try {
			Gson gson = new Gson();
			Type type = new TypeToken<POSData_SaleTransaction>() {}.getType();
			json = gson.toJson(mSaleTrans.getTransaction(sessionDate), type);
		} catch (Exception e) {
			Logger.appendLog(mContext, MPOSApplication.LOG_PATH, MPOSApplication.LOG_FILE_NAME,
					" Error at generate json sale : " + e.getMessage());
		}
		return json;
	}
	
	public String generateEnddayUnSendSale(String sessionDate){
		String json = null;
		try {
			Gson gson = new Gson();
			Type type = new TypeToken<POSData_EndDaySaleTransaction>() {}.getType();
			json = gson.toJson(mSaleTrans.getEndDayUnSendTransaction(sessionDate), type);
		} catch (Exception e) {
			Logger.appendLog(mContext, MPOSApplication.LOG_PATH, MPOSApplication.LOG_FILE_NAME,
					" Error at generate json unsend end day : " + e.getMessage());
		}
		return json;
	}
	
	public String generateEnddaySale(String sessionDate){
		String json = null;
		try {
			Gson gson = new Gson();
			Type type = new TypeToken<POSData_EndDaySaleTransaction>() {}.getType();
			json = gson.toJson(mSaleTrans.getEndDayTransaction(sessionDate), type);
		} catch (Exception e) {
			Logger.appendLog(mContext, MPOSApplication.LOG_PATH, MPOSApplication.LOG_FILE_NAME,
					" Error at generate json end day : " + e.getMessage());
		}
		return json;
	}
}
