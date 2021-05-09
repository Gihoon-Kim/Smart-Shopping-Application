package com.example.marketapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

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

public class UserInformActivity extends Activity {

    private static final String BASE_URL = "https://sundaland.herokuapp.com/api/users/profile";
    private static final String TAG = "UserInformActivity";
    private static final String SERVER_MESSAGE = "serverMessage";

    String serverMessage = "";
    static RequestQueue requestQueue;

    @BindView(R.id.textViewUserEmail)
    TextView tvUserEmail;
    @BindView(R.id.textViewUserName)
    TextView tvUserName;
    @BindView(R.id.textViewUserAddress)
    TextView tvUserAddress;

    String userAddress;
    String userCity;
    String userPostalCode;
    String userCountry;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinformactivity);

        ButterKnife.bind(this);

        Intent dataIntent = getIntent();
        serverMessage = dataIntent.getStringExtra(SERVER_MESSAGE);
        Log.d(TAG, "server Message = " + serverMessage);

        JSONObject jsonObject;
        try {

            jsonObject = new JSONObject(serverMessage);
            tvUserEmail.setText(jsonObject.getString("email"));
            tvUserName.setText(jsonObject.getString("name"));
            Log.d(TAG, jsonObject.getJSONObject("userAddress").getString("address")
                    .concat(" ")
                    .concat(jsonObject.getJSONObject("userAddress").getString("city"))
                    .concat(" ")
                    .concat(jsonObject.getJSONObject("userAddress").getString("postalCode"))
                    .concat(" ")
                    .concat(jsonObject.getJSONObject("userAddress").getString("country"))
            );
            tvUserAddress.setText(jsonObject.getJSONObject("userAddress").getString("address")
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

    @OnClick(R.id.buttonUpdate)
    public void onUpdateButtonClicked() {

        newProfileDialog();
    }

    @OnClick(R.id.buttonBack)
    public void onButtonBackClicked() {

        finish();
    }

    private void newProfileDialog() {

        @SuppressLint("InflateParams") View dialogView = getLayoutInflater().inflate(R.layout.addressupdatedialog, null);
        EditText etAddress = dialogView.findViewById(R.id.editTextAddress);
        EditText etCity = dialogView.findViewById(R.id.editTextCity);
        EditText etPostalCode = dialogView.findViewById(R.id.editTextPostalCode);
        EditText etCountry = dialogView.findViewById(R.id.editTextCountry);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        builder.setPositiveButton("OK", (dialog, which) -> {

            userAddress = etAddress.getText().toString();
            userCity = etCity.getText().toString();
            userPostalCode = etPostalCode.getText().toString();
            userCountry = etCountry.getText().toString();

            UpdateProfile();
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void UpdateProfile() {

        Intent tokenIntent = getIntent();
        String userToken = tokenIntent.getStringExtra("token");

        JSONObject requestJSONObject = new JSONObject();

        try {

            JSONObject addressParam = new JSONObject();

            Log.d(TAG, "address = " + userAddress + "\n" + "city = " + userCity + "\n" + "postalCode = " + userPostalCode + "\n" + "Country = " + userCountry);
            addressParam.put("address", userAddress);
            addressParam.put("city", userCity);
            addressParam.put("postalCode", userPostalCode);
            addressParam.put("country", userCountry);

            requestJSONObject.put("userAddress", addressParam);
        } catch (JSONException e) {

            e.printStackTrace();
        }
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                BASE_URL,
                requestJSONObject,
                response -> {

                    Log.d(TAG, "response = " + response);
                    JSONObject dataJSONObject = null;
                    try {

                        dataJSONObject = new JSONObject(String.valueOf(response));
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }

                    try {

                        assert dataJSONObject != null;
                        Log.d(TAG, dataJSONObject.getJSONObject("userAddress").getString("address"));
                        tvUserAddress.setText(
                                        dataJSONObject.getJSONObject("userAddress").getString("address").concat(" ") +
                                        dataJSONObject.getJSONObject("userAddress").getString("city").concat(" " ) +
                                        dataJSONObject.getJSONObject("userAddress").getString("postalCode").concat(" ") +
                                        dataJSONObject.getJSONObject("userAddress").getString("country").concat(" ")
                                );
                        User.getInstance().setUserToken(response.getString("token"));
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show()) {

            public Map<String, String> getHeaders() {

                HashMap<String, String> params = new HashMap<>();
                Log.d(TAG, "getParams token = " + userToken);
                assert userToken != null;
                params.put("Authorization", "Bearer ".concat(userToken));
                return params;
            }
        };

        requestQueue.add(request);
    }
}
