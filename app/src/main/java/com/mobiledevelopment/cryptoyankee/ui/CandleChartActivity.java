package com.mobiledevelopment.cryptoyankee.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mobiledevelopment.cryptoyankee.R;
import com.mobiledevelopment.cryptoyankee.model.CandlesDTO;

import java.util.ArrayList;
import java.util.Objects;


public class CandleChartActivity extends AppCompatActivity {

    private String currentCoinName;
    private boolean weeklyCandlesOn = true;
    private CandlesDTO candlesDTO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_main);
        Objects.requireNonNull(getSupportActionBar()).setTitle("UHLC");
        currentCoinName = getIntent().getStringExtra(COIN_NAME_KEY);
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.rootLayout);
        swipeRefreshLayout.post(this::load_candles);
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
        TextView textView = findViewById(R.id.candlesTextView);
        if (weeklyCandlesOn) {
            textView.setText("candlesDTO.weeklyCandles.toString()");
        } else {
            textView.setText("candlesDTO.monthlyCandles.toString()");
        }
    }

    private void load_candles() {
        runOnUiThread(() -> {
            // initialize candles (both weekly and monthly) from candles service TODO
            candlesDTO = new CandlesDTO("bitcoin",
                    new ArrayList<>(),
                    new ArrayList<>()
            );
        });
    }

    public static final String COIN_NAME_KEY = "coin_name";
}
