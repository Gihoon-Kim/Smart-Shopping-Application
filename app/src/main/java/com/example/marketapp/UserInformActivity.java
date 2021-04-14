package com.example.marketapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserInformActivity extends Activity {

    private static final String TAG = "UserInformActivity";
    private static final String SERVER_MESSAGE = "serverMessage";

    String serverMessage = "";

    @BindView(R.id.textViewUserEmail)
    TextView tvUserEmail;
    @BindView(R.id.textViewUserName)
    TextView tvUserName;
    @BindView(R.id.textViewUserAddress)
    TextView tvUserAddress;


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
}
