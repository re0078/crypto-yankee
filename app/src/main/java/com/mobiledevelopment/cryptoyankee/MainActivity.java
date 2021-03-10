package com.mobiledevelopment.cryptoyankee;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mobiledevelopment.cryptoyankee.adapter.CoinAdapter;
import com.mobiledevelopment.cryptoyankee.db.dao.CoinRepository;
import com.mobiledevelopment.cryptoyankee.db.entity.Coin;
import com.mobiledevelopment.cryptoyankee.model.CoinDTO;
import com.mobiledevelopment.cryptoyankee.util.CoinModelConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    private CoinAdapter coinAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CoinRepository coinRepository;
    private CoinModelConverter coinModelConverter;
//    private List<CoinDTO> coins = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coin_main);
        Log.d("Chq", "Main");
        Objects.requireNonNull(getSupportActionBar()).setTitle("Price Indication");
        swipeRefreshLayout = findViewById(R.id.rootLayout);
        swipeRefreshLayout.post(this::loadTenCoins);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Toast.makeText(MainActivity.this, "Please Wait till loading is complete.", Toast.LENGTH_SHORT).show();
            // loading bar logic
//            loadTenCoins();
        });
        setupBeans();
    }


/*
    private void loadFirst10Coin(int index) {
        swipeRefreshLayout.setRefreshing(true);
        CoinDTO testItem = new CoinDTO("1", "bitcoin", "$", "50000", "3", "12", "20");

        // fetch items

        runOnUiThread(() -> {
            List<CoinDTO> newItems = Arrays.asList(testItem, testItem, testItem, testItem, testItem, testItem, testItem, testItem, testItem, testItem, testItem, testItem, testItem, testItem);
            coinAdapter.setCoins(newItems);
            coinAdapter.notifyDataSetChanged();
        });

        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }
*/

    private void setupBeans() {
        coinModelConverter = CoinModelConverter.getInstance();
        //TODO add some sample coins to register some data in DB
        List<Coin> coins = Arrays.asList(
                new Coin(1, "bitcoin", 1000, 1002, 1005, 1009),
                new Coin(1, "bitcoin", 1000, 1002, 1005, 1009));
        coinRepository = CoinRepository.getInstance(getBaseContext());
        coinRepository.deleteCoins();
        coinRepository.putCoins(coins);
        recyclerView = findViewById(R.id.coinList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        coinAdapter = CoinAdapter.getInstance();
        recyclerView.setAdapter(coinAdapter);
        loadTenCoins();
/*
        adapter.setiLoadMore(() -> {
            if (coins.size() <= 1000) //Max Size is 1000 coins
            {
                loadNext10coin(coins.size());
            } else {
                Toast.makeText(MainActivity.this, "Max items is 1000", Toast.LENGTH_SHORT).show();
            }
        });
*/
    }

/*
    private void loadNext10coin(int index) {
        List<CoinDTO> newItems = Collections.singletonList(new CoinDTO("123", "bitcoin", "$", "50000", "3", "12", "20"));

        // fetch new items

        runOnUiThread(() -> {
            coins.addAll(newItems);
//            adapter.setLoaded();
            coinAdapter.setCoins(coins);
            swipeRefreshLayout.setRefreshing(false);
        });
    }
*/

    private void loadTenCoins() {
        runOnUiThread(() -> {
            List<Coin> coins = coinRepository.getTenCoins(0);
            Log.d("coin", String.valueOf(coins.size()));
            List<CoinDTO> coinDTOS = new ArrayList<>();
            coins.forEach(coin -> coinDTOS.add(coinModelConverter.getCoinDTO(coin)));
            Log.d("coin", String.valueOf(coinDTOS.size()));
            coinAdapter.setCoins(coinDTOS);
            coinAdapter.notifyDataSetChanged();
        });
    }

    private void storeCoins() {
        List<CoinDTO> coinDTOS = new ArrayList<>();
        List<Coin> coins = new ArrayList<>();
        coinDTOS.forEach(coinDTO -> coins.add(coinModelConverter.getCoinEntity(coinDTO)));
        coinRepository.updateCoins(coins);
    }
}