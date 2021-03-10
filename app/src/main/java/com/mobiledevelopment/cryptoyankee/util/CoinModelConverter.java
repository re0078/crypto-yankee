package com.mobiledevelopment.cryptoyankee.util;

import android.util.Log;

import com.mobiledevelopment.cryptoyankee.db.entity.Coin;
import com.mobiledevelopment.cryptoyankee.model.CoinDTO;

import java.util.Locale;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CoinModelConverter {
    private static final CoinModelConverter COIN_MODEL_CONVERTER = new CoinModelConverter();

    public static CoinModelConverter getInstance() {
        return COIN_MODEL_CONVERTER;
    }

    public Coin getCoinEntity(CoinDTO coinDTO) {
        Coin coin = new Coin();
        coin.setId(Integer.parseInt(coinDTO.id));
        coin.setName(coinDTO.name);
        coin.setPriceUsd(Integer.parseInt(coinDTO.priceUsd));
        //TODO formula conversion from percentage to price
        coin.setHPriceUsd(Integer.parseInt(coinDTO.percentChange1H));
        coin.setDPriceUsd(Integer.parseInt(coinDTO.percentChange1H));
        coin.setWPriceUsd(Integer.parseInt(coinDTO.percentChange7D));
        return coin;
    }

    public CoinDTO getCoinDTO(Coin coin) {
        CoinDTO coinDTO = new CoinDTO();
        coinDTO.setId(Integer.toString(coin.getId()));
        coinDTO.setName(coin.getName());

        coinDTO.setPriceUsd(String.format(Locale.ENGLISH, "%f", coin.getPriceUsd()));

        coinDTO.setPercentChange1H(String.format(Locale.ENGLISH, "%.2f", calcDiffPercentage(coin.getHPriceUsd(), coin.getPriceUsd())));
        coinDTO.setPercentChange24H(String.format(Locale.ENGLISH, "%.2f", calcDiffPercentage(coin.getDPriceUsd(), coin.getPriceUsd())));
        coinDTO.setPercentChange7D(String.format(Locale.ENGLISH, "%.2f", calcDiffPercentage(coin.getWPriceUsd(), coin.getPriceUsd())));

        return coinDTO;
    }

    private double calcDiffPercentage(float part, float whole) {
        return part * 100.0 / (whole - part);
    }
}
