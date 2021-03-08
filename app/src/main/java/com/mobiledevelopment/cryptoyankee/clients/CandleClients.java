package com.mobiledevelopment.cryptoyankee.clients;

import android.util.Log;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.mobiledevelopment.cryptoyankee.utilities.DateUtility;

import org.jetbrains.annotations.NotNull;

public class CandleClients {

    public void getCandles(String symbol, Range range) {

        OkHttpClient okHttpClient = new OkHttpClient();

        String miniUrl;
        final String description;
        switch (range) {

            case weekly:
                miniUrl = "period_id=1DAY".concat("&time_end=".concat(DateUtility.getCurrentDate()).concat("&limit=7"));
                description = "Daily candles from now for one week";
                break;

            case oneMonth:
                miniUrl = "period_id=1DAY".concat("&time_end=".concat(DateUtility.getCurrentDate()).concat("&limit=30"));
                description = "Daily candles from now for a month";
                break;

            default:
                miniUrl = "";
                description = "";

        }

        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse("https://rest.coinapi.io/v1/ohlcv/".concat(symbol).concat("/USD/history?".concat(miniUrl))))
                .newBuilder();

        String url = urlBuilder.build().toString();

        final Request request = new Request.Builder().url(url)
                .addHeader("X-CoinAPI-Key", "D51B46F7-5CC6-4ABA-B679-443708973913")
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, IOException e) {
                Log.v("CandleClient", e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {

                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    //extractCandlesFromResponse(response.body().string(), description);
                }
            }
        });

    }
}
