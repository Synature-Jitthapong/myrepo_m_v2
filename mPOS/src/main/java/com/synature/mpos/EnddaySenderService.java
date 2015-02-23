package com.synature.mpos;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.synature.mpos.database.MPOSDatabase;
import com.synature.mpos.database.SessionDao;
import com.synature.mpos.database.TransactionDao;
import com.synature.util.Logger;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ResultReceiver;
import android.text.TextUtils;

public class EnddaySenderService extends SaleSenderServiceBase{

	public static final String TAG = EnddaySenderService.class.getSimpleName();
	
	public static final int SEND_CURRENT = 1;
	public static final int SEND_ALL = 2;
	
	public static final String RECEIVER_NAME = "enddaySenderReceiver";

	private ExecutorService mExecutor;
	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;
	
	private final class ServiceHandler extends Handler{
		public ServiceHandler(Looper looper){
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			Bundle b = msg.getData();
			ResultReceiver receiver = (ResultReceiver) b.getParcelable(RECEIVER_NAME);
			String sessionDate = b.getString(SESSION_DATE_PARAM);
			int whatToDo = b.getInt(WHAT_TO_DO_PARAM, SEND_CURRENT);
			int shopId = b.getInt(SHOP_ID_PARAM, 0);
			int computerId = b.getInt(COMPUTER_ID_PARAM, 0);
			int staffId = b.getInt(STAFF_ID_PARAM, 0);
			if(whatToDo == SEND_CURRENT){
				sendEndday(SEND_CURRENT, sessionDate, shopId, computerId, staffId, receiver);
			}
		}
		
	}

	@Override
	public void onCreate() {
		HandlerThread thread = new HandlerThread("ServiceStartArguments",
				android.os.Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		mExecutor = Executors.newFixedThreadPool(THREAD_POOL);
		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
	}
	
	@Override
	public void onDestroy() {
		mExecutor.shutdown();
		super.onDestroy();
	}

	/**
	 * Intent require parameter 
	 * resultReceiver
	 * sessionDate
	 * sendMode
	 * shopId
	 * computerId
	 * staffId
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent != null){
			Message msg = mServiceHandler.obtainMessage();
			msg.arg1 = startId;
			Bundle b = intent.getExtras();
			msg.setData(b);
			mServiceHandler.sendMessage(msg);
		}
		return START_NOT_STICKY;
	}
	
	/**
	 * @author j1tth4
	 * The receiver for send endday
	 */
	private class EnddayReceiver extends ResultReceiver{
		
		private ResultReceiver receiver;
		private String sessionDate;
		private String jsonEndday;
		private int sendMode;
		private int shopId;
		private int computerId;
		private int staffId;
		
		/**
		 * @param handler
		 * @param sessionDate
		 * @param jsonEndday
		 * @param shopId
		 * @param computerId
		 * @param staffId
		 * @param sendMode
		 * @param receiver
		 */
		public EnddayReceiver(Handler handler, String sessionDate, String jsonEndday, 
				int shopId, int computerId, int staffId, int sendMode, ResultReceiver receiver) {
			super(handler);
			this.sessionDate = sessionDate;
			this.jsonEndday = jsonEndday;
			this.receiver = receiver;
			this.shopId = shopId;
			this.computerId = computerId;
			this.staffId = staffId;
			this.sendMode = sendMode;
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			super.onReceiveResult(resultCode, resultData);
			switch(resultCode){
			case RESULT_SUCCESS:
				flagSendStatus(sessionDate, MPOSDatabase.ALREADY_SEND);
				Utils.deleteOverSale(getApplicationContext());
//				try {
//					JSONSaleLogFile.appendEnddaySale(getApplicationContext(), sessionDate, jsonEndday);
//				} catch (Exception e) {}
				if(receiver != null){
					receiver.send(RESULT_SUCCESS, null);
				}
				break;
			case RESULT_ERROR:
				String msg = resultData.getString("msg");
				if(sendMode != SEND_ALL){
					// try again with send all
					sendEndday(SEND_ALL, sessionDate, shopId, computerId, staffId, receiver);
					Logger.appendLog(getApplicationContext(), MPOSApplication.ERR_LOG_PATH, "", 
							"Send endday with unsend sale fail: " + msg + "\n" + jsonEndday);
				}else{
					// if send all not work 
					flagSendStatus(sessionDate, MPOSDatabase.NOT_SEND);
					Logger.appendLog(getApplicationContext(), MPOSApplication.ERR_LOG_PATH, "", 
							"Send all endday fail: " + msg + "\n" + jsonEndday);
					
					Intent intent = new Intent(getApplicationContext(), RemoteStackTraceService.class);
					intent.putExtra("stackTrace", "Send all endday fail: " + msg + "\n" + jsonEndday);
					startService(intent);
					
					if(receiver != null){
						receiver.send(RESULT_ERROR, resultData);
					}
				}
				break;
			}
		}
		
	}
	
	/**
	 * Send current endday data
	 * @param sendMode
	 * @param sessionDate
	 * @param shopId
	 * @param computerId
	 * @param staffId
	 * @param receiver
	 */
	private void sendEndday(int sendMode, String sessionDate, int shopId, 
			int computerId, int staffId, ResultReceiver receiver){
		JSONSaleSerialization jsonGenerator = new JSONSaleSerialization(this);
		if(sendMode == SEND_CURRENT){ 
			String jsonEndday = jsonGenerator.generateEnddayUnSendSale(sessionDate);
			if(!TextUtils.isEmpty(jsonEndday)){
				EnddayReceiver enddayReceiver = new EnddayReceiver(new Handler(), 
						sessionDate, jsonEndday, shopId, computerId, staffId, sendMode, receiver);
				EndDayUnSendSaleSender sender = new EndDayUnSendSaleSender(getApplicationContext(), shopId, computerId, 
						staffId, jsonEndday, enddayReceiver);
				mExecutor.execute(sender);
			}else{
				if(receiver != null)
					receiver.send(RESULT_SUCCESS, null);
			}
		}else if(sendMode == SEND_ALL){
			String jsonEndday = jsonGenerator.generateEnddaySale(sessionDate);
			if(!TextUtils.isEmpty(jsonEndday)){
				EnddayReceiver enddayReceiver = new EnddayReceiver(new Handler(), 
						sessionDate, jsonEndday, shopId, computerId, staffId, sendMode, receiver);
				EndDaySaleSender sender = new EndDaySaleSender(getApplicationContext(), shopId, computerId, 
						staffId, jsonEndday, enddayReceiver);
				mExecutor.execute(sender);
			}else{
				if(receiver != null)
					receiver.send(RESULT_SUCCESS, null);
			}
		}
	}
	
	/**
	 * @param sessionDate
	 * @param status
	 */
	private void flagSendStatus(String sessionDate, int status){
		SessionDao session = new SessionDao(getApplicationContext());
		TransactionDao trans = new TransactionDao(getApplicationContext());
		
		session.updateSessionEnddayDetail(sessionDate, status);
		trans.updateTransactionSendStatus(sessionDate, status);
		trans.updateTransactionWasteSendStatus(sessionDate, status);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
