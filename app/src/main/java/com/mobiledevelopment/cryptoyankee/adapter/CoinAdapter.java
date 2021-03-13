package com.mobiledevelopment.cryptoyankee.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mobiledevelopment.cryptoyankee.MainActivity;
import com.mobiledevelopment.cryptoyankee.R;
import com.mobiledevelopment.cryptoyankee.models.coin.CoinDTO;
import com.mobiledevelopment.cryptoyankee.ui.CoinViewHolder;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;
import lombok.Setter;
import okhttp3.HttpUrl;

public class CoinAdapter extends RecyclerView.Adapter<CoinViewHolder> {

    private final Activity activity;
    @Getter
    private final SortedMap<Long, CoinDTO> coinsMap;
    @Setter
    private Loadable loadable;
    @Getter
    private AtomicBoolean isLoading = new AtomicBoolean(false);
    private final String LOG_TAG = "ca-TAG";

    public CoinAdapter(RecyclerView recyclerView, Activity activity, int loadLimit) {
        this.coinsMap = new TreeMap<>();
        this.activity = activity;
        AtomicInteger visibleThreshold = new AtomicInteger(5);
        if (loadLimit != 0)
            visibleThreshold.set(loadLimit);

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d(LOG_TAG, "page scrolled :" + dy);
                if (dy > 0) {
                    assert linearLayoutManager != null;
                    int totalItemCount = linearLayoutManager.getItemCount();
                    int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!isLoading.get() && totalItemCount <= (lastVisibleItem + visibleThreshold.get())) {
                        isLoading.set(true);
                        if (loadable != null)
                            loadable.onLoadMore();
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public CoinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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

        double usd = Double.parseDouble(item.getPriceUsd()) * 1000000;
        double round = (double) (Math.round(usd) / 1000000.0);
        holder.coin_name.setText(item.getName());
        holder.coin_symbol.setText(item.getSymbol());
        holder.coin_price.setText(String.format(Locale.ENGLISH, "%.4f", round));
        holder.seven_days_change.setText(String.format(Locale.ENGLISH, "%s%%", item.getPercentChange7D()));
        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(
                activity.getResources().getString(R.string.icons_api).
                        concat(item.getId()).
                        concat(activity.getResources().getString(R.string.png_format)))).
                newBuilder();
        Glide
                .with(holder.itemView)
                .load(builder.build().toString())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .into(holder.coin_icon);

        try {
            bindPercentChangeViews(holder.one_hour_change, item.getPercentChange1H());
            bindPercentChangeViews(holder.twenty_hours_change, item.getPercentChange24H());
            bindPercentChangeViews(holder.seven_days_change, item.getPercentChange7D());
        } catch (Exception e) {
            Log.d("ColorError", e.getMessage());
        }
    }

    private void bindPercentChangeViews(TextView textView, String percentageValue) {
        percentageValue = String.format(Locale.ENGLISH, "%.4f",
                Float.valueOf(percentageValue));
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
        return coinsMap.size();
    }
}
