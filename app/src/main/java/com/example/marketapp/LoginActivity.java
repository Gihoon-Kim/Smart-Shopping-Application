package com.example.marketapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";
    private static final String LOGIN_ERROR = "Login Error";
    private static final String BASE_URL = "https://sundaland.herokuapp.com/api/users/login";

    @BindView(R.id.editTextEmail)
    EditText editTextEmail;
    @BindView(R.id.editTextPassword)
    EditText editTextPassword;
    @BindView(R.id.textViewError)
    TextView textViewError;

    String userEmail = "";
    String userPassword = "";
    StringBuilder builder = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivity);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.buttonLogin)
    public void onLoginButtonClicked() {

        userEmail = editTextEmail.getText().toString();
        userPassword = editTextPassword.getText().toString();

        new LoginRequest().execute();
    }

    @OnClick(R.id.buttonRegister)
    public void onRegisterButtonClicked() {

        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public class LoginRequest extends AsyncTask<Void, Void, String> {

        String errorMsg = "";

        @Override
        protected String doInBackground(Void... voids) {

            JSONObject jsonObject = new JSONObject();

            try {

                // Create JSONObject and put data as Key-value format
                jsonObject.accumulate("email", userEmail);
                jsonObject.accumulate("password", userPassword);

                HttpURLConnection connection = null;
                BufferedReader reader = null;

                try {

                    URL url = new URL(BASE_URL);
                    connection = (HttpURLConnection) url.openConnection();

                    connection.setRequestMethod("POST");    // Send by POST method
                    connection.setRequestProperty("Cache-Control", "no-cache"); // Set Cache
                    connection.setRequestProperty("Content-Type", "application/json");  // Send by application/json format
                    connection.setDoInput(true);    // Send post data as OutStream
                    connection.setDoOutput(true);   // Get response by server as InputStream
                    connection.connect();

                    // Create Stream to send to server
                    OutputStream outputStream = connection.getOutputStream();
                    //Create Buffer and put
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();

                    // get Data from server
                    InputStream inputStream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {

                        builder.append(line);
                    }

                    Log.i(TAG, builder.toString());
                    errorMsg = builder.toString();

                } catch (IOException e) {

                    e.printStackTrace();
                    errorMsg = LOGIN_ERROR;
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (errorMsg.equals(LOGIN_ERROR)) {

                textViewError.setText(R.string.login_failure);
            } else {

                Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                // Send data from server to UserMainActivity
                Intent intent = new Intent(LoginActivity.this, UserMainActivity.class);
                intent.putExtra("serverMessage", builder.toString());
                startActivity(intent);
            }
        }
    }
}
