package com.example.marketapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends Activity {

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

        String userEmail = editTextEmail.getText().toString();
        String userPassword = editTextPassword.getText().toString();

        Response.Listener<String> responseListener = response -> {

            try {

                JSONObject jsonObject = new JSONObject(response);

                if (true) { // Login Success

                    String userEmail1 = jsonObject.getString("email");
                    Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("userEmail", userEmail1);
                    startActivity(intent);
                } else {    // Login Failure

                    Toast.makeText(getApplicationContext(), "Login Failure", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (JSONException e) {

                e.printStackTrace();
            }
        };

        LoginRequest loginRequest = new LoginRequest(userEmail, userPassword, responseListener);
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(loginRequest);
    }

    @OnClick(R.id.buttonRegister)
    public void onRegisterButtonClicked() {

        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
