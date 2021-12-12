package com.netwokz.mystaticip;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class StaticIpViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

    CardView mCardView;
    TextView mIp;
    TextView mName;
    TextView mMac;
    TextView mDate;
    ImageView mType;

    StaticIpViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
        mCardView = itemView.findViewById(R.id.card_view);
        mIp = itemView.findViewById(R.id.tv_ip);
        mName = itemView.findViewById(R.id.tv_name);
        mMac = itemView.findViewById(R.id.tv_mac);
        mDate = itemView.findViewById(R.id.tv_date);
        mType = itemView.findViewById(R.id.iv_type);
        mCardView.setRadius(20);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(this.getAdapterPosition(), 1, 1, "Edit");
        menu.add(this.getAdapterPosition(), 2, 2, "Delete");
        int positionOfMenuItem = 1; // or whatever...
        MenuItem item = menu.getItem(positionOfMenuItem);
        SpannableString s = new SpannableString("Delete");
        s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
        item.setTitle(s);
    }

    @Override
    public void onClick(View v) {

    }

}
