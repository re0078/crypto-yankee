package com.mobiledevelopment.cryptoyankee.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mobiledevelopment.cryptoyankee.db.entity.Coin;
import com.mobiledevelopment.cryptoyankee.db.entity.CoinEntry;

import static com.mobiledevelopment.cryptoyankee.db.entity.CoinEntry.*;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.mobiledevelopment.cryptoyankee.db.entity.CoinEntry.CURR_NAME;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CoinRepository {
    private static final CoinRepository COIN_REPOSITORY = new CoinRepository();
    private static final int MAX_RECORDS = 10;
    private CoinDBHelper coinDBHelper;
    private Integer offset = 0;

    public static CoinRepository getInstance(Context context) {
        COIN_REPOSITORY.coinDBHelper = new CoinDBHelper(context);
        return COIN_REPOSITORY;
    }

    public List<Coin> getTenCoins() {
        SQLiteDatabase db = coinDBHelper.getReadableDatabase();
        String[] columns = {_ID, CURR_NAME, PRICE_USD, H_PRICE_USD, D_PRICE_USD, W_PRICE_USD};
        Cursor cursor = db.query(TABLE_NAME, columns, null, null,
                null, null, null, MAX_RECORDS + "," + offset);
        offset += MAX_RECORDS;
        List<Coin> coins = new ArrayList<>();
        while (cursor.moveToNext()) {
            coins.add(readCoin(cursor));
        }
        cursor.close();
        return coins;
    }

    private Coin readCoin(Cursor cursor) {
        Coin coin = new Coin();
        coin.setId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)));
        coin.setName(cursor.getString(cursor.getColumnIndexOrThrow(CURR_NAME)));
        coin.setPriceUsd(cursor.getInt(cursor.getColumnIndexOrThrow(PRICE_USD)));
        coin.setHPriceUsd(cursor.getInt(cursor.getColumnIndexOrThrow(H_PRICE_USD)));
        coin.setDPriceUsd(cursor.getInt(cursor.getColumnIndexOrThrow(D_PRICE_USD)));
        coin.setWPriceUsd(cursor.getInt(cursor.getColumnIndexOrThrow(W_PRICE_USD)));
        return coin;
    }

    public void putCoins(List<Coin> coins) {
        SQLiteDatabase db = coinDBHelper.getWritableDatabase();
        coins.forEach(coin -> coin.setId((int) db.insert(TABLE_NAME, null, setCoinValues(coin))));
    }

    public void updateCoins(List<Coin> coins) {
        SQLiteDatabase db = coinDBHelper.getWritableDatabase();
        coins.forEach(coin -> {
            String selection = _ID + " = ";
            String[] selectionArgs = {Integer.toString(coin.getId())};
            db.update(TABLE_NAME, setCoinValues(coin), selection, selectionArgs);
        });
    }

    private ContentValues setCoinValues(Coin coin) {
        ContentValues values = new ContentValues();
        values.put(CURR_NAME, coin.getName());
        values.put(PRICE_USD, coin.getPriceUsd());
        values.put(H_PRICE_USD, coin.getHPriceUsd());
        values.put(D_PRICE_USD, coin.getDPriceUsd());
        values.put(W_PRICE_USD, coin.getWPriceUsd());
        return values;
    }
}
