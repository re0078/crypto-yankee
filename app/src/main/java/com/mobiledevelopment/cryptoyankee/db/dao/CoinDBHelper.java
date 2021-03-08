package com.mobiledevelopment.cryptoyankee.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import static com.mobiledevelopment.cryptoyankee.db.entity.CoinEntry.SQL_CREATE_ENTRIES;
import static com.mobiledevelopment.cryptoyankee.db.entity.CoinEntry.SQL_DELETE_ENTRIES;

public class CoinDBHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "yankee_db";
    static final Integer DATABASE_VERSION = 1;

    public CoinDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
    }
}
