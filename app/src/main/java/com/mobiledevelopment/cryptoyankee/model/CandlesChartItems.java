package com.mobiledevelopment.cryptoyankee.model;

import java.util.Date;

import lombok.Data;

@Data
public class CandlesChartItems {
    public Date timestamp;
    public float priceOpen;
    public float priceLow;
    public float priceHigh;
    public float priceClose;
}
