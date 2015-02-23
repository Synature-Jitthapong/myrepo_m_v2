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

public abstract class RegisterServiceBase extends Ksoap2WebServiceTask{
	
	protected ResultReceiver mReceiver;
	
	public RegisterServiceBase(Context context, String method, ResultReceiver receiver) {
		super(context, MPOSApplication.REGISTER_URL, method, Utils.getConnectionTimeOut(context));
		
		mReceiver = receiver;
		
		mProperty = new PropertyInfo();
		mProperty.setName(MPOSServiceBase.DEVICE_CODE_PARAM);
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
