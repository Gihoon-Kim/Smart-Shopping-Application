package com.example.marketapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;

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
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends Activity {

    private static final String TAG = "RegisterActivity";

    @BindView(R.id.editTextName)
    EditText editTextName;
    @BindView(R.id.editTextEmail)
    EditText editTextEmail;
    @BindView(R.id.editTextPassword)
    EditText editTextPassword;

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

    public class RegisterRequest extends AsyncTask<Void, Void, String> {

        private static final String BASE_URL = "https://sundaland.herokuapp.com/api/users";

        @Override
        protected String doInBackground(Void... voids) {

            JSONObject jsonObject = new JSONObject();

            try {

                jsonObject.accumulate("name", userName);
                jsonObject.accumulate("email", userEmail);
                jsonObject.accumulate("password", userPassword);

                HttpURLConnection connection = null;
                BufferedReader reader = null;

                try {

                    URL url = new URL(BASE_URL);
                    connection = (HttpURLConnection) url.openConnection();

                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Cache-Control", "no-cache");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
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
                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while ((line = reader.readLine()) != null) {

                        buffer.append(line);
                    }

                    return buffer.toString();
                } catch (MalformedURLException e) {

                    e.printStackTrace();
                } catch (IOException e) {

                    e.printStackTrace();
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
    }

}
