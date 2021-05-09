package com.example.marketapp;

import android.view.View;

import org.json.JSONException;

public interface OnStoreItemClickListener {

    public void onItemClick(RecyclerAdapter.ItemViewHolder holder, View view, int position);
}
