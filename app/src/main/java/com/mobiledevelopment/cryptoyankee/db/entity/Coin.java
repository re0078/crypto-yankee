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
    private Double priceUsd;
    private Double hChangePercentage;
    private Double dChangePercentage;
    private Double wChangePercentage;
}
