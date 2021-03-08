package com.mobiledevelopment.cryptoyankee.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CoinModel {
    public String id;
    public String name;
    public String symbol;
    public String priceUsd;
    public String percentChange1H;
    public String percentChange24H;
    public String percentChange7D;
}

