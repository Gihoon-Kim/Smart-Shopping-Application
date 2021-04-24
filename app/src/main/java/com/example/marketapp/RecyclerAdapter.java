package com.example.marketapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {

    // List to input in adapter
    private ArrayList<storeData> listData = new ArrayList<storeData>();

    @NonNull
    @Override
    public RecyclerAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Inflate item xml file through LayoutInflater
        // return type is ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.storelistrecyclerview, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        // showing (binding) items
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {

        // total number of RecyclerView
        return listData.size();
    }

    void addItem(storeData data) {

        // adding item at outside
        listData.add(data);
    }

    // View Holder
    // Setting subView
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView storeName;
        private TextView storeAddress;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            storeName = itemView.findViewById(R.id.storeName);
            storeAddress = itemView.findViewById(R.id.storeAddress);
        }

        void onBind(storeData data) {

            storeName.setText(data.getStoreName());
            storeAddress.setText(data.getStoreAddress());
        }
    }
}
