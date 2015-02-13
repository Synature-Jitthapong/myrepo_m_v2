package com.synature.mpos;

import com.synature.util.LevelTextPrint;
import com.synature.util.LevelTextPrint.ThreeLevelPrint;

import android.content.Context;
import android.text.TextUtils;
import cn.wintec.wtandroidjar2.ComIO;
import cn.wintec.wtandroidjar2.Printer;

public class WintecPrinter extends PrinterBase{
	
	/**
	 * ISO8859-11 character
	 */
	public static final String ISO_8859_11 = "ISO8859-11";//"x-iso-8859-11";
	
	protected Context mContext;
	protected Printer mPrinter;
	
	public WintecPrinter(Context context){
		super(context);
		mContext = context;
		mPrinter = new Printer(Utils.getWintecPrinterDevPath(mContext), 
				ComIO.Baudrate.valueOf(Utils.getWintecPrinterBaudRate(mContext)));
		mPrinter.PRN_DisableChinese();
		mPrinter.PRN_SetCodePage(70);
	}

	protected void print(){
		String[] subElement = mTextToPrint.toString().split("\n");
    	for(String data : subElement){
    		ThreeLevelPrint supportThai = LevelTextPrint.parsingThaiLevel(data);
    		if(!TextUtils.isEmpty(supportThai.getLine1())){
    			mPrinter.PRN_Print(supportThai.getLine1(), ISO_8859_11);
    		}
    		mPrinter.PRN_Print(supportThai.getLine2(), ISO_8859_11);
    		if(!TextUtils.isEmpty(supportThai.getLine3())){
    			mPrinter.PRN_Print(supportThai.getLine3(), ISO_8859_11);
    		}
		}
    	mPrinter.PRN_PrintAndFeedLine(6);		
    	mPrinter.PRN_HalfCutPaper();
    	close();
	}
	
	private void close(){
		if(mPrinter != null)
			mPrinter.PRN_Close();
	}
}
