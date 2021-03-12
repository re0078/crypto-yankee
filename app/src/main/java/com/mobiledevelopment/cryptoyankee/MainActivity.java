package com.mobiledevelopment.cryptoyankee;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mobiledevelopment.cryptoyankee.adapter.CoinAdapter;
import com.mobiledevelopment.cryptoyankee.clients.ApiService;
import com.mobiledevelopment.cryptoyankee.db.dao.CoinRepository;
import com.mobiledevelopment.cryptoyankee.db.entity.Coin;
import com.mobiledevelopment.cryptoyankee.services.ThreadPoolService;
import com.mobiledevelopment.cryptoyankee.ui.CandleChartActivity;
import com.mobiledevelopment.cryptoyankee.models.coin.CoinDTO;
import com.mobiledevelopment.cryptoyankee.models.exception.ApiConnectivityException;
import com.mobiledevelopment.cryptoyankee.services.ModelConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class MainActivity extends AppCompatActivity {
    private CoinAdapter coinAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CoinRepository coinRepository;
    private ModelConverter modelConverter;
    private ApiService apiService;
    private ThreadPoolService threadPoolService;
    private final Map<Integer, CoinDTO> coinsMap = new HashMap<>();
    private Integer loadLimit;
    private Integer maxCoinsCount;
    private Integer nonCompletePage;
    private final AtomicInteger offset = new AtomicInteger(0);
    private final AtomicBoolean hasCachedData = new AtomicBoolean(false);

    private final String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coin_main);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Price Indication");
        setupBeans();
        runProcessWithLoading(this::loadCoins);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Toast.makeText(MainActivity.this, "Please Wait until loading is complete.", Toast.LENGTH_SHORT).show();
            runProcessWithLoading(this::fetchCoins);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        storeCoins(hasCachedData.get());
    }

    private void setupBeans() {
        loadLimit = getResources().getInteger(R.integer.fetch_limit);
        maxCoinsCount = getResources().getInteger(R.integer.max_poll);
        nonCompletePage = (maxCoinsCount % loadLimit) == 0 ? 0 : 1;
        apiService = ApiService.getInstance(getResources());
        threadPoolService = ThreadPoolService.getInstance();
        swipeRefreshLayout = findViewById(R.id.rootLayout);
        modelConverter = ModelConverter.getInstance();
        coinRepository = CoinRepository.getInstance(getBaseContext(), loadLimit);
        coinRepository.deleteCoins();
        initAdapter();
    }

    private void runProcessWithLoading(Runnable runnable) {
        threadPoolService.execute(() -> {
            swipeRefreshLayout.setRefreshing(true);
            runnable.run();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    public void loadExtraCoins() {
        int from = offset.get();
        List<Coin> coins = coinRepository.getLimitedCoins(from);
        int to = offset.addAndGet(coins.size());
        coins.forEach(coin -> {
            coinsMap.put(coin.getId(), modelConverter.getCoinDTO(coin));
            coinAdapter.getCoinsMap().put(coin.getId(), modelConverter.getCoinDTO(coin));
        });
        adaptLoadedCoins();
    }

    private void fetchCoins() {
        runOnUiThread(() -> {
            try {
                for (int i = 0; i < 1; i++) {
                    List<CoinDTO> coinDTOS = apiService.getCoinsInfo(i * loadLimit + 1);
                    coinDTOS.forEach(coinDTO -> {
                        coinsMap.put(Integer.parseInt(coinDTO.getId()), coinDTO);
                        coinAdapter.getCoinsMap().put(Integer.parseInt(coinDTO.getId()), coinDTO);
                    });
                    for (Integer id : coinsMap.keySet()) {
                        Log.i(LOG_TAG, String.valueOf(id));
                    }
                    adaptLoadedCoins();
                }
                Log.d(LOG_TAG, "size of coinsMap: " + coinsMap.size());
            } catch (ApiConnectivityException e) {
//            loadCoins();
            }
        });
    }

    private void loadCoins() {
        List<Coin> coins = coinRepository.getLimitedCoins(0);
        coins.forEach(coin -> {
            coinsMap.put(coin.getId(), modelConverter.getCoinDTO(coin));
            coinAdapter.getCoinsMap().put(coin.getId(), modelConverter.getCoinDTO(coin));
        });
        int size = coins.size();
        adaptLoadedCoins();
        offset.addAndGet(size);
        hasCachedData.set(size != 0);
    }

    private void adaptLoadedCoins() {
        coinAdapter.notifyDataSetChanged();
    }

    public void showUTLCChart(String coinName, String coinSymbol) {
        Intent intent = new Intent(MainActivity.this, CandleChartActivity.class);
        intent.putExtra(CandleChartActivity.COIN_NAME_KEY, coinName);
        intent.putExtra(CandleChartActivity.COIN_SYMBOL_KEY, coinSymbol);
        startActivity(intent);
        Log.i(LOG_TAG, "UTLC Chart Activity Started");
    }

    private void storeCoins(boolean isUpdate) {
        List<Coin> coins = new ArrayList<>();
        coinsMap.forEach((id, coinDTO) -> coins.add(modelConverter.getCoinEntity(coinDTO)));
        if (isUpdate)
            coinRepository.updateCoins(coins);
        else
            coinRepository.putCoins(coins);
    }

    private void initAdapter() {
        recyclerView = findViewById(R.id.coinList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        coinAdapter = new CoinAdapter(recyclerView, this, loadLimit);
        recyclerView.setAdapter(coinAdapter);
        coinAdapter.setLoadable(() -> {
            if (coinsMap.size() <= maxCoinsCount) {
                //runProcessWithLoading(this::fetchCoins);
            } else {
                Toast.makeText(MainActivity.this, "Max items is 1000", Toast.LENGTH_SHORT).show();
            }
        });
    }
}