package com.mobiledevelopment.cryptoyankee.models;

import android.content.res.Resources;

import com.mobiledevelopment.cryptoyankee.R;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandlesDTO {
    public String coinId;
    public String coinName;
    public ArrayList<CandlesChartItems> weeklyCandles;
    public ArrayList<CandlesChartItems> monthlyCandles;
}
