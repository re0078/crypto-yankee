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
import com.mobiledevelopment.cryptoyankee.MainActivity;
import com.mobiledevelopment.cryptoyankee.R;
import com.mobiledevelopment.cryptoyankee.clients.ApiService;
import com.mobiledevelopment.cryptoyankee.models.candle.CandlesChartItems;
import com.mobiledevelopment.cryptoyankee.models.candle.CandlesDTO;
import com.mobiledevelopment.cryptoyankee.models.exception.ApiConnectivityException;
import com.mobiledevelopment.cryptoyankee.services.ThreadPoolService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;


public class CandleChartActivity extends AppCompatActivity {

    public static final String COIN_NAME_KEY = "coin_name";
    public static final String COIN_SYMBOL_KEY = "coin_symbol";
    private static String LOG_TAG = "cca-TAG";

    private boolean weeklyCandlesOn = true;
    private ApiService apiService;
    private ThreadPoolService threadPoolService;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static CandlesDTO candlesDTO;
    private static String currentCoinName;
    private static String currentCoinSymbol;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_main);
        apiService = ApiService.getInstance(getResources());
        currentCoinName = getIntent().getStringExtra(COIN_NAME_KEY);
        currentCoinSymbol = getIntent().getStringExtra(COIN_SYMBOL_KEY);
        threadPoolService = ThreadPoolService.getInstance();
        Objects.requireNonNull(getSupportActionBar()).setTitle(currentCoinSymbol);
        swipeRefreshLayout = findViewById(R.id.rootLayout);
        swipeRefreshLayout.setOnRefreshListener(this::setupChart);
        findViewById(R.id.weeklyCandlesToggle).setOnClickListener(view -> toggleCandles(candlesDTO));
        setupChart();
    }

    private void setupChart() {
        CompletableFuture<Boolean> completableFutureLock = new CompletableFuture<>();
        runOnUiThread(() -> swipeRefreshLayout.setRefreshing(true));
        threadPoolService.execute(() -> {
            try {
                candlesDTO = getCandlesInfo(currentCoinSymbol, currentCoinName);
                completableFutureLock.complete(true);
            } catch (ApiConnectivityException e) {
                completableFutureLock.complete(false);
            }
        });
        runOnUiThread(() -> {
            if (!completableFutureLock.join()) {
                Toast.makeText(this, "API not accessible. Please checkout your network connection.", Toast.LENGTH_SHORT).show();
                Log.e(LOG_TAG, "Error fetching candles.");
            }
            if (Objects.nonNull(candlesDTO)) draw_chart(currentCoinName, candlesDTO);
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private CandlesDTO getCandlesInfo(String symbol, String name) throws ApiConnectivityException {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime startWeek = currentTime.minusDays(7);
        LocalDateTime startMonth = currentTime.minusDays(30);
        CandlesDTO candlesDTO = apiService.getCandleInfo(symbol, startWeek, startMonth);
        candlesDTO.setCoinName(name);
        candlesDTO.setCoinSymbol(symbol);
        return candlesDTO;
    }

    private void toggleCandles(CandlesDTO candlesDTO) {
        weeklyCandlesOn = !weeklyCandlesOn;
        runOnUiThread(() -> {
            if (Objects.nonNull(candlesDTO))
                draw_chart(candlesDTO.getCoinName(), candlesDTO);
        });
    }

    private void draw_chart(String coinName, CandlesDTO candlesDTO) {
        ArrayList<CandlesChartItems> candlesList;
        if (weeklyCandlesOn)
            candlesList = candlesDTO.getWeeklyCandles();
        else
            candlesList = candlesDTO.getMonthlyCandles();

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
            dataSetTag = coinName + " (Weekly)";
        } else {
            dataSetTag = coinName + " (Monthly)";
        }
        CandleDataSet candleDataSet = new CandleDataSet(entries, dataSetTag);
        candleDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        candleDataSet.setShadowColor(Color.rgb(128, 0, 128));
        candleDataSet.setValueTextColor(Color.BLUE);
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
