package com.mobiledevelopment.cryptoyankee.model;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class CandlesDTO {
    public String coinName;
    public ArrayList<CandlesChartItems> weeklyCandles;
    public ArrayList<CandlesChartItems> monthlyCandles;
}
