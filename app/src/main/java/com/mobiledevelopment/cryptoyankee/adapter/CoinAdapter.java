package com.mobiledevelopment.cryptoyankee.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobiledevelopment.cryptoyankee.MainActivity;
import com.mobiledevelopment.cryptoyankee.R;
import com.mobiledevelopment.cryptoyankee.models.coin.CoinDTO;
import com.mobiledevelopment.cryptoyankee.ui.CoinViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;
import lombok.Setter;

public class CoinAdapter extends RecyclerView.Adapter<CoinViewHolder> {

    private final Activity activity;
    @Getter
    private final Map<Integer, CoinDTO> coinsMap;
    @Setter
    private Loadable loadable;
    @Getter
    private AtomicBoolean isLoading = new AtomicBoolean(false);

    public CoinAdapter(RecyclerView recyclerView, Activity activity, int loadLimit) {
        this.coinsMap = new HashMap<>();
        this.activity = activity;
        AtomicInteger visibleThreshold = new AtomicInteger(5);
        if (loadLimit != 0)
            visibleThreshold.set(loadLimit);

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    assert linearLayoutManager != null;
                    int totalItemCount = linearLayoutManager.getItemCount();
                    int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    Log.d("ca-TAG", "isLoading: " + isLoading.get() + " tIC: " +
                            totalItemCount + " lVI: " + lastVisibleItem + " vT: " + visibleThreshold.get());
                    if (totalItemCount <= (lastVisibleItem + visibleThreshold.get())) {
                        Log.d("ca-TAG", "Track #2");
                        if (loadable != null)
                            loadable.onLoadMore();
                        isLoading.set(true);
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public CoinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("Chq", "On create view holder");
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.coin_layout, parent, false);
        view.setOnClickListener(v -> (
                (MainActivity) activity).showUTLCChart(
                ((TextView) view.findViewById(R.id.coin_name)).getText().toString(),
                ((TextView) view.findViewById(R.id.coin_symbol)).getText().toString()
        ));
        return new CoinViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoinViewHolder holder, int position) {
        CoinDTO item = new ArrayList<>(coinsMap.values()).get(position);

        float usd = Float.parseFloat(item.priceUsd) * 1000000;
        float round = (float) (Math.round(usd) / 1000000.0);
/*
        Log.d("Value_Bug", item.toString());
        Log.d("Value_Bug", String.format(Locale.ENGLISH, "%f", round));
*/
        holder.coin_name.setText(item.name);
        holder.coin_symbol.setText(item.symbol);
        holder.coin_price.setText(String.format(Locale.ENGLISH, "%f", round));
        holder.seven_days_change.setText(String.format(Locale.ENGLISH, "%s%%", item.percentChange7D));

        try {
            bindPercentChangeViews(holder.one_hour_change, item.percentChange1H);
            bindPercentChangeViews(holder.twenty_hours_change, item.percentChange24H);
            bindPercentChangeViews(holder.seven_days_change, item.percentChange7D);
        } catch (Exception e) {
            Log.d("ColorError", e.getMessage());
        }
    }

    private void bindPercentChangeViews(TextView textView, String percentageValue) {
        if (percentageValue.contains("-")) {
            String data = percentageValue.replace("-", "▼");
            textView.setTextColor(Color.parseColor("#FF0000"));
            textView.setText(data);
        } else if (!percentageValue.contains("-")) {
            String data = "▲";
            data = data.concat(percentageValue);
            textView.setTextColor(Color.parseColor("#32CD32"));
            textView.setText(data);
        }
    }

    @Override
    public int getItemCount() {
        if (coinsMap.isEmpty())
            Toast.makeText(activity, "There is no cached data. " +
                    "Please swipe down to fetch online data from server", Toast.LENGTH_LONG).show();
        return coinsMap.size();
    }
}
