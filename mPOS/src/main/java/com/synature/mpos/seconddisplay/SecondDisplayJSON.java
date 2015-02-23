package com.synature.mpos.seconddisplay;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.synature.mpos.database.GlobalPropertyDao;
import com.synature.mpos.database.model.OrderDetail;
import com.synature.pos.SecondDisplayProperty;
import com.synature.pos.SecondDisplayProperty.clsSecDisplayItemData;
import com.synature.pos.SecondDisplayProperty.clsSecDisplay_ChangePayment;
import com.synature.pos.SecondDisplayProperty.clsSecDisplay_ClearScreen;
import com.synature.pos.SecondDisplayProperty.clsSecDisplay_DetailItem;
import com.synature.pos.SecondDisplayProperty.clsSecDisplay_TransSummary;
import com.synature.pos.SecondDisplayProperty.clsSecDisplay_Transaction;

public class SecondDisplayJSON {
	
	public static String genClearDisplay(){
		Gson gson = new Gson();
		return gson.toJson(new clsSecDisplay_ClearScreen());
	}
	
	/**
	 * @param totalPay
	 * @param change
	 * @return
	 */
	public static String genChangePayment(String grandTotal, String totalPay, String change){
		Gson gson = new Gson();
		clsSecDisplay_ChangePayment changePayment = new clsSecDisplay_ChangePayment();
		changePayment.szGrandTotalAmount = grandTotal;
		changePayment.szPayAmount = totalPay;
		changePayment.szCashChangeAmount = change;
		return gson.toJson(changePayment);
	}

	/**
	 * @param format
	 * @param orderDetailLst
	 * @param transSummLst
	 * @param grandTotal
	 * @return
	 */
	public static String genDisplayItem(GlobalPropertyDao format, List<OrderDetail> orderDetailLst, 
			List<clsSecDisplay_TransSummary> transSummLst, String grandTotal){
		Gson gson = new Gson();
		clsSecDisplayItemData displayData = new clsSecDisplayItemData();
		List<clsSecDisplay_DetailItem> itemLst = new ArrayList<clsSecDisplay_DetailItem>();
		for(OrderDetail orderDetail : orderDetailLst){
			clsSecDisplay_DetailItem item = new clsSecDisplay_DetailItem();
			item.szItemName = orderDetail.getProductName();
			item.szItemQty = format.qtyFormat(orderDetail.getOrderQty());
			item.szItemTotalPrice = format.currencyFormat(orderDetail.getTotalRetailPrice());
			item.szImageUrl = "";
			itemLst.add(item);
		}
		clsSecDisplay_Transaction trans = new clsSecDisplay_Transaction();
		trans.iNoCustomer = 0;
		trans.szCustName = "";
		trans.szTransName = "";
		displayData.xTransaction = trans;
		displayData.szGrandTotalPrice = grandTotal;
		displayData.xListDetailItems = itemLst;
		displayData.xListTransSummarys = transSummLst;
		return gson.toJson(displayData);
	}
	
	/**
	 * @param shopName
	 * @param staffName
	 * @return JSON String
	 */
	public static String genInitDisplay(String shopName, String staffName){
		Gson gson = new Gson();
		SecondDisplayProperty.clsSecDisplayInitial init 
			= new SecondDisplayProperty.clsSecDisplayInitial();
		init.szShopName = shopName;
		init.szStaffName = staffName;
		return gson.toJson(init);
	}
}
