package com.synature.mpos;

import java.text.ParseException;

import android.content.Context;
import android.text.TextUtils;
import cn.wintec.wtandroidjar2.ComIO;
import cn.wintec.wtandroidjar2.DspPos;

public class WintecCustomerDisplay{
	
	public static final int MAX_TEXT_LENGTH = 20;
	public static final int LIMIT_LENGTH = 10;
	
	private Context mContext;
	
	private DspPos mDsp;
	
	private String itemExtra;
	private String itemName;
	private String itemQty;
	private String itemAmount;
	private String itemTotalQty;
	private String itemTotalAmount;
	
	public WintecCustomerDisplay(Context context){
		mDsp = new DspPos(Utils.getWintecDspPath(context), 
				ComIO.Baudrate.valueOf(Utils.getWintecDspBaudRate(context)));
		mContext = context;
	}
	
	public void displayPayment(String payType, String amount){
		clearScreen();
		mDsp.DSP_Dispay(payType);
		mDsp.DSP_MoveCursorDown();
		mDsp.DSP_MoveCursorEndLeft();
		mDsp.DSP_MoveCursor(1, MAX_TEXT_LENGTH - amount.length());
		mDsp.DSP_Dispay(amount);
	}
	
	public void displayTotalPay(String totalPay, String change){
		clearScreen();
		mDsp.DSP_Dispay("Receive");
		mDsp.DSP_MoveCursor(1, MAX_TEXT_LENGTH - totalPay.length());
		mDsp.DSP_Dispay(totalPay);
		try {
			if(Utils.stringToDouble(change) > 0){
				mDsp.DSP_MoveCursorDown();
				mDsp.DSP_MoveCursorEndLeft();
				mDsp.DSP_Dispay("Change");
				mDsp.DSP_MoveCursor(2, MAX_TEXT_LENGTH - change.length());
				mDsp.DSP_Dispay(change);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public void displayOrder() throws Exception{
		if(!TextUtils.isEmpty(itemName) && itemName.length() > LIMIT_LENGTH){
			itemName = limitString(itemName);
		}
		clearScreen();
		String combindText = itemQty + "@" + itemAmount;
		String combindTotalText = itemTotalQty + "@" + itemTotalAmount;
		mDsp.DSP_Dispay(itemName);
		mDsp.DSP_MoveCursor(1, MAX_TEXT_LENGTH - combindText.length());
		mDsp.DSP_Dispay(combindText);
		mDsp.DSP_MoveCursorDown();
		mDsp.DSP_MoveCursorEndLeft();
		mDsp.DSP_Dispay("Total");
		mDsp.DSP_MoveCursor(2, MAX_TEXT_LENGTH - combindTotalText.length());
		mDsp.DSP_Dispay(combindTotalText);
	}
	
	public void displayWelcome(){
		clearScreen();
		String line1 = Utils.getWintecDspTextLine1(mContext);
		String line2 = Utils.getWintecDspTextLine2(mContext);
		mDsp.DSP_Dispay(line1);
		mDsp.DSP_MoveCursorDown();
		mDsp.DSP_MoveCursorEndLeft();
		mDsp.DSP_Dispay(line2);
	}
	
	private String limitString(String text){
		return text.substring(0, LIMIT_LENGTH);
	}
	
	public void clearScreen(){
		mDsp.DSP_ClearScreen();
	}
	
	public void close(){
		if(mDsp != null)
			mDsp.DSP_Close();
	}

	public String getItemExtra() {
		return itemExtra;
	}

	public void setItemExtra(String itemExtra) {
		this.itemExtra = itemExtra;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemQty() {
		return itemQty;
	}

	public void setItemQty(String itemQty) {
		this.itemQty = itemQty;
	}

	public String getItemAmount() {
		return itemAmount;
	}

	public void setItemAmount(String itemAmount) {
		this.itemAmount = itemAmount;
	}

	public String getItemTotalQty() {
		return itemTotalQty;
	}

	public void setItemTotalQty(String itemTotalQty) {
		this.itemTotalQty = itemTotalQty;
	}

	public String getItemTotalAmount() {
		return itemTotalAmount;
	}

	public void setItemTotalAmount(String itemTotalAmount) {
		this.itemTotalAmount = itemTotalAmount;
	}
}
