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
import java.util.concurrent.CompletableFuture;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;


public class MainActivity extends AppCompatActivity {
    private CoinAdapter coinAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CoinRepository coinRepository;
    private ModelConverter modelConverter;
    private ApiService apiService;
    private ThreadPoolService threadPoolService;
    private final SortedMap<Long, CoinDTO> coinsMap = new TreeMap<>();
    private Integer loadLimit;
    private Integer maxCoinsCount;
    private final AtomicInteger offset = new AtomicInteger(0);
    private final AtomicInteger storedDataSize = new AtomicInteger(0);

    private final String LOG_TAG = "ma-TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coin_main);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Price Indication");
        setupBeans();
        runProcessWithLoading(() -> initCoins(true));
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Toast.makeText(MainActivity.this, "Please Wait until loading is complete.", Toast.LENGTH_SHORT).show();
            runProcessWithLoading(() -> initCoins(false));
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        storeCoins();
    }

    private void initCoins(boolean fromDB) {
        if (fromDB) {
            boolean hasCachedData = loadCoins();
            if (!hasCachedData)
                initCoins(false);
        } else {
            coinRepository.deleteCoins();
            coinsMap.clear();
            coinAdapter.getCoinsMap().clear();
            storedDataSize.set(0);
            fetchCoins(false);
            fetchCoins(true);
        }
    }

    private void setupBeans() {
        loadLimit = getResources().getInteger(R.integer.fetch_limit);
        maxCoinsCount = getResources().getInteger(R.integer.max_poll);
        apiService = ApiService.getInstance(getResources());
        threadPoolService = ThreadPoolService.getInstance();
        swipeRefreshLayout = findViewById(R.id.rootLayout);
        modelConverter = ModelConverter.getInstance();
        coinRepository = CoinRepository.getInstance(getBaseContext(), 2 * loadLimit);
        initAdapter();
    }

    private void runProcessWithLoading(Runnable runnable) {
        CompletableFuture<Boolean> completableFutureLock = new CompletableFuture<>();
        runOnUiThread(() -> swipeRefreshLayout.setRefreshing(true));
        threadPoolService.execute(() -> {
            try {
                runnable.run();
                completableFutureLock.complete(true);
            } catch (ApiConnectivityException e) {
                completableFutureLock.complete(false);
            }
        });
        runOnUiThread(() -> {
            if (!completableFutureLock.join()) {
                Toast.makeText(MainActivity.this, "API not accessible. Please checkout your network connection.", Toast.LENGTH_SHORT).show();
                Log.e(LOG_TAG, "Error fetching coins from api");
            }
            swipeRefreshLayout.setRefreshing(false);
            coinAdapter.notifyDataSetChanged();
        });
    }

    private void fetchCoins(boolean isFromOffset) throws ApiConnectivityException {
        try {
            Log.d(LOG_TAG, "size of coinsMap: " + coinsMap.size());
            List<CoinDTO> coinDTOS = apiService.getCoinsInfo(
                    (isFromOffset ? 1 : 0) * offset.get() * loadLimit + 1);
            int offsetIndex = offset.get();
            for (int i = 0; i < coinDTOS.size(); i++) {
                CoinDTO coinDTO = coinDTOS.get(i);
                coinDTO.setDbId((long) i + 1 + offsetIndex);
                coinsMap.put(coinDTO.getDbId(), coinDTO);
                coinAdapter.getCoinsMap().put(coinDTO.getDbId(), coinDTO);
            }
            if (isFromOffset)
                offset.addAndGet(loadLimit);
            else
                offset.set(loadLimit);
            Log.d(LOG_TAG, "size of coinsMap: " + coinsMap.size());
        } catch (ApiConnectivityException e) {
            loadCoins();
            throw e;
        }
    }

    private boolean loadCoins() {
        List<Coin> coins = coinRepository.getLimitedCoins(0);
        coins.forEach(coin -> {
            CoinDTO coinDTO = modelConverter.getCoinDTO(coin);
            coinsMap.put(coin.getDbId(), coinDTO);
            coinAdapter.getCoinsMap().put(coin.getDbId(), coinDTO);
        });
        int size = coins.size();
        offset.addAndGet(size);
        storedDataSize.addAndGet(size);
        return size != 0;
    }

    public void showUTLCChart(String coinName, String coinSymbol) {
        Intent intent = new Intent(MainActivity.this, CandleChartActivity.class);
        intent.putExtra(CandleChartActivity.COIN_NAME_KEY, coinName);
        intent.putExtra(CandleChartActivity.COIN_SYMBOL_KEY, coinSymbol);
        startActivity(intent);
    }

    private void storeCoins() {
        List<Coin> coins = new ArrayList<>();
        coinsMap.forEach((id, coinDTO) -> coins.add(modelConverter.getCoinEntity(coinDTO)));
        int dataSize = storedDataSize.get();
        coinRepository.updateCoins(coins.subList(0, dataSize));
        coinRepository.putCoins(coins.subList(dataSize, coins.size()));
    }

    private void initAdapter() {
        recyclerView = findViewById(R.id.coinList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        coinAdapter = new CoinAdapter(recyclerView, this, loadLimit);
        recyclerView.setAdapter(coinAdapter);
        coinAdapter.setLoadable(() -> {
            if (coinsMap.size() <= maxCoinsCount) {
                runProcessWithLoading(() -> {
                    fetchCoins(true);
                    coinAdapter.getIsLoading().set(false);
                });
            } else {
                Toast.makeText(MainActivity.this, "Max items is " + maxCoinsCount,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}