package com.synature.mpos.database.table;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.synature.mpos.MPOSApplication;
import com.synature.mpos.database.MPOSDatabase;

import java.text.DateFormat;
import java.util.Calendar;

public class OrderDetailTable extends BaseColumn{

    public static final String TAG = OrderDetailTable.class.getSimpleName();

	public static final String TABLE_ORDER = "OrderDetail";
	public static final String TEMP_ORDER = "OrderDetailTemp";
	public static final String TABLE_ORDER_WASTE = "OrderDetailWaste";
	public static final String COLUMN_ORDER_ID = "order_detail_id";
	public static final String COLUMN_ORDER_QTY = "order_qty";
	public static final String COLUMN_TOTAL_RETAIL_PRICE = "total_retail_price";
	public static final String COLUMN_TOTAL_SALE_PRICE = "total_sale_price";
	public static final String COLUMN_TOTAL_VAT = "total_vat_amount";
	public static final String COLUMN_TOTAL_VAT_EXCLUDE = "total_vat_amount_exclude";
	public static final String COLUMN_MEMBER_DISCOUNT = "member_discount_amount";
	public static final String COLUMN_PRICE_DISCOUNT = "price_discount_amount";
	public static final String COLUMN_PRICE_OR_PERCENT = "price_or_percent";
	public static final String COLUMN_DEDUCT_AMOUNT = "deduct_amount";
	public static final String COLUMN_PARENT_ORDER_ID = "parent_order_id";
    public static final String COLUMN_ORDER_STATUS = "order_status";

	private static final String ORDER_SQL_CREATE = 
			" create table " + TABLE_ORDER + " ( " 
			+ COLUMN_ORDER_ID + " integer not null, "
			+ OrderTransTable.COLUMN_TRANS_ID + " integer not null, "
			+ ComputerTable.COLUMN_COMPUTER_ID + " integer not null, "
			+ ProductTable.COLUMN_PRODUCT_ID + " integer not null, "
			+ ProductTable.COLUMN_PRODUCT_TYPE_ID + " integer default 0, "
			+ COLUMN_ORDER_QTY + " real default 1, "
			+ ProductTable.COLUMN_PRODUCT_PRICE + " real default 0, "
			+ COLUMN_PRICE_OR_PERCENT + " integer default 2, "
			+ ProductTable.COLUMN_VAT_TYPE + " integer default 1, "
			+ COLUMN_TOTAL_VAT + " real default 0, "
			+ COLUMN_TOTAL_VAT_EXCLUDE + " real default 0, "
			+ COLUMN_MEMBER_DISCOUNT + " real default 0, "
			+ COLUMN_PRICE_DISCOUNT + " real default 0, "
			+ COLUMN_TOTAL_RETAIL_PRICE + " real default 0, "
			+ COLUMN_TOTAL_SALE_PRICE + " real default 0, "
			+ ProductComponentTable.COLUMN_PGROUP_ID + " integer, "
			+ ProductComponentGroupTable.COLUMN_REQ_AMOUNT + " real default 0, " 
			+ ProductComponentGroupTable.COLUMN_REQ_MIN_AMOUNT + " real default 0, "
			+ COLUMN_DEDUCT_AMOUNT + " real default 0,"
			+ ProductTable.COLUMN_SALE_MODE + " integer default 1, "
			+ PromotionPriceGroupTable.COLUMN_PRICE_GROUP_ID + " integer default 0,"
			+ PromotionPriceGroupTable.COLUMN_PROMOTION_TYPE_ID + " integer default 0,"
			+ PromotionPriceGroupTable.COLUMN_COUPON_HEADER + " text,"
			+ COLUMN_PARENT_ORDER_ID + " integer default 0, "
			+ COLUMN_REMARK + " text, "
            + COLUMN_ORDER_STATUS + " integer default 1, "
			+ " primary key (" + COLUMN_ORDER_ID + " desc));";

	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(ORDER_SQL_CREATE);
		db.execSQL("create table " + TEMP_ORDER + " as select * from " + TABLE_ORDER + " where 0;");
		db.execSQL("create table " + TABLE_ORDER_WASTE + " as select * from " + TABLE_ORDER + " where 0;");
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
        boolean isOrderWasteExists = MPOSDatabase.checkTableExists(db, TABLE_ORDER_WASTE);
        db.beginTransaction();
        try{
            Log.i(TAG, "upgrading " + TABLE_ORDER + " from " + oldVersion + " to " + MPOSApplication.DB_VERSION);
            String tbOrderCopy = "OrderDetailCopy";
            String tbOrderWasteCopy = "OrderWasteCopy";
            Log.i(TAG, DateFormat.getTimeInstance().format(Calendar.getInstance().getTime())
                    + " backup data...");
            db.execSQL("create table " + tbOrderCopy + " as select * from " + TABLE_ORDER);
            db.execSQL("drop table if exists " + TABLE_ORDER);
            db.execSQL("drop table if exists " + TEMP_ORDER);
            Log.i(TAG, DateFormat.getTimeInstance().format(Calendar.getInstance().getTime())
                    + " backup data successfully.");
            if(isOrderWasteExists){
                db.execSQL("create table " + tbOrderWasteCopy + " as select * from " + TABLE_ORDER_WASTE);
                db.execSQL("drop table if exists " + TABLE_ORDER_WASTE);
            }
            onCreate(db);
            copyToNewTable(db, tbOrderCopy, TABLE_ORDER);
            if(isOrderWasteExists){
                copyToNewTable(db, tbOrderWasteCopy, TABLE_ORDER_WASTE);
            }
            db.execSQL("create index if not exists ord_idx on " + TABLE_ORDER + "(" + COLUMN_ORDER_ID + ");");
            db.execSQL("create index if not exists ord_waste_idx on " + TABLE_ORDER_WASTE + "(" + COLUMN_ORDER_ID + ");");
            db.execSQL("reindex ord_idx;");
            db.execSQL("reindex ord_waste_idx;");
            db.setTransactionSuccessful();
        }finally{
            db.endTransaction();
            Log.i(TAG, DateFormat.getTimeInstance().format(Calendar.getInstance().getTime())
                    + " upgrade " + TABLE_ORDER + " successfully.");
        }
	}

    private static void copyToNewTable(SQLiteDatabase db, String sourceTable, String destTable){
        Log.i(TAG, DateFormat.getTimeInstance().format(Calendar.getInstance().getTime())
                + " begin restore " + destTable + "...");
        db.execSQL("insert into " + destTable + " ( "
                + COLUMN_ORDER_ID + ", "
                + OrderTransTable.COLUMN_TRANS_ID + ", "
                + ComputerTable.COLUMN_COMPUTER_ID + ", "
                + ProductTable.COLUMN_PRODUCT_ID + ", "
                + ProductTable.COLUMN_PRODUCT_TYPE_ID + ", "
                + COLUMN_ORDER_QTY + ", "
                + ProductTable.COLUMN_PRODUCT_PRICE + ", "
                + COLUMN_PRICE_OR_PERCENT + ", "
                + ProductTable.COLUMN_VAT_TYPE + ", "
                + COLUMN_TOTAL_VAT + ", "
                + COLUMN_TOTAL_VAT_EXCLUDE + ", "
                + COLUMN_MEMBER_DISCOUNT + ", "
                + COLUMN_PRICE_DISCOUNT + ", "
                + COLUMN_TOTAL_RETAIL_PRICE + ", "
                + COLUMN_TOTAL_SALE_PRICE + ", "
                + ProductComponentTable.COLUMN_PGROUP_ID + ", "
                + ProductComponentGroupTable.COLUMN_REQ_AMOUNT + ", "
                + ProductComponentGroupTable.COLUMN_REQ_MIN_AMOUNT + ", "
                + COLUMN_DEDUCT_AMOUNT + ","
                + ProductTable.COLUMN_SALE_MODE + ", "
                + PromotionPriceGroupTable.COLUMN_PRICE_GROUP_ID + ","
                + PromotionPriceGroupTable.COLUMN_PROMOTION_TYPE_ID + ","
                + PromotionPriceGroupTable.COLUMN_COUPON_HEADER + ","
                + COLUMN_PARENT_ORDER_ID + ", "
                + COLUMN_REMARK
                + " ) select * from " + sourceTable);
        db.execSQL("drop table " + sourceTable);
        Log.i(TAG, DateFormat.getTimeInstance().format(Calendar.getInstance().getTime())
                + " restore " + destTable + " successfully.");
    }
}