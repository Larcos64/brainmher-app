package com.uan.brainmher.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.uan.brainmher.R;
import com.uan.brainmher.databinding.ActivityLoginBinding;

public class Login extends AppCompatActivity {
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using View Binding
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Keep the screen on and in fullscreen mode.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Button listeners
        binding.linkRegistration.setOnClickListener(v -> register());
    }

    private void register() {
        Intent intent = new Intent(Login.this,CaregiverRegistration.class);
        startActivity(intent);
    }
}
