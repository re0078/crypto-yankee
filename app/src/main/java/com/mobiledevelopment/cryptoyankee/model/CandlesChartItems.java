package com.mobiledevelopment.cryptoyankee.model;

import java.util.Date;

import lombok.Data;

@Data
public class CandlesChartItems {
    public float priceOpen;
    public float priceClose;
    public float priceHigh;
    public float priceLow;
}
