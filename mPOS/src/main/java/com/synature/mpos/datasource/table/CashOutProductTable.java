package com.synature.mpos.datasource.table;

import android.database.sqlite.SQLiteDatabase;

import com.synature.mpos.datasource.MPOSDatabase;

/**
 * Created by j1tth4 on 2/13/15.
 */
public class CashOutProductTable {

    public static final String TAฺBLE_CASH_OUT_PRODUCT = "CashOutProduct";
    public static final String COLUMN_CASH_OUT_TYPE = "cash_out_type";

    private static final String SQL_CREATE =
            "create table " + TAฺBLE_CASH_OUT_PRODUCT + "("
                    + ProductTable.COLUMN_PRODUCT_ID + " integer,"
                    + ProductDeptTable.COLUMN_PRODUCT_DEPT_ID + " integer,"
                    + ProductTable.COLUMN_PRODUCT_CODE + " text,"
                    + ProductTable.COLUMN_PRODUCT_NAME + " text,"
                    + COLUMN_CASH_OUT_TYPE + " integer default -1,"
                    + BaseColumn.COLUMN_DELETED + " integer default 0,"
                    + BaseColumn.COLUMN_INSERT_DATE + " text,"
                    + BaseColumn.COLUMN_ORDERING + " integer default 0);";

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion,
                                 int newVersion) {
        db.execSQL("drop table if exists " + TAฺBLE_CASH_OUT_PRODUCT);
    }
}
