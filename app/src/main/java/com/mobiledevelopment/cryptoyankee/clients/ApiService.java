package com.mobiledevelopment.cryptoyankee.clients;


import android.content.res.Resources;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobiledevelopment.cryptoyankee.R;
import com.mobiledevelopment.cryptoyankee.models.candle.CandlesChartItems;
import com.mobiledevelopment.cryptoyankee.models.candle.CandlesDTO;
import com.mobiledevelopment.cryptoyankee.models.candle.ServerCandleDTO;
import com.mobiledevelopment.cryptoyankee.models.coin.CoinDTO;
import com.mobiledevelopment.cryptoyankee.models.coin.ServerInfoResponse;
import com.mobiledevelopment.cryptoyankee.models.exception.ApiConnectivityException;
import com.mobiledevelopment.cryptoyankee.services.ModelConverter;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiService {
    private static final ApiService API_UTIL = new ApiService();
    private OkHttpClient client;
    private Resources resources;
    private ObjectMapper objectMapper;
    private ModelConverter converter;

    public static ApiService getInstance(Resources resources) {
        API_UTIL.resources = resources;
        API_UTIL.client = new OkHttpClient();
        API_UTIL.objectMapper = new ObjectMapper();
        API_UTIL.objectMapper.setVisibility(API_UTIL.objectMapper.getSerializationConfig().
                getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        API_UTIL.converter = ModelConverter.getInstance();
        return API_UTIL;
    }

    public List<CoinDTO> getCoinsInfo(int start) {
        Request request = buildFetchCoinsInfoRequest(start);
        List<CoinDTO> coinDTOS = new ArrayList<>();
        CompletableFuture<Boolean> lockCompletableFuture = new CompletableFuture<>();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.wtf("Api", "getCoinsInfo->onFailure: ", e);
                lockCompletableFuture.complete(false);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    ServerInfoResponse serverInfoResponse = objectMapper.reader().
                            readValue(Objects.requireNonNull(response.body()).string(),
                                    ServerInfoResponse.class);
                    serverInfoResponse.getServerCoinDTOS().forEach(serverCoinDTO -> coinDTOS.add(converter.getCoinDTO(serverCoinDTO)));
                    lockCompletableFuture.complete(true);
                } else {
                    Log.e("Api", "getCoinsInfo->onResponse code: " + response.code());
                    lockCompletableFuture.complete(false);
                }
            }
        });
        if (!lockCompletableFuture.join())
            throw new ApiConnectivityException();
        return coinDTOS;
    }

    private Request buildFetchCoinsInfoRequest(int offset) {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(resources.getString(R.string.info_api))).
                newBuilder().
                addQueryParameter(resources.getString(R.string.limit_key), String.valueOf(resources.getInteger(R.integer.fetch_limit))).
                addQueryParameter(resources.getString(R.string.convert_key), resources.getString(R.string.usd_val)).
                addQueryParameter(resources.getString(R.string.start_key), String.valueOf(offset));
        return new Request.Builder().
                url(urlBuilder.build().toString()).
                addHeader(resources.getString(R.string.info_api_key), resources.getString(R.string.info_api_key_value)).
                build();
    }

    public CandlesDTO getCandleInfo(String symbol, LocalDateTime startWeek, LocalDateTime startMonth) {
        CandlesDTO candlesDTO = new CandlesDTO();
        Log.d("week_tag", symbol + startWeek);
        Request request = buildFetchCandlesInfoRequest(symbol, startWeek);
        Log.d("req1_tag", request.toString());
        candlesDTO.setWeeklyCandles(callCandlesInfoApi(request));
        Log.d("month_tag", symbol + startMonth);
        Request request2 = buildFetchCandlesInfoRequest(symbol, startMonth);
        Log.d("req2_tag", request2.toString());
        candlesDTO.setMonthlyCandles(callCandlesInfoApi(request2));
        return candlesDTO;
    }

    private ArrayList<CandlesChartItems> callCandlesInfoApi(Request request) {
        CompletableFuture<Void> lockCompletableFuture = new CompletableFuture<>();
        ArrayList<CandlesChartItems> items = new ArrayList<>();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.wtf("Api", "callCandlesInfoApi->onFailure: ", e);
                lockCompletableFuture.complete(null);
                throw new ApiConnectivityException();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    ServerCandleDTO[] responseItems = objectMapper.reader().
                            readValue(Objects.requireNonNull(response.body()).string(),
                                    ServerCandleDTO[].class);
                    for (ServerCandleDTO serverCandleDTO : responseItems) {
                        items.add(converter.getChartItem(serverCandleDTO));
                    }
                } else {
                    Log.e("Api", "callCandlesInfoApi->onResponse code: " + response.code());
                    throw new ApiConnectivityException();
                }
                lockCompletableFuture.complete(null);
            }
        });
        lockCompletableFuture.join();
        Log.d("number_tag", Integer.toString(items.size()));
        return items;
    }

    private Request buildFetchCandlesInfoRequest(String symbol, LocalDateTime startTime) {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(resources.getString(R.string.candle_api).
                concat(symbol).concat(resources.getString(R.string.usd_history_uri)))).
                newBuilder().
                addQueryParameter(resources.getString(R.string.period_key), resources.getString(R.string.period_val)).
                addQueryParameter(resources.getString(R.string.time_start_key), startTime.toString());
        return new Request.Builder()
                .url(urlBuilder.build())
                .get()
                .addHeader(resources.getString(R.string.chart_api_key), resources.getString(R.string.chart_api_key_value))
                .build();
    }
}
