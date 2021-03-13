package com.mobiledevelopment.cryptoyankee.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mobiledevelopment.cryptoyankee.db.entity.Coin;

import static com.mobiledevelopment.cryptoyankee.db.entity.CoinEntry.*;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.mobiledevelopment.cryptoyankee.db.entity.CoinEntry.CURR_NAME;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CoinRepository {
    private static final CoinRepository COIN_REPOSITORY = new CoinRepository();
    private int maxRecords = 5;
    private CoinDBHelper coinDBHelper;

    public static CoinRepository getInstance(Context context, int maxRecords) {
        COIN_REPOSITORY.coinDBHelper = new CoinDBHelper(context);
        COIN_REPOSITORY.maxRecords = maxRecords;
        return COIN_REPOSITORY;
    }

    public List<Coin> getLimitedCoins(int offset) {
        SQLiteDatabase db = coinDBHelper.getReadableDatabase();
        String[] columns = {_ID, COIN_ID, CURR_NAME, CURR_SYMBOL, PRICE_USD, H_CHANGE_PERCENT, D_CHANGE_PERCENT, W_CHANGE_PERCENT};
        Cursor cursor = db.query(TABLE_NAME, columns, null, null,
                null, null, null, null);
        List<Coin> coins = new ArrayList<>();
        while (cursor.moveToNext()) {
            coins.add(readCoin(cursor));
        }
        cursor.close();
        return coins;
    }

    private Coin readCoin(Cursor cursor) {
        Coin coin = new Coin();
        coin.setDbId(cursor.getLong(cursor.getColumnIndexOrThrow(_ID)));
        coin.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COIN_ID)));
        coin.setName(cursor.getString(cursor.getColumnIndexOrThrow(CURR_NAME)));
        coin.setSymbol(cursor.getString(cursor.getColumnIndexOrThrow(CURR_SYMBOL)));
        coin.setPriceUsd(cursor.getDouble(cursor.getColumnIndexOrThrow(PRICE_USD)));
        coin.setHChangePercentage(cursor.getDouble(cursor.getColumnIndexOrThrow(H_CHANGE_PERCENT)));
        coin.setDChangePercentage(cursor.getDouble(cursor.getColumnIndexOrThrow(D_CHANGE_PERCENT)));
        coin.setWChangePercentage(cursor.getDouble(cursor.getColumnIndexOrThrow(W_CHANGE_PERCENT)));
        return coin;
    }

    public void putCoins(List<Coin> coins) {
        SQLiteDatabase db = coinDBHelper.getWritableDatabase();
        coins.forEach(coin -> db.insert(TABLE_NAME, null, setCoinValues(coin, false)));
        Log.d("db-TAG", "size: " + coins.size());
    }

    public void updateCoins(List<Coin> coins) {
        SQLiteDatabase db = coinDBHelper.getWritableDatabase();
        coins.forEach(coin -> {
            String selection = COIN_ID + " = " + coin.getId();
            db.update(TABLE_NAME, setCoinValues(coin, true), selection, null);
        });
    }

    public void deleteCoins() {
        SQLiteDatabase db = coinDBHelper.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
    }

    private ContentValues setCoinValues(Coin coin, boolean isUpdate) {
        ContentValues values = new ContentValues();
        if (!isUpdate)
            values.put(COIN_ID, coin.getId());
        values.put(CURR_NAME, coin.getName());
        values.put(CURR_SYMBOL, coin.getSymbol());
        values.put(PRICE_USD, coin.getPriceUsd());
        values.put(H_CHANGE_PERCENT, coin.getHChangePercentage());
        values.put(D_CHANGE_PERCENT, coin.getDChangePercentage());
        values.put(W_CHANGE_PERCENT, coin.getWChangePercentage());
        return values;
    }
}
