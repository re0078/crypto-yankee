package com.mobiledevelopment.cryptoyankee.model;

import java.util.Date;

import lombok.Data;

@Data
public class CandlesChartItems {
    public float timestampOpen;
    public float timestampClose;
    public float priceOpen;
    public float priceClose;
}
