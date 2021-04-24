package com.example.marketapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OwnerMainActivity extends AppCompatActivity {

    private static final String PROFILE_BASE_URL = "https://sundaland.herokuapp.com/api/users/profile";
    private static final String ADD_STORE_BASE_URL = "https://sundaland.herokuapp.com/api/stores";
    private static final String SERVER_MESSAGE = "serverMessage";
    private static final String TAG = "OwnerMainActivity";

    String serverMessage = "";
    static RequestQueue requestQueue;
    static String ownerToken = "";

    @BindView(R.id.buttonViewMyStore)
    Button btnViewMyStore;
    @BindView(R.id.imageViewOwnerMenu)
    ImageView imageViewOwnerMenu;
    @BindView(R.id.textViewOwnerName)
    TextView tvOwnerName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ownermainactivity);

        ButterKnife.bind(this);

        // Get data from LoginActivity
        Intent dataIntent = getIntent();
        serverMessage = dataIntent.getStringExtra(SERVER_MESSAGE);

        Log.i(TAG, "Server Message = " + serverMessage);

        try {

            // get user name
            JSONObject jsonObject = new JSONObject(serverMessage);
            tvOwnerName.setText(jsonObject.getString("name"));
            ownerToken = jsonObject.getString("token");
        } catch (JSONException e) {

            e.printStackTrace();
        }

    }

    @OnClick(R.id.imageViewOwnerMenu)
    public void onOwnerMenuClicked() {

        PopupMenu popupMenu = new PopupMenu(this, imageViewOwnerMenu);
        MenuInflater menuInflater = getMenuInflater();
        Menu menu = popupMenu.getMenu();
        menuInflater.inflate(R.menu.ownermenu, menu);

        popupMenu.setOnMenuItemClickListener(item -> {

            switch (item.getItemId()) {

                case R.id.ownerInform:

                    RequestProfile();
                    break;

                case R.id.addStore:

                    AddNewStore();
            }

            return false;
        });

        popupMenu.show();
    }

    private void RequestProfile() {

        Log.d(TAG, "RequestProfile Token = " + ownerToken);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest request = new StringRequest(
                Request.Method.GET,
                PROFILE_BASE_URL,
                response -> {

                    Log.d(TAG, "Response = " + response);
                    Intent intent = new Intent(getApplicationContext(), OwnerInformActivity.class);
                    intent.putExtra(SERVER_MESSAGE, response);
                    intent.putExtra("token", ownerToken);
                    startActivity(intent);
                },
                error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show()) {


            public Map<String, String> getHeaders() {

                HashMap<String, String> params = new HashMap<>();
                Log.d(TAG, "RequestProfile_getHeaders " + ownerToken);
                params.put("Authorization", "Bearer ".concat(ownerToken));

                return params;
            }
        };

        requestQueue.add(request);
    }

    private void AddNewStore() {

        View dialogView = getLayoutInflater().inflate(R.layout.addnewstore, null);
        EditText etStoreName = dialogView.findViewById(R.id.etStoreName);
        EditText etStoreAddress = dialogView.findViewById(R.id.etStoreAddress);
        EditText etStoreCity = dialogView.findViewById(R.id.etStoreCity);
        EditText etStoreProvince = dialogView.findViewById(R.id.etStoreProvince);
        EditText etStorePostalCode = dialogView.findViewById(R.id.etStorePostalCode);
        EditText etStoreCountry = dialogView.findViewById(R.id.etStoreCountry);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        builder.setPositiveButton("OK", (dialog, which) -> {

            if (
                    etStoreName.getText().toString().trim().equals("") ||
                    etStoreAddress.getText().toString().trim().equals("") ||
                    etStoreCity.getText().toString().trim().equals("") ||
                    etStoreProvince.getText().toString().trim().equals("") ||
                    etStorePostalCode.getText().toString().trim().equals("") ||
                    etStoreCountry.getText().toString().trim().equals("")
            ) {

                Toast.makeText(getApplicationContext(), "Address is went wrong", Toast.LENGTH_LONG).show();
            } else {
                AddNewStoreInDatabase(
                        etStoreName.getText().toString(),
                        etStoreAddress.getText().toString(),
                        etStoreCity.getText().toString(),
                        etStoreProvince.getText().toString(),
                        etStorePostalCode.getText().toString(),
                        etStoreCountry.getText().toString()
                );
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.show();
    }

    private void AddNewStoreInDatabase(String storeName, String storeAddress, String storeCity, String storeProvince, String storePostalCode, String storeCountry) {

        Log.d(TAG, "AddNewStoreInDatabase " + ownerToken);

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("name", storeName);
            jsonObject.put("streetAddress", storeAddress);
            jsonObject.put("city", storeCity);
            jsonObject.put("province", storeProvince);
            jsonObject.put("postalCode", storePostalCode);
            jsonObject.put("country", storeCountry);
        } catch (JSONException e) {

            e.printStackTrace();
        }

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                ADD_STORE_BASE_URL,
                jsonObject,
                response -> {
                    Log.d(TAG, "AddNewStoreInDatabase " + response);
                    Toast.makeText(getApplicationContext(), "Store Successfully Created", Toast.LENGTH_LONG).show();
                },
                error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show()) {

            public Map<String, String> getHeaders() {

                HashMap<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer ".concat(ownerToken));
                return params;
            }
        };

        requestQueue.add(request);
    }

    @OnClick(R.id.buttonViewMyStore)
    public void onViewMyStoreClicked() {

        // TODO : when owner clicks the button, store list that belongs to the owner should be appeared
        // TODO : if there is nothing to show, show error message.
    }
}
