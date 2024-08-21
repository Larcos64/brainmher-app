package com.uan.brainmher.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.uan.brainmher.databinding.ActivityMainCarerBinding;

public class MainCarer extends AppCompatActivity {

    private ActivityMainCarerBinding binding;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using View Binding
        binding = ActivityMainCarerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
