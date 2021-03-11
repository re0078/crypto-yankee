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
import com.mobiledevelopment.cryptoyankee.adapter.CoinAdapter;
import com.mobiledevelopment.cryptoyankee.communication.ApiService;
import com.mobiledevelopment.cryptoyankee.db.dao.CoinRepository;
import com.mobiledevelopment.cryptoyankee.db.entity.Coin;
import com.mobiledevelopment.cryptoyankee.model.CoinDTO;
import com.mobiledevelopment.cryptoyankee.ui.CandleChartActivity;
import com.mobiledevelopment.cryptoyankee.model.coin.CoinDTO;
import com.mobiledevelopment.cryptoyankee.model.exception.ApiConnectivityException;
import com.mobiledevelopment.cryptoyankee.util.CoinModelConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    private RezaCoinAdapter coinAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CoinRepository coinRepository;
    private CoinModelConverter coinModelConverter;
    private ApiService apiService;
    private List<CoinDTO> coins = new ArrayList<>();

    private final int TOTAL_PAGE_COINS = 1000;
    private final String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coin_main);
        Log.d("Chq", "Main");
        Objects.requireNonNull(getSupportActionBar()).setTitle("Price Indication");
        swipeRefreshLayout = findViewById(R.id.rootLayout);
        swipeRefreshLayout.post(this::loadTenCoins);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Toast.makeText(MainActivity.this, "Please Wait until loading is complete.", Toast.LENGTH_SHORT).show();
            //TODO: loading bar implementation
            reloadTenCoins();
        });
        setupBeans();
    }

    private void setupBeans() {
        apiService = ApiService.getInstance(getResources());
        coinModelConverter = CoinModelConverter.getInstance();
        //TODO add some sample coins to register some data in DB
        List<Coin> coins = new ArrayList<>();
        coinRepository = CoinRepository.getInstance(getBaseContext());
        coinRepository.deleteCoins();
        coinRepository.putCoins(coins);
        recyclerView = findViewById(R.id.coinList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        coinAdapter = CoinAdapter.getInstance();
//        coinAdapter.setCoins(Collections.emptyList());
        coinAdapter = new RezaCoinAdapter(recyclerView, this);
        recyclerView.setAdapter(coinAdapter);
//        loadTenCoins();
        fetchFiveCoins();
        coinAdapter.setLoadable(() -> {
            if (coins.size() <= TOTAL_PAGE_COINS) {
                loadExtraCoins();
            } else {
                Toast.makeText(MainActivity.this, "Max items is 1000", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showUTLCChart(String coinName) {
        Intent intent = new Intent(MainActivity.this, CandleChartActivity.class);
        intent.putExtra(CandleChartActivity.COIN_NAME_KEY, coinName);
        startActivity(intent);
        Log.i(LOG_TAG, "UTLC Chart Activity Started");
    }

    public void loadExtraCoins() {
        runOnUiThread(() -> {
//            List<Coin> coins = coinRepository.getTenCoins(); TODO
            List<Coin> coins = new ArrayList<>();
            coins.add(new Coin(1, "bitcoin", 2000, 46, 788, 1000));
            List<CoinDTO> coinDTOS = new ArrayList<>();
            coins.forEach(coin -> coinDTOS.add(coinModelConverter.getCoinDTO(coin)));
            coinAdapter.addExtraItems(coinDTOS);
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void reloadTenCoins() {
        swipeRefreshLayout.setRefreshing(true);
        runOnUiThread(() -> {
//            List<Coin> coins = coinRepository.getFirstTenCoins(); TODO
            List<Coin> coins = new ArrayList<>();
            coins.add(new Coin(1, "bitcoin2", 2000, 46, 788, 1000));
            adaptLoadedCoins(coins);
        });
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }

    private void loadTenCoins() {
        runOnUiThread(() -> {
            List<Coin> coins = coinRepository.getTenCoins(0);
            List<CoinDTO> coinDTOS = new ArrayList<>();
            coins.forEach(coin -> coinDTOS.add(coinModelConverter.getCoinDTO(coin)));
            coinAdapter.setCoins(coinDTOS);
            coinAdapter.notifyDataSetChanged();
        });
//            List<Coin> coins = coinRepository.getTenCoins(); TODO
            List<Coin> coins = new ArrayList<>();
            coins.add(new Coin(1, "bitcoin", 2000, 46, 788, 1000));
            adaptLoadedCoins(coins);
        });
    }

    private void adaptLoadedCoins(List<Coin> coins) {
        List<CoinDTO> coinDTOS = new ArrayList<>();
        coins.forEach(coin -> coinDTOS.add(coinModelConverter.getCoinDTO(coin)));
        coinAdapter.setCoinItems(coinDTOS);
        coinAdapter.notifyDataSetChanged();
    }

    private void storeCoins(List<CoinDTO> coinDTOS) {
        List<Coin> coins = new ArrayList<>();
        coinDTOS.forEach(coinDTO -> coins.add(coinModelConverter.getCoinEntity(coinDTO)));
        coinRepository.putCoins(coins);
//        coinRepository.updateCoins(coins);
    }

    private void fetchFiveCoins() {
        runOnUiThread(() -> {
            try {
                List<CoinDTO> coinDTOS = apiService.getCoinsInfo(1);
                coinAdapter.setCoins(coinDTOS);
                coinAdapter.notifyDataSetChanged();
                storeCoins(coinDTOS);
            } catch (ApiConnectivityException e) {
                loadTenCoins();
            }
        });
    }
}