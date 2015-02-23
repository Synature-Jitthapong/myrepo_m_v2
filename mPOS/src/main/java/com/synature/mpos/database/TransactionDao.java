package com.synature.mpos.database;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.synature.mpos.Utils;
import com.synature.mpos.database.model.Comment;
import com.synature.mpos.database.model.OrderComment;
import com.synature.mpos.database.model.OrderDetail;
import com.synature.mpos.database.model.OrderSet;
import com.synature.mpos.database.model.OrderTransaction;
import com.synature.mpos.database.table.BaseColumn;
import com.synature.mpos.database.table.ComputerTable;
import com.synature.mpos.database.table.MaxOrderIdTable;
import com.synature.mpos.database.table.MaxTransIdTable;
import com.synature.mpos.database.table.OrderDetailTable;
import com.synature.mpos.database.table.OrderTransTable;
import com.synature.mpos.database.table.PaymentDetailTable;
import com.synature.mpos.database.table.PaymentDetailWasteTable;
import com.synature.mpos.database.table.ProductComponentGroupTable;
import com.synature.mpos.database.table.ProductComponentTable;
import com.synature.mpos.database.table.ProductTable;
import com.synature.mpos.database.table.PromotionPriceGroupTable;
import com.synature.mpos.database.table.SessionDetailTable;
import com.synature.mpos.database.table.SessionTable;
import com.synature.mpos.database.table.ShopTable;
import com.synature.mpos.database.table.StaffTable;
import com.synature.pos.ProgramFeature;

/**
 * 
 * @author j1tth4
 * 
 */
public class TransactionDao extends MPOSDatabase {

	/**
	 * New transaction status
	 */
	public static final int TRANS_STATUS_NEW = 1;

	/**
	 * Success transaction status
	 */
	public static final int TRANS_STATUS_SUCCESS = 2;

	/**
	 * Void transaction status
	 */
	public static final int TRANS_STATUS_VOID = 8;

	/**
	 * Hold transaction status
	 */
	public static final int TRANS_STATUS_HOLD = 9;
	
	/**
	 * Waste success status
	 */
	public static final int WASTE_TRANS_STATUS_SUCCESS = 11;
	
	/**
	 * Waste void status
	 */
	public static final int WASTE_TRANS_STATUS_VOID = 13;

    /**
     * Order status normal
     */
    public static final int ORDER_STATUS_NORMAL = 1;

    /**
     * Order status void
     */
    public static final int ORDER_STATUS_VOID = 4;

    /**
     * Order status free
     */
    public static final int ORDER_STATUS_FREE = 5;

	/**
	 * All columns
	 */
	public static final String[] ALL_TRANS_COLUMNS = {
		BaseColumn.COLUMN_UUID,
		OrderTransTable.COLUMN_TRANS_ID,
		ComputerTable.COLUMN_COMPUTER_ID,
		ShopTable.COLUMN_SHOP_ID,
		OrderTransTable.COLUMN_OPEN_STAFF,
		OrderTransTable.COLUMN_OPEN_TIME,
		OrderTransTable.COLUMN_CLOSE_TIME,
		OrderTransTable.COLUMN_PAID_TIME,
		OrderTransTable.COLUMN_PAID_STAFF_ID,
		OrderTransTable.COLUMN_DOC_TYPE_ID,
		OrderTransTable.COLUMN_STATUS_ID,
		OrderTransTable.COLUMN_RECEIPT_YEAR,
		OrderTransTable.COLUMN_RECEIPT_MONTH,
		OrderTransTable.COLUMN_RECEIPT_ID,
		OrderTransTable.COLUMN_RECEIPT_NO,
		OrderTransTable.COLUMN_SALE_DATE,
		OrderTransTable.COLUMN_TRANS_VAT,
		OrderTransTable.COLUMN_TRANS_VATABLE,
		SessionTable.COLUMN_SESS_ID,
		OrderTransTable.COLUMN_VOID_STAFF_ID,
		OrderTransTable.COLUMN_VOID_REASON,
		OrderTransTable.COLUMN_VOID_TIME,
		OrderTransTable.COLUMN_TRANS_NOTE,
		ProductTable.COLUMN_SALE_MODE,
		ProductTable.COLUMN_VAT_RATE,
		OrderTransTable.COLUMN_TRANS_EXCLUDE_VAT,
		PromotionPriceGroupTable.COLUMN_PRICE_GROUP_ID,
		OrderTransTable.COLUMN_EJ,
		OrderTransTable.COLUMN_EJ_VOID
	};

	/**
	 * All order columns
	 */
	public static final String[] ALL_ORDER_COLUMNS = {
		OrderDetailTable.COLUMN_ORDER_ID,
		OrderTransTable.COLUMN_TRANS_ID,
		ComputerTable.COLUMN_COMPUTER_ID,
		OrderDetailTable.COLUMN_ORDER_QTY,
		OrderDetailTable.COLUMN_TOTAL_RETAIL_PRICE,
		OrderDetailTable.COLUMN_TOTAL_SALE_PRICE,
		OrderDetailTable.COLUMN_PRICE_DISCOUNT,
		OrderDetailTable.COLUMN_TOTAL_VAT,
		OrderDetailTable.COLUMN_TOTAL_VAT_EXCLUDE,
		OrderDetailTable.COLUMN_REMARK,
		OrderDetailTable.COLUMN_PARENT_ORDER_ID,
		ProductTable.COLUMN_PRODUCT_ID,
		ProductTable.COLUMN_PRODUCT_TYPE_ID,
		ProductTable.COLUMN_PRODUCT_PRICE,
		ProductTable.COLUMN_SALE_MODE,
		ProductTable.COLUMN_VAT_TYPE,
		PromotionPriceGroupTable.COLUMN_PROMOTION_TYPE_ID,
		PromotionPriceGroupTable.COLUMN_PRICE_GROUP_ID,
		PromotionPriceGroupTable.COLUMN_COUPON_HEADER
	};
	
	public TransactionDao(Context context) {
		super(context);
	}

	/**
	 * Count order that not confirm
	 * @param saleDate
	 * @return total order that not confirm
	 */
	public int countOrderStatusNotSuccess(String saleDate){
		int total = 0;
		Cursor cursor = getReadableDatabase().rawQuery(
				"SELECT COUNT(b." + OrderDetailTable.COLUMN_ORDER_ID + ")"
				+ " FROM " + OrderTransTable.TEMP_ORDER_TRANS + " a "
				+ " LEFT JOIN " + OrderDetailTable.TEMP_ORDER + " b "
				+ " ON a." + OrderTransTable.COLUMN_TRANS_ID + "=b." + OrderTransTable.COLUMN_TRANS_ID
				+ " WHERE a." + OrderTransTable.COLUMN_STATUS_ID + " in (?, ?)"
				+ " AND a." + OrderTransTable.COLUMN_SALE_DATE + "=?",
				new String[]{
					String.valueOf(TransactionDao.TRANS_STATUS_NEW),
					String.valueOf(TransactionDao.TRANS_STATUS_HOLD),
					saleDate
				});
		if(cursor.moveToFirst()){
			total = cursor.getInt(0);
		}
		cursor.close();
		return total;
	}

	/**
	 * @param sessId
	 * @param saleDate
	 * @return OrderTransaction
	 */
	public OrderTransaction getSummaryTransaction(int sessId, String saleDate) {
		OrderTransaction trans = null;
		Cursor cursor = querySummaryTransaction(
				OrderTransTable.COLUMN_SALE_DATE + "=?"
				+ " AND " + SessionTable.COLUMN_SESS_ID + "=?"
				+ " AND " + OrderTransTable.COLUMN_STATUS_ID + " =? ",
				new String[] {
					saleDate,
					String.valueOf(sessId),
					String.valueOf(TransactionDao.TRANS_STATUS_SUCCESS)
				});
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				trans = toSummaryOrderTransaction(cursor);
			}
			cursor.close();
		}
		return trans;
	}
	
	/**
	 * @param saleDateFrom
	 * @param saleDateTo
	 * @return OrderTransaction
	 */
	public OrderTransaction getSummaryTransaction(String saleDateFrom, String saleDateTo) {
		OrderTransaction trans = null;
		Cursor cursor = querySummaryTransaction(
				OrderTransTable.COLUMN_SALE_DATE + " BETWEEN ? AND ? "
				+ " AND " + OrderTransTable.COLUMN_STATUS_ID + " =? ",
				new String[] {
					saleDateFrom,
					saleDateTo,
					String.valueOf(TransactionDao.TRANS_STATUS_SUCCESS)
				});
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				trans = toSummaryOrderTransaction(cursor);
			}
			cursor.close();
		}
		return trans;
	}
	
	/**
	 * @param saleDate
	 * @return OrderTransaction
	 */
	public OrderTransaction getSummaryTransaction(String saleDate) {
		OrderTransaction trans = null;
		Cursor cursor = querySummaryTransaction(
				OrderTransTable.COLUMN_SALE_DATE + "=?"
				+ " AND " + OrderTransTable.COLUMN_STATUS_ID + " =? ",
				new String[] {
					saleDate,
					String.valueOf(TransactionDao.TRANS_STATUS_SUCCESS)
				});
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				trans = toSummaryOrderTransaction(cursor);
			}
			cursor.close();
		}
		return trans;
	}
	
	/**
	 * query summary transaction
	 * @param selection
	 * @param selectionArgs
	 * @return Cursor
	 */
	private Cursor querySummaryTransaction(String selection, String[] selectionArgs){
		String sql = "SELECT " + OrderTransTable.COLUMN_TRANS_ID + ", "
				+ ComputerTable.COLUMN_COMPUTER_ID + ", "
				+ " SUM(" + OrderTransTable.COLUMN_TRANS_VATABLE + ") AS " + OrderTransTable.COLUMN_TRANS_VATABLE + ","
				+ " SUM(" + OrderTransTable.COLUMN_TRANS_VAT + ") AS " + OrderTransTable.COLUMN_TRANS_VAT + ","
				+ " SUM(" + OrderTransTable.COLUMN_TRANS_EXCLUDE_VAT + ") AS " + OrderTransTable.COLUMN_TRANS_EXCLUDE_VAT + ","
				+ OrderTransTable.COLUMN_STATUS_ID + ","
				+ OrderTransTable.COLUMN_PAID_TIME + ","
				+ OrderTransTable.COLUMN_VOID_TIME + ","
				+ OrderTransTable.COLUMN_VOID_STAFF_ID + ","
				+ OrderTransTable.COLUMN_VOID_REASON + ","
				+ OrderTransTable.COLUMN_RECEIPT_NO + ","
				+ OrderTransTable.COLUMN_OPEN_STAFF
				+ " FROM " + OrderTransTable.TABLE_ORDER_TRANS
				+ " WHERE " + selection;
		return getReadableDatabase().rawQuery(sql, selectionArgs);
	}
	
	/**
	 * @param transId
	 * @param isLoadTemp
	 * @return OrderTransaction
	 */
	public OrderTransaction getTransactionWaste(int transId, boolean isLoadTemp) {
		OrderTransaction trans = null;
		Cursor cursor = getReadableDatabase().query(
				isLoadTemp ? OrderTransTable.TEMP_ORDER_TRANS : OrderTransTable.TABLE_ORDER_TRANS_WASTE,
				ALL_TRANS_COLUMNS, OrderTransTable.COLUMN_TRANS_ID + "=?",
				new String[] { 
					String.valueOf(transId) 
				}, null, null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				trans = toOrderTransaction(cursor);
			}
			cursor.close();
		}
		return trans;
	}
	
	/**
	 * @param transId
	 * @param computerId
	 * @param isLoadTemp
	 * @return OrderTransaction
	 */
	public OrderTransaction getTransaction(int transId, boolean isLoadTemp) {
		OrderTransaction trans = null;
		Cursor cursor = getReadableDatabase().query(
				isLoadTemp ? OrderTransTable.TEMP_ORDER_TRANS : OrderTransTable.TABLE_ORDER_TRANS,
				ALL_TRANS_COLUMNS, OrderTransTable.COLUMN_TRANS_ID + "=?",
				new String[] { 
					String.valueOf(transId) 
				}, null, null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				trans = toOrderTransaction(cursor);
			}
			cursor.close();
		}
		return trans;
	}
	
	private OrderTransaction toOrderTransaction(Cursor cursor){
		OrderTransaction trans = new OrderTransaction();
		trans.setTransactionId(cursor.getInt(cursor.getColumnIndex(OrderTransTable.COLUMN_TRANS_ID)));
		trans.setTransactionVatable(cursor.getDouble(cursor.getColumnIndex(OrderTransTable.COLUMN_TRANS_VATABLE)));
		trans.setTransactionVat(cursor.getDouble(cursor.getColumnIndex(OrderTransTable.COLUMN_TRANS_VAT)));
		trans.setTransactionVatExclude(cursor.getDouble(cursor.getColumnIndex(OrderTransTable.COLUMN_TRANS_EXCLUDE_VAT)));
		trans.setComputerId(cursor.getInt(cursor.getColumnIndex(ComputerTable.COLUMN_COMPUTER_ID)));
		trans.setTransactionStatusId(cursor.getInt(cursor.getColumnIndex(OrderTransTable.COLUMN_STATUS_ID)));
		trans.setPaidTime(cursor.getString(cursor.getColumnIndex(OrderTransTable.COLUMN_PAID_TIME)));
		trans.setVoidTime(cursor.getString(cursor.getColumnIndex(OrderTransTable.COLUMN_VOID_TIME)));
		trans.setVoidStaffId(cursor.getInt(cursor.getColumnIndex(OrderTransTable.COLUMN_VOID_STAFF_ID)));
		trans.setVoidReason(cursor.getString(cursor.getColumnIndex(OrderTransTable.COLUMN_VOID_REASON)));
		trans.setReceiptNo(cursor.getString(cursor.getColumnIndex(OrderTransTable.COLUMN_RECEIPT_NO)));
		trans.setOpenStaffId(cursor.getInt(cursor.getColumnIndex(OrderTransTable.COLUMN_OPEN_STAFF)));
		trans.setPromotionPriceGroupId(cursor.getInt(cursor.getColumnIndex(PromotionPriceGroupTable.COLUMN_PRICE_GROUP_ID)));
		trans.setTransactionNote(cursor.getString(cursor.getColumnIndex(OrderTransTable.COLUMN_TRANS_NOTE)));
		trans.setEj(cursor.getString(cursor.getColumnIndex(OrderTransTable.COLUMN_EJ)));
		trans.setEjVoid(cursor.getString(cursor.getColumnIndex(OrderTransTable.COLUMN_EJ_VOID)));
		return trans;
	}
	
	private OrderTransaction toSummaryOrderTransaction(Cursor cursor){
		OrderTransaction trans = new OrderTransaction();
		trans.setTransactionId(cursor.getInt(cursor.getColumnIndex(OrderTransTable.COLUMN_TRANS_ID)));
		trans.setTransactionVatable(cursor.getDouble(cursor.getColumnIndex(OrderTransTable.COLUMN_TRANS_VATABLE)));
		trans.setTransactionVat(cursor.getDouble(cursor.getColumnIndex(OrderTransTable.COLUMN_TRANS_VAT)));
		trans.setTransactionVatExclude(cursor.getDouble(cursor.getColumnIndex(OrderTransTable.COLUMN_TRANS_EXCLUDE_VAT)));
		trans.setComputerId(cursor.getInt(cursor.getColumnIndex(ComputerTable.COLUMN_COMPUTER_ID)));
		trans.setTransactionStatusId(cursor.getInt(cursor.getColumnIndex(OrderTransTable.COLUMN_STATUS_ID)));
		trans.setPaidTime(cursor.getString(cursor.getColumnIndex(OrderTransTable.COLUMN_PAID_TIME)));
		trans.setVoidTime(cursor.getString(cursor.getColumnIndex(OrderTransTable.COLUMN_VOID_TIME)));
		trans.setVoidStaffId(cursor.getInt(cursor.getColumnIndex(OrderTransTable.COLUMN_VOID_STAFF_ID)));
		trans.setVoidReason(cursor.getString(cursor.getColumnIndex(OrderTransTable.COLUMN_VOID_REASON)));
		trans.setReceiptNo(cursor.getString(cursor.getColumnIndex(OrderTransTable.COLUMN_RECEIPT_NO)));
		trans.setOpenStaffId(cursor.getInt(cursor.getColumnIndex(OrderTransTable.COLUMN_OPEN_STAFF)));
		return trans;
	}

	/**
	 * Get summary of void order
	 * @param sessId
	 * @param sessDate
	 * @return OrderDetail
	 */
	public OrderDetail getSummaryVoidOrderInDay(int sessId, String sessDate) {
		OrderDetail orderDetail = new OrderDetail();
		String selection = OrderTransTable.COLUMN_SALE_DATE + "=?"
				+ " AND " + OrderTransTable.COLUMN_STATUS_ID + "=?";
		String[] selectionArgs = new String[]{
			sessDate, 
			String.valueOf(TransactionDao.TRANS_STATUS_VOID)
		};
		if(sessId != 0){
			selection += " AND " + SessionTable.COLUMN_SESS_ID + "=?";
			selectionArgs = new String[]{
				sessDate, 
				String.valueOf(TransactionDao.TRANS_STATUS_VOID),
				String.valueOf(sessId)
			};
		}
		String sql = "SELECT " + OrderTransTable.COLUMN_TRANS_ID + ", "
				+ " COUNT(" + OrderTransTable.COLUMN_TRANS_ID + ") AS TotalVoid, "
				+ " SUM(" + OrderTransTable.COLUMN_TRANS_VATABLE + ") AS Vatable "
				+ " FROM " + OrderTransTable.TABLE_ORDER_TRANS
				+ " WHERE " + selection;
		Cursor cursor = getReadableDatabase().rawQuery(sql, selectionArgs);
		if (cursor.moveToFirst()) {
			orderDetail.setOrderQty(cursor.getDouble(cursor.getColumnIndex("TotalVoid")));
			orderDetail.setTotalSalePrice(cursor.getDouble(cursor.getColumnIndex("Vatable")));
		}
		cursor.close();
		return orderDetail;
	}
	
	/**
	 * @param sessId
	 * @param dateFrom
	 * @param dateTo
	 * @return OrderDetail
	 */
	public OrderDetail getSummaryOrder(int sessId, String dateFrom, String dateTo) {
		OrderDetail ord = new OrderDetail();
		String whereArg = " a." + OrderTransTable.COLUMN_SALE_DATE + " BETWEEN ? AND ? "
		                + " AND a." + OrderTransTable.COLUMN_STATUS_ID + "=? "
						+ " AND b." + ProductTable.COLUMN_PRODUCT_TYPE_ID + " IN(?, ?, ?, ?) ";
		String[] whereArgs = new String[] { 
				dateFrom,
				dateTo,
                String.valueOf(TransactionDao.TRANS_STATUS_SUCCESS),
				String.valueOf(ProductsDao.NORMAL_TYPE),
				String.valueOf(ProductsDao.SET_CAN_SELECT),
				String.valueOf(ProductsDao.CHILD_OF_SET_HAVE_PRICE),
				String.valueOf(ProductsDao.COMMENT_HAVE_PRICE)
			};
		if(sessId != 0){
			whereArg += " AND a." + SessionTable.COLUMN_SESS_ID + "=?";
			whereArgs = new String[] { 
					dateFrom,
					dateTo,
	                String.valueOf(TransactionDao.TRANS_STATUS_SUCCESS),
					String.valueOf(ProductsDao.NORMAL_TYPE),
					String.valueOf(ProductsDao.SET_CAN_SELECT),
					String.valueOf(ProductsDao.CHILD_OF_SET_HAVE_PRICE),
					String.valueOf(ProductsDao.COMMENT_HAVE_PRICE),
					String.valueOf(sessId)
				};
		}
		Cursor cursor = querySummaryOrder(
				OrderTransTable.TABLE_ORDER_TRANS + " a "
				+ " LEFT JOIN " + OrderDetailTable.TABLE_ORDER + " b "
				+ " ON a." + OrderTransTable.COLUMN_TRANS_ID + "=b." + OrderTransTable.COLUMN_TRANS_ID
				+ " LEFT JOIN " + PromotionPriceGroupTable.TABLE_PROMOTION_PRICE_GROUP + " c "
				+ " ON a." + PromotionPriceGroupTable.COLUMN_PRICE_GROUP_ID + "=c." + PromotionPriceGroupTable.COLUMN_PRICE_GROUP_ID,
				whereArg, whereArgs);
		if (cursor.moveToFirst()) {
			ord = toSumOrderDetail(cursor);
		}
		cursor.close();
		return ord;
	}
	
	/**
	 * @param dateFrom
	 * @param dateTo
	 * @return OrderDetail
	 */
	public OrderDetail getSummaryOrder(String dateFrom, String dateTo) {
		OrderDetail ord = new OrderDetail();
		Cursor cursor = querySummaryOrder(
				OrderTransTable.TABLE_ORDER_TRANS + " a "
				+ " LEFT JOIN " + OrderDetailTable.TABLE_ORDER + " b "
				+ " ON a." + OrderTransTable.COLUMN_TRANS_ID + "=b." + OrderTransTable.COLUMN_TRANS_ID
				+ " LEFT JOIN " + PromotionPriceGroupTable.TABLE_PROMOTION_PRICE_GROUP + " c "
				+ " ON a." + PromotionPriceGroupTable.COLUMN_PRICE_GROUP_ID + "=c." + PromotionPriceGroupTable.COLUMN_PRICE_GROUP_ID,
				" a." + OrderTransTable.COLUMN_SALE_DATE + " BETWEEN ? AND ? "
                + " AND a." + OrderTransTable.COLUMN_STATUS_ID + "=? "
				+ " AND b." + ProductTable.COLUMN_PRODUCT_TYPE_ID + " IN(?, ?, ?, ?) ",
				new String[] { 
					dateFrom,
					dateTo,
                    String.valueOf(TransactionDao.TRANS_STATUS_SUCCESS),
					String.valueOf(ProductsDao.NORMAL_TYPE),
					String.valueOf(ProductsDao.SET_CAN_SELECT),
					String.valueOf(ProductsDao.CHILD_OF_SET_HAVE_PRICE),
					String.valueOf(ProductsDao.COMMENT_HAVE_PRICE)
				});
		if (cursor.moveToFirst()) {
			ord = toSumOrderDetail(cursor);
		}
		cursor.close();
		return ord;
	}
	
	/**
	 * @param transactionId
	 * @param isLoadTemp
	 * @return OrderDetail
	 */
	public OrderDetail getSummaryOrderWaste(int transactionId, boolean isLoadTemp) {
		OrderDetail ord = new OrderDetail(); 
		String tables = (isLoadTemp ? OrderTransTable.TEMP_ORDER_TRANS : OrderTransTable.TABLE_ORDER_TRANS_WASTE) + " a "
				+ " LEFT JOIN " + (isLoadTemp ? OrderDetailTable.TEMP_ORDER : OrderDetailTable.TABLE_ORDER_WASTE) + " b "
				+ " ON a." + OrderTransTable.COLUMN_TRANS_ID + "=b." + OrderTransTable.COLUMN_TRANS_ID
				+ " LEFT JOIN " + PromotionPriceGroupTable.TABLE_PROMOTION_PRICE_GROUP + " c "
				+ " ON a." + PromotionPriceGroupTable.COLUMN_PRICE_GROUP_ID + "=c." + PromotionPriceGroupTable.COLUMN_PRICE_GROUP_ID;
		String selection = " a." + OrderTransTable.COLUMN_TRANS_ID + "=?"
				+ " AND b." + ProductTable.COLUMN_PRODUCT_TYPE_ID + " IN (?, ?, ?, ?) ";
		Cursor cursor = querySummaryOrder(
				tables, selection,
				new String[] { 
					String.valueOf(transactionId),
					String.valueOf(ProductsDao.NORMAL_TYPE),
					String.valueOf(ProductsDao.SET_CAN_SELECT),
					String.valueOf(ProductsDao.CHILD_OF_SET_HAVE_PRICE),
					String.valueOf(ProductsDao.COMMENT_HAVE_PRICE)
				});
		if (cursor.moveToFirst()) {
			ord = toSumOrderDetail(cursor);
		}
		cursor.close();
		return ord;
	}
	
	/**
	 * Get summary order
	 * @param transactionId
	 * @param isLoadTemp
	 * @return OrderDetail
	 */
	public OrderDetail getSummaryOrder(int transactionId, boolean isLoadTemp) {
		OrderDetail ord = new OrderDetail(); 
		String tables = (isLoadTemp ? OrderTransTable.TEMP_ORDER_TRANS : OrderTransTable.TABLE_ORDER_TRANS) + " a "
				+ " LEFT JOIN " + (isLoadTemp ? OrderDetailTable.TEMP_ORDER : OrderDetailTable.TABLE_ORDER) + " b "
				+ " ON a." + OrderTransTable.COLUMN_TRANS_ID + "=b." + OrderTransTable.COLUMN_TRANS_ID
				+ " LEFT JOIN " + PromotionPriceGroupTable.TABLE_PROMOTION_PRICE_GROUP + " c "
				+ " ON a." + PromotionPriceGroupTable.COLUMN_PRICE_GROUP_ID + "=c." + PromotionPriceGroupTable.COLUMN_PRICE_GROUP_ID;
		String selection = " a." + OrderTransTable.COLUMN_TRANS_ID + "=?"
				+ " AND b." + ProductTable.COLUMN_PRODUCT_TYPE_ID + " IN (?, ?, ?, ?) ";
		Cursor cursor = querySummaryOrder(
				tables, selection,
				new String[] { 
					String.valueOf(transactionId),
					String.valueOf(ProductsDao.NORMAL_TYPE),
					String.valueOf(ProductsDao.SET_CAN_SELECT),
					String.valueOf(ProductsDao.CHILD_OF_SET_HAVE_PRICE),
					String.valueOf(ProductsDao.COMMENT_HAVE_PRICE)
				});
		if (cursor.moveToFirst()) {
			ord = toSumOrderDetail(cursor);
		}
		cursor.close();
		return ord;
	}
	
	/**
	 * Get summary
	 * @param cursor
	 * @return OrderDetail
	 */
	private OrderDetail toSumOrderDetail(Cursor cursor){
		OrderDetail ord = new OrderDetail();
		String proName = cursor.getString(cursor.getColumnIndex(PromotionPriceGroupTable.COLUMN_PROMOTION_NAME));
		String otherDisDesc = cursor.getString(cursor.getColumnIndex(OrderTransTable.COLUMN_OTHER_DISCOUNT_DESC));
		if(!TextUtils.isEmpty(proName))
			ord.setPromotionName(proName);
		else if(!TextUtils.isEmpty(otherDisDesc))
			ord.setPromotionName(otherDisDesc);
		else 
			ord.setPromotionName("");
		ord.setOrderQty(cursor.getDouble(cursor.getColumnIndex(OrderDetailTable.COLUMN_ORDER_QTY)));
		ord.setProductPrice(cursor.getDouble(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_PRICE)));
		ord.setPriceDiscount(cursor.getDouble(cursor.getColumnIndex(OrderDetailTable.COLUMN_PRICE_DISCOUNT)));
		ord.setTotalRetailPrice(cursor.getDouble(cursor.getColumnIndex(OrderDetailTable.COLUMN_TOTAL_RETAIL_PRICE)));
		ord.setTotalSalePrice(cursor.getDouble(cursor.getColumnIndex(OrderDetailTable.COLUMN_TOTAL_SALE_PRICE)));
		ord.setVat(cursor.getDouble(cursor.getColumnIndex(OrderDetailTable.COLUMN_TOTAL_VAT)));
		ord.setVatExclude(cursor.getDouble(cursor.getColumnIndex(OrderDetailTable.COLUMN_TOTAL_VAT_EXCLUDE)));
		return ord;
	}
	
	/**
	 * Query summary
	 * @param tables
	 * @param selection
	 * @param selectionArgs
	 * @return Cursor
	 */
	private Cursor querySummaryOrder(String tables, String selection, String[] selectionArgs){
		String sql = "SELECT a." + OrderTransTable.COLUMN_OTHER_DISCOUNT_DESC + ", "
				+ " SUM(b." + OrderDetailTable.COLUMN_ORDER_QTY + ") AS " + OrderDetailTable.COLUMN_ORDER_QTY + ", "
				+ " SUM(b." + ProductTable.COLUMN_PRODUCT_PRICE + ") AS " + ProductTable.COLUMN_PRODUCT_PRICE + ", "
				+ " SUM(b." + OrderDetailTable.COLUMN_PRICE_DISCOUNT + ") AS " + OrderDetailTable.COLUMN_PRICE_DISCOUNT + ", "
				+ " SUM(b." + OrderDetailTable.COLUMN_TOTAL_RETAIL_PRICE + ") AS " + OrderDetailTable.COLUMN_TOTAL_RETAIL_PRICE + ", "
				+ " SUM(b." + OrderDetailTable.COLUMN_TOTAL_SALE_PRICE + ") AS " + OrderDetailTable.COLUMN_TOTAL_SALE_PRICE + ", "
				+ " SUM(b." + OrderDetailTable.COLUMN_TOTAL_VAT + ") AS " + OrderDetailTable.COLUMN_TOTAL_VAT + ", "
				+ " SUM(b." + OrderDetailTable.COLUMN_TOTAL_VAT_EXCLUDE + ") AS " + OrderDetailTable.COLUMN_TOTAL_VAT_EXCLUDE + ","
				+ " c." + PromotionPriceGroupTable.COLUMN_PROMOTION_NAME + ","
				+ " c." + PromotionPriceGroupTable.COLUMN_BUTTON_NAME
				+ " FROM " + tables
				+ " WHERE " + selection;
		return getReadableDatabase().rawQuery(sql, selectionArgs);
	}
	
	/**
	 * @param transactionId
	 * @return max total retail price
	 */
	public double getMaxTotalRetailPrice(int transactionId){
		double maxTotalRetailPrice = 0.0d;
		Cursor cursor = getReadableDatabase().rawQuery(
				"SELECT MAX(" + OrderDetailTable.COLUMN_TOTAL_RETAIL_PRICE + ")"
				+ " FROM " + OrderDetailTable.TEMP_ORDER
				+ " WHERE " + OrderTransTable.COLUMN_TRANS_ID + "=?",
				new String[]{
					String.valueOf(transactionId)
				});
		if(cursor.moveToFirst()){
			maxTotalRetailPrice = cursor.getDouble(0);
		}
		cursor.close();
		return maxTotalRetailPrice;
	}

	/**
	 * @param sessId
	 * @param saleDate
	 * @return max receipt no
	 */
	public String getMaxReceiptNo(int sessId, String saleDate){
		String receiptNo = "";
		String selection = OrderTransTable.COLUMN_SALE_DATE + "=?"
				+ " AND " + OrderTransTable.COLUMN_STATUS_ID + " IN(?,?) ";
		String[] selectionArgs = new String[]{
			saleDate, 
			String.valueOf(TransactionDao.TRANS_STATUS_SUCCESS),
			String.valueOf(TransactionDao.TRANS_STATUS_VOID)
		};
		if(sessId != 0){
			selection += " AND " + SessionTable.COLUMN_SESS_ID + "=?";
			selectionArgs = new String[]{
				saleDate, 
				String.valueOf(TransactionDao.TRANS_STATUS_SUCCESS),
				String.valueOf(TransactionDao.TRANS_STATUS_VOID),
				String.valueOf(sessId)
			};
		}
		Cursor cursor = getReadableDatabase().rawQuery(
				"SELECT " + OrderTransTable.COLUMN_RECEIPT_NO
				+ " FROM " + OrderTransTable.TABLE_ORDER_TRANS
				+ " WHERE " + selection
				+ " ORDER BY " + OrderTransTable.COLUMN_TRANS_ID
				+ " DESC LIMIT 1", selectionArgs);
		if(cursor.moveToFirst()){
			receiptNo = cursor.getString(0);
		}
		cursor.close();
		return receiptNo;
	}
	
	/**
	 * Get min receipt no
	 * @param sessId
	 * @param saleDate
	 * @return min receipt no
	 */
	public String getMinReceiptNo(int sessId, String saleDate){
		String receiptNo = "";
		String selection = OrderTransTable.COLUMN_SALE_DATE + "=?"
				+ " AND " + OrderTransTable.COLUMN_STATUS_ID + " IN(?,?) ";
		String[] selectionArgs = new String[]{
			saleDate, 
			String.valueOf(TransactionDao.TRANS_STATUS_SUCCESS),
			String.valueOf(TransactionDao.TRANS_STATUS_VOID)
		};
		if(sessId != 0){
			selection += " AND " + SessionTable.COLUMN_SESS_ID + "=?";
			selectionArgs = new String[]{
				saleDate, 
				String.valueOf(TransactionDao.TRANS_STATUS_SUCCESS),
				String.valueOf(TransactionDao.TRANS_STATUS_VOID),
				String.valueOf(sessId)
			};
		}
		Cursor cursor = getReadableDatabase().rawQuery(
				"SELECT " + OrderTransTable.COLUMN_RECEIPT_NO
				+ " FROM " + OrderTransTable.TABLE_ORDER_TRANS
				+ " WHERE " + selection
				+ " ORDER BY " + OrderTransTable.COLUMN_TRANS_ID
				+ " ASC LIMIT 1", selectionArgs);
		if(cursor.moveToFirst()){
			receiptNo = cursor.getString(0);
		}
		cursor.close();
		return receiptNo;
	}
	
	/**
	 * Summary vat for update transaction
	 * @param transactionId
	 * @return summary of order
	 */
	private OrderDetail getSummaryVat(int transactionId) {
		OrderDetail orderDetail = new OrderDetail();
		String sql = "SELECT SUM (" + OrderDetailTable.COLUMN_TOTAL_VAT + ") AS " + OrderDetailTable.COLUMN_TOTAL_VAT + ", "
				+ " SUM (" + OrderDetailTable.COLUMN_TOTAL_VAT_EXCLUDE + ") AS " + OrderDetailTable.COLUMN_TOTAL_VAT_EXCLUDE
				+ " FROM " + OrderDetailTable.TEMP_ORDER 
				+ " WHERE " + OrderTransTable.COLUMN_TRANS_ID + "=?"
				+ " AND " + ProductTable.COLUMN_VAT_TYPE + " != ?";
		Cursor cursor = getReadableDatabase().rawQuery(
				sql, 
				new String[] {
					String.valueOf(transactionId), 
					String.valueOf(ProductsDao.NO_VAT) 
				});
		if (cursor.moveToFirst()) {
			orderDetail.setVat(cursor.getDouble(cursor.getColumnIndex(OrderDetailTable.COLUMN_TOTAL_VAT)));
			orderDetail.setVatExclude(cursor.getDouble(cursor.getColumnIndex(OrderDetailTable.COLUMN_TOTAL_VAT_EXCLUDE)));
		}
		cursor.close();
		return orderDetail;
	}

	/**
	 * @param transactionId
	 * @param isLoadTemp
	 * @return List<OrderDetail>
	 */
	public List<OrderDetail> listGroupedAllOrderDetailWaste(int transactionId, boolean isLoadTemp) {
		List<OrderDetail> orderDetailLst = new ArrayList<OrderDetail>();
		String tables = (isLoadTemp ? OrderDetailTable.TEMP_ORDER : OrderDetailTable.TABLE_ORDER_WASTE) + " a"
				+ " LEFT JOIN " + ProductTable.TABLE_PRODUCT + " b"
				+ " ON a."  + ProductTable.COLUMN_PRODUCT_ID + " =b." + ProductTable.COLUMN_PRODUCT_ID;
		String selection = "a." + OrderTransTable.COLUMN_TRANS_ID + "=?"
				+ " AND a." + ProductTable.COLUMN_PRODUCT_TYPE_ID + " IN (?, ?) ";
		String groupBy = "a." + ProductTable.COLUMN_PRODUCT_ID;
		if(countNotNormalTypeOrder(transactionId, isLoadTemp) > 0)
			groupBy = "a." + OrderDetailTable.COLUMN_ORDER_ID + ", a." + ProductTable.COLUMN_PRODUCT_ID;
		Cursor cursor = queryOrderDetail(
				tables,
				selection,
				new String[] { 
					String.valueOf(transactionId),
					String.valueOf(ProductsDao.NORMAL_TYPE),
					String.valueOf(ProductsDao.SET_CAN_SELECT)
				}, groupBy);
		if (cursor.moveToFirst()) {
			do {
				OrderDetail ord = new OrderDetail();
				ord.setTransactionId(cursor.getInt(cursor.getColumnIndex(OrderTransTable.COLUMN_TRANS_ID)));
				ord.setOrderDetailId(cursor.getInt(cursor.getColumnIndex(OrderDetailTable.COLUMN_ORDER_ID)));
				ord.setProductId(cursor.getInt(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_ID)));
				ord.setProductTypeId(cursor.getInt(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_TYPE_ID)));
				ord.setProductName(cursor.getString(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_NAME)));
				ord.setProductName1(cursor.getString(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_NAME1)));
				ord.setOrderQty(cursor.getFloat(cursor.getColumnIndex(OrderDetailTable.COLUMN_ORDER_QTY)));
				ord.setProductPrice(cursor.getFloat(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_PRICE)));
				ord.setTotalRetailPrice(cursor.getFloat(cursor.getColumnIndex(OrderDetailTable.COLUMN_TOTAL_RETAIL_PRICE)));
				ord.setTotalSalePrice(cursor.getFloat(cursor.getColumnIndex(OrderDetailTable.COLUMN_TOTAL_SALE_PRICE)));
				ord.setVatType(cursor.getInt(cursor.getColumnIndex(ProductTable.COLUMN_VAT_TYPE)));
				ord.setMemberDiscount(cursor.getFloat(cursor.getColumnIndex(OrderDetailTable.COLUMN_MEMBER_DISCOUNT)));
				ord.setPriceDiscount(cursor.getFloat(cursor.getColumnIndex(OrderDetailTable.COLUMN_PRICE_DISCOUNT)));
				ord.setPriceOrPercent(cursor.getInt(cursor.getColumnIndex(OrderDetailTable.COLUMN_PRICE_OR_PERCENT)));
				ord.setOrderComment(cursor.getString(cursor.getColumnIndex(BaseColumn.COLUMN_REMARK)));
				ord.setOrdSetDetailLst(listGroupedOrderSetDetail(ord.getTransactionId(), ord.getOrderDetailId(), isLoadTemp));
				ord.setOrderCommentLst(listGroupedOrderComment(ord.getTransactionId(), ord.getOrderDetailId(), isLoadTemp));
				orderDetailLst.add(ord);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return orderDetailLst;
	}
	
	/**
	 * list all order group by productId
	 * @param transactionId
	 * @param isLoadTemp
	 * @return List<OrderDetail>
	 */
	public List<OrderDetail> listGroupedAllOrderDetail(int transactionId, boolean isLoadTemp) {
		List<OrderDetail> orderDetailLst = new ArrayList<OrderDetail>();
		String tables = (isLoadTemp ? OrderDetailTable.TEMP_ORDER : OrderDetailTable.TABLE_ORDER) + " a"
				+ " LEFT JOIN " + ProductTable.TABLE_PRODUCT + " b"
				+ " ON a."  + ProductTable.COLUMN_PRODUCT_ID + " =b." + ProductTable.COLUMN_PRODUCT_ID;
		String selection = "a." + OrderTransTable.COLUMN_TRANS_ID + "=?"
				+ " AND a." + ProductTable.COLUMN_PRODUCT_TYPE_ID + " IN (?, ?) ";
		String groupBy = "a." + ProductTable.COLUMN_PRODUCT_ID;
		if(countNotNormalTypeOrder(transactionId, isLoadTemp) > 0)
			groupBy = "a." + OrderDetailTable.COLUMN_ORDER_ID + ", a." + ProductTable.COLUMN_PRODUCT_ID;
        groupBy += ", a." + ProductTable.COLUMN_PRODUCT_PRICE;
		Cursor cursor = queryOrderDetail(
				tables,
				selection,
				new String[] { 
					String.valueOf(transactionId),
					String.valueOf(ProductsDao.NORMAL_TYPE),
					String.valueOf(ProductsDao.SET_CAN_SELECT)
				}, groupBy);
		if (cursor.moveToFirst()) {
			do {
				OrderDetail ord = new OrderDetail();
				ord.setTransactionId(cursor.getInt(cursor.getColumnIndex(OrderTransTable.COLUMN_TRANS_ID)));
				ord.setOrderDetailId(cursor.getInt(cursor.getColumnIndex(OrderDetailTable.COLUMN_ORDER_ID)));
				ord.setProductId(cursor.getInt(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_ID)));
				ord.setProductTypeId(cursor.getInt(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_TYPE_ID)));
				ord.setProductName(cursor.getString(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_NAME)));
				ord.setProductName1(cursor.getString(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_NAME1)));
				ord.setOrderQty(cursor.getFloat(cursor.getColumnIndex(OrderDetailTable.COLUMN_ORDER_QTY)));
				ord.setProductPrice(cursor.getFloat(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_PRICE)));
				ord.setTotalRetailPrice(cursor.getFloat(cursor.getColumnIndex(OrderDetailTable.COLUMN_TOTAL_RETAIL_PRICE)));
				ord.setTotalSalePrice(cursor.getFloat(cursor.getColumnIndex(OrderDetailTable.COLUMN_TOTAL_SALE_PRICE)));
				ord.setVatType(cursor.getInt(cursor.getColumnIndex(ProductTable.COLUMN_VAT_TYPE)));
				ord.setMemberDiscount(cursor.getFloat(cursor.getColumnIndex(OrderDetailTable.COLUMN_MEMBER_DISCOUNT)));
				ord.setPriceDiscount(cursor.getFloat(cursor.getColumnIndex(OrderDetailTable.COLUMN_PRICE_DISCOUNT)));
				ord.setPriceOrPercent(cursor.getInt(cursor.getColumnIndex(OrderDetailTable.COLUMN_PRICE_OR_PERCENT)));
				ord.setOrderComment(cursor.getString(cursor.getColumnIndex(BaseColumn.COLUMN_REMARK)));
				ord.setOrdSetDetailLst(listGroupedOrderSetDetail(ord.getTransactionId(), ord.getOrderDetailId(), isLoadTemp));
				ord.setOrderCommentLst(listGroupedOrderComment(ord.getTransactionId(), ord.getOrderDetailId(), isLoadTemp));
				orderDetailLst.add(ord);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return orderDetailLst;
	}

	/**
	 * @param transactionId
	 * @param isLoadTemp
	 * @return > 0 if have not normal type product
	 */
	private int countNotNormalTypeOrder(int transactionId, boolean isLoadTemp){
		int count = 0;
		Cursor cursor = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM " 
				+ (isLoadTemp ? OrderDetailTable.TEMP_ORDER : OrderDetailTable.TABLE_ORDER)
				+ " WHERE " + OrderTransTable.COLUMN_TRANS_ID + "=?"
				+ " AND " + ProductTable.COLUMN_PRODUCT_TYPE_ID + "!=?", 
				new String[]{
						String.valueOf(transactionId),
						String.valueOf(ProductsDao.NORMAL_TYPE)
						});
		if(cursor.moveToFirst()){
			count = cursor.getInt(0);
		}
		cursor.close();
		return count;
	}
	
	/**
	 * list order for discount
	 * @param transactionId
	 * @return
	 */
	public List<OrderDetail> listAllOrderForDiscount(int transactionId) {
		List<OrderDetail> ordLst = new ArrayList<OrderDetail>();
		Cursor cursor = queryOrderDetail(
				"a." + OrderTransTable.COLUMN_TRANS_ID + "=?"
				+ " AND a." + ProductTable.COLUMN_PRODUCT_PRICE + " > 0 ",
				new String[] { 
					String.valueOf(transactionId)
				});
		if (cursor.moveToFirst()) {
			do {
				ordLst.add(toOrderDetail(cursor));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return ordLst;
	}
	
	/**
	 * list all order
	 * @param transactionId
	 * @return List<OrderDetail>
	 */
	public List<OrderDetail> listAllOrder(int transactionId) {
		List<OrderDetail> ordLst = null;
		Cursor cursor = queryOrderDetail(
				"a." + OrderTransTable.COLUMN_TRANS_ID + "=?"
				+ " AND a." + ProductTable.COLUMN_PRODUCT_TYPE_ID + " IN(?, ?) ",
				new String[] { 
					String.valueOf(transactionId),
					String.valueOf(ProductsDao.NORMAL_TYPE),
					String.valueOf(ProductsDao.SET_CAN_SELECT)
				});
		if (cursor.moveToFirst()) {
			ordLst = new ArrayList<OrderDetail>();
			do {
				ordLst.add(toOrderDetail(cursor));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return ordLst;
	}
	
	/**
	 * Query order detail by parsing selection string and selectionArgs
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @return Cursor
	 */
	private Cursor queryOrderDetail(String selection, String[] selectionArgs){
		String sql = "SELECT a." + OrderTransTable.COLUMN_TRANS_ID + ","
				+ " a." + OrderDetailTable.COLUMN_ORDER_ID + ","
				+ " a." + ProductTable.COLUMN_PRODUCT_ID + ","
				+ " a." + ProductTable.COLUMN_PRODUCT_TYPE_ID + ","
				+ " a." + OrderDetailTable.COLUMN_ORDER_QTY + ","
				+ " a." + OrderDetailTable.COLUMN_DEDUCT_AMOUNT + ", "
				+ " a." + ProductTable.COLUMN_PRODUCT_PRICE + ","
				+ " a." + OrderDetailTable.COLUMN_TOTAL_RETAIL_PRICE + "," 
				+ " a." + OrderDetailTable.COLUMN_TOTAL_SALE_PRICE + ","
				+ " a." + OrderDetailTable.COLUMN_TOTAL_VAT_EXCLUDE + ", "
				+ " a." + ProductTable.COLUMN_VAT_TYPE + ","
				+ " a." + OrderDetailTable.COLUMN_MEMBER_DISCOUNT + ","
				+ " a." + OrderDetailTable.COLUMN_PRICE_DISCOUNT + ","
				+ " a." + OrderDetailTable.COLUMN_PRICE_OR_PERCENT + ","
				+ " a." + BaseColumn.COLUMN_REMARK + ","
				+ " b." + ProductTable.COLUMN_PRODUCT_NAME + ", "
				+ " b." + ProductTable.COLUMN_PRODUCT_NAME1 + ", "
				+ " b." + ProductTable.COLUMN_PRODUCT_NAME2
				+ " FROM " + OrderDetailTable.TEMP_ORDER + " a"
				+ " LEFT JOIN " + ProductTable.TABLE_PRODUCT + " b"
				+ " ON a."  + ProductTable.COLUMN_PRODUCT_ID + " =b." + ProductTable.COLUMN_PRODUCT_ID
				+ " WHERE " + selection;
		return getReadableDatabase().rawQuery(sql, selectionArgs);
	}
	
	/**
	 * @param tables
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @return Cursor
	 */
	private Cursor queryOrderDetail(String tables, String selection, String[] selectionArgs, String groupBy){
		String sql = "SELECT a." + OrderTransTable.COLUMN_TRANS_ID + ","
				+ " a." + OrderDetailTable.COLUMN_ORDER_ID + ","
				+ " a." + ProductTable.COLUMN_PRODUCT_ID + ","
				+ " a." + ProductTable.COLUMN_PRODUCT_TYPE_ID + ","
				+ " SUM(a." + OrderDetailTable.COLUMN_ORDER_QTY + ") AS " + OrderDetailTable.COLUMN_ORDER_QTY + ","
				+ " a." + OrderDetailTable.COLUMN_DEDUCT_AMOUNT + ", "
				+ " SUM(a." + ProductTable.COLUMN_PRODUCT_PRICE + ") AS " + ProductTable.COLUMN_PRODUCT_PRICE + ","
				+ " SUM(a." + OrderDetailTable.COLUMN_TOTAL_RETAIL_PRICE + ") AS " + OrderDetailTable.COLUMN_TOTAL_RETAIL_PRICE + "," 
				+ " SUM(a." + OrderDetailTable.COLUMN_TOTAL_SALE_PRICE + ") AS " + OrderDetailTable.COLUMN_TOTAL_SALE_PRICE + ","
				+ " a." + ProductTable.COLUMN_VAT_TYPE + ","
				+ " SUM(a." + OrderDetailTable.COLUMN_MEMBER_DISCOUNT + ") AS " + OrderDetailTable.COLUMN_MEMBER_DISCOUNT + ","
				+ " SUM(a." + OrderDetailTable.COLUMN_PRICE_DISCOUNT + ") AS " + OrderDetailTable.COLUMN_PRICE_DISCOUNT + ","
				+ " a." + OrderDetailTable.COLUMN_PRICE_OR_PERCENT + ","
				+ " a." + BaseColumn.COLUMN_REMARK + ","
				+ " b." + ProductTable.COLUMN_PRODUCT_NAME + ", "
				+ " b." + ProductTable.COLUMN_PRODUCT_NAME1 + ", "
				+ " b." + ProductTable.COLUMN_PRODUCT_NAME2
				+ " FROM " + tables
				+ " WHERE " + selection
				+ " GROUP BY " + groupBy
                + " ORDER BY a." + OrderDetailTable.COLUMN_ORDER_ID;
		return getReadableDatabase().rawQuery(sql, selectionArgs);
	}
	
	/**
	 * @param cursor
	 * @return OrderDetail
	 */
	private OrderDetail toOrderDetail(Cursor cursor){
		OrderDetail ord = new OrderDetail();
		ord.setTransactionId(cursor.getInt(cursor.getColumnIndex(OrderTransTable.COLUMN_TRANS_ID)));
		ord.setOrderDetailId(cursor.getInt(cursor.getColumnIndex(OrderDetailTable.COLUMN_ORDER_ID)));
		ord.setProductId(cursor.getInt(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_ID)));
		ord.setProductTypeId(cursor.getInt(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_TYPE_ID)));
		ord.setProductName(cursor.getString(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_NAME)));
		ord.setProductName1(cursor.getString(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_NAME1)));
		ord.setOrderQty(cursor.getFloat(cursor.getColumnIndex(OrderDetailTable.COLUMN_ORDER_QTY)));
		ord.setProductPrice(cursor.getFloat(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_PRICE)));
		ord.setTotalRetailPrice(cursor.getFloat(cursor.getColumnIndex(OrderDetailTable.COLUMN_TOTAL_RETAIL_PRICE)));
		ord.setTotalSalePrice(cursor.getFloat(cursor.getColumnIndex(OrderDetailTable.COLUMN_TOTAL_SALE_PRICE)));
		ord.setVatExclude(cursor.getDouble(cursor.getColumnIndex(OrderDetailTable.COLUMN_TOTAL_VAT_EXCLUDE)));
		ord.setVatType(cursor.getInt(cursor.getColumnIndex(ProductTable.COLUMN_VAT_TYPE)));
		ord.setMemberDiscount(cursor.getFloat(cursor.getColumnIndex(OrderDetailTable.COLUMN_MEMBER_DISCOUNT)));
		ord.setPriceDiscount(cursor.getFloat(cursor.getColumnIndex(OrderDetailTable.COLUMN_PRICE_DISCOUNT)));
		ord.setPriceOrPercent(cursor.getInt(cursor.getColumnIndex(OrderDetailTable.COLUMN_PRICE_OR_PERCENT)));
		ord.setOrderComment(cursor.getString(cursor.getColumnIndex(BaseColumn.COLUMN_REMARK)));
		ord.setOrdSetDetailLst(listOrderSetDetail(ord.getTransactionId(), ord.getOrderDetailId()));
		ord.setOrderCommentLst(listOrderComment(ord.getTransactionId(), ord.getOrderDetailId()));
		return ord;
	}
	
	/**
	 * @param transId
	 * @param ordId
	 * @return OrderDetail
	 */
	public OrderDetail getOrder(int transId, int ordId){
		OrderDetail order = null;
		Cursor cursor = queryOrderDetail(
				"a." + OrderTransTable.COLUMN_TRANS_ID + "=?"
				+ " AND a." + OrderDetailTable.COLUMN_ORDER_ID + "=?",
				new String[] {
					String.valueOf(transId), 
					String.valueOf(ordId) 
				});
		if(cursor.moveToFirst()){
			order = toOrderDetail(cursor);
		}
		cursor.close();
		return order;
	}
	
	public String formatWasteReceiptNo(String docTypeHeader, int year, int month, int day, int id) {
		String receiptYear = String.format(Locale.US, "%04d", year);
		String receiptMonth = String.format(Locale.US, "%02d", month);
		String receiptDay = String.format(Locale.US, "%02d", day);
		String receiptId = String.format(Locale.US, "%04d", id);
		return docTypeHeader + receiptDay + receiptMonth + receiptYear + "/" + receiptId;
	}
	
	public String formatReceiptNo(int year, int month, int day, int id) {
		ComputerDao computer = new ComputerDao(getContext());
		String receiptHeader = computer.getReceiptHeader();
		String receiptYear = String.format(Locale.US, "%04d", year);
		String receiptMonth = String.format(Locale.US, "%02d", month);
		String receiptDay = String.format(Locale.US, "%02d", day);
		String receiptId = String.format(Locale.US, "%04d", id);
		return receiptHeader + receiptDay + receiptMonth + receiptYear + "/" + receiptId;
	}

	/**
	 * @param saleDate
	 * @return List<OrderTransaction>
	 */
	public List<OrderTransaction> listTransactionWaste(String saleDate) {
		List<OrderTransaction> transLst = new ArrayList<OrderTransaction>();
		Cursor cursor = getReadableDatabase().query(
				OrderTransTable.TABLE_ORDER_TRANS_WASTE,
				new String[]{
					OrderTransTable.COLUMN_TRANS_ID,
					ComputerTable.COLUMN_COMPUTER_ID,
					SessionTable.COLUMN_SESS_ID,
					OrderTransTable.COLUMN_PAID_TIME,
					OrderTransTable.COLUMN_TRANS_NOTE,
					OrderTransTable.COLUMN_RECEIPT_NO,
					OrderTransTable.COLUMN_STATUS_ID
				}, 
				OrderTransTable.COLUMN_SALE_DATE + "=? AND "
				+ OrderTransTable.COLUMN_STATUS_ID + " IN(?,?)",
				new String[] { 
					saleDate, 
					String.valueOf(WASTE_TRANS_STATUS_VOID),
					String.valueOf(WASTE_TRANS_STATUS_SUCCESS) 
				}, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				OrderTransaction trans = new OrderTransaction();
				trans.setTransactionId(cursor.getInt(cursor.getColumnIndex(OrderTransTable.COLUMN_TRANS_ID)));
				trans.setComputerId(cursor.getInt(cursor.getColumnIndex(ComputerTable.COLUMN_COMPUTER_ID)));
				trans.setSessionId(cursor.getInt(cursor.getColumnIndex(SessionTable.COLUMN_SESS_ID)));
				trans.setTransactionStatusId(cursor.getInt(cursor.getColumnIndex(OrderTransTable.COLUMN_STATUS_ID)));
				trans.setTransactionNote(cursor.getString(cursor.getColumnIndex(OrderTransTable.COLUMN_TRANS_NOTE)));
				trans.setPaidTime(cursor.getString(cursor.getColumnIndex(OrderTransTable.COLUMN_PAID_TIME)));
				trans.setReceiptNo(cursor.getString(cursor.getColumnIndex(OrderTransTable.COLUMN_RECEIPT_NO)));
				transLst.add(trans);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return transLst;
	}
	
	/**
	 * @param saleDate
	 * @return List<OrderTransaction>
	 */
	public List<OrderTransaction> listTransaction(String saleDate) {
		List<OrderTransaction> transLst = new ArrayList<OrderTransaction>();
		Cursor cursor = getReadableDatabase().query(
				OrderTransTable.TABLE_ORDER_TRANS,
				new String[]{
					OrderTransTable.COLUMN_TRANS_ID,
					ComputerTable.COLUMN_COMPUTER_ID,
					SessionTable.COLUMN_SESS_ID,
					OrderTransTable.COLUMN_PAID_TIME,
					OrderTransTable.COLUMN_TRANS_NOTE,
					OrderTransTable.COLUMN_RECEIPT_NO,
					OrderTransTable.COLUMN_STATUS_ID
				}, 
				OrderTransTable.COLUMN_SALE_DATE + "=? AND "
				+ OrderTransTable.COLUMN_STATUS_ID + " IN(?,?)",
				new String[] { 
					saleDate, 
					String.valueOf(TRANS_STATUS_VOID),
					String.valueOf(TRANS_STATUS_SUCCESS) 
				}, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				OrderTransaction trans = new OrderTransaction();
				trans.setTransactionId(cursor.getInt(cursor.getColumnIndex(OrderTransTable.COLUMN_TRANS_ID)));
				trans.setComputerId(cursor.getInt(cursor.getColumnIndex(ComputerTable.COLUMN_COMPUTER_ID)));
				trans.setSessionId(cursor.getInt(cursor.getColumnIndex(SessionTable.COLUMN_SESS_ID)));
				trans.setTransactionStatusId(cursor.getInt(cursor.getColumnIndex(OrderTransTable.COLUMN_STATUS_ID)));
				trans.setTransactionNote(cursor.getString(cursor.getColumnIndex(OrderTransTable.COLUMN_TRANS_NOTE)));
				trans.setPaidTime(cursor.getString(cursor.getColumnIndex(OrderTransTable.COLUMN_PAID_TIME)));
				trans.setReceiptNo(cursor.getString(cursor.getColumnIndex(OrderTransTable.COLUMN_RECEIPT_NO)));
				transLst.add(trans);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return transLst;
	}

	/**
	 * @param saleDate
	 * @return List<OrderTransaction>
	 */
	public List<OrderTransaction> listSuccessTransactionWaste(String saleDate) {
		List<OrderTransaction> transLst = new ArrayList<OrderTransaction>();
		String sql = " SELECT " + OrderTransTable.COLUMN_TRANS_ID + ", " 
				+ ComputerTable.COLUMN_COMPUTER_ID + ", " 
				+ OrderTransTable.COLUMN_PAID_TIME + ", "
				+ OrderTransTable.COLUMN_TRANS_NOTE + ", "
				+ OrderTransTable.COLUMN_RECEIPT_NO
				+ " FROM " + OrderTransTable.TABLE_ORDER_TRANS_WASTE
				+ " WHERE " + OrderTransTable.COLUMN_SALE_DATE + "=?"
				+ " AND " + OrderTransTable.COLUMN_STATUS_ID + "=?";
		Cursor cursor = getReadableDatabase().rawQuery(
						sql,
						new String[] { 
								saleDate,
								String.valueOf(WASTE_TRANS_STATUS_SUCCESS) 
						});
		if (cursor.moveToFirst()) {
			do {
				OrderTransaction trans = new OrderTransaction();
				trans.setTransactionId(cursor.getInt(cursor.getColumnIndex(OrderTransTable.COLUMN_TRANS_ID)));
				trans.setComputerId(cursor.getInt(cursor.getColumnIndex(ComputerTable.COLUMN_COMPUTER_ID)));
				trans.setTransactionNote(cursor.getString(cursor.getColumnIndex(OrderTransTable.COLUMN_TRANS_NOTE)));
				trans.setPaidTime(cursor.getString(cursor.getColumnIndex(OrderTransTable.COLUMN_PAID_TIME)));
				trans.setReceiptNo(cursor.getString(cursor.getColumnIndex(OrderTransTable.COLUMN_RECEIPT_NO)));
				transLst.add(trans);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return transLst;
	}
	
	/**
	 * @param saleDate
	 * @return List<OrderTransaction>
	 */
	public List<OrderTransaction> listSuccessTransaction(String saleDate) {
		List<OrderTransaction> transLst = new ArrayList<OrderTransaction>();
		String sql = " SELECT " + OrderTransTable.COLUMN_TRANS_ID + ", " 
				+ ComputerTable.COLUMN_COMPUTER_ID + ", " 
				+ OrderTransTable.COLUMN_PAID_TIME + ", "
				+ OrderTransTable.COLUMN_TRANS_NOTE + ", "
				+ OrderTransTable.COLUMN_RECEIPT_NO
				+ " FROM " + OrderTransTable.TABLE_ORDER_TRANS
				+ " WHERE " + OrderTransTable.COLUMN_SALE_DATE + "=?"
				+ " AND " + OrderTransTable.COLUMN_STATUS_ID + "=?";
		Cursor cursor = getReadableDatabase().rawQuery(
						sql,
						new String[] { 
								saleDate,
								String.valueOf(TRANS_STATUS_SUCCESS) 
						});
		if (cursor.moveToFirst()) {
			do {
				OrderTransaction trans = new OrderTransaction();
				trans.setTransactionId(cursor.getInt(cursor.getColumnIndex(OrderTransTable.COLUMN_TRANS_ID)));
				trans.setComputerId(cursor.getInt(cursor.getColumnIndex(ComputerTable.COLUMN_COMPUTER_ID)));
				trans.setTransactionNote(cursor.getString(cursor.getColumnIndex(OrderTransTable.COLUMN_TRANS_NOTE)));
				trans.setPaidTime(cursor.getString(cursor.getColumnIndex(OrderTransTable.COLUMN_PAID_TIME)));
				trans.setReceiptNo(cursor.getString(cursor.getColumnIndex(OrderTransTable.COLUMN_RECEIPT_NO)));
				transLst.add(trans);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return transLst;
	}

	/**
	 * List hold transaction
	 * @param sessionDate
	 * @return List<OrderTransaction>
	 */
	public List<OrderTransaction> listHoldOrder(String sessionDate) {
		List<OrderTransaction> transLst = new ArrayList<OrderTransaction>();
		Cursor cursor = getReadableDatabase().rawQuery(
				" SELECT a." + OrderTransTable.COLUMN_TRANS_ID + ", "
						+ " a." + ComputerTable.COLUMN_COMPUTER_ID + ", " 
						+ " a." + OrderTransTable.COLUMN_OPEN_TIME + ", "
						+ " a." + OrderTransTable.COLUMN_TRANS_NOTE + ", "
						+ " b." + StaffTable.COLUMN_STAFF_CODE + ", " 
						+ " b." + StaffTable.COLUMN_STAFF_NAME 
						+ " FROM " + OrderTransTable.TEMP_ORDER_TRANS + " a "
						+ " LEFT JOIN " + StaffTable.TABLE_STAFF + " b "
						+ " ON a." + OrderTransTable.COLUMN_OPEN_STAFF + "=b." + StaffTable.COLUMN_STAFF_ID
						+ " WHERE a." + OrderTransTable.COLUMN_SALE_DATE + "=?"
						+ " AND a." + OrderTransTable.COLUMN_STATUS_ID + "=?",
				new String[] { 
						sessionDate, 
						String.valueOf(TRANS_STATUS_HOLD) 
				});
		if (cursor.moveToFirst()) {
			do {
				OrderTransaction trans = new OrderTransaction();
				trans.setTransactionId(cursor.getInt(cursor.getColumnIndex(OrderTransTable.COLUMN_TRANS_ID)));
				trans.setComputerId(cursor.getInt(cursor.getColumnIndex(ComputerTable.COLUMN_COMPUTER_ID)));
				trans.setTransactionNote(cursor.getString(cursor.getColumnIndex(OrderTransTable.COLUMN_TRANS_NOTE)));
				trans.setOpenTime(cursor.getString(cursor.getColumnIndex(OrderTransTable.COLUMN_OPEN_TIME)));
				trans.setStaffName(cursor.getString(cursor.getColumnIndex(StaffTable.COLUMN_STAFF_CODE))
						+ ":"
						+ cursor.getString(cursor.getColumnIndex(StaffTable.COLUMN_STAFF_NAME)));
				transLst.add(trans);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return transLst;
	}

	/**
	 * Get max transactionId
	 * @return max transactionId
	 */
	public int getMaxTransaction() {
		int transactionId = 0;
		Cursor cursor = getReadableDatabase().rawQuery(
				" SELECT " + MaxTransIdTable.COLUMN_MAX_TRANS_ID
				+ " FROM " + MaxTransIdTable.TABLE_MAX_TRANS_ID, null);
		if (cursor.moveToFirst()) {
			transactionId = cursor.getInt(0);
		}
		cursor.close();
		if(transactionId == 0){
			cursor = getReadableDatabase().rawQuery(
					"SELECT MAX(" + OrderTransTable.COLUMN_TRANS_ID + ")"
					+ " FROM " + OrderTransTable.TABLE_ORDER_TRANS, null);
			if(cursor.moveToFirst()){
				transactionId = cursor.getInt(0);
			}
			cursor.close();
		}
		transactionId += 1;
		ContentValues cv = new ContentValues();
		cv.put(MaxTransIdTable.COLUMN_MAX_TRANS_ID, transactionId);
		getWritableDatabase().delete(MaxTransIdTable.TABLE_MAX_TRANS_ID, null, null);
		getWritableDatabase().insert(MaxTransIdTable.TABLE_MAX_TRANS_ID, null, cv);
		return transactionId;
	}

	/**
	 * Get max waste receipt id
	 * @param saleDate
	 * @return max waste receipt id
	 */
	public int getMaxWasteReceiptId(String saleDate) {
		int maxReceiptId = 0;
		Cursor cursor = getReadableDatabase().rawQuery(
				" SELECT MAX(" + OrderTransTable.COLUMN_RECEIPT_ID + ") "
				+ " FROM " + OrderTransTable.TABLE_ORDER_TRANS_WASTE
				+ " WHERE " + OrderTransTable.COLUMN_SALE_DATE + "=?",
				new String[] { 
					saleDate 
				});
		if (cursor.moveToFirst()) {
			maxReceiptId = cursor.getInt(0);
		}
		cursor.close();
		return maxReceiptId + 1;
	}
	
	/**
	 * Get max receiptId
	 * @param year
	 * @param month
	 * @return max receiptId
	 */
	public int getMaxReceiptId(String saleDate) {
		int maxReceiptId = 0;
		Cursor cursor = getReadableDatabase().rawQuery(
				" SELECT MAX(" + OrderTransTable.COLUMN_RECEIPT_ID + ") "
				+ " FROM " + OrderTransTable.TABLE_ORDER_TRANS
				+ " WHERE " + OrderTransTable.COLUMN_SALE_DATE + "=?",
				new String[] { 
					saleDate 
				});
		if (cursor.moveToFirst()) {
			maxReceiptId = cursor.getInt(0);
		}
		cursor.close();
		return maxReceiptId + 1;
	}

	/**
	 * Get current transactionId
	 * @param sessionId
	 * @return 0 if not have opened transaction
	 */
	public int getCurrentTransactionId(int sessionId) {
		int transactionId = 0;
		Cursor cursor = getReadableDatabase()
				.query(OrderTransTable.TEMP_ORDER_TRANS,
					new String[]{
						OrderTransTable.COLUMN_TRANS_ID
					}, 
					OrderTransTable.COLUMN_STATUS_ID + "=?"
					+ " AND " + SessionTable.COLUMN_SESS_ID + "=?", 
					new String[] { 
						String.valueOf(TRANS_STATUS_NEW),
						String.valueOf(sessionId)
					}, null, null, null);
		if (cursor.moveToFirst()) {
			transactionId = cursor.getInt(0);
		}
		cursor.close();
		return transactionId;
	}

	/**
	 * @param saleDate
	 * @param shopId
	 * @param computerId
	 * @param sessionId
	 * @param staffId
	 * @param vatRate
	 * @return current transactionId
	 * @throws SQLException
	 */
	public int openTransaction(String saleDate, int shopId, int computerId, int sessionId,
			int staffId, double vatRate) throws SQLException {
		int transactionId = getMaxTransaction();
		Calendar date = Utils.getDate();
		date.setTimeInMillis(Long.parseLong(saleDate));
		Calendar dateTime = Utils.getCalendar();
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_UUID, getUUID());
		cv.put(OrderTransTable.COLUMN_TRANS_ID, transactionId);
		cv.put(ComputerTable.COLUMN_COMPUTER_ID, computerId);
		cv.put(ShopTable.COLUMN_SHOP_ID, shopId);
		cv.put(SessionTable.COLUMN_SESS_ID, sessionId);
		cv.put(OrderTransTable.COLUMN_OPEN_STAFF, staffId);
		cv.put(OrderTransTable.COLUMN_STATUS_ID, 1);
		cv.put(OrderTransTable.COLUMN_DOC_TYPE_ID, 8);
		cv.put(COLUMN_SEND_STATUS, 0);
		cv.put(OrderTransTable.COLUMN_OPEN_TIME, dateTime.getTimeInMillis());
		cv.put(OrderTransTable.COLUMN_SALE_DATE, date.getTimeInMillis());
		cv.put(OrderTransTable.COLUMN_RECEIPT_YEAR, date.get(Calendar.YEAR));
		cv.put(OrderTransTable.COLUMN_RECEIPT_MONTH, date.get(Calendar.MONTH) + 1);
		cv.put(ProductTable.COLUMN_VAT_RATE, vatRate);
		long rowId = getWritableDatabase().insertOrThrow(
				OrderTransTable.TEMP_ORDER_TRANS, null, cv);
		if (rowId == -1)
			transactionId = 0;
		return transactionId;
	}

	/**
	 * Close waste transaction
	 * @param transactionId
	 * @param staffId
	 * @param docType
	 * @param docTypeHeader
	 * @param totalSalePrice
	 */
	public void closeWasteTransaction(int transactionId, int staffId, 
			int docType, String docTypeHeader, double totalSalePrice) {
		Calendar date = Utils.getDate();
		Calendar dateTime = Utils.getCalendar();
		int receiptId = getMaxWasteReceiptId(String.valueOf(date.getTimeInMillis()));
		String receiptNo = formatWasteReceiptNo(docTypeHeader,
				date.get(Calendar.YEAR), 
				date.get(Calendar.MONTH) + 1, 
				date.get(Calendar.DAY_OF_MONTH), receiptId);
		
		ContentValues cv = new ContentValues();
		cv.put(OrderTransTable.COLUMN_STATUS_ID, WASTE_TRANS_STATUS_SUCCESS);
		cv.put(OrderTransTable.COLUMN_RECEIPT_ID, receiptId);
		cv.put(OrderTransTable.COLUMN_CLOSE_TIME, dateTime.getTimeInMillis());
		cv.put(OrderTransTable.COLUMN_PAID_TIME, dateTime.getTimeInMillis()); 
		cv.put(OrderTransTable.COLUMN_TRANS_VATABLE, 0); 
		cv.put(OrderTransTable.COLUMN_TRANS_VAT, 0); 
		cv.put(OrderTransTable.COLUMN_TRANS_EXCLUDE_VAT, 0);
		cv.put(ProductTable.COLUMN_VAT_RATE, 0);
		cv.put(OrderTransTable.COLUMN_DOC_TYPE_ID, docType);
		cv.put(OrderTransTable.COLUMN_PAID_STAFF_ID, staffId);
		cv.put(OrderTransTable.COLUMN_CLOSE_STAFF, staffId);
		cv.put(OrderTransTable.COLUMN_RECEIPT_NO, receiptNo);
		getWritableDatabase().update(OrderTransTable.TEMP_ORDER_TRANS, 
				cv,
				OrderTransTable.COLUMN_TRANS_ID + "=?",
				new String[] { 
						String.valueOf(transactionId) 
				});
		moveTransWasteToRealTable(transactionId);
	}
	
	/**
	 * @param transactionId
	 * @param staffId
	 * @param totalSalePrice
	 */
	public void closeTransaction(int transactionId, int staffId, double totalSalePrice) {
		Calendar date = Utils.getDate();
		Calendar dateTime = Utils.getCalendar();
		int receiptId = getMaxReceiptId(String.valueOf(date.getTimeInMillis()));
		String receiptNo = formatReceiptNo(date.get(Calendar.YEAR), 
				date.get(Calendar.MONTH) + 1, 
				date.get(Calendar.DAY_OF_MONTH), receiptId);
		
		ContentValues cv = new ContentValues();
		// Calculate VAT when amount of receipt is zero
		// featureId = 19
		ProgramFeatureDao featureDao = new ProgramFeatureDao(mContext);
		ProgramFeature feature = featureDao.getProgramFeature(19);
		if(feature != null){
			if(feature.getFeatureValue() == 1 && totalSalePrice == 0){
				totalSalePrice = getSummaryOrder(transactionId, true).getTotalRetailPrice();
				ShopDao shopDao = new ShopDao(mContext);
				double transVat = Utils.calculateVatAmount(totalSalePrice, shopDao.getCompanyVatRate(), 
						shopDao.getCompanyVatType());
				cv.put(OrderTransTable.COLUMN_TRANS_VAT, transVat);
			}
		}
		cv.put(OrderTransTable.COLUMN_STATUS_ID, TRANS_STATUS_SUCCESS);
		cv.put(OrderTransTable.COLUMN_RECEIPT_ID, receiptId);
		cv.put(OrderTransTable.COLUMN_CLOSE_TIME, dateTime.getTimeInMillis());
		cv.put(OrderTransTable.COLUMN_PAID_TIME, dateTime.getTimeInMillis()); 
		cv.put(OrderTransTable.COLUMN_TRANS_VATABLE, totalSalePrice);
		cv.put(OrderTransTable.COLUMN_PAID_STAFF_ID, staffId);
		cv.put(OrderTransTable.COLUMN_CLOSE_STAFF, staffId);
		cv.put(OrderTransTable.COLUMN_RECEIPT_NO, receiptNo);
		getWritableDatabase().update(OrderTransTable.TEMP_ORDER_TRANS, 
				cv,
				OrderTransTable.COLUMN_TRANS_ID + "=?",
				new String[] { 
						String.valueOf(transactionId) 
				});
		moveTransToRealTable(transactionId);
	}

	/**
	 * Move waste transaction to real table
	 * @param transactionId
	 */
	private void moveTransWasteToRealTable(int transactionId){
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try{
			String where = OrderTransTable.COLUMN_TRANS_ID + "=" + transactionId;
			db.execSQL("insert into " + OrderTransTable.TABLE_ORDER_TRANS_WASTE 
					+ " select * from " + OrderTransTable.TEMP_ORDER_TRANS
					+ " where " + where);
			db.execSQL("insert into " + OrderDetailTable.TABLE_ORDER_WASTE
					+ " select * from " + OrderDetailTable.TEMP_ORDER
					+ " where " + where);
			db.execSQL("delete from " + OrderDetailTable.TEMP_ORDER
					+ " where " + where);
			db.execSQL("delete from " + OrderTransTable.TEMP_ORDER_TRANS
					+ " where " + where);
			db.setTransactionSuccessful();
		}finally{
			db.endTransaction();
		}
	}
	
	/**
	 * Copy transaction data from temp to real table
	 * @param transactionId
	 */
	private void moveTransToRealTable(int transactionId){
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try{
			String where = OrderTransTable.COLUMN_TRANS_ID + "=" + transactionId;
			db.execSQL("insert into " + OrderTransTable.TABLE_ORDER_TRANS 
					+ " select * from " + OrderTransTable.TEMP_ORDER_TRANS
					+ " where " + where);
			db.execSQL("insert into " + OrderDetailTable.TABLE_ORDER
					+ " select * from " + OrderDetailTable.TEMP_ORDER
					+ " where " + where);
			db.execSQL("delete from " + OrderDetailTable.TEMP_ORDER
					+ " where " + where);
			db.execSQL("delete from " + OrderTransTable.TEMP_ORDER_TRANS
					+ " where " + where);
			db.setTransactionSuccessful();
		}finally{
			db.endTransaction();
		}
	}
	
	/**
	 * @param transactionId
	 * @return row affected
	 */
	public int prepareTransaction(int transactionId) {
		ContentValues cv = new ContentValues();
		cv.put(OrderTransTable.COLUMN_STATUS_ID, TRANS_STATUS_NEW);
		return getWritableDatabase().update(
				OrderTransTable.TEMP_ORDER_TRANS, cv,
				OrderTransTable.COLUMN_TRANS_ID + "=?",
				new String[] { String.valueOf(transactionId) });
	}

	
	/**
	 * Cancel transaction
	 * @param transactionId
	 */
	public void cancelTransaction(int transactionId) {
		cancelOrder(transactionId);
		deleteTransaction(transactionId);
	}

	/**
	 * Delete OrderDetail and OrderSet by transactionId
	 * @param transactionId
	 */
	public void cancelOrder(int transactionId){
		deleteOrderDetail(transactionId);
	}
	
	/**
	 * Delete OrderDetail and OrderSet by transactonId and orderDetailId
	 * @param transactionId
	 * @param orderDetailId
	 */
	public void deleteOrder(int transactionId, int orderDetailId){
		deleteOrderComment(transactionId, orderDetailId);
		deleteOrderSet(transactionId, orderDetailId);
		deleteOrderDetail(transactionId, orderDetailId);
	}
	
	/**
	 * For clear all
	 * @param dateFrom
	 * @param dateTo
	 */
	public void deleteAllSale(String dateFrom, String dateTo){
		String transIds = getTransactionIds(dateFrom, dateTo);
		String wasteTransIds = getWasteTransactionIds(dateFrom, dateTo);
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("DELETE FROM " + OrderTransTable.TEMP_ORDER_TRANS);
		db.execSQL("DELETE FROM " + OrderDetailTable.TEMP_ORDER);
		db.execSQL("DELETE FROM " + PaymentDetailTable.TEMP_PAYMENT_DETAIL);
		db.execSQL("DELETE FROM " + PaymentDetailWasteTable.TEMP_PAYMENT_DETAIL_WASTE);
		db.beginTransaction();
		try{
			String sessWhere = SessionTable.COLUMN_SESS_DATE + " BETWEEN ? AND ? ";
			String[] sessWhereArgs = {dateFrom, dateTo};
			String transWhere = OrderTransTable.COLUMN_TRANS_ID + " IN (" + transIds + ")";
			String wasteTransWhere = OrderTransTable.COLUMN_TRANS_ID + " IN (" + wasteTransIds + ")";
			db.delete(SessionTable.TABLE_SESSION, sessWhere, sessWhereArgs);
			db.delete(SessionDetailTable.TABLE_SESSION_ENDDAY_DETAIL, sessWhere, sessWhereArgs);
			db.execSQL("DELETE FROM " + OrderDetailTable.TABLE_ORDER + " WHERE " + transWhere);
			db.execSQL("DELETE FROM " + OrderDetailTable.TABLE_ORDER_WASTE + " WHERE " + wasteTransWhere);
			db.execSQL("DELETE FROM " + PaymentDetailTable.TABLE_PAYMENT_DETAIL + " WHERE " + transWhere);
			db.execSQL("DELETE FROM " + PaymentDetailWasteTable.TABLE_PAYMENT_DETAIL_WASTE + " WHERE " + wasteTransWhere);
			db.execSQL("DELETE FROM " + OrderTransTable.TABLE_ORDER_TRANS + " WHERE " + transWhere);
			db.execSQL("DELETE FROM " + OrderTransTable.TABLE_ORDER_TRANS_WASTE + " WHERE " + wasteTransWhere);
			db.setTransactionSuccessful();
		}finally{
			db.endTransaction();
		}
	}
	
	/**
	 * Delete sale specific date
	 * @param dateFrom
	 * @param dateTo
	 */
	public void deleteSale(String dateFrom, String dateTo){
		String transIds = getAlreadySendTransactionIds(dateFrom, dateTo);
		String wasteTransIds = getAlreadySendWasteTransactionIds(dateFrom, dateTo);
		SessionDao session = new SessionDao(mContext);
		String firstSessDate = session.getFirstSessionDateAlreadySend(dateFrom, dateTo);
		String lastSessDate = session.getLastSessionDateAlreadySend(dateFrom, dateTo);
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("DELETE FROM " + OrderTransTable.TEMP_ORDER_TRANS);
		db.execSQL("DELETE FROM " + OrderDetailTable.TEMP_ORDER);
		db.execSQL("DELETE FROM " + PaymentDetailTable.TEMP_PAYMENT_DETAIL);
		db.execSQL("DELETE FROM " + PaymentDetailWasteTable.TEMP_PAYMENT_DETAIL_WASTE);
		db.beginTransaction();
		try{
			String sessWhere = SessionTable.COLUMN_SESS_DATE + " BETWEEN ? AND ? ";
			String[] sessWhereArgs = {firstSessDate, lastSessDate};
			String transWhere = OrderTransTable.COLUMN_TRANS_ID + " IN (" + transIds + ")";
			String wasteTransWhere = OrderTransTable.COLUMN_TRANS_ID + " IN (" + wasteTransIds + ")";
			db.delete(SessionTable.TABLE_SESSION, sessWhere, sessWhereArgs);
			db.delete(SessionDetailTable.TABLE_SESSION_ENDDAY_DETAIL, sessWhere, sessWhereArgs);
			db.execSQL("DELETE FROM " + OrderDetailTable.TABLE_ORDER + " WHERE " + transWhere);
			db.execSQL("DELETE FROM " + OrderDetailTable.TABLE_ORDER_WASTE + " WHERE " + wasteTransWhere);
			db.execSQL("DELETE FROM " + PaymentDetailTable.TABLE_PAYMENT_DETAIL + " WHERE " + transWhere);
			db.execSQL("DELETE FROM " + PaymentDetailWasteTable.TABLE_PAYMENT_DETAIL_WASTE + " WHERE " + wasteTransWhere);
			db.execSQL("DELETE FROM " + OrderTransTable.TABLE_ORDER_TRANS + " WHERE " + transWhere);
			db.execSQL("DELETE FROM " + OrderTransTable.TABLE_ORDER_TRANS_WASTE + " WHERE " + wasteTransWhere);
			db.setTransactionSuccessful();
		}finally{
			db.endTransaction();
		}
	}
	
	private String getAlreadySendWasteTransactionIds(String dateFrom, String dateTo){
		String transIds = "";
		Cursor cursor = getReadableDatabase().rawQuery(
				"SELECT " + OrderTransTable.COLUMN_TRANS_ID
				+ " FROM " + OrderTransTable.TABLE_ORDER_TRANS_WASTE
				+ " WHERE " + OrderTransTable.COLUMN_SALE_DATE + " BETWEEN ? AND ? "
				+ " AND " + COLUMN_SEND_STATUS + "=?", 
				new String[]{
						dateFrom,
						dateTo,
						String.valueOf(ALREADY_SEND)
				});
		if(cursor.moveToFirst()){
			do{
				transIds += cursor.getString(0);
				if(!cursor.isLast())
					transIds += ",";
			}while(cursor.moveToNext());
		}
		cursor.close();
		return transIds;
	}
	
	private String getAlreadySendTransactionIds(String dateFrom, String dateTo){
		String transIds = "";
		Cursor cursor = getReadableDatabase().rawQuery(
				"SELECT " + OrderTransTable.COLUMN_TRANS_ID
				+ " FROM " + OrderTransTable.TABLE_ORDER_TRANS
				+ " WHERE " + OrderTransTable.COLUMN_SALE_DATE + " BETWEEN ? AND ? "
				+ " AND " + COLUMN_SEND_STATUS + "=?", 
				new String[]{
						dateFrom,
						dateTo,
						String.valueOf(ALREADY_SEND)
				});
		if(cursor.moveToFirst()){
			do{
				transIds += cursor.getString(0);
				if(!cursor.isLast())
					transIds += ",";
			}while(cursor.moveToNext());
		}
		cursor.close();
		return transIds;
	}
	
	private String getWasteTransactionIds(String dateFrom, String dateTo){
		String transIds = "";
		Cursor cursor = getReadableDatabase().rawQuery(
				"SELECT " + OrderTransTable.COLUMN_TRANS_ID
				+ " FROM " + OrderTransTable.TABLE_ORDER_TRANS_WASTE
				+ " WHERE " + OrderTransTable.COLUMN_SALE_DATE + " BETWEEN ? AND ? ", 
				new String[]{
						dateFrom,
						dateTo
				});
		if(cursor.moveToFirst()){
			do{
				transIds += cursor.getString(0);
				if(!cursor.isLast())
					transIds += ",";
			}while(cursor.moveToNext());
		}
		cursor.close();
		return transIds;
	}
	
	private String getTransactionIds(String dateFrom, String dateTo){
		String transIds = "";
		Cursor cursor = getReadableDatabase().rawQuery(
				"SELECT " + OrderTransTable.COLUMN_TRANS_ID
				+ " FROM " + OrderTransTable.TABLE_ORDER_TRANS
				+ " WHERE " + OrderTransTable.COLUMN_SALE_DATE + " BETWEEN ? AND ? ", 
				new String[]{
						dateFrom,
						dateTo
				});
		if(cursor.moveToFirst()){
			do{
				transIds += cursor.getString(0);
				if(!cursor.isLast())
					transIds += ",";
			}while(cursor.moveToNext());
		}
		cursor.close();
		return transIds;
	}
	
	/**
	 * @param transactionId
	 * @return row affected
	 */
	public int deleteTransaction(int transactionId) {
		return getWritableDatabase().delete(
				OrderTransTable.TEMP_ORDER_TRANS,
				OrderTransTable.COLUMN_TRANS_ID + "=?",
				new String[] { String.valueOf(transactionId) });
	}

//	/**
//	 * get last transaction that not send
//	 * @return OrderTransaction null if not found
//	 */
//	public OrderTransaction getLastTransactionNotSend(){
//		OrderTransaction trans = null;
//		Cursor cursor = getReadableDatabase().query(OrderTransTable.TABLE_ORDER_TRANS,
//				new String[]{
//					OrderTransTable.COLUMN_TRANS_ID,
//					ComputerTable.COLUMN_COMPUTER_ID,
//					SessionTable.COLUMN_SESS_ID
//				}, OrderTransTable.COLUMN_STATUS_ID + " IN(?,?) "
//                    + " AND " + BaseColumn.COLUMN_SEND_STATUS + " =? ",
//				new String[]{
//					String.valueOf(TransactionDao.TRANS_STATUS_VOID),
//                    String.valueOf(TransactionDao.TRANS_STATUS_SUCCESS),
//				 	String.valueOf(NOT_SEND)
//				}, null, null, OrderTransTable.COLUMN_SALE_DATE + " ASC ", "1");
//		if(cursor.moveToFirst()){
//			trans = new OrderTransaction();
//			trans.setTransactionId(cursor.getInt(cursor.getColumnIndex(OrderTransTable.COLUMN_TRANS_ID)));
//			trans.setComputerId(cursor.getInt(cursor.getColumnIndex(ComputerTable.COLUMN_COMPUTER_ID)));
//			trans.setSessionId(cursor.getInt(cursor.getColumnIndex(SessionTable.COLUMN_SESS_ID)));
//		}
//		cursor.close();
//		return trans;
//	}
//	
//	/** 
//	 * List transaction not send to server
//	 * @return List<OrderTransaction>
//	 */
//	public List<OrderTransaction> listTransactionNotSend(){
//		List<OrderTransaction> transLst = new ArrayList<OrderTransaction>();
//		Cursor cursor = getReadableDatabase().query(OrderTransTable.TABLE_ORDER_TRANS,
//				new String[]{
//					OrderTransTable.COLUMN_TRANS_ID,
//					ComputerTable.COLUMN_COMPUTER_ID,
//					SessionTable.COLUMN_SESS_ID
//				}, OrderTransTable.COLUMN_STATUS_ID + " IN(?,?) "
//                    + " AND " + BaseColumn.COLUMN_SEND_STATUS + " =? ",
//				new String[]{
//					String.valueOf(TransactionDao.TRANS_STATUS_VOID),
//                    String.valueOf(TransactionDao.TRANS_STATUS_SUCCESS),
//				 	String.valueOf(NOT_SEND)
//				}, null, null, null);
//		if(cursor.moveToFirst()){
//			do{
//				OrderTransaction trans = new OrderTransaction();
//				trans.setTransactionId(cursor.getInt(cursor.getColumnIndex(OrderTransTable.COLUMN_TRANS_ID)));
//				trans.setComputerId(cursor.getInt(cursor.getColumnIndex(ComputerTable.COLUMN_COMPUTER_ID)));
//				trans.setSessionId(cursor.getInt(cursor.getColumnIndex(SessionTable.COLUMN_SESS_ID)));
//				transLst.add(trans);
//			}while(cursor.moveToNext());
//		}
//		cursor.close();
//		return transLst;
//	}
	
	/**
	 * @param saleDate
	 * @return total trans is not send
	 */
	public int countTransUnSend(String saleDate) {
		int total = 0;
		Cursor cursor = getReadableDatabase().rawQuery(
				"SELECT COUNT(" + OrderTransTable.COLUMN_TRANS_ID + ") "
                        + " FROM " + OrderTransTable.TABLE_ORDER_TRANS
                        + " WHERE " + OrderTransTable.COLUMN_SALE_DATE + "=?" 
                        + " AND " + OrderTransTable.COLUMN_STATUS_ID + " IN(?,?)"
                        + " AND " + COLUMN_SEND_STATUS + "=?",
				new String[] {
						saleDate,
						String.valueOf(TransactionDao.TRANS_STATUS_VOID),
                        String.valueOf(TransactionDao.TRANS_STATUS_SUCCESS),
						String.valueOf(NOT_SEND) });
		if (cursor.moveToFirst()) {
			total = cursor.getInt(0);
		}
		cursor.close();
		return total;
	}
	
	/**
	 * @return total transaction that not sent
	 */
	public int countTransUnSend() {
		int total = 0;
		Cursor cursor = getReadableDatabase().rawQuery(
				"SELECT COUNT(" + OrderTransTable.COLUMN_TRANS_ID + ") "
                        + " FROM " + OrderTransTable.TABLE_ORDER_TRANS
                        + " WHERE " + OrderTransTable.COLUMN_STATUS_ID + " IN(?,?)"
                        + " AND " + COLUMN_SEND_STATUS + "=?",
				new String[] {
						String.valueOf(TransactionDao.TRANS_STATUS_VOID),
                        String.valueOf(TransactionDao.TRANS_STATUS_SUCCESS),
						String.valueOf(NOT_SEND) });
		if (cursor.moveToFirst()) {
			total = cursor.getInt(0);
		}
		cursor.close();
		return total;
	}

	/**
	 * @param saleDate
	 * @return number of hold transaction
	 */
	public int countHoldOrder(String saleDate) {
		int total = 0;
		Cursor cursor = getReadableDatabase().rawQuery(
				" SELECT COUNT(" + OrderTransTable.COLUMN_TRANS_ID + ") "
				+ " FROM " + OrderTransTable.TEMP_ORDER_TRANS
				+ " WHERE " + OrderTransTable.COLUMN_STATUS_ID + "=?"
				+ " AND " + OrderTransTable.COLUMN_SALE_DATE + "=?",
				new String[] { String.valueOf(TRANS_STATUS_HOLD), saleDate });
		if (cursor.moveToFirst()) {
			total = cursor.getInt(0);
		}
		cursor.close();
		return total;
	}

	/**
	 * @param transactionId
	 * @param note
	 * @return row affected
	 */
	public int holdTransaction(int transactionId, String note) {
		ContentValues cv = new ContentValues();
		cv.put(OrderTransTable.COLUMN_STATUS_ID, TRANS_STATUS_HOLD);
		cv.put(OrderTransTable.COLUMN_TRANS_NOTE, note);
		return getWritableDatabase().update(
				OrderTransTable.TEMP_ORDER_TRANS, cv,
				OrderTransTable.COLUMN_TRANS_ID + "=?",
				new String[] { 
						String.valueOf(transactionId) 
				});
	}

	/**
	 * @param transId
	 * @param ej
	 */
	public void updateTransactionVoidEjournalWaste(int transId, String ej){
		ContentValues cv = new ContentValues();
		cv.put(OrderTransTable.COLUMN_EJ_VOID, ej);
		getWritableDatabase().update(OrderTransTable.TABLE_ORDER_TRANS_WASTE, cv,
				OrderTransTable.COLUMN_TRANS_ID + "=?",
				new String[]{
					String.valueOf(transId)
				}
		);
	}
	
	/**
	 * Update transaction set void e-journal
	 * @param transId
	 * @param ej
	 */
	public void updateTransactionVoidEjournal(int transId, String ej){
		ContentValues cv = new ContentValues();
		cv.put(OrderTransTable.COLUMN_EJ_VOID, ej);
		getWritableDatabase().update(OrderTransTable.TABLE_ORDER_TRANS, cv,
				OrderTransTable.COLUMN_TRANS_ID + "=?",
				new String[]{
					String.valueOf(transId)
				}
		);
	}
	
	/**
	 * @param transId
	 * @param ej
	 */
	public void updateTransactionEjournalWaste(int transId, String ej){
		ContentValues cv = new ContentValues();
		cv.put(OrderTransTable.COLUMN_EJ, ej);
		getWritableDatabase().update(
				OrderTransTable.TABLE_ORDER_TRANS_WASTE, cv,
				OrderTransTable.COLUMN_TRANS_ID + "=?",
				new String[]{
					String.valueOf(transId)
				}
		);
	}
	
	/**
	 * Update transaction set e-journal
	 * @param transId
	 * @param ej
	 */
	public void updateTransactionEjournal(int transId, String ej){
		ContentValues cv = new ContentValues();
		cv.put(OrderTransTable.COLUMN_EJ, ej);
		getWritableDatabase().update(
				OrderTransTable.TABLE_ORDER_TRANS, cv,
				OrderTransTable.COLUMN_TRANS_ID + "=?",
				new String[]{
					String.valueOf(transId)
				}
		);
	}
	
	/**
	 * Update transaction discount description e.g. Discount 10%
	 * @param transId
	 * @param disDesc
	 */
	public void updateTransactionDiscountDesc(int transId, String disDesc){
		ContentValues cv = new ContentValues();
		cv.put(PromotionPriceGroupTable.COLUMN_PRICE_GROUP_ID, 0); // clear price group
		cv.put(OrderTransTable.COLUMN_OTHER_DISCOUNT_DESC, disDesc);
		getWritableDatabase().update(
				OrderTransTable.TEMP_ORDER_TRANS, cv,
				OrderTransTable.COLUMN_TRANS_ID + "=?",
				new String[]{
					String.valueOf(transId)
				}
		);
	}
	
	/**
	 * Update set promotion_price_group_id
	 * @param transId
	 * @param pgId
	 */
	public void updateTransactionPromotion(int transId, int pgId){
		ContentValues cv = new ContentValues();
		cv.put(PromotionPriceGroupTable.COLUMN_PRICE_GROUP_ID, pgId);
		cv.put(OrderTransTable.COLUMN_OTHER_DISCOUNT_DESC, ""); // clear other discount
		getWritableDatabase().update(
				OrderTransTable.TEMP_ORDER_TRANS, cv,
				OrderTransTable.COLUMN_TRANS_ID + "=?",
				new String[]{
					String.valueOf(transId)
				}
		);
	}
	
	/**
	 * @param transactionId
	 * @param staffId
	 * @return row affected
	 */
	public int updateTransaction(int transactionId, int staffId) {
		ContentValues cv = new ContentValues();
		cv.put(OrderTransTable.COLUMN_OPEN_STAFF, staffId);
		return getWritableDatabase().update(
				OrderTransTable.TEMP_ORDER_TRANS, cv,
				OrderTransTable.COLUMN_TRANS_ID + "=?",
				new String[] { String.valueOf(transactionId) });
	}

	/**
	 * @param transactionId
	 * @return row affected
	 */
	public int updateTransactionSendStatus(int transactionId, int status) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_SEND_STATUS, status);
		return getWritableDatabase().update(
				OrderTransTable.TABLE_ORDER_TRANS,
				cv,
				OrderTransTable.COLUMN_TRANS_ID + "=?" + " AND "
						+ OrderTransTable.COLUMN_STATUS_ID + " IN(?,?) ",
				new String[] { String.valueOf(transactionId),
						String.valueOf(TRANS_STATUS_SUCCESS),
						String.valueOf(TRANS_STATUS_VOID) });
	}

	/**
	 * @param saleDate
	 * @return row affected
	 */
	public int updateTransactionWasteSendStatus(String saleDate, int status) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_SEND_STATUS, status);
		return getWritableDatabase().update(
				OrderTransTable.TABLE_ORDER_TRANS_WASTE,
				cv,
				OrderTransTable.COLUMN_SALE_DATE + "=?"
				+ " AND " + OrderTransTable.COLUMN_STATUS_ID + " IN(?,?) "
				+ " AND " + COLUMN_SEND_STATUS + "=?",
				new String[] { 
						saleDate, 
						String.valueOf(TRANS_STATUS_SUCCESS),
						String.valueOf(TRANS_STATUS_VOID),
						String.valueOf(NOT_SEND)});
	}
	
	/**
	 * @param transactionId
	 * @param status
	 * @return rows effected
	 */
	public int updateTransactionWasteSendStatus(int transactionId, int status) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_SEND_STATUS, status);
		return getWritableDatabase().update(
				OrderTransTable.TABLE_ORDER_TRANS_WASTE,
				cv,
				OrderTransTable.COLUMN_TRANS_ID + "=?"
				+ " AND " + OrderTransTable.COLUMN_STATUS_ID + " IN(?,?) "
				+ " AND " + COLUMN_SEND_STATUS + "=?",
				new String[] { 
						String.valueOf(transactionId), 
						String.valueOf(TRANS_STATUS_SUCCESS),
						String.valueOf(TRANS_STATUS_VOID),
						String.valueOf(NOT_SEND)});
	}
	
	/**
	 * @param saleDate
	 * @return row affected
	 */
	public int updateTransactionSendStatus(String saleDate, int status) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_SEND_STATUS, status);
		return getWritableDatabase().update(
				OrderTransTable.TABLE_ORDER_TRANS,
				cv,
				OrderTransTable.COLUMN_SALE_DATE + "=?"
				+ " AND " + OrderTransTable.COLUMN_STATUS_ID + " IN(?,?) "
				+ " AND " + COLUMN_SEND_STATUS + "=?",
				new String[] { 
						saleDate, String.valueOf(TRANS_STATUS_SUCCESS),
						String.valueOf(TRANS_STATUS_VOID),
						String.valueOf(NOT_SEND)});
	}

	/**
	 * Update transaction vat
	 * @param transactionId
	 * @param totalSalePrice
	 * @return row affected
	 */
	private int updateTransactionVat(int transactionId) {
		OrderDetail summOrder = getSummaryVat(transactionId);
		ContentValues cv = new ContentValues();
		cv.put(OrderTransTable.COLUMN_TRANS_VAT, summOrder.getVat());
		cv.put(OrderTransTable.COLUMN_TRANS_EXCLUDE_VAT, summOrder.getVatExclude());
		return getWritableDatabase().update(
				OrderTransTable.TEMP_ORDER_TRANS, cv,
				OrderTransTable.COLUMN_TRANS_ID + "=?",
				new String[] { String.valueOf(transactionId) });
	}

	/**
	 * Update transaction vat
	 * @param transactionId
	 */
	public void summaryTransaction(int transactionId){
		updateTransactionVat(transactionId);	
	}
	
	/**
	 * @param transactionId
	 * @param orderDetailId
	 * @param vatType
	 * @param vatRate
	 * @param salePrice
	 * @param discount
	 * @param priceOrPercent
	 * @param priceGroupId
	 * @param promotionTypeId
	 * @param couponHeader
	 * @return row affected
	 */
	public int discountEatchProduct(int transactionId, int orderDetailId,
			int vatType, double vatRate, double salePrice, double discount,
			int priceOrPercent, int priceGroupId, int promotionTypeId, String couponHeader){
		double vat = Utils.calculateVatAmount(salePrice, vatRate, vatType);
		ContentValues cv = new ContentValues();
		cv.put(OrderDetailTable.COLUMN_PRICE_DISCOUNT, discount);
		cv.put(OrderDetailTable.COLUMN_TOTAL_SALE_PRICE, salePrice);
		cv.put(OrderDetailTable.COLUMN_TOTAL_VAT, vat);
		if (vatType == ProductsDao.VAT_TYPE_EXCLUDE)
			cv.put(OrderDetailTable.COLUMN_TOTAL_VAT_EXCLUDE, vat);
		cv.put(OrderDetailTable.COLUMN_PRICE_OR_PERCENT, priceOrPercent);
		cv.put(PromotionPriceGroupTable.COLUMN_PROMOTION_TYPE_ID,  promotionTypeId);
		cv.put(PromotionPriceGroupTable.COLUMN_PRICE_GROUP_ID, priceGroupId);
		cv.put(PromotionPriceGroupTable.COLUMN_COUPON_HEADER, couponHeader);
		return getWritableDatabase().update(
				OrderDetailTable.TEMP_ORDER,
				cv,
				OrderDetailTable.COLUMN_ORDER_ID + "=? " + " AND "
						+ OrderTransTable.COLUMN_TRANS_ID + "=?",
				new String[] { 
						String.valueOf(orderDetailId),
						String.valueOf(transactionId) 
				});
	}

	/**
	 * @param transactionId
	 * @return row affected
	 */
	private int deleteOrderDetail(int transactionId) {
		return getWritableDatabase().delete(OrderDetailTable.TEMP_ORDER,
				OrderTransTable.COLUMN_TRANS_ID + "=?",
				new String[] { 
					String.valueOf(transactionId) 
				});
	}

	/**
	 * @param transactionId
	 * @param orderDetailId
	 */
	private void deleteOrderDetail(int transactionId, int orderDetailId) {
		getWritableDatabase().delete(
				OrderDetailTable.TEMP_ORDER,
				OrderTransTable.COLUMN_TRANS_ID + "=? AND "
						+ OrderDetailTable.COLUMN_ORDER_ID + "=?",
				new String[] { 
						String.valueOf(transactionId),
						String.valueOf(orderDetailId)
				});
		getWritableDatabase().delete(
				OrderDetailTable.TEMP_ORDER,
				OrderTransTable.COLUMN_TRANS_ID + "=? AND "
						+ OrderDetailTable.COLUMN_PARENT_ORDER_ID + "=?",
				new String[] { 
						String.valueOf(transactionId),
						String.valueOf(orderDetailId)
				});
	}
	
	/**
	 * @param transactionId
	 * @param orderDetailId
	 * @param orderComment
	 */
	public void updateOrderComment(int transactionId, int orderDetailId, String orderComment){
		ContentValues cv = new ContentValues();
		cv.put(BaseColumn.COLUMN_REMARK, orderComment);
		getWritableDatabase().update(OrderDetailTable.TEMP_ORDER, cv, 
				OrderTransTable.COLUMN_TRANS_ID + "=?"
				+ " AND " + OrderDetailTable.COLUMN_ORDER_ID + "=?", 
				new String[]{
					String.valueOf(transactionId), 
					String.valueOf(orderDetailId)
				});
	}
	
	/**
	 * @param transactionId
	 * @param orderDetailId
	 * @return row affected
	 */
	public int updateOrderDetailFreePrice(int transactionId, int orderDetailId) {
		ContentValues cv = new ContentValues();
        cv.put(OrderDetailTable.COLUMN_ORDER_STATUS, ORDER_STATUS_FREE);
        cv.put(ProductTable.COLUMN_PRODUCT_PRICE, 0);
        cv.put(OrderDetailTable.COLUMN_TOTAL_RETAIL_PRICE, 0);
		cv.put(OrderDetailTable.COLUMN_TOTAL_SALE_PRICE, 0);
		cv.put(OrderDetailTable.COLUMN_TOTAL_VAT, 0);
		cv.put(OrderDetailTable.COLUMN_TOTAL_VAT_EXCLUDE, 0);
		cv.put(OrderDetailTable.COLUMN_PRICE_DISCOUNT, 0);
		return getWritableDatabase().update(
				OrderDetailTable.TEMP_ORDER,
				cv,
				OrderTransTable.COLUMN_TRANS_ID + "=? AND "
						+ OrderDetailTable.COLUMN_ORDER_ID + "=? ",
				new String[] { String.valueOf(transactionId),
						String.valueOf(orderDetailId) });
	}
	
	/**
	 * @param transactionId
	 * @param orderDetailId
	 * @param vatType
	 * @param vatRate
	 * @param orderQty
	 * @param pricePerUnit
	 * @return row affected
	 */
	public int updateOrderDetail(int transactionId, int orderDetailId,
			int vatType, double vatRate, double orderQty, double pricePerUnit) {
		double totalRetailPrice = pricePerUnit * orderQty;
		double vat = Utils.calculateVatAmount(totalRetailPrice, vatRate, vatType);
		ContentValues cv = new ContentValues();
		cv.put(OrderDetailTable.COLUMN_ORDER_QTY, orderQty);
		cv.put(OrderDetailTable.COLUMN_TOTAL_RETAIL_PRICE, totalRetailPrice);
		cv.put(OrderDetailTable.COLUMN_TOTAL_SALE_PRICE, totalRetailPrice);
		cv.put(OrderDetailTable.COLUMN_TOTAL_VAT, vat);
		if (vatType == ProductsDao.VAT_TYPE_EXCLUDE)
			cv.put(OrderDetailTable.COLUMN_TOTAL_VAT_EXCLUDE, vat);
		cv.put(OrderDetailTable.COLUMN_PRICE_DISCOUNT, 0);
		return getWritableDatabase().update(
				OrderDetailTable.TEMP_ORDER,
				cv,
				OrderTransTable.COLUMN_TRANS_ID + "=? AND "
						+ OrderDetailTable.COLUMN_ORDER_ID + "=? ",
				new String[] { String.valueOf(transactionId),
						String.valueOf(orderDetailId) });
	}
	
	/**
	 * @param transactionId
	 * @param computerId
	 * @param productId
	 * @param productCode
	 * @param productName
	 * @param productType
	 * @param vatType
	 * @param vatRate
	 * @param orderQty
	 * @param pricePerUnit
	 * @return current orderDetailId
	 */
	public int addOrderDetail(int transactionId, int computerId, 
			int productId, int productType, int vatType, double vatRate, 
			double orderQty, double pricePerUnit) {
		double totalRetailPrice = pricePerUnit * orderQty;
		double vat = Utils.calculateVatAmount(totalRetailPrice, vatRate, vatType);
		int orderDetailId = getMaxOrderDetailId();
		ContentValues cv = new ContentValues();
		cv.put(OrderDetailTable.COLUMN_ORDER_ID, orderDetailId);
		cv.put(OrderTransTable.COLUMN_TRANS_ID, transactionId);
		cv.put(ComputerTable.COLUMN_COMPUTER_ID, computerId);
		cv.put(ProductTable.COLUMN_PRODUCT_ID, productId);
		cv.put(OrderDetailTable.COLUMN_ORDER_QTY, orderQty);
		cv.put(ProductTable.COLUMN_PRODUCT_PRICE, pricePerUnit);
		cv.put(OrderDetailTable.COLUMN_TOTAL_RETAIL_PRICE, totalRetailPrice);
		cv.put(OrderDetailTable.COLUMN_TOTAL_SALE_PRICE, totalRetailPrice);
		cv.put(ProductTable.COLUMN_VAT_TYPE, vatType);
		cv.put(OrderDetailTable.COLUMN_TOTAL_VAT, vat);
		cv.put(ProductTable.COLUMN_PRODUCT_TYPE_ID, productType);
		cv.put(OrderDetailTable.COLUMN_REMARK, "");
        cv.put(OrderDetailTable.COLUMN_ORDER_STATUS, ORDER_STATUS_NORMAL);
		if (vatType == ProductsDao.VAT_TYPE_EXCLUDE)
			cv.put(OrderDetailTable.COLUMN_TOTAL_VAT_EXCLUDE, vat);
		long rowId = getWritableDatabase().insertOrThrow(
				OrderDetailTable.TEMP_ORDER, null, cv);
		if (rowId == -1)
			orderDetailId = 0;
		return orderDetailId;
	}
	
	/**
	 * @return max orderId
	 */
	public int getMaxOrderDetailId() {
		int orderDetailId = 0;
		Cursor cursor = getReadableDatabase().rawQuery(
				" SELECT " + MaxOrderIdTable.COLUMN_MAX_ORDER_ID
				+ " FROM " + MaxOrderIdTable.TABLE_MAX_ORDER_ID, null);
		if (cursor.moveToFirst()) {
			orderDetailId = cursor.getInt(0);
		}
		cursor.close();
		if(orderDetailId == 0){
			cursor = getReadableDatabase().rawQuery(
					"SELECT MAX(" + OrderDetailTable.COLUMN_ORDER_ID + ")"
					+ " FROM " + OrderDetailTable.TABLE_ORDER, null);
			if(cursor.moveToFirst()){
				orderDetailId = cursor.getInt(0);
			}
			cursor.close();
		}
		orderDetailId += 1;
		ContentValues cv = new ContentValues();
		cv.put(MaxOrderIdTable.COLUMN_MAX_ORDER_ID, orderDetailId);
		getWritableDatabase().delete(MaxOrderIdTable.TABLE_MAX_ORDER_ID, null, null);
		getWritableDatabase().insert(MaxOrderIdTable.TABLE_MAX_ORDER_ID, null, cv);
		return orderDetailId;
	}

	/**
	 * @param transactionId
	 * @param staffId
	 * @param reason
	 */
	public void voidTransactionWaste(int transactionId, int staffId, String reason) {
		ContentValues cv = new ContentValues();
		cv.put(OrderTransTable.COLUMN_STATUS_ID, WASTE_TRANS_STATUS_VOID);
		cv.put(OrderTransTable.COLUMN_VOID_STAFF_ID, staffId);
		cv.put(OrderTransTable.COLUMN_VOID_REASON, reason);
		cv.put(COLUMN_SEND_STATUS, MPOSDatabase.NOT_SEND);
		cv.put(OrderTransTable.COLUMN_VOID_TIME, Utils.getCalendar()
				.getTimeInMillis());
		getWritableDatabase().update(
				OrderTransTable.TABLE_ORDER_TRANS_WASTE, cv,
				OrderTransTable.COLUMN_TRANS_ID + "=? ",
				new String[] { String.valueOf(transactionId) });
        cv = new ContentValues();
        cv.put(OrderDetailTable.COLUMN_ORDER_STATUS, ORDER_STATUS_VOID);
        getWritableDatabase().update(OrderDetailTable.TABLE_ORDER_WASTE, cv,
                OrderTransTable.COLUMN_TRANS_ID + "=?",
                new String[]{String.valueOf(transactionId)});
	}
	
	/**
	 * @param transactionId
	 * @param staffId
	 * @param reason
	 */
	public void voidTransaction(int transactionId, int staffId, String reason) {
		ContentValues cv = new ContentValues();
		cv.put(OrderTransTable.COLUMN_STATUS_ID, TRANS_STATUS_VOID);
		cv.put(OrderTransTable.COLUMN_VOID_STAFF_ID, staffId);
		cv.put(OrderTransTable.COLUMN_VOID_REASON, reason);
		cv.put(COLUMN_SEND_STATUS, MPOSDatabase.NOT_SEND);
		cv.put(OrderTransTable.COLUMN_VOID_TIME, Utils.getCalendar()
				.getTimeInMillis());
		getWritableDatabase().update(
				OrderTransTable.TABLE_ORDER_TRANS, cv,
				OrderTransTable.COLUMN_TRANS_ID + "=? ",
				new String[] { String.valueOf(transactionId) });
        cv = new ContentValues();
        cv.put(OrderDetailTable.COLUMN_ORDER_STATUS, ORDER_STATUS_VOID);
        getWritableDatabase().update(OrderDetailTable.TABLE_ORDER, cv,
                OrderTransTable.COLUMN_TRANS_ID + "=?",
                new String[]{String.valueOf(transactionId)});
	}

	/**
	 * @param sessId
	 * @param sessDate
	 * @return total receipt specific by sale date
	 */
	public int getTotalReceipt(int sessId, String sessDate) {
		int totalReceipt = 0;
		String selection = OrderTransTable.COLUMN_SALE_DATE + "=? "
				+ " AND " + OrderTransTable.COLUMN_STATUS_ID
				+ " IN (?,?)";
		String[] selectionArgs = new String[]{
			sessDate,
			String.valueOf(TransactionDao.TRANS_STATUS_SUCCESS),
			String.valueOf(TransactionDao.TRANS_STATUS_VOID)	
		};
		if(sessId != 0){
			selection += " AND " + SessionTable.COLUMN_SESS_ID + "=?";
			selectionArgs = new String[]{
				sessDate,
				String.valueOf(TransactionDao.TRANS_STATUS_SUCCESS),
				String.valueOf(TransactionDao.TRANS_STATUS_VOID),
				String.valueOf(sessId)
			};
		}
		Cursor cursor = getReadableDatabase().rawQuery(
				"SELECT COUNT (" + OrderTransTable.COLUMN_TRANS_ID + ") "
				+ " FROM " + OrderTransTable.TABLE_ORDER_TRANS
				+ " WHERE " + selection, selectionArgs);
		if (cursor.moveToFirst()) {
			totalReceipt = cursor.getInt(0);
		}
		cursor.close();
		return totalReceipt;
	}

	/**
	 * @param sessionDate
	 * @return total receipt amount
	 */
	public double getTotalReceiptAmount(String sessionDate) {
		double totalReceiptAmount = 0.0f;
		Cursor cursor = getReadableDatabase().rawQuery(
				"SELECT "
				+ " SUM (b." + PaymentDetailTable.COLUMN_PAY_AMOUNT + ") "
				+ " FROM " + OrderTransTable.TABLE_ORDER_TRANS + " a "
				+ " LEFT JOIN " + PaymentDetailTable.TABLE_PAYMENT_DETAIL + " b "
				+ " ON a." + OrderTransTable.COLUMN_TRANS_ID + "=b." + OrderTransTable.COLUMN_TRANS_ID 
				+ " WHERE a." + OrderTransTable.COLUMN_SALE_DATE + "=? "
				+ " AND a." + OrderTransTable.COLUMN_STATUS_ID + " IN(?,?) ",
				new String[] { sessionDate,
						String.valueOf(TransactionDao.TRANS_STATUS_SUCCESS),
						String.valueOf(TransactionDao.TRANS_STATUS_VOID)
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
	public double getTotalReceiptAmount(int sessionId) {
		double totalReceiptAmount = 0.0f;
		Cursor cursor = getReadableDatabase().rawQuery(
				"SELECT "
				+ " SUM (b." + PaymentDetailTable.COLUMN_PAY_AMOUNT + ") "
				+ " FROM " + OrderTransTable.TABLE_ORDER_TRANS + " a "
				+ " LEFT JOIN " + PaymentDetailTable.TABLE_PAYMENT_DETAIL + " b "
				+ " ON a." + OrderTransTable.COLUMN_TRANS_ID + "=b." + OrderTransTable.COLUMN_TRANS_ID 
				+ " WHERE a." + SessionTable.COLUMN_SESS_ID + "=? "
				+ " AND a." + OrderTransTable.COLUMN_STATUS_ID + " IN(?,?) ",
				new String[] { 
						String.valueOf(sessionId),
						String.valueOf(TransactionDao.TRANS_STATUS_SUCCESS),
						String.valueOf(TransactionDao.TRANS_STATUS_VOID)
				});
		if (cursor.moveToFirst()) {
			totalReceiptAmount = cursor.getDouble(0);
		}
		cursor.close();
		return totalReceiptAmount;
	}
	
	/**
	 * @param transactionId
	 * @param orderDetailId
	 * @param orderSetId
	 * @return OrderSet
	 */
	public OrderSet getOrderSet(int transactionId, int orderDetailId, int orderSetId) {
		OrderSet orderSet = null;
		String selection = " a." + OrderTransTable.COLUMN_TRANS_ID + "=? "
				+ " AND a." + OrderDetailTable.COLUMN_PARENT_ORDER_ID + "=?"
				+ " AND a." + OrderDetailTable.COLUMN_ORDER_ID + "=?";
		String[] selectionArgs = { 
				String.valueOf(transactionId),
				String.valueOf(orderDetailId),
				String.valueOf(orderSetId)
		};
		Cursor cursor = queryOrderSet(selection, selectionArgs);
		if (cursor.moveToFirst()) {
			orderSet = new OrderSet();
			int pgId = cursor.getInt(cursor.getColumnIndex(ProductComponentTable.COLUMN_PGROUP_ID));
			orderSet.setTransId(transactionId);
			orderSet.setOrdId(orderDetailId);
			orderSet.setSetGroupId(pgId);
			orderSet.setSetGroupNo(cursor.getInt(cursor.getColumnIndex(ProductComponentGroupTable.COLUMN_SET_GROUP_NO)));
			orderSet.setSetGroupName(cursor.getString(cursor.getColumnIndex(ProductComponentGroupTable.COLUMN_SET_GROUP_NAME)));
			orderSet.setReqAmount(cursor.getDouble(cursor.getColumnIndex(ProductComponentGroupTable.COLUMN_REQ_AMOUNT)));
			orderSet.setReqMinAmount(cursor.getDouble(cursor.getColumnIndex(ProductComponentGroupTable.COLUMN_REQ_MIN_AMOUNT)));
			orderSet.setOrderSetDetail(listOrderSetDetail(transactionId, orderDetailId, pgId));
		}
		cursor.close();
		return orderSet;
	}
	
	/**
	 * @param transactionId
	 * @param orderDetailId
	 * @return List<OrderSet>
	 */
	public List<OrderSet> listOrderSet(int transactionId, int orderDetailId) {
		List<OrderSet> orderSetLst = null;
		String selection = " a." + OrderTransTable.COLUMN_TRANS_ID + "=? "
				+ " AND a." + OrderDetailTable.COLUMN_PARENT_ORDER_ID + "=?";
		String[] selectionArgs = { 
				String.valueOf(transactionId),
				String.valueOf(orderDetailId)
		};
		Cursor cursor = queryOrderSet(selection, selectionArgs);
		if (cursor.moveToFirst()) {
			orderSetLst = new ArrayList<OrderSet>();
			do {
				int pgId = cursor.getInt(cursor.getColumnIndex(ProductComponentTable.COLUMN_PGROUP_ID));
				OrderSet orderSet = new OrderSet();
				orderSet.setTransId(transactionId);
				orderSet.setOrdId(orderDetailId);
				orderSet.setSetGroupId(pgId);
				orderSet.setSetGroupNo(cursor.getInt(cursor.getColumnIndex(ProductComponentGroupTable.COLUMN_SET_GROUP_NO)));
				orderSet.setSetGroupName(cursor.getString(cursor.getColumnIndex(ProductComponentGroupTable.COLUMN_SET_GROUP_NAME)));
				orderSet.setReqAmount(cursor.getDouble(cursor.getColumnIndex(ProductComponentGroupTable.COLUMN_REQ_AMOUNT)));
				orderSet.setReqMinAmount(cursor.getDouble(cursor.getColumnIndex(ProductComponentGroupTable.COLUMN_REQ_MIN_AMOUNT)));
				orderSet.setOrderSetDetail(listOrderSetDetail(transactionId, orderDetailId, pgId));
				orderSetLst.add(orderSet);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return orderSetLst;
	}

	/**
	 * @param selection
	 * @param selectionArgs
	 * @return cursor
	 */
	private Cursor queryOrderSet(String selection, String[] selectionArgs){
		String sql = " SELECT b." + ProductComponentTable.COLUMN_PGROUP_ID + ", "
				+ " b." + ProductComponentGroupTable.COLUMN_SET_GROUP_NO + ", "
				+ " b." + ProductComponentGroupTable.COLUMN_SET_GROUP_NAME + ", " 
				+ " b." + ProductComponentGroupTable.COLUMN_REQ_AMOUNT + ", "
				+ " b." + ProductComponentGroupTable.COLUMN_REQ_MIN_AMOUNT
				+ " FROM " + OrderDetailTable.TEMP_ORDER + " a "
				+ " LEFT JOIN " + ProductComponentGroupTable.TABLE_PCOMPONENT_GROUP + " b " 
				+ " ON a." + ProductComponentTable.COLUMN_PGROUP_ID + "=b." + ProductComponentTable.COLUMN_PGROUP_ID
				+ " WHERE " + selection
				+ " GROUP BY b." + ProductComponentTable.COLUMN_PGROUP_ID;
		return getReadableDatabase().rawQuery(sql, selectionArgs);
	}
	
	/**
	 * @param transId
	 * @param ordId
	 * @param pgId
	 * @return List<OrderSet.OrderSetDetail>
	 */
	public List<OrderSet.OrderSetDetail> listOrderSetDetail(int transId, int ordId, int pgId) {
		List<OrderSet.OrderSetDetail> sdl = null;
		Cursor cursor = queryOrderDetail(
				"a." + OrderTransTable.COLUMN_TRANS_ID + "=?"
				+ " AND a." + OrderDetailTable.COLUMN_PARENT_ORDER_ID + "=?"
				+ " AND a." + ProductComponentTable.COLUMN_PGROUP_ID + "=?",
				new String[] {
					String.valueOf(transId),
					String.valueOf(ordId),
					String.valueOf(pgId),
				});
		if (cursor.moveToFirst()) {
			sdl = new ArrayList<OrderSet.OrderSetDetail>();
			do {
				sdl.add(toOrderSetDetail(cursor));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return sdl;
	}
	
	/**
	 * @param transId
	 * @param ordId
	 * @return List<OrderSet.OrderSetDetail>
	 */
	public List<OrderSet.OrderSetDetail> listOrderSetDetail(int transId, int ordId) {
		List<OrderSet.OrderSetDetail> sdl = null;
		Cursor cursor = queryOrderDetail(
				"a." + OrderTransTable.COLUMN_TRANS_ID + "=?"
				+ " AND a." + OrderDetailTable.COLUMN_PARENT_ORDER_ID + "=?"
				+ " AND a." + ProductTable.COLUMN_PRODUCT_TYPE_ID + "=?",
				new String[] {
					String.valueOf(transId),
					String.valueOf(ordId),
					String.valueOf(ProductsDao.CHILD_OF_SET_HAVE_PRICE)
				});
		if (cursor.moveToFirst()) {
			sdl = new ArrayList<OrderSet.OrderSetDetail>();
			do {
				sdl.add(toOrderSetDetail(cursor));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return sdl;
	}
	
	/**
	 * @param transactionId
	 * @param parentOrderId
	 * @param isLoadTemp
	 * @return List<OrderSet.OrderSetDetail>
	 */
	public List<OrderSet.OrderSetDetail> listGroupedOrderSetDetail(int transactionId, int parentOrderId, boolean isLoadTemp) {
		List<OrderSet.OrderSetDetail> sdl = null;
		String tables = (isLoadTemp ? OrderDetailTable.TEMP_ORDER : OrderDetailTable.TABLE_ORDER) + " a"
				+ " LEFT JOIN " + ProductTable.TABLE_PRODUCT + " b"
				+ " ON a."  + ProductTable.COLUMN_PRODUCT_ID + " =b." + ProductTable.COLUMN_PRODUCT_ID;
		String selection = "a." + OrderTransTable.COLUMN_TRANS_ID + "=?"
				+ " AND a." + OrderDetailTable.COLUMN_PARENT_ORDER_ID + "=?"
				+ " AND a." + ProductTable.COLUMN_PRODUCT_TYPE_ID + "=?";
		Cursor cursor = queryOrderDetail(
				tables,
				selection,
				new String[] {
					String.valueOf(transactionId),
					String.valueOf(parentOrderId),
					String.valueOf(ProductsDao.CHILD_OF_SET_HAVE_PRICE)
				}, "a." + ProductTable.COLUMN_PRODUCT_ID);
		if (cursor.moveToFirst()) {
			sdl = new ArrayList<OrderSet.OrderSetDetail>();
			do {
				sdl.add(toOrderSetDetail(cursor));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return sdl;
	}
	
	/**
	 * @param cursor
	 * @return OrderSet.OrderSetDetail
	 */
	private OrderSet.OrderSetDetail toOrderSetDetail(Cursor cursor){
		OrderSet.OrderSetDetail sd = new OrderSet.OrderSetDetail();
		sd.setOrderSetId(cursor.getInt(cursor.getColumnIndex(OrderDetailTable.COLUMN_ORDER_ID)));
		sd.setProductId(cursor.getInt(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_ID)));
		sd.setProductName(cursor.getString(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_NAME)));
		sd.setProductName1(cursor.getString(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_NAME1)));
		sd.setOrderSetQty(cursor.getDouble(cursor.getColumnIndex(OrderDetailTable.COLUMN_ORDER_QTY)));
		sd.setDeductAmount(cursor.getDouble(cursor.getColumnIndex(OrderDetailTable.COLUMN_DEDUCT_AMOUNT)));
		sd.setProductPrice(cursor.getDouble(cursor.getColumnIndex(OrderDetailTable.COLUMN_TOTAL_RETAIL_PRICE)));
		return sd;
	}
	
	/**
	 * @param transactionId
	 * @param orderDetailId
	 * @param pcompGroupId
	 * @return total qty of group
	 */
	public double getOrderSetTotalQty(int transactionId, int orderDetailId, int pcompGroupId) {
		double totalQty = 0;
		Cursor cursor = getReadableDatabase().rawQuery(
				" SELECT SUM(" + OrderDetailTable.COLUMN_ORDER_QTY + ") "
				+ " FROM " + OrderDetailTable.TEMP_ORDER 
				+ " WHERE " + OrderTransTable.COLUMN_TRANS_ID + "=? "
				+ " AND " + OrderDetailTable.COLUMN_PARENT_ORDER_ID + "=? "
				+ " AND " + ProductComponentTable.COLUMN_PGROUP_ID + "=? ",
				new String[] { 
					String.valueOf(transactionId),
					String.valueOf(orderDetailId),
					String.valueOf(pcompGroupId) 
				});
		if (cursor.moveToFirst()) {
			totalQty = cursor.getDouble(0);
		}
		cursor.close();
		return totalQty;
	}

	/**
	 * @param transactionId
	 * @param orderDetailId
	 */
	public void deleteOrderSet(int transactionId, int orderDetailId) {
		getWritableDatabase().delete(
				OrderDetailTable.TEMP_ORDER,
				OrderTransTable.COLUMN_TRANS_ID + "=? "
				+ " and " + OrderDetailTable.COLUMN_PARENT_ORDER_ID + "=? ",
				new String[] { 
					String.valueOf(transactionId),
					String.valueOf(orderDetailId) 
				});
	}

	/**
	 * @param transactionId
	 * @param orderDetailId
	 * @param orderSetId
	 */
	public void deleteOrderSet(int transactionId, int orderDetailId, int orderSetId) {
		getWritableDatabase().delete(
				OrderDetailTable.TEMP_ORDER,
				OrderTransTable.COLUMN_TRANS_ID + "=? "
				+ " and " + OrderDetailTable.COLUMN_PARENT_ORDER_ID + "=? " 
				+ " and " + OrderDetailTable.COLUMN_ORDER_ID + "=?",
				new String[] { 
					String.valueOf(transactionId),
					String.valueOf(orderDetailId),
					String.valueOf(orderSetId) 
				});
	}
	
	/**
	 * @param transactionId
	 * @param orderDetailId
	 * @param pCompGroupId
	 */
	public void deleteOrderSetByGroup(int transactionId, int orderDetailId, int pCompGroupId) {
		getWritableDatabase().delete(
				OrderDetailTable.TEMP_ORDER,
				OrderTransTable.COLUMN_TRANS_ID + "=? "
				+ " and " + OrderDetailTable.COLUMN_PARENT_ORDER_ID + "=? " 
				+ " and " + ProductComponentTable.COLUMN_PGROUP_ID + "=?",
				new String[] { 
					String.valueOf(transactionId),
					String.valueOf(orderDetailId),
					String.valueOf(pCompGroupId) 
				});
	}
	
	/**
	 * @param transactionId
	 * @param orderDetailId
	 * @param orderSetId
	 * @param productId
	 * @param orderSetQty
	 */
	public void updateOrderSet(int transactionId, int orderDetailId,
			int orderSetId, int productId, double orderSetQty) {
		ContentValues cv = new ContentValues();
		cv.put(OrderDetailTable.COLUMN_ORDER_QTY, orderSetQty);
		getWritableDatabase().update(
				OrderDetailTable.TEMP_ORDER,
				cv,
				OrderTransTable.COLUMN_TRANS_ID + "=? "
				+ " and " + OrderDetailTable.COLUMN_PARENT_ORDER_ID + "=? " 
				+ " and " + OrderDetailTable.COLUMN_ORDER_ID + "=?",
				new String[] { 
					String.valueOf(transactionId),
					String.valueOf(orderDetailId),
					String.valueOf(orderSetId) 
				});
	}

	/**
	 * @param transactionId
	 * @param computerId
	 * @param orderDetailId
	 * @param productId
	 * @param productTypeId
	 * @param orderSetQty
	 * @param productPrice
	 * @param pcompGroupId
	 * @param reqAmount
	 * @param reqMinAmount
	 * @return maxOrderId
	 */
	public int addOrderSet(int transactionId, int computerId, int orderDetailId,
			int productId, int productTypeId, double orderSetQty, double productPrice, 
			int pcompGroupId, double reqAmount, double reqMinAmount) {
		int maxOrderId = getMaxOrderDetailId();
		double totalRetailPrice = productPrice * orderSetQty;
		ContentValues cv = new ContentValues();
		cv.put(OrderDetailTable.COLUMN_ORDER_ID, maxOrderId);
		cv.put(OrderTransTable.COLUMN_TRANS_ID, transactionId);
		cv.put(ComputerTable.COLUMN_COMPUTER_ID, computerId);
		cv.put(ProductTable.COLUMN_PRODUCT_ID, productId);
		cv.put(OrderDetailTable.COLUMN_ORDER_QTY, orderSetQty);
		cv.put(OrderDetailTable.COLUMN_DEDUCT_AMOUNT, orderSetQty);
		cv.put(ProductTable.COLUMN_PRODUCT_PRICE, productPrice);
		cv.put(OrderDetailTable.COLUMN_TOTAL_RETAIL_PRICE, totalRetailPrice);
		cv.put(OrderDetailTable.COLUMN_TOTAL_SALE_PRICE, totalRetailPrice);
		cv.put(ProductTable.COLUMN_PRODUCT_TYPE_ID, productTypeId);
		cv.put(ProductComponentTable.COLUMN_PGROUP_ID, pcompGroupId);
		cv.put(ProductComponentGroupTable.COLUMN_REQ_AMOUNT, reqAmount);
		cv.put(ProductComponentGroupTable.COLUMN_REQ_MIN_AMOUNT, reqMinAmount);
		cv.put(OrderDetailTable.COLUMN_REMARK, "");
		cv.put(OrderDetailTable.COLUMN_PARENT_ORDER_ID, orderDetailId);
        cv.put(OrderDetailTable.COLUMN_ORDER_STATUS, ORDER_STATUS_NORMAL);
		try {
			getWritableDatabase().insertOrThrow(OrderDetailTable.TEMP_ORDER, null, cv);
		} catch (SQLException e) {
			maxOrderId = 0;
		}
		return maxOrderId;
	}

	/**
	 * @param transactionId
	 * @param orderDetailId
	 * @param productGroupId
	 * @return rows
	 */
	public int checkAddedOrderSet(int transactionId, int orderDetailId, int productGroupId){
		int added = 0;
		Cursor cursor = getReadableDatabase().rawQuery(
				"select count(" + OrderDetailTable.COLUMN_ORDER_ID + ") "
				+ " from " + OrderDetailTable.TEMP_ORDER
				+ " where " + OrderTransTable.COLUMN_TRANS_ID + "=?"
				+ " and " + OrderDetailTable.COLUMN_PARENT_ORDER_ID + "=? "
				+ " and " + ProductComponentTable.COLUMN_PGROUP_ID + "=?", 
				new String[]{
					String.valueOf(transactionId),
					String.valueOf(orderDetailId),
					String.valueOf(productGroupId)
				});
		if(cursor.moveToFirst()){
			added = cursor.getInt(0);
		}
		cursor.close();
		return added;
	}
	
	/**
	 * @param transId
	 * @param ordId
	 * @return List<Comment> 
	 */
	public List<OrderComment> listOrderComment(int transId, int ordId){
		List<OrderComment> ordCmLst = new ArrayList<OrderComment>();
		String selection = "a." + OrderTransTable.COLUMN_TRANS_ID + "=?"
				+ " AND a." + OrderDetailTable.COLUMN_PARENT_ORDER_ID + "=?"
				+ " AND a." + ProductTable.COLUMN_PRODUCT_TYPE_ID + " IN(?,?) ";
		Cursor cursor = queryOrderDetail(
				selection,
				new String[]{
					String.valueOf(transId),
					String.valueOf(ordId),
					String.valueOf(ProductsDao.COMMENT_HAVE_PRICE),
					String.valueOf(ProductsDao.COMMENT_NOT_HAVE_PRICE)
				});
		if(cursor.moveToFirst()){
			do{
				ordCmLst.add(toOrderComment(cursor));
			}while(cursor.moveToNext());
		}
		cursor.close();
		return ordCmLst;
	}
	
	/**
	 * @param transactionId
	 * @param parentOrderId
	 * @param isLoadTemp
	 * @return List<OrderComment>
	 */
	public List<OrderComment> listGroupedOrderComment(int transactionId, int parentOrderId, boolean isLoadTemp){
		List<OrderComment> ordCmLst = new ArrayList<OrderComment>();
		String tables = (isLoadTemp ? OrderDetailTable.TEMP_ORDER : OrderDetailTable.TABLE_ORDER) + " a"
				+ " LEFT JOIN " + ProductTable.TABLE_PRODUCT + " b"
				+ " ON a."  + ProductTable.COLUMN_PRODUCT_ID + " =b." + ProductTable.COLUMN_PRODUCT_ID;
		String selection = "a." + OrderTransTable.COLUMN_TRANS_ID + "=? "
				+ " AND a." + OrderDetailTable.COLUMN_PARENT_ORDER_ID + "=? "
				+ " AND a." + ProductTable.COLUMN_PRODUCT_TYPE_ID + " IN(?,?) "; 
		Cursor cursor = queryOrderDetail(
				tables,
				selection,
				new String[]{
					String.valueOf(transactionId),
					String.valueOf(parentOrderId),
					String.valueOf(ProductsDao.COMMENT_HAVE_PRICE),
					String.valueOf(ProductsDao.COMMENT_NOT_HAVE_PRICE)
				}, "a." + ProductTable.COLUMN_PRODUCT_ID);
		if(cursor.moveToFirst()){
			do{
				ordCmLst.add(toOrderComment(cursor));	
			}while(cursor.moveToNext());
		}
		cursor.close();
		return ordCmLst;
	}
	
	private OrderComment toOrderComment(Cursor cursor){
		OrderComment cm = new OrderComment();
		cm.setCommentId(cursor.getInt(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_ID)));
		cm.setCommentName(cursor.getString(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_NAME)));
		cm.setCommentName1(cursor.getString(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_NAME1)));
		cm.setCommentQty(cursor.getDouble(cursor.getColumnIndex(OrderDetailTable.COLUMN_ORDER_QTY)));
		cm.setCommentPrice(cursor.getDouble(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_PRICE)));
		cm.setCommentTotalPrice(cursor.getDouble(cursor.getColumnIndex(OrderDetailTable.COLUMN_TOTAL_RETAIL_PRICE)));
		return cm;
	}
	
	/**
	 * @param transId
	 * @param ordId
	 * @param cmId
	 * @return Comment
	 */
	public Comment getOrderComment(int transId, int ordId, int cmId){
		Comment ordCm = new Comment();
		String selection = " a." + OrderTransTable.COLUMN_TRANS_ID + "=?"
				+ " AND a." + OrderDetailTable.COLUMN_PARENT_ORDER_ID + "=?"
				+ " AND a." + ProductTable.COLUMN_PRODUCT_ID + "=?"
				+ " AND a." + ProductTable.COLUMN_PRODUCT_TYPE_ID + " IN(?,?) ";
		Cursor cursor = queryOrderDetail(
				selection,
				new String[]{
					String.valueOf(transId),
					String.valueOf(ordId),
					String.valueOf(cmId),
					String.valueOf(ProductsDao.COMMENT_HAVE_PRICE),
					String.valueOf(ProductsDao.COMMENT_NOT_HAVE_PRICE)
				});
		if(cursor.moveToFirst()){
			ordCm = toOrderComment(cursor);
		}
		cursor.close();
		return ordCm;
	}

	/**
	 * @param transactionId
	 * @param orderDetailId
	 * @param commentId
	 */
	public void deleteOrderComment(int transactionId, int orderDetailId, int commentId){
		getWritableDatabase().delete(OrderDetailTable.TEMP_ORDER, 
				OrderTransTable.COLUMN_TRANS_ID + "=?"
				+ " and " + OrderDetailTable.COLUMN_PARENT_ORDER_ID + "=?"
				+ " and " + ProductTable.COLUMN_PRODUCT_ID + "=?", 
			new String[]{
				String.valueOf(transactionId),
				String.valueOf(orderDetailId),
				String.valueOf(commentId)
			});
	}
	
	/**
	 * @param transactionId
	 * @param orderDetailId
	 */
	public void deleteOrderComment(int transactionId, int orderDetailId){
		getWritableDatabase().delete(OrderDetailTable.TEMP_ORDER, 
				OrderTransTable.COLUMN_TRANS_ID + "=?"
				+ " and " + OrderDetailTable.COLUMN_PARENT_ORDER_ID + "=?", 
			new String[]{
				String.valueOf(transactionId),
				String.valueOf(orderDetailId)
			});
	}
	
	/**
	 * @param transactionId
	 * @param orderDetailId
	 * @param commentId
	 * @param commentQty
	 * @param commentPrice
	 * @throws SQLException
	 */
	public void updateOrderComment(int transactionId, int orderDetailId, 
			int commentId, double commentQty, double commentPrice) throws SQLException{
		double totalRetailPrice = commentPrice * commentQty;
		ContentValues cv = new ContentValues();
		cv.put(ProductTable.COLUMN_PRODUCT_ID, commentId);
		cv.put(OrderDetailTable.COLUMN_ORDER_QTY, commentQty);
		cv.put(OrderDetailTable.COLUMN_TOTAL_SALE_PRICE, totalRetailPrice);
		cv.put(OrderDetailTable.COLUMN_TOTAL_RETAIL_PRICE, totalRetailPrice);
		getWritableDatabase().update(
				OrderDetailTable.TEMP_ORDER, cv, 
				OrderTransTable.COLUMN_TRANS_ID + "=?"
				+ " AND " + OrderDetailTable.COLUMN_PARENT_ORDER_ID + "=?"
				+ " AND " + ProductTable.COLUMN_PRODUCT_ID + "=?", 
				new String[]{
					String.valueOf(transactionId),
					String.valueOf(orderDetailId),
					String.valueOf(commentId)
				});
	}
	
	/**
	 * @param transactionId
	 * @param computerId
	 * @param orderDetailId
	 * @param commentId
	 * @param productTypeId
	 * @param commentQty
	 * @param commentPrice
	 * @throws SQLException
	 */
	public void addOrderComment(int transactionId, int computerId, int orderDetailId, int commentId,
			int productTypeId, double commentQty, double commentPrice) throws SQLException{
		int maxOrderId = getMaxOrderDetailId();
		ContentValues cv = new ContentValues();
		double totalRetailPrice = commentPrice * commentQty;
		cv.put(OrderDetailTable.COLUMN_ORDER_ID, maxOrderId);
		cv.put(OrderTransTable.COLUMN_TRANS_ID, transactionId);
		cv.put(ComputerTable.COLUMN_COMPUTER_ID, computerId);
		cv.put(ProductTable.COLUMN_PRODUCT_ID, commentId);
		cv.put(OrderDetailTable.COLUMN_ORDER_QTY, commentQty);
		cv.put(ProductTable.COLUMN_PRODUCT_PRICE, commentPrice);
		cv.put(OrderDetailTable.COLUMN_TOTAL_RETAIL_PRICE, totalRetailPrice);
		cv.put(OrderDetailTable.COLUMN_TOTAL_SALE_PRICE, totalRetailPrice);
		cv.put(ProductTable.COLUMN_PRODUCT_TYPE_ID, productTypeId);
		cv.put(OrderDetailTable.COLUMN_REMARK, "");
		cv.put(OrderDetailTable.COLUMN_PARENT_ORDER_ID, orderDetailId);
        cv.put(OrderDetailTable.COLUMN_ORDER_STATUS, ORDER_STATUS_NORMAL);
		getWritableDatabase().insertOrThrow(OrderDetailTable.TEMP_ORDER, null, cv);
	}
	
	/**
	 * @param transactionId
	 * @param orderDetailId
	 * @param commentId
	 * @return
	 */
	public boolean checkAddedComment(int transactionId, int orderDetailId, int commentId){
		boolean isAdded = false;
		Cursor cursor = getReadableDatabase().query(
				OrderDetailTable.TEMP_ORDER, 
				new String[]{
					ProductTable.COLUMN_PRODUCT_ID
				}, 
				OrderTransTable.COLUMN_TRANS_ID + "=?"
				+ " and " + OrderDetailTable.COLUMN_PARENT_ORDER_ID + "=?"
				+ " and " + ProductTable.COLUMN_PRODUCT_ID + "=?", 
				new String[]{
					String.valueOf(transactionId),
					String.valueOf(orderDetailId),
					String.valueOf(commentId)
				}, null, null, null);
		if(cursor.moveToFirst()){
			if(cursor.getInt(0) != 0)
				isAdded = true;
		}
		cursor.close();
		return isAdded;
	}
}
