package com.example.marketapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends Activity {

    private static final String TAG = "RegisterActivity";
    private static final String BASE_URL = "https://sundaland.herokuapp.com/api/users";
    private static final String REGISTRATION_ERROR = "Registration Error";
    private static final String REGISTRATION_SUCCESS = "Registration Success";

    static RequestQueue requestQueue;

    @BindView(R.id.editTextName)
    EditText editTextName;
    @BindView(R.id.editTextEmail)
    EditText editTextEmail;
    @BindView(R.id.editTextPassword)
    EditText editTextPassword;
    @BindView(R.id.editTextAddress)
    EditText editTextAddress;
    @BindView(R.id.editTextCity)
    EditText editTextCity;
    @BindView(R.id.editTextPostalCode)
    EditText editTextPostalCode;
    @BindView(R.id.editTextCountry)
    EditText editTextCountry;
    @BindView(R.id.textViewError)
    TextView textViewError;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registeractivity);

        ButterKnife.bind(this);

    }

    @OnClick(R.id.buttonRegister)
    public void onRegisterButtonClicked() {


        RegisterRequest();
    }

    public void RegisterRequest() {

        String userEmail = editTextEmail.getText().toString();
        String userName = editTextName.getText().toString();
        String userPassword = editTextPassword.getText().toString();
        String userAddress = editTextAddress.getText().toString();
        String userCity = editTextCity.getText().toString();
        String userPostalCode = editTextPostalCode.getText().toString();
        String userCountry = editTextCountry.getText().toString();

        JSONObject requestJsonObject = new JSONObject();

        try {

            //Create JSONObject and put data as Key-value format
            requestJsonObject.put("name", userName);
            requestJsonObject.put("email", userEmail);
            requestJsonObject.put("password", userPassword);

            JSONObject addressParam = new JSONObject();

            addressParam.put("address", userAddress);
            addressParam.put("city", userCity);
            addressParam.put("postalCode", userPostalCode);
            addressParam.put("country", userCountry);

            requestJsonObject.put("userAddress", addressParam);
        } catch (JSONException e) {

            e.printStackTrace();
        }

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                BASE_URL,
                requestJsonObject,
                response -> {

                    String responseData = response.toString();
                    Log.d(TAG, "response = " + response);
                    Log.d(TAG, "responseData = " + responseData);

                    if (responseData.equals(response.toString())) {

                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.putExtra("serverMessage", responseData);
                        startActivity(intent);
                    } else {

                        textViewError.setText(R.string.login_failure);
                        Toast.makeText(getApplicationContext(), REGISTRATION_SUCCESS, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {

                    textViewError.setText(R.string.login_failure);
                    Toast.makeText(getApplicationContext(), REGISTRATION_ERROR, Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(request);

    }
}
