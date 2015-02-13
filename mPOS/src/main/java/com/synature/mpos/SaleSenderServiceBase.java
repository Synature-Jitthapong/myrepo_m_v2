package com.synature.mpos;

import android.app.Service;

public abstract class SaleSenderServiceBase extends Service{
	
	public static final int RESULT_ERROR = 0;
	public static final int RESULT_SUCCESS = 1;
	public static final int THREAD_POOL = 5;
	
	public static final String WHAT_TO_DO_PARAM = "whatToDo";
	public static final String SESSION_DATE_PARAM = "sessionDate";
	public static final String SHOP_ID_PARAM = "shopId";
	public static final String COMPUTER_ID_PARAM = "computerId";
	public static final String STAFF_ID_PARAM = "staffId";
}
