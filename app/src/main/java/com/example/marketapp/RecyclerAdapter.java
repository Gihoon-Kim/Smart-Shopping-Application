package com.example.marketapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> implements OnStoreItemClickListener {

    private static final String TAG = "RecyclerAdapter";

    // List to input in adapter
    private ArrayList<StoreData> listData = new ArrayList<StoreData>();
    OnStoreItemClickListener listener;

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

    void addItem(StoreData data) {

        // adding item at outside
        listData.add(data);
    }

    void deleteAllItems() {

        listData.clear();
    }

    @Override
    public void onItemClick(ItemViewHolder holder, View view, int position) {

        if (listener != null) {

            listener.onItemClick(holder, view, position);
        }
    }

    public void setOnItemClickListener(OnStoreItemClickListener listener) {
        this.listener = listener;
    }

    public StoreData getItem(int position) {

        return listData.get(position);
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int pos = getAdapterPosition();
                    if (listener != null) {

                        listener.onItemClick(ItemViewHolder.this, v, pos);
                    }
                }
            });
        }

        void onBind(StoreData data) {

            storeName.setText(data.getStoreName());
            storeAddress.setText(data.getStoreAddress());
        }
    }
}
