package com.mobiledevelopment.cryptoyankee.models.candle;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CandlesChartItems {
    public float priceOpen;
    public float priceClose;
    public float priceHigh;
    public float priceLow;
}
