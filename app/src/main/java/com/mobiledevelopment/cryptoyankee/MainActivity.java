package com.mobiledevelopment.cryptoyankee;

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
import com.mobiledevelopment.cryptoyankee.util.CoinModelConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    private RezaCoinAdapter coinAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CoinRepository coinRepository;
    private CoinModelConverter coinModelConverter;
    private List<CoinDTO> coins = new ArrayList<>();

    private final int TOTAL_PAGE_COINS = 1000;

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
        coinModelConverter = CoinModelConverter.getInstance();
        //TODO add some sample coins to register some data in DB
        List<Coin> coins = new ArrayList<>();
        coinRepository = CoinRepository.getInstance(getBaseContext());
        coinRepository.putCoins(coins);
        recyclerView = findViewById(R.id.coinList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        coinAdapter = new RezaCoinAdapter(recyclerView);
        recyclerView.setAdapter(coinAdapter);
        coinAdapter.setLoadable(() -> {
            if (coins.size() <= TOTAL_PAGE_COINS) {
                loadTenCoins();
            } else {
                Toast.makeText(MainActivity.this, "Max items is 1000", Toast.LENGTH_SHORT).show();
            }
        });
    }

/*
    private void loadNext10coin(int index) {
        List<CoinDTO> newItems = Collections.singletonList(new CoinDTO("123", "bitcoin", "$", "50000", "3", "12", "20"));
        runOnUiThread(() -> {
            coins.addAll(newItems);
//            adapter.setLoaded();
            coinAdapter.setCoinItems(coins);
            swipeRefreshLayout.setRefreshing(false);
        });
    }
*/

/*
private void loadFirst10Coin(int index) {
        swipeRefreshLayout.setRefreshing(true);
        CoinDTO testItem = new CoinDTO("1", "bitcoin", "$", "50000", "3", "12", "20");
        runOnUiThread(() -> {
            List<CoinDTO> newItems = Arrays.asList(testItem, testItem, testItem, testItem, testItem, testItem, testItem, testItem, testItem, testItem, testItem, testItem, testItem, testItem);
            coinAdapter.setCoinItems(newItems);
            coinAdapter.notifyDataSetChanged();
        });

        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }
*/

    private void reloadTenCoins() {
        swipeRefreshLayout.setRefreshing(true);
        runOnUiThread(() -> {
            List<Coin> coins = coinRepository.reloadTenCoins();
            adaptLoadedCoins(coins);
        });
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }

    private void loadTenCoins() {
        runOnUiThread(() -> {
            List<Coin> coins = coinRepository.getTenCoins();
            adaptLoadedCoins(coins);
        });
    }

    private void adaptLoadedCoins(List<Coin> coins) {
        List<CoinDTO> coinDTOS = new ArrayList<>();
        coins.forEach(coin -> coinDTOS.add(coinModelConverter.getCoinDTO(coin)));
        coinAdapter.setCoinItems(coinDTOS);
        coinAdapter.notifyDataSetChanged();
    }

    private void storeCoins() {
        List<CoinDTO> coinDTOS = new ArrayList<>();
        List<Coin> coins = new ArrayList<>();
        coinDTOS.forEach(coinDTO -> coins.add(coinModelConverter.getCoinEntity(coinDTO)));
        coinRepository.updateCoins(coins);
    }
}