package com.example.marketapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";
    private static final String BASE_URL = "https://sundaland.herokuapp.com/";

    @BindView(R.id.editTextID)
    EditText editTextID;
    @BindView(R.id.editTextPassword)
    EditText editTextPassword;
    @BindView(R.id.textViewError)
    TextView textViewError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivity);

        ButterKnife.bind(this);

        // Fulfillment HttpURLConnection through AsyncTask
        NetworkTask networkTask = new NetworkTask(BASE_URL, null, textViewError);
        networkTask.execute();
    }

    @OnClick(R.id.buttonLogin)
    public void onLoginButtonClicked() {

    }

    @OnClick(R.id.buttonRegister)
    public void onRegisterButtonClicked() {

        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
