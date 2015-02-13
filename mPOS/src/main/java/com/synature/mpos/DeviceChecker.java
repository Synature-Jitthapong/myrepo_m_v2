package com.synature.mpos;

import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

public class DeviceChecker extends MPOSServiceBase {
	
	public static final String CHECK_DEVICE_METHOD = "WSmPOS_CheckAuthenShopDevice";

	/**
	 * @param context
	 * @param receiver
	 */
	public DeviceChecker(Context context, ResultReceiver receiver) {
		super(context, CHECK_DEVICE_METHOD, receiver);
	}

	@Override
	protected void onPostExecute(String result) {
		try {
			int shopId = Integer.parseInt(result);
			if (shopId > 0){
				if(mReceiver != null){
					Bundle b = new Bundle();
					b.putInt("shopId", shopId);
					mReceiver.send(MPOSServiceBase.RESULT_SUCCESS, b);
				}
			}else if (shopId == 0){
				if(mReceiver != null){
					Bundle b = new Bundle();
					b.putString("msg", mContext.getString(R.string.device_not_register));
					mReceiver.send(MPOSServiceBase.RESULT_ERROR, b);
				}
			}else if (shopId == -1){
				if(mReceiver != null){
					Bundle b = new Bundle();
					b.putString("msg", mContext.getString(R.string.computer_setting_not_valid));
					mReceiver.send(MPOSServiceBase.RESULT_ERROR, b);
				}
			}
		} catch (NumberFormatException e) {
			if(mReceiver != null){
				Bundle b = new Bundle();
				b.putString("msg", TextUtils.isEmpty(result) ? e.getMessage() : result);
				mReceiver.send(MPOSServiceBase.RESULT_ERROR, b);
			}
		}
	}
}
