package com.mobiledevelopment.cryptoyankee.ui;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
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

    private boolean weeklyCandlesOn = true;
    private CandlesDTO candlesDTO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_main);
        Objects.requireNonNull(getSupportActionBar()).setTitle("UHLC");
        String currentCoinName = getIntent().getStringExtra(COIN_NAME_KEY);
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.rootLayout);
        swipeRefreshLayout.post(this::load_candles);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Toast.makeText(CandleChartActivity.this,
                    "Please Wait until loading is complete.", Toast.LENGTH_SHORT).show();
            load_candles();
        });
        draw_chart(candlesDTO.weeklyCandles);
        findViewById(R.id.weeklyCandlesToggle).setOnClickListener(this::toggleCandles);
    }

    private void toggleCandles(View view) {
        weeklyCandlesOn = !weeklyCandlesOn;
        // TODO change to chart

        if (weeklyCandlesOn) {
            draw_chart(candlesDTO.weeklyCandles);
        } else {
            draw_chart(candlesDTO.monthlyCandles);
        }
    }

    private void load_candles() {
        runOnUiThread(() -> {
            // initialize candles (both weekly and monthly) from candles service TODO
            candlesDTO = new CandlesDTO("1", "bitcoin",
                    new ArrayList<>(),
                    new ArrayList<>()
            );

        });
    }

    private void draw_chart(ArrayList<CandlesChartItems> list) {
        ArrayList<CandleEntry> entries = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            float mul = list.size() + 10;
            float val = (float) (Math.random() * 100) + mul;

            float high = (float) (list.get(i).priceClose) + 8f;
            float low = (float) (list.get(i).priceOpen) + 8f;

            float open = (float) (list.get(i).timestampOpen) + 1f;
            float close = (float) (list.get(i).timestampClose) + 1f;

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
    public static final String COIN_NAME_KEY = "coin_name";
}