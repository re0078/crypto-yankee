package com.mobiledevelopment.cryptoyankee;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mobiledevelopment.cryptoyankee.adapter.CoinAdapter;
import com.mobiledevelopment.cryptoyankee.communication.ApiService;
import com.mobiledevelopment.cryptoyankee.db.dao.CoinRepository;
import com.mobiledevelopment.cryptoyankee.db.entity.Coin;
import com.mobiledevelopment.cryptoyankee.model.coin.CoinDTO;
import com.mobiledevelopment.cryptoyankee.model.exception.ApiConnectivityException;
import com.mobiledevelopment.cryptoyankee.util.CoinModelConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    private CoinAdapter coinAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CoinRepository coinRepository;
    private CoinModelConverter coinModelConverter;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coin_main);
        Log.d("Chq", "Main");
        Objects.requireNonNull(getSupportActionBar()).setTitle("Price Indication");
        swipeRefreshLayout = findViewById(R.id.rootLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Toast.makeText(MainActivity.this, "Please Wait till loading is complete.", Toast.LENGTH_SHORT).show();
//            fetchTenCoins();
        });
        setupBeans();
    }

    private void setupBeans() {
        apiService = ApiService.getInstance(getResources());
        coinModelConverter = CoinModelConverter.getInstance();
        coinRepository = CoinRepository.getInstance(getBaseContext());
        coinRepository.deleteCoins();
        recyclerView = findViewById(R.id.coinList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        coinAdapter = CoinAdapter.getInstance();
//        coinAdapter.setCoins(Collections.emptyList());
        recyclerView.setAdapter(coinAdapter);
//        loadTenCoins();
        fetchFiveCoins();
    }

    private void loadTenCoins() {
        runOnUiThread(() -> {
            List<Coin> coins = coinRepository.getTenCoins(0);
            List<CoinDTO> coinDTOS = new ArrayList<>();
            coins.forEach(coin -> coinDTOS.add(coinModelConverter.getCoinDTO(coin)));
            coinAdapter.setCoins(coinDTOS);
            coinAdapter.notifyDataSetChanged();
        });
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