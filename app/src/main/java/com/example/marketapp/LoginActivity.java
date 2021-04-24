package com.example.marketapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";
    private static final String BASE_URL = "https://sundaland.herokuapp.com/api/users/login";
    private static final String LOGIN_ERROR = "Error";
    private static final String LOGIN_SUCCESS = "Login Success";
    private static final String SERVER_MESSAGE = "serverMessage";
    static RequestQueue requestQueue;

    @BindView(R.id.editTextEmail)
    EditText editTextEmail;
    @BindView(R.id.editTextPassword)
    EditText editTextPassword;
    @BindView(R.id.textViewError)
    TextView textViewError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivity);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.buttonLogin)
    public void onLoginButtonClicked() {

        LoginRequest();
    }

    @OnClick(R.id.buttonRegister)
    public void onRegisterButtonClicked() {

        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void LoginRequest() {

        String userEmail = editTextEmail.getText().toString();
        String userPassword = editTextPassword.getText().toString();

        JSONObject requestJsonObject = new JSONObject();
        try {

            requestJsonObject.put("email", userEmail);
            requestJsonObject.put("password", userPassword);
        } catch (JSONException e) {

            e.printStackTrace();
        }

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                BASE_URL,
                requestJsonObject,
                response -> {
                    boolean isOwner = false;
                    String responseData = response.toString();
                    Log.d(TAG, "response = " + response);
                    Log.d(TAG, "responseData = " + responseData);
                    try {

                        isOwner = response.getBoolean("isOwner");
                        Log.d(TAG, "isOwner = " + isOwner);
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }

                    if (responseData.equals(response.toString())) {

                        if (isOwner) {

                            Intent intent = new Intent(getApplicationContext(), OwnerMainActivity.class);
                            intent.putExtra(SERVER_MESSAGE, responseData);
                            startActivity(intent);
                        } else {

                            Intent intent = new Intent(getApplicationContext(), UserMainActivity.class);
                            intent.putExtra(SERVER_MESSAGE, responseData);
                            startActivity(intent);
                        }
                    } else {

                        textViewError.setText(R.string.login_failure);
                        Toast.makeText(getApplicationContext(), LOGIN_SUCCESS, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {

                    textViewError.setText(R.string.login_failure);
                    Toast.makeText(getApplicationContext(), LOGIN_ERROR, Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(request);
    }
}
