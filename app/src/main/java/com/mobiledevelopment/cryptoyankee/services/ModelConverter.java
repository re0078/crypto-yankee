package com.mobiledevelopment.cryptoyankee.services;

import com.mobiledevelopment.cryptoyankee.db.entity.Coin;
import com.mobiledevelopment.cryptoyankee.models.candle.CandlesChartItems;
import com.mobiledevelopment.cryptoyankee.models.candle.ServerCandleDTO;
import com.mobiledevelopment.cryptoyankee.models.coin.CoinDTO;
import com.mobiledevelopment.cryptoyankee.models.coin.ServerCoinDTO;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ModelConverter {
    private static final ModelConverter MODEL_CONVERTER = new ModelConverter();

    public static ModelConverter getInstance() {
        return MODEL_CONVERTER;
    }

    public Coin getCoinEntity(CoinDTO coinDTO) {
        Coin coin = new Coin();
        coin.setId(Integer.parseInt(coinDTO.getId()));
        coin.setName(coinDTO.getName());
        coin.setSymbol(coinDTO.getSymbol());
        coin.setPriceUsd(Double.parseDouble(coinDTO.getPriceUsd()));
        coin.setHChangePercentage(Double.parseDouble(coinDTO.getPercentChange1H()));
        coin.setDChangePercentage(Double.parseDouble(coinDTO.getPercentChange24H()));
        coin.setWChangePercentage(Double.parseDouble(coinDTO.getPercentChange7D()));
        return coin;
    }

    public CoinDTO getCoinDTO(Coin coin) {
        CoinDTO coinDTO = new CoinDTO();
        coinDTO.setDbId(coin.getDbId());
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

    public CandlesChartItems getChartItem(ServerCandleDTO serverCandleDTO) {
        return new CandlesChartItems(
                (float) serverCandleDTO.getOpen(),
                (float) serverCandleDTO.getClose(),
                (float) serverCandleDTO.getHigh(),
                (float) serverCandleDTO.getLow());
    }
}
