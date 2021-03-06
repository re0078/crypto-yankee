package com.mobiledevelopment.cryptoyankee.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mobiledevelopment.cryptoyankee.R;


public class CoinViewHolder extends RecyclerView.ViewHolder{

    public ImageView coin_icon;
    public TextView coin_symbol,coin_name,coin_price,one_hour_change,twenty_hours_change,seven_days_change;

    public CoinViewHolder(View itemView) {
        super(itemView);

        coin_icon = itemView.findViewById(R.id.coin_icon);
        coin_symbol = itemView.findViewById(R.id.coin_symbol);
        coin_name = itemView.findViewById(R.id.coin_name);
        coin_price = itemView.findViewById(R.id.priceUsdText);
        one_hour_change = itemView.findViewById(R.id.oneHourText);
        twenty_hours_change = itemView.findViewById(R.id.twentyFourHourText);
        seven_days_change = itemView.findViewById(R.id.sevenDayText);
    }
}
