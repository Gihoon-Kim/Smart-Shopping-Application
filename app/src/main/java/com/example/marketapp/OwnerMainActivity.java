package com.example.marketapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OwnerMainActivity extends AppCompatActivity {

    private static final String PROFILE_BASE_URL = "https://sundaland.herokuapp.com/api/users/profile";
    private static final String MY_STORE_BASE_URL = "https://sundaland.herokuapp.com/api/stores";
    private static final String ADD_STORE_TO_OWNER_STORE_LIST_BASE_URL = "https://sundaland.herokuapp.com/api/users/store";
    private static final String DELETE_STORE_URL = "https://sundaland.herokuapp.com/api/stores/";
    private static final String SERVER_MESSAGE = "serverMessage";
    private static final String TAG = "OwnerMainActivity";

    String serverMessage = "";
    static RequestQueue requestQueue;
    static String ownerToken = "";
    private final RecyclerAdapter adapter = new RecyclerAdapter();

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btnViewMyStoreList)
    Button btnViewMyStore;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.imageViewOwnerMenu)
    ImageView imageViewOwnerMenu;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.textViewOwnerName)
    TextView tvOwnerName;

    // Update Store dialog views
    TextView tvStoreName;
    TextView tvStoreAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ownermainactivity);

        ButterKnife.bind(this);

        // Get data from LoginActivity
        Intent dataIntent = getIntent();
        serverMessage = dataIntent.getStringExtra(SERVER_MESSAGE);

        Log.i(TAG, "Server Message = " + serverMessage);

        try {

            // get user name
            JSONObject jsonObject = new JSONObject(serverMessage);
            tvOwnerName.setText(jsonObject.getString("name"));
            ownerToken = jsonObject.getString("token");
        } catch (JSONException e) {

            e.printStackTrace();
        }

        adapter.setOnItemClickListener((holder, view, position) -> {

            StoreData storeData = adapter.getItem(position);
            Log.d(TAG, "Store Data. getStoreName = " + storeData.getStoreName());
            Toast.makeText(getApplicationContext(), storeData.getStoreName(), Toast.LENGTH_LONG).show();

            startStoreDialog(storeData);
        });
    }

    private void startStoreDialog(StoreData storeData) {

        View dialogView = getLayoutInflater().inflate(R.layout.ownerstoredialog, null);
        tvStoreName = dialogView.findViewById(R.id.tvStoreName);
        tvStoreAddress = dialogView.findViewById(R.id.tvStoreAddress);

        Button btnUpdate = dialogView.findViewById(R.id.btnUpdateInfo);
        Button btnViewProduct = dialogView.findViewById(R.id.btnViewProduct);
        Button btnDelete = dialogView.findViewById(R.id.btnDeleteStore);

        tvStoreName.setText(storeData.getStoreName());
        tvStoreAddress.setText(storeData.getStoreAddress());

        btnViewProduct.setOnClickListener(v -> {

            Intent intent = new Intent(OwnerMainActivity.this, OwnerStoreProductActivity.class);
            intent.putExtra("storeID", storeData.getStoreID());
            intent.putExtra("storeName", storeData.getStoreName());
            intent.putExtra("storeAddress", storeData.getStoreAddress());
            intent.putExtra("ownerToken", ownerToken);
            startActivity(intent);
        });

        btnUpdate.setOnClickListener(v -> UpdateStoreInfo(storeData));

        btnDelete.setOnClickListener(v -> DeleteStoreFromDatabase(storeData));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        builder.setNegativeButton("Close", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void DeleteStoreFromDatabase(StoreData storeData) {

        new AlertDialog.Builder(this)
                .setMessage("Really want to remove the store?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", (dialog, which) -> {

                    requestQueue = Volley.newRequestQueue(getApplicationContext());

                    StringRequest request = new StringRequest(
                            Request.Method.DELETE,
                            DELETE_STORE_URL + storeData.getStoreID(),
                            response -> Log.d(TAG, "Response = " + response),
                            error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show()) {

                        public Map<String, String> getHeaders() {

                            HashMap<String, String> params = new HashMap<>();
                            params.put("Authorization", "Bearer ".concat(ownerToken));

                            return params;
                        }
                    };

                    requestQueue.add(request);

                    CallStoreList();
                })
                .show();
    }

    private void UpdateStoreInfo(StoreData storeData) {

        View dialogView = getLayoutInflater().inflate(R.layout.storeaddressupdatedialog, null);
        EditText etStoreName = dialogView.findViewById(R.id.etStoreName);
        EditText etStoreAddress = dialogView.findViewById(R.id.etStoreAddress);
        EditText etStoreCity = dialogView.findViewById(R.id.etStoreCity);
        EditText etStoreProvince = dialogView.findViewById(R.id.etStoreProvince);
        EditText etStorePostalCode = dialogView.findViewById(R.id.etStorePostalCode);
        EditText etStoreCountry = dialogView.findViewById(R.id.editTextCountry);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        builder.setPositiveButton("OK", (dialog, which) -> {

            String newStoreName = etStoreName.getText().toString();
            String newStoreAddress = etStoreAddress.getText().toString();
            String newStoreCity = etStoreCity.getText().toString();
            String newStoreProvince = etStoreProvince.getText().toString();
            String newStorePostalCode = etStorePostalCode.getText().toString();
            String newStoreCountry = etStoreCountry.getText().toString();

            UpdateStoreAddress(storeData, newStoreName, newStoreAddress, newStoreCity, newStoreProvince, newStorePostalCode, newStoreCountry);
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void UpdateStoreAddress(StoreData storeData, String newStoreName, String newStoreAddress, String newStoreCity, String newStoreProvince, String newStorePostalCode, String newStoreCountry) {

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        JSONObject storeAddressJSON = new JSONObject();

        try {

            storeAddressJSON.put("_id", storeData.getStoreID());
            storeAddressJSON.put("name", newStoreName);
            storeAddressJSON.put("city", newStoreCity);
            storeAddressJSON.put("province", newStoreProvince);
            storeAddressJSON.put("country", newStoreCountry);
            storeAddressJSON.put("postalCode", newStorePostalCode);
            storeAddressJSON.put("streetAddress", newStoreAddress);
        } catch (JSONException e) {

            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                MY_STORE_BASE_URL,
                storeAddressJSON,
                response -> {

                    Log.d(TAG, "response = " + response);

                    try {

                        tvStoreName.setText(response.getString("name"));
                        tvStoreAddress.setText(
                                response.getString("streetAddress") + " " +
                                        response.getString("city") + " " +
                                        response.getString("province") + " " +
                                        response.getString("postalCode") + " " +
                                        response.getString("country")
                        );
                        storeData.setStoreName(response.getString("name"));
                        storeData.setStoreAddress(
                                response.getString("streetAddress") + " " +
                                        response.getString("city") + " " +
                                        response.getString("province") + " " +
                                        response.getString("postalCode") + " " +
                                        response.getString("country")
                        );
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }

                    CallStoreList();
                },
                error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show()) {

            public Map<String, String> getHeaders() {

                HashMap<String, String> params = new HashMap<>();
                Log.d(TAG, "Owner Token = " + ownerToken);
                params.put("Authorization", "Bearer ".concat(ownerToken));
                return params;
            }
        };

        requestQueue.add(request);
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.imageViewOwnerMenu)
    public void onOwnerMenuClicked() {

        PopupMenu popupMenu = new PopupMenu(this, imageViewOwnerMenu);
        MenuInflater menuInflater = getMenuInflater();
        Menu menu = popupMenu.getMenu();
        menuInflater.inflate(R.menu.ownermenu, menu);

        popupMenu.setOnMenuItemClickListener(item -> {

            switch (item.getItemId()) {

                case R.id.ownerInform:

                    RequestProfile();
                    break;

                case R.id.addStore:

                    AddNewStore();
            }

            return false;
        });

        popupMenu.show();
    }

    private void RequestProfile() {

        Log.d(TAG, "RequestProfile Token = " + ownerToken);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest request = new StringRequest(
                Request.Method.GET,
                PROFILE_BASE_URL,
                response -> {

                    Log.d(TAG, "Response = " + response);
                    Intent intent = new Intent(getApplicationContext(), OwnerInformActivity.class);
                    intent.putExtra(SERVER_MESSAGE, response);
                    intent.putExtra("token", ownerToken);
                    startActivity(intent);
                },
                error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show()) {


            public Map<String, String> getHeaders() {

                HashMap<String, String> params = new HashMap<>();
                Log.d(TAG, "RequestProfile_getHeaders " + ownerToken);
                params.put("Authorization", "Bearer ".concat(ownerToken));

                return params;
            }
        };

        requestQueue.add(request);
    }

    private void AddNewStore() {

        View dialogView = getLayoutInflater().inflate(R.layout.addnewstore, null);
        EditText etStoreName = dialogView.findViewById(R.id.etStoreName);
        EditText etStoreAddress = dialogView.findViewById(R.id.etStoreAddress);
        EditText etStoreCity = dialogView.findViewById(R.id.etStoreCity);
        EditText etStoreProvince = dialogView.findViewById(R.id.etStoreProvince);
        EditText etStorePostalCode = dialogView.findViewById(R.id.etStorePostalCode);
        EditText etStoreCountry = dialogView.findViewById(R.id.etStoreCountry);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        builder.setPositiveButton("OK", (dialog, which) -> {

            if (
                    etStoreName.getText().toString().trim().equals("") ||
                            etStoreAddress.getText().toString().trim().equals("") ||
                            etStoreCity.getText().toString().trim().equals("") ||
                            etStoreProvince.getText().toString().trim().equals("") ||
                            etStorePostalCode.getText().toString().trim().equals("") ||
                            etStoreCountry.getText().toString().trim().equals("")
            ) {

                Toast.makeText(getApplicationContext(), "Address is went wrong", Toast.LENGTH_LONG).show();
            } else {

                AddNewStoreInDatabase(
                        etStoreName.getText().toString(),
                        etStoreAddress.getText().toString(),
                        etStoreCity.getText().toString(),
                        etStoreProvince.getText().toString(),
                        etStorePostalCode.getText().toString(),
                        etStoreCountry.getText().toString()
                );
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.show();
    }

    private void AddNewStoreInDatabase(String storeName, String storeAddress, String storeCity, String storeProvince, String storePostalCode, String storeCountry) {

        Log.d(TAG, "AddNewStoreInDatabase " + ownerToken);

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("name", storeName);
            jsonObject.put("streetAddress", storeAddress);
            jsonObject.put("city", storeCity);
            jsonObject.put("province", storeProvince);
            jsonObject.put("postalCode", storePostalCode);
            jsonObject.put("country", storeCountry);
        } catch (JSONException e) {

            e.printStackTrace();
        }

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        AtomicReference<String> storeID = new AtomicReference<>("");

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                MY_STORE_BASE_URL,
                jsonObject,
                response -> {

                    Log.d(TAG, "AddNewStoreInDatabase " + response);
                    Toast.makeText(getApplicationContext(), "Store Successfully Created", Toast.LENGTH_LONG).show();

                    try {

                        storeID.set(response.getString("_id"));
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show()) {

            public Map<String, String> getHeaders() {

                HashMap<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer ".concat(ownerToken));
                return params;
            }
        };

        requestQueue.add(request);

        CallStoreList();
        // Add the have just created store to owner's store list
        AddToMyStoreList(storeID);
    }

    private void AddToMyStoreList(AtomicReference<String> responseID) {

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest request = new StringRequest(
                Request.Method.POST,
                ADD_STORE_TO_OWNER_STORE_LIST_BASE_URL,
                response -> {

                    Log.d(TAG, "AddNewStoreInOwnerList " + response);
                    Toast.makeText(getApplicationContext(), "Store Added to Owner's list", Toast.LENGTH_LONG).show();
                },
                error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show()) {

            public Map<String, String> getHeaders() {

                HashMap<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer ".concat(ownerToken));
                params.put("_id", String.valueOf(responseID));

                return params;
            }
        };

        requestQueue.add(request);
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.btnViewMyStoreList)
    public void onViewMyStoreClicked() {

        CallStoreList();
    }

    // modify server to send store information instead of only IDs
    private void CallStoreList() {

        Log.d(TAG, "Token = " + ownerToken);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        adapter.deleteAllItems();
        StringRequest request = new StringRequest(
                Request.Method.GET,
                OwnerMainActivity.MY_STORE_BASE_URL,
                response -> {

                    Log.d(TAG, "Response = " + response);

                    RecyclerView recyclerView = findViewById(R.id.rvStoreList);

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                    recyclerView.setLayoutManager(linearLayoutManager);

                    recyclerView.setAdapter(adapter);

                    Intent intent = new Intent(OwnerMainActivity.this, RecyclerAdapter.class);
                    intent.putExtra("token", ownerToken);

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
                            StoreData data = new StoreData();
                            data.setStoreID(storeJSONObject.getString("_id"));
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
                error -> Toast.makeText(OwnerMainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show()) {

            public Map<String, String> getHeaders() {

                HashMap<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer ".concat(ownerToken));

                return params;
            }
        };

        requestQueue.add(request);
    }
}
