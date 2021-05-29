package com.example.marketapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OwnerStoreProductActivity extends Activity {

    private static final String TAG = "OwnerProductActivity";
    String ownerToken;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tvStoreName)
    TextView tvStoreName;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tvStoreAddress)
    TextView tvStoreAddress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ownerstoreproductactivity);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        String storeName = intent.getStringExtra("storeName");
        String storeAddress = intent.getStringExtra("storeAddress");
        ownerToken = intent.getStringExtra("ownerToken");
        String storeID = intent.getStringExtra("storeID");

        Log.d(TAG, "Store ID = " + storeID);
        Log.d(TAG, "Store Name = " + storeName);
        Log.d(TAG, "Store Address = " + storeAddress);
        Log.d(TAG, "User Token = " + ownerToken);

        tvStoreName.setText(storeName);
        tvStoreAddress.setText(storeAddress);


    }
}
