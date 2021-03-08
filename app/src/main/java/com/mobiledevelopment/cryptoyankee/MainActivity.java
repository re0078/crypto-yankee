package com.mobiledevelopment.cryptoyankee;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mobiledevelopment.cryptoyankee.adaptars.CoinAdapter;
import com.mobiledevelopment.cryptoyankee.models.CoinModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    CoinAdapter adapter;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    List<CoinModel> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coin_main);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Price Indication");
        swipeRefreshLayout = findViewById(R.id.rootLayout);
        swipeRefreshLayout.post(() -> loadFirst10Coin(0));
        swipeRefreshLayout.setOnRefreshListener(() -> {
            items.clear();
            Toast.makeText(MainActivity.this, "Please Wait till loading is complete.", Toast.LENGTH_SHORT).show();
            // loading bar logic
            loadFirst10Coin(0);
        });
        recyclerView = findViewById(R.id.coinList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setupAdapter();
    }


    private void loadFirst10Coin(int index) {
        swipeRefreshLayout.setRefreshing(true);
        CoinModel testItem = new CoinModel("1", "bitcoin", "$", "50000", "3", "12", "20");

        // fetch items

        runOnUiThread(() -> {
            List<CoinModel> newItems = List.of(testItem, testItem, testItem, testItem, testItem, testItem, testItem, testItem, testItem, testItem, testItem, testItem, testItem, testItem);
            adapter.updateData(newItems);

        });

        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }

    private void setupAdapter() {
        adapter = new CoinAdapter(recyclerView, MainActivity.this, items);
        recyclerView.setAdapter(adapter);
        adapter.setiLoadMore(() -> {
            if (items.size() <= 1000) //Max Size is 1000 coins
            {
                loadNext10coin(items.size());
            } else {
                Toast.makeText(MainActivity.this, "Max items is 1000", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadNext10coin(int index) {
        List<CoinModel> newItems = List.of(new CoinModel("123", "bitcoin", "$", "50000", "3", "12", "20"));

        // fetch new items

        runOnUiThread(() -> {
            items.addAll(newItems);
            adapter.setLoaded();
            adapter.updateData(items);
            swipeRefreshLayout.setRefreshing(false);
        });
    }
}