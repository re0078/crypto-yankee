package com.mobiledevelopment.cryptoyankee.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Coin {
    private Integer id;
    private String name;
    private Integer priceUsd;
    private Integer hPriceUsd;
    private Integer dPriceUsd;
    private Integer wPriceUsd;
}
