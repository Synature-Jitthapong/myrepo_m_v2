package com.synature.mpos;

import org.ksoap2.serialization.PropertyInfo;

import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

import com.synature.pos.WebServiceResult;

public class EndDayUnSendSaleSender extends MPOSServiceBase{

	public static final String SEND_SALE_TRANS_METHOD = "WSmPOS_JSON_SendUnsendSaleTransactionDataWithEndDay";
	
	/**
	 * @param context
	 * @param shopId
	 * @param computerId
	 * @param staffId
	 * @param jsonSale
	 * @param listener
	 */
	public EndDayUnSendSaleSender(Context context, int shopId, int computerId,
			int staffId, String jsonSale, ResultReceiver receiver) {
		super(context, SEND_SALE_TRANS_METHOD, receiver);

		// shopId
		mProperty = new PropertyInfo();
		mProperty.setName(SHOP_ID_PARAM);
		mProperty.setValue(shopId);
		mProperty.setType(int.class);
		mSoapRequest.addProperty(mProperty);
		// computerId
		mProperty = new PropertyInfo();
		mProperty.setName(COMPUTER_ID_PARAM);
		mProperty.setValue(computerId);
		mProperty.setType(int.class);
		mSoapRequest.addProperty(mProperty);
		// staffId
		mProperty = new PropertyInfo();
		mProperty.setName(STAFF_ID_PARAM);
		mProperty.setValue(staffId);
		mProperty.setType(int.class);
		mSoapRequest.addProperty(mProperty);
		// json sale
		mProperty = new PropertyInfo();
		mProperty.setName(JSON_SALE_PARAM);
		mProperty.setValue(jsonSale);
		mProperty.setType(String.class);
		mSoapRequest.addProperty(mProperty);
	}
	
	@Override
	protected void onPostExecute(String result) {
		try {
			WebServiceResult ws = (WebServiceResult) toServiceObject(result);
			if(ws.getiResultID() == WebServiceResult.SUCCESS_STATUS){
				if(mReceiver != null){
					mReceiver.send(RESULT_SUCCESS, null);
				}
			}else{
				if(mReceiver != null){
					Bundle b = new Bundle();
					b.putString("msg", TextUtils.isEmpty(ws.getSzResultData()) ? result :
						ws.getSzResultData());
					mReceiver.send(RESULT_ERROR, b);
				}
			}
		} catch (Exception e) {
			if(mReceiver != null){
				Bundle b = new Bundle();
				b.putString("msg", TextUtils.isEmpty(result) ? e.getMessage() :
					result);
				mReceiver.send(RESULT_ERROR, b);
			}
		}
	}
}
