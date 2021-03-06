package com.mobiledevelopment.cryptoyankee.db.entity;

import android.provider.BaseColumns;

public class CoinEntry implements BaseColumns {
    public static final String TABLE_NAME = "coin_table";
    public static final String COIN_ID = "coin_id";
    public static final String CURR_NAME = "name";
    public static final String CURR_SYMBOL = "symbol";
    public static final String PRICE_USD = "price_usd";
    public static final String H_CHANGE_PERCENT = "h_change_percent";
    public static final String D_CHANGE_PERCENT = "d_change_percent";
    public static final String W_CHANGE_PERCENT = "w_change_percent";
    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME +
            "(" +
            _ID + " INTEGER PRIMARY KEY," +
            COIN_ID + " INTEGER UNIQUE," +
            CURR_NAME + " VARCHAR(16)," +
            CURR_SYMBOL + " VARCHAR(8)," +
            PRICE_USD + " DOUBLE," +
            H_CHANGE_PERCENT + " DOUBLE," +
            D_CHANGE_PERCENT + " DOUBLE," +
            W_CHANGE_PERCENT + " DOUBLE" +
            ")";
    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
