package com.mobiledevelopment.cryptoyankee.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobiledevelopment.cryptoyankee.R;
import com.mobiledevelopment.cryptoyankee.model.CoinDTO;
import com.mobiledevelopment.cryptoyankee.viewHolder.CoinViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lombok.Setter;

public class RezaCoinAdapter extends RecyclerView.Adapter<CoinViewHolder> {
    @Setter
    private List<CoinDTO> coinItems;
    private Loadable loadable;
    private boolean isLoading;
    private final int VISIBLE_THRESHOLD = 5;

    int lastVisibleItem, totalItemCount;

    public RezaCoinAdapter(RecyclerView recyclerView) {
        this.coinItems = new ArrayList<>();

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                assert linearLayoutManager != null;
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + VISIBLE_THRESHOLD)) {
                    if (loadable != null)
                        loadable.onLoadMore();
                    isLoading = true;
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
        return new CoinViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoinViewHolder holder, int position) {
        CoinDTO item = coinItems.get(position);

        float usd = Float.parseFloat(item.priceUsd) * 1000000;
        float round = (float) (Math.round(usd) / 1000000.0);

        holder.coin_name.setText(item.name);
        holder.coin_symbol.setText(item.symbol);
        holder.coin_price.setText(String.format(Locale.ENGLISH, "%f", round));
        holder.seven_days_change.setText(String.format(Locale.ENGLISH, "%s%%", item.percentChange7D));

        //Load Images (Picasso)
//        Picasso.with(activity)
//                .load(new StringBuilder("https://res.cloudinary.com/dxi90ksom/image/upload/")
//                        .append(item.getSymbol().toLowerCase()).append(".png").toString())
//                .into(holderItem.coin_icon);
//▼▾▲▴
//        This section of code Change the color of text to Red incase of a drop and Green incase of a rise!

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
        return coinItems.size();
    }

    public void setLoadable(Loadable loadable) {
        this.loadable = loadable;
    }

    public void setLoaded() {
        isLoading = false;
    }

    public void updateData(List<CoinDTO> coinDTOS) {
        this.coinItems = coinDTOS;
        notifyDataSetChanged();
    }
}
