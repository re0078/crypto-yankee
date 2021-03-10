package com.mobiledevelopment.cryptoyankee.model;

import java.util.Date;

import lombok.Data;

@Data
public class CandlesChartItems {
    public Date timestampOpen;
    public Date timestampClose;
    public float priceOpen;
    public float priceClose;
}
