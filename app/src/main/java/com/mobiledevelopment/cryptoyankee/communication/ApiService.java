package com.mobiledevelopment.cryptoyankee.communication;


import android.content.res.Resources;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobiledevelopment.cryptoyankee.R;
import com.mobiledevelopment.cryptoyankee.model.coin.CoinDTO;
import com.mobiledevelopment.cryptoyankee.model.coin.ServerInfoResponse;
import com.mobiledevelopment.cryptoyankee.model.exception.ApiConnectivityException;
import com.mobiledevelopment.cryptoyankee.util.CoinModelConverter;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

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
    private CoinModelConverter converter;

    public static ApiService getInstance(Resources resources) {
        API_UTIL.resources = resources;
        API_UTIL.client = new OkHttpClient();
        API_UTIL.objectMapper = new ObjectMapper();
        API_UTIL.converter = CoinModelConverter.getInstance();
        return API_UTIL;
    }

    public List<CoinDTO> getCoinsInfo(int start) {
        Request request = buildFetchCoinsInfoRequest(start);
        List<CoinDTO> coinDTOS = new ArrayList<>();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.wtf("Api", "onFailure: ", e);
                throw new ApiConnectivityException();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    ServerInfoResponse serverInfoResponse = objectMapper.reader().readValue(response.body().string());
                    serverInfoResponse.getServerCoinDTOS().forEach(serverCoinDTO -> coinDTOS.add(converter.getCoinDTO(serverCoinDTO)));
                } else
                    Log.e("Api", "onResponse code: " + response.code());
            }
        });
        return coinDTOS;
    }

    private Request buildFetchCoinsInfoRequest(int offset) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(resources.getString(R.string.info_api)).
                newBuilder().
                addQueryParameter(resources.getString(R.string.limit_key), resources.getString(R.string.fetch_limit)).
                addQueryParameter(resources.getString(R.string.convert_key), resources.getString(R.string.usd_val)).
                addQueryParameter(resources.getString(R.string.start_key), String.valueOf(offset));
        return new Request.Builder().
                url(urlBuilder.build().toString()).
                addHeader(resources.getString(R.string.api_key), resources.getString(R.string.api_key_value)).
                build();
    }
}
