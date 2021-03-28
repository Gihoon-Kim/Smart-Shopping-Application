package com.example.marketapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends Activity {

    private static final String TAG = "RegisterActivity";

    @BindView(R.id.editTextEmail)
    EditText editTextEmail;
    @BindView(R.id.editTextPassword)
    EditText editTextPassword;
    @BindView(R.id.editTextPasswordCheck)
    EditText editTextPasswordCheck;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registeractivity);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.buttonRegister)
    public void onRegisterButtonClicked() {

        String email = editTextEmail.getText().toString();

        if (editTextPassword.getText().toString().equals(editTextPasswordCheck.getText().toString())) {

            String password = editTextPasswordCheck.getText().toString();
        } else {

            Toast.makeText(this, "ID or Password should not be empty", Toast.LENGTH_SHORT).show();
        }
    }
}
