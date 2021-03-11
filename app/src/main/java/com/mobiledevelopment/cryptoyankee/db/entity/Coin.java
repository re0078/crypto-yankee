package com.mobiledevelopment.cryptoyankee.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Coin {
    private int id;
    private String name;
    private float priceUsd;
    private float hPriceUsd;
    private float dPriceUsd;
    private float wPriceUsd;
}
