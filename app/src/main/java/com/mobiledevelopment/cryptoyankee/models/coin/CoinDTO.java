package com.mobiledevelopment.cryptoyankee.models.coin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@NoArgsConstructor
@Data
public class CoinDTO {
    private Long dbId;
    @NonNull
    private String id;
    @NonNull
    private String name;
    @NonNull
    private String symbol;
    @NonNull
    private String priceUsd;
    @NonNull
    private String percentChange1H;
    @NonNull
    private String percentChange24H;
    @NonNull
    private String percentChange7D;
}

