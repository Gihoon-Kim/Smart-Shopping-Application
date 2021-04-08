package com.example.marketapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

public class UserMainActivity extends Activity {

    private static final String SERVER_MESSAGE = "serverMessage";
    private static final String TAG = "UserMainActivity";
    private static final String BASE_URL = "https://sundaland.herokuapp.com/api/users/profile";

    String serverMessage = "";

    @BindView(R.id.buttonViewStoreList)
    Button buttonViewStoreList;
    @BindView(R.id.imageViewMenu)
    ImageView imageViewMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usermainactivity);

        TextView textViewUserEmail = findViewById(R.id.textViewUserEmail);

        Intent dataIntent = getIntent();
        serverMessage = dataIntent.getStringExtra(SERVER_MESSAGE);

        Log.i(TAG, serverMessage != null ? serverMessage : "null");

        String userName = "";

        try {

            // get user name
            JSONObject jsonObject = new JSONObject(serverMessage);
            userName = jsonObject.getString("name");
        } catch (JSONException e) {

            e.printStackTrace();
        }

        textViewUserEmail.setText(userName);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.imageViewMenu)
    public void onMenuClicked() {

        PopupMenu popupMenu = new PopupMenu(this, imageViewMenu);
        MenuInflater menuInflater = getMenuInflater();
        Menu menu = popupMenu.getMenu();
        menuInflater.inflate(R.menu.usermenu, menu);

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {

                case R.id.userInform:

                    Intent intent = new Intent(getApplicationContext(), UserInformActivity.class);
                    intent.putExtra(SERVER_MESSAGE, serverMessage);
                    startActivity(intent);
            }

            return false;
        });
        popupMenu.show();
    }

    @OnClick(R.id.buttonViewStoreList)
    public void onViewStoreListClicked() {

        new requestProfile().execute();
    }

    public class requestProfile extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... voids) {

            try {

                HttpURLConnection connection = null;
                BufferedReader reader = null;

                try {

                    URL url = new URL(BASE_URL);
                    connection = (HttpURLConnection) url.openConnection();

                    connection.setRequestProperty("Cache-Control", "no-cache");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    JSONObject jsonObject = new JSONObject(serverMessage);
                    String userToken = jsonObject.getString("token");

                    JSONObject jsonSend = new JSONObject();

                    jsonSend.accumulate("token", userToken);
                    connection.addRequestProperty("Authorization", "Bearer " + userToken);
                    connection.connect();
                    Log.i(TAG, "Token = " + userToken);

                    OutputStream outputStream = connection.getOutputStream();
//                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
//                    writer.write(jsonSend.toString());
                    outputStream.write(userToken.getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                    outputStream.close();

                    InputStream inputStream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder builder = new StringBuilder();

                    String line;
                    while ((line = reader.readLine()) != null) {

                        builder.append(line);
                    }

                    Log.i(TAG, builder.toString());

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {

                e.printStackTrace();
            }
            return null;
        }
    }
}
