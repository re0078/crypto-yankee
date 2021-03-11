package com.mobiledevelopment.cryptoyankee.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mobiledevelopment.cryptoyankee.R;
import com.mobiledevelopment.cryptoyankee.model.CoinDTO;
import com.mobiledevelopment.cryptoyankee.viewHolder.CoinViewHolder;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CoinAdapter extends RecyclerView.Adapter<CoinViewHolder> {
    private static final CoinAdapter COIN_ADAPTER = new CoinAdapter();
    @Setter
    private List<CoinDTO> coins;

    public static CoinAdapter getInstance() {
        return COIN_ADAPTER;
    }

    @NonNull
    @Override
    public CoinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.coin_layout, parent, false);
        return new CoinViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoinViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return coins.size();
    }
}

