package com.mobiledevelopment.cryptoyankee.models.candle;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandlesDTO {
    private String coinId;
    private String coinName;
    private ArrayList<CandlesChartItems> weeklyCandles;
    private ArrayList<CandlesChartItems> monthlyCandles;
}
