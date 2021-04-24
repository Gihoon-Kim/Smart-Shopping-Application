package com.example.marketapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserMainActivity extends Activity {

    private static final String PROFILE_BASE_URL = "https://sundaland.herokuapp.com/api/users/profile";
    private static final String ALL_STORE_BASE_URL = "https://sundaland.herokuapp.com/api/stores/all";
    private static final String SERVER_MESSAGE = "serverMessage";
    private static final String TAG = "UserMainActivity";

    String serverMessage = "";
    static RequestQueue requestQueue;
    private RecyclerAdapter adapter;

    @BindView(R.id.buttonViewAllStoreList)
    Button buttonViewAllStoreList;
    @BindView(R.id.imageViewMenu)
    ImageView imageViewMenu;
    @BindView(R.id.textViewUserName)
    TextView textViewUserName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usermainactivity);

        ButterKnife.bind(this);

        // Get data from LoginActivity
        Intent dataIntent = getIntent();
        serverMessage = dataIntent.getStringExtra(SERVER_MESSAGE);

        Log.i(TAG, "Server Message = " + serverMessage);

        try {

            // get user name
            JSONObject jsonObject = new JSONObject(serverMessage);
            textViewUserName.setText(jsonObject.getString("name"));
        } catch (JSONException e) {

            e.printStackTrace();
        }

    }

    @OnClick(R.id.imageViewMenu)
    public void onMenuClicked() {

        PopupMenu popupMenu = new PopupMenu(this, imageViewMenu);
        MenuInflater menuInflater = getMenuInflater();
        Menu menu = popupMenu.getMenu();
        menuInflater.inflate(R.menu.usermenu, menu);

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {

                case R.id.userInform:

                    RequestProfile();
            }

            return false;
        });
        popupMenu.show();
    }

    @OnClick(R.id.buttonViewAllStoreList)
    public void onViewAllStoreListClicked() {

        RequestAllStores();
    }

    private void RequestAllStores() {

        String userToken;
        JSONObject jsonObject;
        try {

            Log.d(TAG, serverMessage);
            jsonObject = new JSONObject(serverMessage);
            userToken = jsonObject.getString("token");
        } catch (JSONException e) {

            userToken = "Error";
            e.printStackTrace();
        }

        Log.d(TAG, "Token = " + userToken);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        String finalUserToken = userToken;

        StringRequest request = new StringRequest(
                Request.Method.GET,
                ALL_STORE_BASE_URL,
                response -> {

                    Log.d(TAG, "Response = " + response);
                    // TODO : Put the response data into ListView

                    RecyclerView recyclerView = findViewById(R.id.rvStoreList);

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                    recyclerView.setLayoutManager(linearLayoutManager);

                    adapter = new RecyclerAdapter();
                    recyclerView.setAdapter(adapter);

                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {

                            Log.d(TAG, jsonArray.getString(i));
                            JSONObject storeJSONObject;
                            storeJSONObject = jsonArray.getJSONObject(i);
                            Log.d(TAG, storeJSONObject.getString("name"));
                            Log.d(TAG, "store Address = " +
                                    storeJSONObject.getString("streetAddress").concat(", ").
                                            concat(storeJSONObject.getString("city").concat(", ").
                                                    concat(storeJSONObject.getString("province").concat(", ").
                                                            concat(storeJSONObject.getString("country")))));
                            storeData data = new storeData();
                            data.setStoreName(storeJSONObject.getString("name"));
                            data.setStoreAddress(storeJSONObject.getString("streetAddress").concat(", ").
                                    concat(storeJSONObject.getString("city").concat(", ").
                                            concat(storeJSONObject.getString("province").concat(", ").
                                                    concat(storeJSONObject.getString("country")))));

                            adapter.addItem(data);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(UserMainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show()) {

            public Map<String, String> getHeaders() {

                HashMap<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer ".concat(finalUserToken));

                return params;
            }
        };

        requestQueue.add(request);
    }

    private void RequestProfile() {

        String userToken;
        JSONObject jsonObject;
        try {

            Log.d(TAG, serverMessage);
            jsonObject = new JSONObject(serverMessage);
            userToken = jsonObject.getString("token");
        } catch (JSONException e) {

            userToken = "Error";
            e.printStackTrace();
        }

        Log.d(TAG, "Token = " + userToken);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        String finalUserToken = userToken;

        StringRequest request = new StringRequest(
                Request.Method.GET,
                PROFILE_BASE_URL,
                response -> {

                    Log.d(TAG, "Response = " + response);
                    Intent intent = new Intent(getApplicationContext(), UserInformActivity.class);
                    intent.putExtra(SERVER_MESSAGE, response);
                    intent.putExtra("token", finalUserToken);
                    startActivity(intent);
                },
                error -> Toast.makeText(UserMainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show()) {

            public Map<String, String> getHeaders() {

                HashMap<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer ".concat(finalUserToken));

                return params;
            }
        };

        requestQueue.add(request);
    }
}

