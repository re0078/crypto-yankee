package com.mobiledevelopment.cryptoyankee.util;

import com.mobiledevelopment.cryptoyankee.db.entity.Coin;
import com.mobiledevelopment.cryptoyankee.model.CoinDTO;

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
        coinDTO.setPriceUsd(Integer.toBinaryString(coin.getPriceUsd()));
        //TODO formula conversion from percentage to price
        coinDTO.setPercentChange1H(Integer.toBinaryString(coin.getHPriceUsd()));
        coinDTO.setPercentChange24H(Integer.toBinaryString(coin.getDPriceUsd()));
        coinDTO.setPercentChange7D(Integer.toBinaryString(coin.getWPriceUsd()));
        return coinDTO;
    }
}
