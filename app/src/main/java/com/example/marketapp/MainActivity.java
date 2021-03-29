package com.example.marketapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.textViewUserEmail)
    TextView textViewUserEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);

        Intent intent = getIntent();
        String userEmail = intent.getStringExtra("userEmail");

        textViewUserEmail.setText(userEmail);
    }
}
