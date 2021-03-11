package com.mobiledevelopment.cryptoyankee;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mobiledevelopment.cryptoyankee.adapter.RezaCoinAdapter;
import com.mobiledevelopment.cryptoyankee.db.dao.CoinRepository;
import com.mobiledevelopment.cryptoyankee.db.entity.Coin;
import com.mobiledevelopment.cryptoyankee.model.CoinDTO;
import com.mobiledevelopment.cryptoyankee.ui.CandleChartActivity;
import com.mobiledevelopment.cryptoyankee.util.CoinModelConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


public class MainActivity extends AppCompatActivity {
    private RezaCoinAdapter coinAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CoinRepository coinRepository;
    private CoinModelConverter coinModelConverter;
    private List<CoinDTO> coinsDTOs = new ArrayList<>();
    private AtomicInteger offset = new AtomicInteger(0);

    private final int TOTAL_PAGE_COINS = 1000;
    private final String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coin_main);
        Log.d("Chq", "Main");
        Objects.requireNonNull(getSupportActionBar()).setTitle("Price Indication");
        setupBeans();
        runProcessWithLoading(this::initializeCoins);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Toast.makeText(MainActivity.this, "Please Wait until loading is complete.", Toast.LENGTH_SHORT).show();
            runProcessWithLoading(this::reloadTenCoins);
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        storeCoins();
    }

    private void setupBeans() {
        swipeRefreshLayout = findViewById(R.id.rootLayout);
        coinModelConverter = CoinModelConverter.getInstance();
        //TODO add some sample coins to register some data in DB
        List<Coin> coins = new ArrayList<>();
        coinRepository = CoinRepository.getInstance(getBaseContext());
        coinRepository.putCoins(coins);
        recyclerView = findViewById(R.id.coinList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        coinAdapter = new RezaCoinAdapter(recyclerView, this);
        recyclerView.setAdapter(coinAdapter);
        coinAdapter.setLoadable(() -> {
            if (coinsDTOs.size() <= TOTAL_PAGE_COINS) {
                runProcessWithLoading(this::loadExtraCoins);
            } else {
                Toast.makeText(MainActivity.this, "Max items is 1000", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void runProcessWithLoading(Runnable runnable) {
        runOnUiThread(() -> {
            swipeRefreshLayout.setRefreshing(true);
            runnable.run();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    public void loadExtraCoins() {
        List<Coin> coins = coinRepository.getTenCoins(offset.get());
        offset.addAndGet(coins.size());

//        List<Coin> coins = new ArrayList<>();
//        coins.add(new Coin(1, "bitcoin", 2000, 46, 788, 1000));

        List<CoinDTO> coinDTOS = new ArrayList<>();
        coins.forEach(coin -> coinDTOS.add(coinModelConverter.getCoinDTO(coin)));

        coinAdapter.addExtraItems(coinDTOS);
    }

    private void reloadTenCoins() {
        List<Coin> coins = coinRepository.getTenCoins(0);
        offset.set(coins.size());

//        List<Coin> coins = new ArrayList<>();
//        coins.add(new Coin(1, "bitcoin2", 2000, 46, 788, 1000));

        adaptLoadedCoins(coins);
    }

    private void initializeCoins() {
        List<Coin> coins = coinRepository.getTenCoins(0);
        offset.set(coins.size());

//        List<Coin> coins = new ArrayList<>();
//        coins.add(new Coin(1, "bitcoin", 2000, 46, 788, 1000));

        adaptLoadedCoins(coins);
    }

    private void adaptLoadedCoins(List<Coin> coins) {
        List<CoinDTO> coinDTOS = new ArrayList<>();
        Log.d("NULL_ERROR", String.format(Locale.ENGLISH, "%b, %b", coins == null, coinModelConverter == null));
        coins.forEach(coin -> coinDTOS.add(coinModelConverter.getCoinDTO(coin)));
        coinAdapter.setCoinItems(coinDTOS);
        coinAdapter.notifyDataSetChanged();
    }

    public void showUTLCChart(String coinName) {
        Intent intent = new Intent(MainActivity.this, CandleChartActivity.class);
        intent.putExtra(CandleChartActivity.COIN_NAME_KEY, coinName);
        startActivity(intent);
        Log.i(LOG_TAG, "UTLC Chart Activity Started");
    }

    private void storeCoins() {
        List<Coin> coins = new ArrayList<>();
        coinsDTOs.forEach(coinDTO -> coins.add(coinModelConverter.getCoinEntity(coinDTO)));
        coinRepository.updateCoins(coins);
    }
}