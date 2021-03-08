package com.mobiledevelopment.cryptoyankee.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CoinDTO {
    public String id;
    public String name;
    public String symbol;
    public String priceUsd;
    public String percentChange1H;
    public String percentChange24H;
    public String percentChange7D;
}

