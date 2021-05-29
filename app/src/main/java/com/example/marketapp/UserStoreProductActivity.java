package com.example.marketapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserStoreProductActivity extends Activity {

    private static final String TAG = "UserProductActivity";
    private static final String BASE_URL = "https://sundaland.herokuapp.com/api/users/store";

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tvStoreName)
    TextView tvStoreName;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tvStoreAddress)
    TextView tvStoreAddress;
    @BindView(R.id.ivSubscribe)
    ImageView ivSubscribe;

    static RequestQueue requestQueue;
    boolean isSubscribed = false;
    String userToken;
    String storeID;
    ArrayList<String> userSubscribedStoresString;
    ArrayList<JSONObject> userSubscribedStoresJSON = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userstoreproductactivity);

        ButterKnife.bind(this);

        Log.d(TAG, "User store product Activity started");
        // get Data from UserMainActivity
        Intent intent = getIntent();
        String storeName = intent.getStringExtra("storeName");
        String storeAddress = intent.getStringExtra("storeAddress");
        userToken = intent.getStringExtra("userToken");
        storeID = intent.getStringExtra("storeID");
        userSubscribedStoresString = intent.getStringArrayListExtra("userSubscribedStoresList");

        for (int i = 0; i < userSubscribedStoresString.size(); i++) {

            try {

                JSONObject jsonObject = new JSONObject(userSubscribedStoresString.get(i));
                userSubscribedStoresJSON.add(jsonObject);
            } catch (JSONException e) {

                e.printStackTrace();
            }
        }

        Log.d(TAG, "Store ID = " + storeID);
        Log.d(TAG, "Store Name = " + storeName);
        Log.d(TAG, "Store Address = " + storeAddress);
        Log.d(TAG, "User Token = " + userToken);

        for (int i = 0; i < userSubscribedStoresJSON.size(); i++) {

            Log.d(TAG, "Subscribed Stores = " + userSubscribedStoresJSON.get(i).toString() + "\n");
        }

        tvStoreName.setText(storeName);
        tvStoreAddress.setText(storeAddress);

        for (int i = 0; i < userSubscribedStoresJSON.size(); i++) {

            try {

                if (userSubscribedStoresJSON.get(i).getString("_id").equals(storeID)) {

                    Log.d(TAG, "The store was already Subscribed");
                    ivSubscribe.setImageResource(R.drawable.ic_baseline_star_24);
                    isSubscribed = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick(R.id.ivSubscribe)
    public void onIvSubscribeClicked() {

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        if (isSubscribed) {

            // if the store is in User's database, the star should be full.
            // if the star is clicked, star will be changed to outline star, and remove the store from user database
            ivSubscribe.setImageResource(R.drawable.ic_outline_star_outline_24);
            isSubscribed = !isSubscribed;
            Toast.makeText(this, "Subscribe Cancel", Toast.LENGTH_SHORT).show();

            // TODO : Take off the store from the User database

        } else {

            // if the store is not in User's database, the store should be outlined
            // if the star is clicked, star will be changed to full star, and put the store data into user database
            ivSubscribe.setImageResource(R.drawable.ic_baseline_star_24);
            isSubscribed = !isSubscribed;
            Toast.makeText(this, "Subscribe Success", Toast.LENGTH_SHORT).show();

            // TODO : Add the store into the User database

            JSONObject requestJsonOBJECT = new JSONObject();
            Log.d(TAG, "Store ID = " + storeID);
            try {

                requestJsonOBJECT.put("storeId", storeID);
            } catch (JSONException e) {

                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    BASE_URL,
                    requestJsonOBJECT,
                    response -> {

                        Log.d(TAG, "Response = " + response);
                        Toast.makeText(UserStoreProductActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                        },
                    error -> Toast.makeText(UserStoreProductActivity.this, error.getMessage(), Toast.LENGTH_LONG).show()) {

                public Map<String, String> getHeaders() {

                    HashMap<String, String> params = new HashMap<>();
                    params.put("Authorization", "Bearer ".concat(userToken));

                    return params;
                }
            };

            requestQueue.add(request);
        }
    }
}
