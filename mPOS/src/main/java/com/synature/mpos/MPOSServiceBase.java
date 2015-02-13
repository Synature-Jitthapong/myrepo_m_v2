package com.synature.mpos;

import java.lang.reflect.Type;

import org.ksoap2.serialization.PropertyInfo;

import android.content.Context;
import android.os.ResultReceiver;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.synature.connection.Ksoap2WebServiceTask;
import com.synature.pos.WebServiceResult;


public abstract class MPOSServiceBase extends Ksoap2WebServiceTask{
	
	/**
	 * The response of WebServiceResult
	 * 0 success -1 error
	 */
	public static final int RESPONSE_SUCCESS = 0;
	public static final int RESPONSE_ERROR = -1;
	
	/**
	 * The result of ResultReceiver
	 */
	public static final int RESULT_SUCCESS = 1;
	public static final int RESULT_ERROR = 0;
	
	public static final String SHOP_ID_PARAM = "iShopID";
	public static final String COMPUTER_ID_PARAM = "iComputerID";
	public static final String STAFF_ID_PARAM = "iStaffID";
	public static final String DEVICE_CODE_PARAM = "szDeviceCode";
	public static final String JSON_SALE_PARAM = "szJsonSaleTransData";
	
	protected ResultReceiver mReceiver;
	
	public MPOSServiceBase(Context context, String method, ResultReceiver receiver) {
		super(context, Utils.getFullUrl(context), method, Utils.getConnectionTimeOut(context));
		
		mReceiver = receiver;
		
		mProperty = new PropertyInfo();
		mProperty.setName(DEVICE_CODE_PARAM);
		mProperty.setValue(Utils.getDeviceCode(context));
		mProperty.setType(String.class);
		mSoapRequest.addProperty(mProperty);
	}

	public WebServiceResult toServiceObject(String json) throws JsonSyntaxException{
		Gson gson = new Gson();
		Type type = new TypeToken<WebServiceResult>(){}.getType();
		WebServiceResult ws = gson.fromJson(json, type);
		return ws;
	}
}
