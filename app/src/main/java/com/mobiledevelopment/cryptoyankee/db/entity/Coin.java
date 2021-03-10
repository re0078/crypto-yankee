package com.mobiledevelopment.cryptoyankee.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Coin {
    private int id;
    private String name;
    private int priceUsd;
    private int hPriceUsd;
    private int dPriceUsd;
    private int wPriceUsd;
}
