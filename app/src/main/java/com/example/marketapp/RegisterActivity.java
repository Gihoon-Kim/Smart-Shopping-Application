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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends Activity {

    private static final String TAG = "RegisterActivity";
    private static final String BASE_URL = "https://sundaland.herokuapp.com/api/users";
    private static final String REGISTRATION_ERROR = "Registration Error";

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

    String userName = "";
    String userEmail = "";
    String userPassword = "";
    String userAddress = "";
    String userCity = "";
    String userPostalCode = "";
    String userCountry = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registeractivity);

        ButterKnife.bind(this);

    }

    @OnClick(R.id.buttonRegister)
    public void onRegisterButtonClicked() {

        userEmail = editTextEmail.getText().toString();
        userName = editTextName.getText().toString();
        userPassword = editTextPassword.getText().toString();
        userAddress = editTextAddress.getText().toString();
        userCity = editTextCity.getText().toString();
        userPostalCode = editTextPostalCode.getText().toString();
        userCountry = editTextCountry.getText().toString();
        new RegisterRequest().execute();
    }

    public class RegisterRequest extends AsyncTask<Void, Void, Void> {

        String errorMsg = "";

        @Override
        protected Void doInBackground(Void... voids) {

            JSONObject jsonObject = new JSONObject();

            try {

                //Create JSONObject and put data as Key-value format
                jsonObject.put("name", userName);
                jsonObject.put("email", userEmail);
                jsonObject.put("password", userPassword);

                JSONObject addressParam = new JSONObject();

                addressParam.put("address", userAddress);
                addressParam.put("city", userCity);
                addressParam.put("postalCode", userPostalCode);
                addressParam.put("country", userCountry);

                jsonObject.put("userAddress", addressParam);
                HttpURLConnection connection = null;
                BufferedReader reader = null;

                try {

                    URL url = new URL(BASE_URL);
                    connection = (HttpURLConnection) url.openConnection();

                    connection.setRequestMethod("POST");    // send by POST Method
                    connection.setRequestProperty("Cache-Control", "no-cache"); // Set Cache
                    connection.setRequestProperty("Content-Type", "application/json");  // Send by Application/JSON format
                    connection.setDoInput(true);    // Send post data as OutStream
                    connection.setDoOutput(true);   // Get response by server as InputStream
                    connection.connect();

                    // Create Stream to Send to server
                    OutputStream outputStream = connection.getOutputStream();

                    // create Buffer and put
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();

                    // get Data from server
                    InputStream inputStream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder builder = new StringBuilder();

                    String line;
                    while ((line = reader.readLine()) != null) {

                        builder.append(line);
                    }

                    JSONObject flag;
                    String isError = "";
                    try {

                        flag = new JSONObject(builder.toString());
                        Log.d(TAG, "builder.toString()" + builder.toString());
                        isError = flag.getString("message");
                        Log.d(TAG, "isError " + isError);

                        if (!isError.equals("")) {

                            errorMsg = REGISTRATION_ERROR;
                        }
                    } catch (JSONException e) {

                        Log.d(TAG, isError + "isError");
                        e.printStackTrace();
                    }
                    Log.i(TAG, builder.toString());


                } catch (IOException e) {

                    e.printStackTrace();
                    Log.d(TAG, e.toString());
                    errorMsg = REGISTRATION_ERROR;
                } finally {

                    if (connection != null) {

                        connection.disconnect();
                    }

                    try {

                        if (reader != null) {

                            reader.close();
                        }
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {

                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (errorMsg.equals(REGISTRATION_ERROR)) {

                textViewError.setText(R.string.regist_failure);
            } else {

                Toast.makeText(getApplicationContext(), "Registration Success", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
    }
}
