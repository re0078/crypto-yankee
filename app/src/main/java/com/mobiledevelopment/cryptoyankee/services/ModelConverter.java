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

    public CandlesChartItems getChartItem(ServerCandleDTO serverCandleDTO) {
        return new CandlesChartItems(
                (float) serverCandleDTO.getOpen(),
                (float) serverCandleDTO.getOpen(),
                (float) serverCandleDTO.getOpen(),
                (float) serverCandleDTO.getOpen());
    }
}
