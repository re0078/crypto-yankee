package com.mobiledevelopment.cryptoyankee.util;

import com.mobiledevelopment.cryptoyankee.db.entity.Coin;
import com.mobiledevelopment.cryptoyankee.model.coin.CoinDTO;
import com.mobiledevelopment.cryptoyankee.model.coin.ServerCoinDTO;

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
        coin.setSymbol(coinDTO.symbol);
        coin.setPriceUsd(Double.parseDouble(coinDTO.priceUsd));
        coin.setHChangePercentage(Double.parseDouble(coinDTO.percentChange1H));
        coin.setDChangePercentage(Double.parseDouble(coinDTO.percentChange24H));
        coin.setWChangePercentage(Double.parseDouble(coinDTO.percentChange7D));
        return coin;
    }

    public CoinDTO getCoinDTO(Coin coin) {
        CoinDTO coinDTO = new CoinDTO();
        coinDTO.setId(Integer.toString(coin.getId()));
        coinDTO.setName(coin.getName());
        coinDTO.setSymbol(coin.getSymbol());
        coinDTO.setPriceUsd(Double.toString(coin.getPriceUsd()));
        coinDTO.setPercentChange1H(Double.toString(coin.getHChangePercentage()));
        coinDTO.setPercentChange24H(Double.toString(coin.getDChangePercentage()));
        coinDTO.setPercentChange7D(Double.toString(coin.getWChangePercentage()));
        return coinDTO;
    }

    public CoinDTO getCoinDTO(ServerCoinDTO serverCoinDTO) {
        return new CoinDTO(String.valueOf(serverCoinDTO.getId()), serverCoinDTO.getName(),
                serverCoinDTO.getSymbol(),
                String.valueOf(serverCoinDTO.getQuote().getUsd().getPrice()),
                String.valueOf(serverCoinDTO.getQuote().getUsd().getHChange()),
                String.valueOf(serverCoinDTO.getQuote().getUsd().getDChange()),
                String.valueOf(serverCoinDTO.getQuote().getUsd().getWChange()));
    }
}
