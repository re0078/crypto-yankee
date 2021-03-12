package com.mobiledevelopment.cryptoyankee.models;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CandlesDTO {
    public String coinId;
    public String coinName;
    public ArrayList<CandlesChartItems> weeklyCandles;
    public ArrayList<CandlesChartItems> monthlyCandles;
}
