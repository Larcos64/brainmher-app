package com.uan.brainmher.ui;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.uan.brainmher.databinding.ActivityCaregiverRegistrationBinding;
import com.uan.brainmher.databinding.ActivityLoginBinding;

public class CaregiverRegistration extends AppCompatActivity {
    private ActivityCaregiverRegistrationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using View Binding
        binding = ActivityCaregiverRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

}
