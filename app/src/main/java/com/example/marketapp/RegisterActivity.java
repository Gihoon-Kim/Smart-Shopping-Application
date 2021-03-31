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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends Activity {

    private static final String TAG = "RegisterActivity";
    private static final String BASE_URL = "https://sundaland.herokuapp.com/api/users";

    @BindView(R.id.editTextName)
    EditText editTextName;
    @BindView(R.id.editTextEmail)
    EditText editTextEmail;
    @BindView(R.id.editTextPassword)
    EditText editTextPassword;
    @BindView(R.id.textViewError)
    TextView textViewError;

    String userName = "";
    String userEmail = "";
    String userPassword = "";

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
        new RegisterRequest().execute();
    }

    public class RegisterRequest extends AsyncTask<Void, Void, Void> {

        String errorMsg = "";

        @Override
        protected Void doInBackground(Void... voids) {

            JSONObject jsonObject = new JSONObject();

            try {

                // Create JSONObject and put data as Key-value format
                jsonObject.accumulate("name", userName);
                jsonObject.accumulate("email", userEmail);
                jsonObject.accumulate("password", userPassword);

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

                    Log.i(TAG, builder.toString());
                    errorMsg = builder.toString();
                } catch (IOException e) {

                    e.printStackTrace();
                    errorMsg = "Registration Error";
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

            if (errorMsg.equals("Registration Error")) {

                textViewError.setText(R.string.regist_failure);
            } else {

                Toast.makeText(getApplicationContext(), "Registration Success", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
    }
}
