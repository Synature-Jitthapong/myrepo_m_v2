package com.synature.mpos;

import org.ksoap2.serialization.PropertyInfo;

import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.synature.mpos.database.BankDao;
import com.synature.mpos.database.ComputerDao;
import com.synature.mpos.database.CreditCardDao;
import com.synature.mpos.database.GlobalPropertyDao;
import com.synature.mpos.database.HeaderFooterReceiptDao;
import com.synature.mpos.database.LanguageDao;
import com.synature.mpos.database.MenuCommentDao;
import com.synature.mpos.database.PaymentAmountButtonDao;
import com.synature.mpos.database.PaymentDetailDao;
import com.synature.mpos.database.ProductPriceDao;
import com.synature.mpos.database.ProductsDao;
import com.synature.mpos.database.ProgramFeatureDao;
import com.synature.mpos.database.PromotionDiscountDao;
import com.synature.mpos.database.ShopDao;
import com.synature.mpos.database.StaffsDao;
import com.synature.mpos.database.SyncHistoryDao;
import com.synature.pos.MasterData;
import com.synature.util.Logger;

public class MasterDataLoader extends MPOSServiceBase{
	
	public static final String LOAD_MASTER_METHOD = "WSmPOS_JSON_LoadShopMasterData";

	/**
	 * @param context
	 * @param listener
	 */
	public MasterDataLoader(Context context, int shopId, ResultReceiver receiver) {
		super(context, LOAD_MASTER_METHOD, receiver);
		
		// shopId
		mProperty = new PropertyInfo();
		mProperty.setName(SHOP_ID_PARAM);
		mProperty.setValue(shopId);
		mProperty.setType(int.class);
		mSoapRequest.addProperty(mProperty);
	}

	@Override
	protected void onPostExecute(String result) {
		Gson gson = new Gson();
		try {
			MasterData master = gson.fromJson(result, MasterData.class);
			updateMasterData(master);
		} catch (JsonSyntaxException e) {
			if(mReceiver != null){
				Bundle b = new Bundle();
				b.putString("msg", e.getMessage());
				mReceiver.send(RESULT_ERROR, b);
			}
		}
	}

	private void updateMasterData(MasterData master){
		SyncHistoryDao sync = new SyncHistoryDao(mContext);
		ShopDao shop = new ShopDao(mContext);
		ComputerDao computer = new ComputerDao(mContext);
		GlobalPropertyDao format = new GlobalPropertyDao(mContext);
		StaffsDao staff = new StaffsDao(mContext);
		LanguageDao lang = new LanguageDao(mContext);
		HeaderFooterReceiptDao hf = new HeaderFooterReceiptDao(mContext);
		BankDao bank = new BankDao(mContext);
		CreditCardDao cd = new CreditCardDao(mContext);
		PaymentDetailDao pd = new PaymentDetailDao(mContext);
		PaymentAmountButtonDao pb = new PaymentAmountButtonDao(mContext);
		ProductsDao p = new ProductsDao(mContext);
		ProductPriceDao pp = new ProductPriceDao(mContext);
		MenuCommentDao mc = new MenuCommentDao(mContext);
		PromotionDiscountDao promo = new PromotionDiscountDao(mContext);
		ProgramFeatureDao feature = new ProgramFeatureDao(mContext);
		try {
			sync.insertSyncLog();
			shop.insertShopProperty(master.getShopProperty());
			computer.insertComputer(master.getComputerProperty());
			format.insertProperty(master.getGlobalProperty());
			staff.insertStaff(master.getStaffs());
			staff.insertStaffPermission(master.getStaffPermission());
			lang.insertLanguage(master.getLanguage());
			hf.insertHeaderFooterReceipt(master.getHeaderFooterReceipt());
			bank.insertBank(master.getBankName());
			cd.insertCreditCardType(master.getCreditCardType());
			pd.insertPaytype(master.getPayType());
			pd.insertPaytypeFinishWaste(master.getPayTypeFinishWaste());
			pb.insertPaymentAmountButton(master.getPaymentAmountButton());
			p.insertProductGroup(master.getProductGroup());
			p.insertProductDept(master.getProductDept());
			p.insertProducts(master.getProducts());
			pp.insertProductPrice(master.getProductPrice());
			p.insertPComponentGroup(master.getPComponentGroup());
			p.insertProductComponent(master.getProductComponent());
			mc.insertMenuFixComment(master.getMenuFixComment());
			promo.insertPromotionPriceGroup(master.getPromotionPriceGroup());
			promo.insertPromotionProductDiscount(master.getPromotionProductDiscount());
			feature.insertProgramFeature(master.getProgramFeature());
			
			// log sync history
			sync.updateSyncStatus(SyncHistoryDao.SYNC_STATUS_SUCCESS);
			if(mReceiver != null)
				mReceiver.send(RESULT_SUCCESS, null);
		} catch (Exception e) {
			// log sync history
			sync.updateSyncStatus(SyncHistoryDao.SYNC_STATUS_FAIL);
			Logger.appendLog(mContext, MPOSApplication.LOG_PATH, 
					MPOSApplication.LOG_FILE_NAME, 
					"Error when add shop data : " + e.getMessage());
			if(mReceiver != null){
				Bundle b = new Bundle();
				b.putString("msg", e.getMessage());
				mReceiver.send(RESULT_ERROR, b);
			}
		}
	}
}
