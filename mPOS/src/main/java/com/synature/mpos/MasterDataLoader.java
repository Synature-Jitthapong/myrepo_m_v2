package com.synature.mpos;

import org.ksoap2.serialization.PropertyInfo;

import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.synature.mpos.datasource.BankDataSource;
import com.synature.mpos.datasource.ComputerDataSource;
import com.synature.mpos.datasource.CreditCardDataSource;
import com.synature.mpos.datasource.GlobalPropertyDataSource;
import com.synature.mpos.datasource.HeaderFooterReceiptDataSource;
import com.synature.mpos.datasource.LanguageDataSource;
import com.synature.mpos.datasource.MenuCommentDataSource;
import com.synature.mpos.datasource.PaymentAmountButtonDataSource;
import com.synature.mpos.datasource.PaymentDetailDao;
import com.synature.mpos.datasource.ProductPriceDatasource;
import com.synature.mpos.datasource.ProductsDatasource;
import com.synature.mpos.datasource.ProgramFeatureDatasource;
import com.synature.mpos.datasource.PromotionDiscountDatasource;
import com.synature.mpos.datasource.ShopDataSource;
import com.synature.mpos.datasource.StaffsDataSource;
import com.synature.mpos.datasource.SyncHistoryDao;
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
		ShopDataSource shop = new ShopDataSource(mContext);
		ComputerDataSource computer = new ComputerDataSource(mContext);
		GlobalPropertyDataSource format = new GlobalPropertyDataSource(mContext);
		StaffsDataSource staff = new StaffsDataSource(mContext);
		LanguageDataSource lang = new LanguageDataSource(mContext);
		HeaderFooterReceiptDataSource hf = new HeaderFooterReceiptDataSource(mContext);
		BankDataSource bank = new BankDataSource(mContext);
		CreditCardDataSource cd = new CreditCardDataSource(mContext);
		PaymentDetailDao pd = new PaymentDetailDao(mContext);
		PaymentAmountButtonDataSource pb = new PaymentAmountButtonDataSource(mContext);
		ProductsDatasource p = new ProductsDatasource(mContext);
		ProductPriceDatasource pp = new ProductPriceDatasource(mContext);
		MenuCommentDataSource mc = new MenuCommentDataSource(mContext);
		PromotionDiscountDatasource promo = new PromotionDiscountDatasource(mContext);
		ProgramFeatureDatasource feature = new ProgramFeatureDatasource(mContext);
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
