package com.mobiledevelopment.cryptoyankee.ui;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.mobiledevelopment.cryptoyankee.R;
import com.mobiledevelopment.cryptoyankee.clients.ApiService;
import com.mobiledevelopment.cryptoyankee.models.candle.CandlesChartItems;
import com.mobiledevelopment.cryptoyankee.models.candle.CandlesDTO;
import com.mobiledevelopment.cryptoyankee.services.ThreadPoolService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;


public class CandleChartActivity extends AppCompatActivity {

    public static final String COIN_NAME_KEY = "coin_name";
    public static final String COIN_SYMBOL_KEY = "coin_symbol";

    private boolean weeklyCandlesOn = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_main);
        ApiService apiService = ApiService.getInstance(getResources());
        String currentCoinName = getIntent().getStringExtra(COIN_NAME_KEY);
        String currentCoinSymbol = getIntent().getStringExtra(COIN_SYMBOL_KEY);
        Objects.requireNonNull(getSupportActionBar()).setTitle(currentCoinSymbol);
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.rootLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Toast.makeText(CandleChartActivity.this,
                    "Please Wait until Loading is Complete.", Toast.LENGTH_SHORT).show();
        });

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime startWeek = currentTime.minusDays(7);
        LocalDateTime startMonth = currentTime.minusDays(30);

        CandlesDTO candlesDTO = apiService.getCandleInfo(currentCoinSymbol, startWeek, startMonth);

        candlesDTO.setCoinName(currentCoinName);
        candlesDTO.setCoinSymbol(currentCoinSymbol);

        draw_chart(currentCoinName, candlesDTO.getWeeklyCandles());
        findViewById(R.id.weeklyCandlesToggle).setOnClickListener(view -> toggleCandles(candlesDTO));
    }

    private void toggleCandles(CandlesDTO candlesDTO) {
        weeklyCandlesOn = !weeklyCandlesOn;
        if (weeklyCandlesOn) {
            draw_chart(candlesDTO.getCoinName(), candlesDTO.getWeeklyCandles());
        } else {
            draw_chart(candlesDTO.getCoinName(), candlesDTO.getMonthlyCandles());
        }
    }

    private void draw_chart(String coinName, ArrayList<CandlesChartItems> candlesList) {
        ArrayList<CandleEntry> entries = new ArrayList<>();
        for (int i = 0; i < candlesList.size(); i++) {

            float high = candlesList.get(i).priceHigh;
            float low = candlesList.get(i).priceLow;

            float open = candlesList.get(i).priceOpen;
            float close = candlesList.get(i).priceClose;

            entries.add(new CandleEntry(i + 1,
                    high,
                    low,
                    open,
                    close
            ));
        }
        String dataSetTag;
        if (weeklyCandlesOn) {
            dataSetTag = coinName + "-weekly";
        } else {
            dataSetTag = coinName + "-monthly";
        }
        CandleDataSet candleDataSet = new CandleDataSet(entries, dataSetTag);
        candleDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        candleDataSet.setShadowColor(Color.GRAY);
        candleDataSet.setShadowWidth(0.5f);
        candleDataSet.setDecreasingColor(Color.RED);
        candleDataSet.setDecreasingPaintStyle(Paint.Style.FILL);
        candleDataSet.setIncreasingPaintStyle(Paint.Style.FILL);
        candleDataSet.setIncreasingColor(Color.GREEN);
        candleDataSet.setNeutralColor(Color.BLUE);
        CandleStickChart candleStickChart = findViewById(R.id.candle_stick_chart);
        CandleData candleData = new CandleData(candleDataSet);
        candleStickChart.setData(candleData);
        candleStickChart.invalidate();
    }
}
