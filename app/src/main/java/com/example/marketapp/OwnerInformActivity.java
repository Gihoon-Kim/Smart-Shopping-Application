package com.example.marketapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OwnerInformActivity extends AppCompatActivity {

    private static final String PROFILE_BASE_URL = "https://sundaland.herokuapp.com/api/users/profile";
    private static final String SERVER_MESSAGE = "serverMessage";
    private static final String TAG = "OwnerInformActivity";

    String serverMessage;
    static RequestQueue requestQueue;

    @BindView(R.id.tvOwnerEmail)
    TextView tvOwnerEmail;
    @BindView(R.id.tvOwnerName)
    TextView tvOwnerName;
    @BindView(R.id.tvOwnerAddress)
    TextView tvOwnerAddress;

    String ownerAddress;
    String ownerCity;
    String ownerPostalCode;
    String ownerCountry;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ownerinformactivity);

        ButterKnife.bind(this);

        Intent dataIntent = getIntent();
        serverMessage = dataIntent.getStringExtra(SERVER_MESSAGE);
        Log.d(TAG, "server Message = " + serverMessage);

        JSONObject jsonObject;
        try {

            jsonObject = new JSONObject(serverMessage);
            tvOwnerEmail.setText(jsonObject.getString("email"));
            tvOwnerName.setText(jsonObject.getString("name"));
            tvOwnerAddress.setText(jsonObject.getJSONObject("userAddress").getString("address")
                    .concat(" ")
                    .concat(jsonObject.getJSONObject("userAddress").getString("city"))
                    .concat(" ")
                    .concat(jsonObject.getJSONObject("userAddress").getString("postalCode"))
                    .concat(" ")
                    .concat(jsonObject.getJSONObject("userAddress").getString("country"))
            );
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }

    @OnClick(R.id.buttonBack)
    public void onButtonBackClicked() {

        finish();
    }

    @OnClick(R.id.buttonUpdate)
    public void onUpdateButtonClicked() {

        newProfileDialog();
    }

    private void newProfileDialog() {

        View dialogView = getLayoutInflater().inflate(R.layout.addressupdatedialog, null);
        EditText etAddress = dialogView.findViewById(R.id.editTextAddress);
        EditText etCity = dialogView.findViewById(R.id.editTextCity);
        EditText etPostalCode = dialogView.findViewById(R.id.editTextPostalCode);
        EditText etCountry = dialogView.findViewById(R.id.editTextCountry);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        builder.setPositiveButton("OK", (dialog, which) -> {

            ownerAddress = etAddress.getText().toString();
            ownerCity = etCity.getText().toString();
            ownerPostalCode = etPostalCode.getText().toString();
            ownerCountry = etCountry.getText().toString();

            UpdateProfile();
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void UpdateProfile() {

        Intent tokenIntent = getIntent();
        String ownerToken = tokenIntent.getStringExtra("token");

        JSONObject requestJSONObject = new JSONObject();

        try {

            JSONObject addressParam = new JSONObject();

            Log.d(TAG, "address = " + ownerAddress + "\n" + "city = " + ownerCity + "\n" + "postalCode = " + ownerPostalCode + "\n" + "Country = " + ownerCountry);
            addressParam.put("address", ownerAddress);
            addressParam.put("city", ownerCity);
            addressParam.put("postalCode", ownerPostalCode);
            addressParam.put("country", ownerCountry);

            requestJSONObject.put("userAddress", addressParam);
        } catch (JSONException e) {

            e.printStackTrace();
        }

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                PROFILE_BASE_URL,
                requestJSONObject,
                response -> {

                    Log.d(TAG, "response = " + response);
                    JSONObject dataJSONObject = null;
                    try {

                        dataJSONObject = new JSONObject(String.valueOf(response));
                        OwnerMainActivity.ownerToken = dataJSONObject.getString("token");

                        Log.d(TAG, "new Token " + OwnerMainActivity.ownerToken);
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }

                    try {

                        assert dataJSONObject != null;
                        Log.d(TAG, dataJSONObject.getJSONObject("userAddress").getString("address"));
                        tvOwnerAddress.setText(
                                dataJSONObject.getJSONObject("userAddress").getString("address").concat(" ") +
                                        dataJSONObject.getJSONObject("userAddress").getString("city").concat(" ") +
                                        dataJSONObject.getJSONObject("userAddress").getString("postalCode").concat(" ") +
                                        dataJSONObject.getJSONObject("userAddress").getString("country").concat(" ")
                        );
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show()) {

            public Map<String, String> getHeaders() {

                HashMap<String, String> params = new HashMap<>();
                Log.d(TAG, "getParams token = " + ownerToken);
                assert ownerToken != null;
                params.put("Authorization", "Bearer ".concat(ownerToken));
                return params;
            }
        };

        requestQueue.add(request);

    }
}
