package com.synature.mpos;

import android.content.Context;

import com.epson.eposprint.BatteryStatusChangeEventListener;
import com.epson.eposprint.Builder;
import com.epson.eposprint.EposException;
import com.epson.eposprint.Print;
import com.epson.eposprint.StatusChangeEventListener;
import com.synature.util.LevelTextPrint;
import com.synature.util.LevelTextPrint.ThreeLevelByteCode;

public class EPSONPrinter extends PrinterBase implements 
	BatteryStatusChangeEventListener, StatusChangeEventListener{
	
	public static final int PRINT_NORMAL = 0;
	public static final int PRINT_THAI_LEVEL = 1;
	public static final int PRINT_LAO_LEVEL = 2;
	
	protected Context mContext;
	protected Print mPrinter;
	protected Builder mBuilder;
	protected int mLangToPrint;
	
	/**
	 * @param context
	 */
	public EPSONPrinter(Context context){
		super(context);
		mContext = context;
		mPrinter = new Print(context.getApplicationContext());
		mPrinter.setStatusChangeEventCallback(this);
		mPrinter.setBatteryStatusChangeEventCallback(this);
		try {
			mBuilder = new Builder(Utils.getEPSONModelName(mContext), Builder.MODEL_ANK, mContext);
			mBuilder.addTextSize(1, 1);
			mBuilder.addTextFont(Builder.FONT_B);
			open();
		} catch (EposException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param context
	 * @param langToPrint
	 */
	public EPSONPrinter(Context context, int langToPrint){
		this(context);
		mLangToPrint = langToPrint;
	}
	
	protected void print(){
		int[] status = new int[1];
		int[] battery = new int[1];
		try {
			if(mLangToPrint == PRINT_LAO_LEVEL){
				createLaoBuilderCommand();
			}else{
				createBuilder();
			}
			mBuilder.addFeedUnit(30);
			mBuilder.addCut(Builder.CUT_FEED);
			mPrinter.sendData(mBuilder, 10000, status, battery);
		} catch (EposException e) {
			e.printStackTrace();
		}
		if (mBuilder != null) {
			mBuilder.clearCommandBuffer();
		}
		close();
	}
	
	private void createLaoBuilderCommand() throws EposException{
		ThreeLevelByteCode level = LevelTextPrint.parsingLaoLevel(mTextToPrint.toString());
		mBuilder.addCommand(level.getLine2());
	}
	
	private void createBuilder() throws EposException{
		mBuilder.addText(mTextToPrint.toString());
	}
	
	private void open(){
		try {
			mPrinter.openPrinter(Print.DEVTYPE_TCP, Utils.getPrinterIp(mContext), 0, 1000);
		} catch (EposException e) {
			e.printStackTrace();
		}	
	}

	private void close(){
		try {
			mPrinter.closePrinter();
			mPrinter = null;
		} catch (EposException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onStatusChangeEvent(String arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBatteryStatusChangeEvent(String arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
}
