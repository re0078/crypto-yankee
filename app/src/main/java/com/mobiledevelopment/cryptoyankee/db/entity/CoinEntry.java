package com.mobiledevelopment.cryptoyankee.db.entity;

import android.provider.BaseColumns;

public class CoinEntry implements BaseColumns {
    public static final String TABLE_NAME = "coin_table";
    public static final String CURR_NAME = "name";
    public static final String PRICE_USD = "price_usd";
    public static final String H_PRICE_USD = "h_price_usd";
    public static final String D_PRICE_USD = "d_price_usd";
    public static final String W_PRICE_USD = "w_price_usd";
    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME +
            "(" +
            _ID + " INTEGER PRIMARY KEY," +
            CURR_NAME + " VARCHAR(16)," +
            PRICE_USD + " INTEGER," +
            H_PRICE_USD + " INTEGER," +
            D_PRICE_USD + " INTEGER," +
            W_PRICE_USD + " INTEGER" +
            ")";
    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
