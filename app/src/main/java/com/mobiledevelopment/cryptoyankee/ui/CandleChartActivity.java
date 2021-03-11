package com.mobiledevelopment.cryptoyankee.ui;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
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
import com.mobiledevelopment.cryptoyankee.model.CandlesChartItems;
import com.mobiledevelopment.cryptoyankee.model.CandlesDTO;

import java.util.ArrayList;
import java.util.Objects;


public class CandleChartActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean weeklyCandlesOn = true;
    private CandlesDTO candlesDTO;
    private String currentCoinName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_main);
        Objects.requireNonNull(getSupportActionBar()).setTitle("UHLC");
        currentCoinName = getIntent().getStringExtra(COIN_NAME_KEY);
        swipeRefreshLayout = findViewById(R.id.rootLayout);
        load_candles();
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Toast.makeText(CandleChartActivity.this,
                    "Please Wait until loading is complete.", Toast.LENGTH_SHORT).show();
            load_candles();
        });
        findViewById(R.id.weeklyCandlesToggle).setOnClickListener(this::toggleCandles);
    }

    private void toggleCandles(View view) {
        weeklyCandlesOn = !weeklyCandlesOn;
        // TODO change to chart

        if (weeklyCandlesOn) {
//            draw_chart(7);
            draw_chart(candlesDTO.weeklyCandles);
        } else {
//            draw_chart(30);
            draw_chart(candlesDTO.monthlyCandles);
        }
    }

    private void load_candles() {
        runOnUiThread(() -> {
            swipeRefreshLayout.setRefreshing(true);

            // set candlesDTO from candles service TODO

            if (weeklyCandlesOn) draw_chart(candlesDTO.weeklyCandles);
            else draw_chart(candlesDTO.monthlyCandles);

            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void draw_chart(ArrayList<CandlesChartItems> candles) {
        ArrayList<CandleEntry> entries = new ArrayList<>();
        for (int i = 0; i < candles.size(); i++) {
            float mul = candles.size() + 10;
            float val = (float) (Math.random() * 100) + mul; // todo what is this ?

            float high = (float) (candles.get(i).priceClose) + 8f;
            float low = (float) (candles.get(i).priceOpen) + 8f;

            float open = (float) (candles.get(i).timestampOpen) + 1f;
            float close = (float) (candles.get(i).timestampClose) + 1f;

            boolean odd = i % 2 != 0;
            entries.add(new CandleEntry(i + 1, val + high,
                    val - low,
                    !odd ? val + open : val - open,
                    odd ? val - close : val + close
            ));
        }
        CandleDataSet candleDataSet = new CandleDataSet(entries, candlesDTO.coinName);
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

    private void draw_chart(int num) {
        ArrayList<CandleEntry> entries = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            float mul = num + 10;
            float val = (float) (Math.random() * 100) + mul;

            float high = (float) (Math.random() * 15) + 8f;
            float low = (float) (Math.random() * 15) + 8f;

            float open = (float) (Math.random() * 6) + 1f;
            float close = (float) (Math.random() * 6) + 1f;
            boolean odd = i % 2 != 0;
            entries.add(new CandleEntry(i + 1, val + high,
                    val - low,
                    !odd ? val + open : val - open,
                    odd ? val - close : val + close));
        }
        CandleDataSet candleDataSet = new CandleDataSet(entries, currentCoinName);

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

    public static final String COIN_NAME_KEY = "coin_name";
}
