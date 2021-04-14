package com.example.marketapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserMainActivity extends Activity {

    private static final String SERVER_MESSAGE = "serverMessage";
    private static final String TAG = "UserMainActivity";
    private static final String BASE_URL = "https://sundaland.herokuapp.com/api/users/profile";

    static RequestQueue requestQueue;

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

        new RequestProfile().execute();
    }

    public class RequestProfile extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {

            String userToken;
            JSONObject jsonObject = null;
            try {

                jsonObject = new JSONObject(serverMessage);
                userToken = jsonObject.getString("token");
            } catch (JSONException e) {

                userToken = "Error";
                e.printStackTrace();
            }

            Log.i(TAG, "Token = " + userToken);

            requestQueue = Volley.newRequestQueue(getApplicationContext());
            String finalUserToken = userToken;

            final StringRequest request = new StringRequest(
                    Request.Method.GET,
                    BASE_URL,
                    response -> Log.i(TAG, "Response = " + response),
                    error -> Toast.makeText(UserMainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show()){

                public Map<String, String> getHeaders() {

                    HashMap<String, String> params = new HashMap<>();
                    params.put("Authorization", "Bearer ".concat(finalUserToken));

                    return params;
                }
            };

            requestQueue.add(request);
            return null;
        }
    }
}

