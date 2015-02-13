package com.synature.mpos.datasource.table;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by j1tth4 on 2/13/15.
 */
public class CashOutOrderTransactionTable {

    private static final String SQL_CREATE = "";

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion,
                                 int newVersion) {
        db.execSQL("drop table if exists " );
    }
}
