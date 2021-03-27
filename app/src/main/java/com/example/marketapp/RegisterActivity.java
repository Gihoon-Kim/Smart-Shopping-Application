package com.example.marketapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

    private FirebaseAuth mAuth;
    private String email = "";
    private String password = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registeractivity);

        ButterKnife.bind(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @OnClick(R.id.buttonRegister)
    public void onRegisterButtonClicked() {

        email = editTextEmail.getText().toString();

        if (editTextPassword.getText().toString().equals(editTextPasswordCheck.getText().toString())) {

            password = editTextPasswordCheck.getText().toString();

            signUp();
        } else {

            Toast.makeText(this, "ID or Password should not be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void signUp() {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:Success");
                        FirebaseUser user = mAuth.getCurrentUser();
                    } else {

                        // If sign in fails, display a message to the user
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
