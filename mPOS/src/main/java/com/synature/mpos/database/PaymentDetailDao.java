package com.synature.mpos.database;

import java.util.ArrayList;
import java.util.List;

import com.synature.mpos.database.model.MPOSPaymentDetail;
import com.synature.mpos.database.table.BankTable;
import com.synature.mpos.database.table.BaseColumn;
import com.synature.mpos.database.table.ComputerTable;
import com.synature.mpos.database.table.CreditCardTable;
import com.synature.mpos.database.table.MaxPaymentIdTable;
import com.synature.mpos.database.table.OrderTransTable;
import com.synature.mpos.database.table.PayTypeFinishWasteTable;
import com.synature.mpos.database.table.PayTypeTable;
import com.synature.mpos.database.table.PaymentDetailTable;
import com.synature.mpos.database.table.PaymentDetailWasteTable;
import com.synature.mpos.database.table.SessionTable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

public class PaymentDetailDao extends MPOSDatabase {
	
	public static final int PAY_TYPE_CASH = 1;
	public static final int PAY_TYPE_CREDIT = 2;

	/**
	 * All payment columns
	 */
	public static final String[] ALL_PAYMENT_DETAIL_COLUMNS = {
		PaymentDetailTable.COLUMN_PAY_ID,
		ComputerTable.COLUMN_COMPUTER_ID,
		BankTable.COLUMN_BANK_ID,
		PayTypeTable.COLUMN_PAY_TYPE_ID,
		CreditCardTable.COLUMN_CREDITCARD_TYPE_ID,
		CreditCardTable.COLUMN_CREDITCARD_NO,
		CreditCardTable.COLUMN_EXP_MONTH,
		CreditCardTable.COLUMN_EXP_YEAR,
		PaymentDetailTable.COLUMN_REMARK,
		PaymentDetailTable.COLUMN_PAY_AMOUNT
	};
	
	public PaymentDetailDao(Context context) {
		super(context);
	}
	
	/**
	 * Get total bill that pay by cash
     * @param sessId
	 * @param saleDate
	 * @return total transaction pay by cash
	 */
	public int getTotalTransPayByCash(int sessId, String saleDate){
		int totalTrans = 0;
        String selection = " a." + OrderTransTable.COLUMN_SALE_DATE + "=?"
                + " AND b." + PayTypeTable.COLUMN_PAY_TYPE_ID + "=?";
        String[] selectionArgs = {
                saleDate,
                String.valueOf(PaymentDetailDao.PAY_TYPE_CASH)
        };
        if(sessId != 0){
            selection += " AND a." + SessionTable.COLUMN_SESS_ID + "=?";
            selectionArgs = new String[]{
                    saleDate,
                    String.valueOf(PaymentDetailDao.PAY_TYPE_CASH),
                    String.valueOf(sessId)
            };
        }
        String sql = "SELECT COUNT(a." + OrderTransTable.COLUMN_TRANS_ID + ")"
                + " FROM " + OrderTransTable.TABLE_ORDER_TRANS + " a "
                + " LEFT JOIN " + PaymentDetailTable.TABLE_PAYMENT_DETAIL + " b "
                + " ON a." + OrderTransTable.COLUMN_TRANS_ID + "=b." + OrderTransTable.COLUMN_TRANS_ID
                + " WHERE " + selection
                + " GROUP BY a." + OrderTransTable.COLUMN_SALE_DATE;
		Cursor cursor = getReadableDatabase().rawQuery(
				sql, selectionArgs);
		if(cursor.moveToFirst()){
			totalTrans = cursor.getInt(0);
		}
		cursor.close();
		return totalTrans;
	}
	
	/**
	 * Get summary of payment
     * @param sessId
	 * @param saleDate
	 * @return List<MPOSPaymentDetail>
	 */
	public List<MPOSPaymentDetail> listSummaryPayment(int sessId, String saleDate){
		List<MPOSPaymentDetail> paymentLst = new ArrayList<MPOSPaymentDetail>();
        String selection = " a." + OrderTransTable.COLUMN_SALE_DATE + "=? "
                + " AND a." + OrderTransTable.COLUMN_STATUS_ID + "=?";
        String[] selectionArgs = {
                saleDate,
                String.valueOf(TransactionDao.TRANS_STATUS_SUCCESS)
        };
        if(sessId != 0){
            selection += " AND a." + SessionTable.COLUMN_SESS_ID + "=?";
            selectionArgs = new String[]{
                    saleDate,
                    String.valueOf(TransactionDao.TRANS_STATUS_SUCCESS),
                    String.valueOf(sessId)
            };
        }
        String sql = "SELECT c." + PayTypeTable.COLUMN_PAY_TYPE_NAME + ", "
                + " SUM(b." + PaymentDetailTable.COLUMN_PAY_AMOUNT + ") "
                + " AS " + PaymentDetailTable.COLUMN_PAY_AMOUNT
                + " FROM " + OrderTransTable.TABLE_ORDER_TRANS + " a "
                + " LEFT JOIN " + PaymentDetailTable.TABLE_PAYMENT_DETAIL + " b "
                + " ON a." + OrderTransTable.COLUMN_TRANS_ID + "=b." + OrderTransTable.COLUMN_TRANS_ID
                + " LEFT JOIN " + PayTypeTable.TABLE_PAY_TYPE + " c "
                + " ON b." + PayTypeTable.COLUMN_PAY_TYPE_ID + "=c." + PayTypeTable.COLUMN_PAY_TYPE_ID
                + " WHERE " + selection
                + " GROUP BY c." + PayTypeTable.COLUMN_PAY_TYPE_ID;
		Cursor cursor = getReadableDatabase().rawQuery(
				sql, selectionArgs);
		if(cursor.moveToFirst()){
			do{
				MPOSPaymentDetail payment = new MPOSPaymentDetail();
				payment.setPayTypeName(cursor.getString(cursor.getColumnIndex(PayTypeTable.COLUMN_PAY_TYPE_NAME)));
				payment.setPayAmount(cursor.getDouble(cursor.getColumnIndex(PaymentDetailTable.COLUMN_PAY_AMOUNT)));
				paymentLst.add(payment);
			}while(cursor.moveToNext());
		}
		cursor.close();
		return paymentLst;
	}
	
	/**
	 * @return List<Payment.PayType>
	 */
	public List<com.synature.pos.PayType> listPayType(){
		List<com.synature.pos.PayType> payTypeLst = new ArrayList<com.synature.pos.PayType>();
		Cursor cursor = getReadableDatabase().query(PayTypeTable.TABLE_PAY_TYPE,
				new String[]{
				PayTypeTable.COLUMN_PAY_TYPE_ID,
				PayTypeTable.COLUMN_PAY_TYPE_CODE,
				PayTypeTable.COLUMN_PAY_TYPE_NAME
				}, null, null, null, null, COLUMN_ORDERING);
		if(cursor.moveToFirst()){
			do{
				com.synature.pos.PayType payType = new com.synature.pos.PayType();
				payType.setPayTypeID(cursor.getInt(cursor.getColumnIndex(PayTypeTable.COLUMN_PAY_TYPE_ID)));
				payType.setPayTypeCode(cursor.getString(cursor.getColumnIndex(PayTypeTable.COLUMN_PAY_TYPE_CODE)));
				payType.setPayTypeName(cursor.getString(cursor.getColumnIndex(PayTypeTable.COLUMN_PAY_TYPE_NAME)));
				payTypeLst.add(payType);
			}while(cursor.moveToNext());
		}
		cursor.close();
		return payTypeLst;
	}
	
	/**
	 * @param transactionId
	 * @return List<MPOSPaymentDetail>
	 */
	public List<MPOSPaymentDetail> listPaymentGroupByType(int transactionId){
		List<MPOSPaymentDetail> paymentLst = new ArrayList<MPOSPaymentDetail>();
		Cursor cursor = getReadableDatabase().rawQuery(
				"SELECT a." + PayTypeTable.COLUMN_PAY_TYPE_ID + ", " + " a."
						+ CreditCardTable.COLUMN_CREDITCARD_TYPE_ID + ", "
						+ " a." + CreditCardTable.COLUMN_CREDITCARD_NO + ", "
						+ " SUM(a." + PaymentDetailTable.COLUMN_TOTAL_PAY_AMOUNT + ") AS "
						+ PaymentDetailTable.COLUMN_TOTAL_PAY_AMOUNT + ", " + " SUM(a."
						+ PaymentDetailTable.COLUMN_PAY_AMOUNT + ") AS "
						+ PaymentDetailTable.COLUMN_PAY_AMOUNT + ", " + " b."
						+ PayTypeTable.COLUMN_PAY_TYPE_CODE + ", " + " b."
						+ PayTypeTable.COLUMN_PAY_TYPE_NAME + " FROM "
						+ PaymentDetailTable.TABLE_PAYMENT_DETAIL + " a " + " LEFT JOIN "
						+ PayTypeTable.TABLE_PAY_TYPE + " b " + " ON a."
						+ PayTypeTable.COLUMN_PAY_TYPE_ID + "=b."
						+ PayTypeTable.COLUMN_PAY_TYPE_ID + " WHERE a."
						+ OrderTransTable.COLUMN_TRANS_ID + "=?"
						+ " GROUP BY a." + PayTypeTable.COLUMN_PAY_TYPE_ID
						+ " ORDER BY a." + PaymentDetailTable.COLUMN_PAY_ID,
				new String[]{
					String.valueOf(transactionId)
				}
		);
		if(cursor.moveToFirst()){
			do{
				MPOSPaymentDetail payment = new MPOSPaymentDetail();
				payment.setPayTypeId(cursor.getInt(cursor.getColumnIndex(PayTypeTable.COLUMN_PAY_TYPE_ID)));
				payment.setCreditCardTypeId(cursor.getInt(cursor.getColumnIndex(CreditCardTable.COLUMN_CREDITCARD_TYPE_ID)));
				payment.setCreditCardNo(cursor.getString(cursor.getColumnIndex(CreditCardTable.COLUMN_CREDITCARD_NO)));
				payment.setTotalPay(cursor.getDouble(cursor.getColumnIndex(PaymentDetailTable.COLUMN_TOTAL_PAY_AMOUNT)));
				payment.setPayAmount(cursor.getDouble(cursor.getColumnIndex(PaymentDetailTable.COLUMN_PAY_AMOUNT)));
				payment.setPayTypeCode(cursor.getString(cursor.getColumnIndex(PayTypeTable.COLUMN_PAY_TYPE_CODE)));
				payment.setPayTypeName(cursor.getString(cursor.getColumnIndex(PayTypeTable.COLUMN_PAY_TYPE_NAME)));
				paymentLst.add(payment);
			}while(cursor.moveToNext());
		}
		cursor.close();
		return paymentLst;
	}
	
	/**
	 * @param transactionId
	 * @return row affected
	 */
	public int deleteAllPaymentDetail(int transactionId){
		return getWritableDatabase().delete(PaymentDetailTable.TEMP_PAYMENT_DETAIL, 
					OrderTransTable.COLUMN_TRANS_ID + "=?",
					new String[]{String.valueOf(transactionId)});
	}
	
	/**
	 * @return max paymentDetailId
	 */
	public int getMaxPaymentDetailId() {
		int payId = 0;
		Cursor cursor = getReadableDatabase().rawQuery(
				" SELECT " + MaxPaymentIdTable.COLUMN_MAX_PAY_ID
				+ " FROM " + MaxPaymentIdTable.TABLE_MAX_PAY_ID, null);
		if (cursor.moveToFirst()) {
			payId = cursor.getInt(0);
		}
		cursor.close();
		if(payId == 0){
			cursor = getReadableDatabase().rawQuery(
					"SELECT MAX(" + PaymentDetailTable.COLUMN_PAY_ID + ")"
					+ " FROM " + PaymentDetailTable.TABLE_PAYMENT_DETAIL, null);
			if(cursor.moveToFirst()){
				payId = cursor.getInt(0);
			}
			cursor.close();
		}
		payId += 1;
		ContentValues cv = new ContentValues();
		cv.put(MaxPaymentIdTable.COLUMN_MAX_PAY_ID, payId);
		getWritableDatabase().delete(MaxPaymentIdTable.TABLE_MAX_PAY_ID, null, null);
		getWritableDatabase().insert(MaxPaymentIdTable.TABLE_MAX_PAY_ID, null, cv);
		return payId;
	}

	/**
	 * Confirm payment move from temp to real table
	 * @param transactionId
	 */
	public void confirmPayment(int transactionId){
		getWritableDatabase().beginTransaction();
		try {
			getWritableDatabase().execSQL("insert into " + PaymentDetailTable.TABLE_PAYMENT_DETAIL
					+ " select * from " + PaymentDetailTable.TEMP_PAYMENT_DETAIL
					+ " where " + OrderTransTable.COLUMN_TRANS_ID + "=" + transactionId);
			getWritableDatabase().execSQL("delete from " + PaymentDetailTable.TEMP_PAYMENT_DETAIL
					+ " where " + OrderTransTable.COLUMN_TRANS_ID + "=" + transactionId);
			getWritableDatabase().setTransactionSuccessful();
		} finally {
			getWritableDatabase().endTransaction();
		}
	}
	
	/**
	 * @param transactionId
	 * @param computerId
	 * @param payTypeId
	 * @param totalPay
	 * @param pay
	 * @param creditCardNo
	 * @param expireMonth
	 * @param expireYear
	 * @param bankId
	 * @param creditCardTypeId
	 * @param remark
	 * @return The ID of newly row inserted
	 * @throws SQLException
	 */
	public void addPaymentDetail(int transactionId, int computerId, 
			int payTypeId, double totalPay, double pay , String creditCardNo, int expireMonth, 
			int expireYear, int bankId,int creditCardTypeId, String remark) throws SQLException {
		if(checkThisPayTypeIsAdded(transactionId, payTypeId)){
			// update payment
			double totalPayment = getTotalPayAmount(transactionId, payTypeId, true) + totalPay;
			double totalPayed = getTotalPaid(transactionId, payTypeId, true) + pay;
			updatePaymentDetail(transactionId, payTypeId, totalPayment, totalPayed);
		}else{
			int paymentId = getMaxPaymentDetailId();
			ContentValues cv = new ContentValues();
			cv.put(PaymentDetailTable.COLUMN_PAY_ID, paymentId);
			cv.put(OrderTransTable.COLUMN_TRANS_ID, transactionId);
			cv.put(ComputerTable.COLUMN_COMPUTER_ID, computerId);
			cv.put(PayTypeTable.COLUMN_PAY_TYPE_ID, payTypeId);
			cv.put(PaymentDetailTable.COLUMN_TOTAL_PAY_AMOUNT, totalPay);
			cv.put(PaymentDetailTable.COLUMN_PAY_AMOUNT, pay);
			cv.put(CreditCardTable.COLUMN_CREDITCARD_NO, creditCardNo);
			cv.put(CreditCardTable.COLUMN_EXP_MONTH, expireMonth);
			cv.put(CreditCardTable.COLUMN_EXP_YEAR, expireYear);
			cv.put(CreditCardTable.COLUMN_CREDITCARD_TYPE_ID, creditCardTypeId);
			cv.put(BankTable.COLUMN_BANK_ID, bankId);
			cv.put(PaymentDetailTable.COLUMN_REMARK, remark);
			getWritableDatabase().insertOrThrow(PaymentDetailTable.TEMP_PAYMENT_DETAIL, null, cv);
		}
	}

	/**
	 * @param transactionId
	 * @param payTypeId
	 * @return added or not
	 */
	public boolean checkThisPayTypeIsAdded(int transactionId, int payTypeId){
		boolean isAdded = false;
		Cursor cursor = getReadableDatabase().rawQuery(
				"SELECT COUNT(" + PaymentDetailTable.COLUMN_PAY_ID + ")"
				+ " FROM " + PaymentDetailTable.TEMP_PAYMENT_DETAIL
				+ " WHERE " + OrderTransTable.COLUMN_TRANS_ID + "=?"
				+ " AND " + PayTypeTable.COLUMN_PAY_TYPE_ID + "=?", 
				new String[]{String.valueOf(transactionId), String.valueOf(payTypeId)});
		if(cursor.moveToFirst()){
			if(cursor.getInt(0) > 0)
				isAdded = true;
		}
		cursor.close();
		return isAdded;
	}
	
	/**
	 * @param transactionId
	 * @param payTypeId
	 * @param paid
	 * @param amount
	 * @return row affected
	 */
	public int updatePaymentDetail(int transactionId, int payTypeId,
			double paid, double amount){
		ContentValues cv = new ContentValues();
		cv.put(PaymentDetailTable.COLUMN_TOTAL_PAY_AMOUNT, paid);
		cv.put(PaymentDetailTable.COLUMN_PAY_AMOUNT, amount);
		return getWritableDatabase().update(PaymentDetailTable.TEMP_PAYMENT_DETAIL, cv, 
				OrderTransTable.COLUMN_TRANS_ID + "=? "
								+ " AND " + PayTypeTable.COLUMN_PAY_TYPE_ID + "=?", 
				new String[]{
					String.valueOf(transactionId),
					String.valueOf(payTypeId)
				}
		);
	}
	
	/**
	 * @param transactionId
	 * @param payTypeId
	 * @return row affected
	 */
	public int deletePaymentDetail(int transactionId, int payTypeId){
		return getWritableDatabase().delete(PaymentDetailTable.TEMP_PAYMENT_DETAIL,
				OrderTransTable.COLUMN_TRANS_ID + "=? AND "
				+ PayTypeTable.COLUMN_PAY_TYPE_ID + "=?", 
				new String[]{
					String.valueOf(transactionId),
					String.valueOf(payTypeId)});
	}
	
	/**
	 * get payment type by pay type id
	 * @param payTypeId
	 * @return pay type name
	 */
	public String getPaymentTypeName(int payTypeId){
		String paymentType = null;
		Cursor cursor = getReadableDatabase().rawQuery(
				"SELECT " + PayTypeTable.COLUMN_PAY_TYPE_NAME
				+ " FROM " + PayTypeTable.TABLE_PAY_TYPE
				+ " WHERE " + PayTypeTable.COLUMN_PAY_TYPE_ID + "=?", 
				new String[]{
					String.valueOf(payTypeId)
				});
		if(cursor.moveToFirst()){
			paymentType = cursor.getString(0);
		}
		cursor.close();
		return paymentType;
	}
	
	/**
     * @param sessId
	 * @param saleDate
	 * @return total cash amount
	 */
	public double getTotalCash(int sessId, String saleDate){
		double totalCash = 0.0d;
        String selection = " a." + OrderTransTable.COLUMN_SALE_DATE + "=? "
                + " AND a." + OrderTransTable.COLUMN_STATUS_ID + "=?"
                + " AND b." + PayTypeTable.COLUMN_PAY_TYPE_ID + "=?";
        String[] selectionArgs = {
                saleDate,
                String.valueOf(TransactionDao.TRANS_STATUS_SUCCESS),
                String.valueOf(PAY_TYPE_CASH)
        };
        if(sessId != 0){
            selection += " AND a." + SessionTable.COLUMN_SESS_ID + "=?";
            selectionArgs = new String[]{
                    saleDate,
                    String.valueOf(TransactionDao.TRANS_STATUS_SUCCESS),
                    String.valueOf(PAY_TYPE_CASH),
                    String.valueOf(sessId)
            };
        }
        String sql = " SELECT SUM(b." + PaymentDetailTable.COLUMN_PAY_AMOUNT + ") "
                + " FROM " + OrderTransTable.TABLE_ORDER_TRANS + " a "
                + " INNER JOIN " + PaymentDetailTable.TABLE_PAYMENT_DETAIL + " b "
                + " ON a." + OrderTransTable.COLUMN_TRANS_ID + "=b." + OrderTransTable.COLUMN_TRANS_ID
                + " WHERE " + selection;
		Cursor cursor = getReadableDatabase().rawQuery(
				sql, selectionArgs);
		if(cursor.moveToFirst()){
			totalCash = cursor.getDouble(0);
		}
		cursor.close();
		return totalCash;
	}
	
	/**
	 * @param sessionDate
	 * @return total receipt amount
	 */
	public double getTotalPaymentReceipt(String sessionDate) {
		double totalReceiptAmount = 0.0f;
		Cursor cursor = getReadableDatabase().rawQuery(
				"SELECT "
				+ " SUM (b." + PaymentDetailTable.COLUMN_PAY_AMOUNT + ") "
				+ " FROM " + OrderTransTable.TABLE_ORDER_TRANS + " a "
				+ " LEFT JOIN " + PaymentDetailTable.TABLE_PAYMENT_DETAIL + " b "
				+ " ON a." + OrderTransTable.COLUMN_TRANS_ID + "=b." + OrderTransTable.COLUMN_TRANS_ID 
				+ " WHERE a." + OrderTransTable.COLUMN_SALE_DATE + "=? "
				+ " AND a." + OrderTransTable.COLUMN_STATUS_ID + " =? ",
				new String[] { sessionDate,
						String.valueOf(TransactionDao.TRANS_STATUS_SUCCESS)
				});
		if (cursor.moveToFirst()) {
			totalReceiptAmount = cursor.getDouble(0);
		}
		cursor.close();
		return totalReceiptAmount;
	}

	/**
	 * @param sessionId
	 * @return total receipt amount specific by sessionId
	 */
	public double getTotalPaymentReceipt(int sessionId) {
		double totalReceiptAmount = 0.0f;
		Cursor cursor = getReadableDatabase().rawQuery(
				"SELECT "
				+ " SUM (b." + PaymentDetailTable.COLUMN_PAY_AMOUNT + ") "
				+ " FROM " + OrderTransTable.TABLE_ORDER_TRANS + " a "
				+ " LEFT JOIN " + PaymentDetailTable.TABLE_PAYMENT_DETAIL + " b "
				+ " ON a." + OrderTransTable.COLUMN_TRANS_ID + "=b." + OrderTransTable.COLUMN_TRANS_ID 
				+ " WHERE a." + SessionTable.COLUMN_SESS_ID + "=? "
				+ " AND a." + OrderTransTable.COLUMN_STATUS_ID + " =? ",
				new String[] { 
						String.valueOf(sessionId),
						String.valueOf(TransactionDao.TRANS_STATUS_SUCCESS)
				});
		if (cursor.moveToFirst()) {
			totalReceiptAmount = cursor.getDouble(0);
		}
		cursor.close();
		return totalReceiptAmount;
	}
	
	/**
	 * @param transactionId
	 * @param isLoadTemp
	 * @return total paid waste
	 */
	public double getTotalPaidWaste(int transactionId, boolean isLoadTemp){
		double totalPaid = 0.0d;
		Cursor cursor = queryPaymentDetail(
				isLoadTemp ? PaymentDetailWasteTable.TEMP_PAYMENT_DETAIL_WASTE : PaymentDetailWasteTable.TABLE_PAYMENT_DETAIL_WASTE,
				new String[]{
					"sum(" + PaymentDetailTable.COLUMN_PAY_AMOUNT + ")"
				},
				OrderTransTable.COLUMN_TRANS_ID + "=?",
				new String[]{
					String.valueOf(transactionId)
				}, null, null);
		if(cursor.moveToFirst()){
			totalPaid = cursor.getDouble(0);
		}
		cursor.close();
		return totalPaid;
	}
	
	/**
	 * @param transactionId
	 * @param payTypeId
	 * @param isLoadTemp
	 * @return total paid by pay type
	 */
	public double getTotalPaid(int transactionId, int payTypeId, boolean isLoadTemp){
		double totalPaid = 0.0d;
		Cursor cursor = queryPaymentDetail(
				isLoadTemp ? PaymentDetailTable.TEMP_PAYMENT_DETAIL : PaymentDetailTable.TABLE_PAYMENT_DETAIL,
				new String[]{
					"sum(" + PaymentDetailTable.COLUMN_PAY_AMOUNT + ")"
				},
				OrderTransTable.COLUMN_TRANS_ID + "=?"
				+ " and " + PayTypeTable.COLUMN_PAY_TYPE_ID + "=?",
				new String[]{
					String.valueOf(transactionId),
					String.valueOf(payTypeId)
				}, null, null);
		if(cursor.moveToFirst()){
			totalPaid = cursor.getDouble(0);
		}
		cursor.close();
		return totalPaid;
	}
	
	/**
	 * Get total payed
	 * @param transactionId
	 * @param isLoadTemp
	 * @return total paid amount (real paid)
	 */
	public double getTotalPaid(int transactionId, boolean isLoadTemp){
		double totalPaid = 0.0d;
		Cursor cursor = queryPaymentDetail(
				isLoadTemp ? PaymentDetailTable.TEMP_PAYMENT_DETAIL : PaymentDetailTable.TABLE_PAYMENT_DETAIL,
				new String[]{
					"sum(" + PaymentDetailTable.COLUMN_PAY_AMOUNT + ")"
				},
				OrderTransTable.COLUMN_TRANS_ID + "=?",
				new String[]{
					String.valueOf(transactionId)
				}, null, null);
		if(cursor.moveToFirst()){
			totalPaid = cursor.getDouble(0);
		}
		cursor.close();
		return totalPaid;
	}
	
	/**
	 * @param transactionId
	 * @param isLoadTemp
	 * @return total waste payment by transactionId
	 */
	public double getTotalPayAmountWaste(int transactionId, boolean isLoadTemp){
		double totalPaid = 0.0d;
		Cursor cursor = queryPaymentDetail(
				isLoadTemp ? PaymentDetailWasteTable.TEMP_PAYMENT_DETAIL_WASTE : PaymentDetailWasteTable.TABLE_PAYMENT_DETAIL_WASTE,
				new String[]{
					"sum(" + PaymentDetailTable.COLUMN_TOTAL_PAY_AMOUNT + ")",
				}, 
				OrderTransTable.COLUMN_TRANS_ID + "=?",
				new String[]{
					String.valueOf(transactionId)
				}, null, null);
		if(cursor.moveToFirst()){
			totalPaid = cursor.getDouble(0);
		}
		cursor.close();
		return totalPaid;
	}
	
	/**
	 * @param transactionId
	 * @param payTypeId
	 * @param isLoadTemp
	 * @return total pay amount by pay type
	 */
	public double getTotalPayAmount(int transactionId, int payTypeId, boolean isLoadTemp){
		double totalPaid = 0.0d;
		Cursor cursor = queryPaymentDetail(
				isLoadTemp ? PaymentDetailTable.TEMP_PAYMENT_DETAIL : PaymentDetailTable.TABLE_PAYMENT_DETAIL,
				new String[]{
					"sum(" + PaymentDetailTable.COLUMN_TOTAL_PAY_AMOUNT + ")",
				}, 
				OrderTransTable.COLUMN_TRANS_ID + "=?"
				+ " and " + PayTypeTable.COLUMN_PAY_TYPE_ID + "=?",
				new String[]{
					String.valueOf(transactionId),
					String.valueOf(payTypeId)
				}, null, null);
		if(cursor.moveToFirst()){
			totalPaid = cursor.getDouble(0);
		}
		cursor.close();
		return totalPaid;
	}
	
	/**
	 * Get total pay
	 * @param transactionId
	 * @param isLoadTemp
	 * @return total pay amount
	 */
	public double getTotalPayAmount(int transactionId, boolean isLoadTemp){
		double totalPaid = 0.0d;
		Cursor cursor = queryPaymentDetail(
				isLoadTemp ? PaymentDetailTable.TEMP_PAYMENT_DETAIL : PaymentDetailTable.TABLE_PAYMENT_DETAIL,
				new String[]{
					"sum(" + PaymentDetailTable.COLUMN_TOTAL_PAY_AMOUNT + ")",
				}, 
				OrderTransTable.COLUMN_TRANS_ID + "=?",
				new String[]{
					String.valueOf(transactionId)
				}, null, null);
		if(cursor.moveToFirst()){
			totalPaid = cursor.getDouble(0);
		}
		cursor.close();
		return totalPaid;
	}
	
	/**
	 * @param table
	 * @param columns
	 * @param selection
	 * @param selectArgs
	 * @param groupBy
	 * @param orderBy
	 * @return Cursor may be null
	 */
	private Cursor queryPaymentDetail(String table, String[] columns, String selection, 
			String[] selectArgs, String groupBy, String orderBy){
		StringBuilder strQuery = new StringBuilder(" select ");
		for(int i = 0; i < columns.length; i++){
			String column = columns[i];
			strQuery.append(column);
			if(i < columns.length - 1)
				strQuery.append(",");
		}
		strQuery.append(" from ");
		strQuery.append(table);
		strQuery.append(" where ");
		strQuery.append(selection);
		if(groupBy != null)
			strQuery.append(groupBy);
		if(orderBy != null)
			strQuery.append(orderBy);
		return getReadableDatabase().rawQuery(strQuery.toString(), selectArgs);
	}
	
	/**
	 * @param transactionId
	 * @return List<MPOSPaymentDetail>
	 */
	public List<MPOSPaymentDetail> listPayment(int transactionId){
		List<MPOSPaymentDetail> paymentLst = new ArrayList<MPOSPaymentDetail>();
		Cursor cursor = getReadableDatabase().rawQuery(
				" SELECT a." + PaymentDetailTable.COLUMN_PAY_ID + ", " 
						+ " a." + PayTypeTable.COLUMN_PAY_TYPE_ID + ", " 
						+ " a." + PaymentDetailTable.COLUMN_TOTAL_PAY_AMOUNT + ", " 
						+ " a." + PaymentDetailTable.COLUMN_REMARK + ", " 
						+ " b." + PayTypeTable.COLUMN_PAY_TYPE_CODE + ", " 
						+ " b." + PayTypeTable.COLUMN_PAY_TYPE_NAME 
						+ " FROM " + PaymentDetailTable.TEMP_PAYMENT_DETAIL + " a " 
						+ " LEFT JOIN " + PayTypeTable.TABLE_PAY_TYPE + " b "
						+ " ON a." + PayTypeTable.COLUMN_PAY_TYPE_ID 
						+ "=b." + PayTypeTable.COLUMN_PAY_TYPE_ID 
						+ " WHERE a." + OrderTransTable.COLUMN_TRANS_ID + "=?"
						+ " GROUP BY a." + PayTypeTable.COLUMN_PAY_TYPE_ID,
				new String[]{
						String.valueOf(transactionId)});
		if(cursor.moveToFirst()){
			do{
				MPOSPaymentDetail payDetail = new MPOSPaymentDetail();
				payDetail.setTransactionId(transactionId);
				payDetail.setPaymentDetailId(cursor.getInt(cursor.getColumnIndex(PaymentDetailTable.COLUMN_PAY_ID)));
				payDetail.setPayTypeId(cursor.getInt(cursor.getColumnIndex(PayTypeTable.COLUMN_PAY_TYPE_ID)));
				payDetail.setPayTypeCode(cursor.getString(cursor.getColumnIndex(PayTypeTable.COLUMN_PAY_TYPE_CODE)));
				payDetail.setPayTypeName(cursor.getString(cursor.getColumnIndex(PayTypeTable.COLUMN_PAY_TYPE_NAME)));
				payDetail.setTotalPay(cursor.getDouble(cursor.getColumnIndex(PaymentDetailTable.COLUMN_TOTAL_PAY_AMOUNT)));
				payDetail.setRemark(cursor.getString(cursor.getColumnIndex(PaymentDetailTable.COLUMN_REMARK)));
				paymentLst.add(payDetail);
			}while(cursor.moveToNext());
		}
		cursor.close();
		return paymentLst;
	}
	
	public int countPayTypeWaste(){
		int total = 0;
		Cursor cursor = getReadableDatabase().rawQuery("select count(*) from " 
				+ PayTypeFinishWasteTable.TABLE_PAY_TYPE_FINISH_WASTE, null);
		if(cursor.moveToFirst()){
			total = cursor.getInt(0);
		}
		cursor.close();
		return total;
	}
	
	/**
	 * @return List<com.synature.pos.PayType>
	 */
	public List<com.synature.pos.PayType> listPaytypeWest(){
		List<com.synature.pos.PayType> westLst = null;
		Cursor cursor = getReadableDatabase().query(PayTypeFinishWasteTable.TABLE_PAY_TYPE_FINISH_WASTE, 
				new String[]{
					PayTypeTable.COLUMN_PAY_TYPE_ID,
					PayTypeTable.COLUMN_PAY_TYPE_CODE,
					PayTypeTable.COLUMN_PAY_TYPE_NAME,
					PayTypeFinishWasteTable.COLUMN_DOCUMENT_TYPE_ID,
					PayTypeFinishWasteTable.COLUMN_DOCUMENT_TYPE_HEADER,
				}, null, null, null, null, COLUMN_ORDERING);
		if(cursor.moveToFirst()){
			westLst = new ArrayList<com.synature.pos.PayType>();
			do{
				com.synature.pos.PayType west = new com.synature.pos.PayType();
				west.setPayTypeID(cursor.getInt(cursor.getColumnIndex(PayTypeTable.COLUMN_PAY_TYPE_ID)));
				west.setPayTypeCode(cursor.getString(cursor.getColumnIndex(PayTypeTable.COLUMN_PAY_TYPE_CODE)));
				west.setPayTypeName(cursor.getString(cursor.getColumnIndex(PayTypeTable.COLUMN_PAY_TYPE_NAME)));
				west.setDocumentTypeID(cursor.getInt(cursor.getColumnIndex(PayTypeFinishWasteTable.COLUMN_DOCUMENT_TYPE_ID)));
				west.setDocumentTypeHeader(cursor.getString(cursor.getColumnIndex(PayTypeFinishWasteTable.COLUMN_DOCUMENT_TYPE_HEADER)));
				westLst.add(west);
			}while(cursor.moveToNext());
		}
		cursor.close();
		return westLst;
	}
	
	public void addPaymentDetailWaste(int transactionId, int computerId, 
			int payTypeId, double payAmount, String remark) throws SQLException{
		int maxId = getMaxPaymentDetailWasteId();
		ContentValues cv = new ContentValues();
		cv.put(PaymentDetailTable.COLUMN_PAY_ID, maxId);
		cv.put(OrderTransTable.COLUMN_TRANS_ID, transactionId);
		cv.put(ComputerTable.COLUMN_COMPUTER_ID, computerId);
		cv.put(PayTypeTable.COLUMN_PAY_TYPE_ID, payTypeId);
		cv.put(PaymentDetailTable.COLUMN_PAY_AMOUNT, payAmount);
		cv.put(PaymentDetailTable.COLUMN_REMARK, remark);
		getWritableDatabase().insertOrThrow(PaymentDetailWasteTable.TEMP_PAYMENT_DETAIL_WASTE, 
				PaymentDetailTable.COLUMN_REMARK, cv);
	}

	public void deletePaymentDetailWaste(int transactionId){
		delete(OrderTransTable.COLUMN_TRANS_ID + "=?", 
				new String[]{
					String.valueOf(transactionId)
				});
	}

	public void deletePaymentDetailWaste(int transactionId, int paymentId){
		delete(OrderTransTable.COLUMN_TRANS_ID + "=?"
				+ " AND " + PaymentDetailTable.COLUMN_PAY_ID + "=?", 
				new String[]{
					String.valueOf(transactionId),
					String.valueOf(paymentId)
				});
	}
	
	private void delete(String whereClause, String[] whereArgs){
		getWritableDatabase().delete(
				PaymentDetailWasteTable.TEMP_PAYMENT_DETAIL_WASTE,
				whereClause,
				whereArgs);	
	}
	
	public void confirmWastePayment(int transactionId) throws SQLException{
		String where = OrderTransTable.COLUMN_TRANS_ID + "=" + transactionId;
		getWritableDatabase().beginTransaction();
		try {
			getWritableDatabase().execSQL("insert into " + PaymentDetailWasteTable.TABLE_PAYMENT_DETAIL_WASTE
					+ " select * from " + PaymentDetailWasteTable.TEMP_PAYMENT_DETAIL_WASTE
					+ " where " + where);
			getWritableDatabase().execSQL("delete from " + PaymentDetailWasteTable.TEMP_PAYMENT_DETAIL_WASTE
					+ " where " + where);
			getWritableDatabase().setTransactionSuccessful();
		} finally {
			getWritableDatabase().endTransaction();
		}
	}
	
	public int getMaxPaymentDetailWasteId(){
		int maxId = 0;
		Cursor cursor = getReadableDatabase().rawQuery(
				"select max(" + PaymentDetailTable.COLUMN_PAY_ID + ")"
				+ " from " + PaymentDetailWasteTable.TABLE_PAYMENT_DETAIL_WASTE, null);
		if(cursor.moveToFirst()){
			maxId = cursor.getInt(0);
		}
		return maxId + 1;
	}
	
	/**
	 * @param payTypeLst
	 */
	public void insertPaytypeFinishWaste(List<com.synature.pos.PayType> payTypeLst){
		getWritableDatabase().beginTransaction();
		try {
			getWritableDatabase().delete(PayTypeFinishWasteTable.TABLE_PAY_TYPE_FINISH_WASTE, null, null);
			for(com.synature.pos.PayType payType : payTypeLst){
				ContentValues cv = new ContentValues();
				cv.put(PayTypeTable.COLUMN_PAY_TYPE_ID, payType.getPayTypeID());
				cv.put(PayTypeTable.COLUMN_PAY_TYPE_CODE, payType.getPayTypeCode());
				cv.put(PayTypeTable.COLUMN_PAY_TYPE_NAME, payType.getPayTypeName());
				cv.put(BaseColumn.COLUMN_DOCUMENT_TYPE_ID, payType.getDocumentTypeID());
				cv.put(BaseColumn.COLUMN_DOCUMENT_TYPE_HEADER, payType.getDocumentTypeHeader());
				cv.put(BaseColumn.COLUMN_ORDERING, payType.getOrdering());
				getWritableDatabase().insertOrThrow(PayTypeFinishWasteTable.TABLE_PAY_TYPE_FINISH_WASTE, null, cv);
			}
			getWritableDatabase().setTransactionSuccessful();
		} finally {
			getWritableDatabase().endTransaction();
		}
	}
	
	/**
	 * @param payTypeLst
	 */
	public void insertPaytype(List<com.synature.pos.PayType> payTypeLst){
		getWritableDatabase().beginTransaction();
		try {
			getWritableDatabase().delete(PayTypeTable.TABLE_PAY_TYPE, null, null);
			for(com.synature.pos.PayType payType : payTypeLst){
				ContentValues cv = new ContentValues();
				cv.put(PayTypeTable.COLUMN_PAY_TYPE_ID, payType.getPayTypeID());
				cv.put(PayTypeTable.COLUMN_PAY_TYPE_CODE, payType.getPayTypeCode());
				cv.put(PayTypeTable.COLUMN_PAY_TYPE_NAME, payType.getPayTypeName());
				cv.put(BaseColumn.COLUMN_ORDERING, payType.getOrdering());
				getWritableDatabase().insertOrThrow(PayTypeTable.TABLE_PAY_TYPE, null, cv);
			}
			getWritableDatabase().setTransactionSuccessful();
		} finally {
			getWritableDatabase().endTransaction();
		}
	}
}
