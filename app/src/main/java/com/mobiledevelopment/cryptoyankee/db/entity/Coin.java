package com.mobiledevelopment.cryptoyankee.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Coin {
    private Long dbId;
    private Integer id;
    private String name;
    private String symbol;
    private Double priceUsd;
    private Double hChangePercentage;
    private Double dChangePercentage;
    private Double wChangePercentage;
}
