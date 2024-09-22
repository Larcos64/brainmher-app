package com.uan.brainmher.application.ui.activities.general;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.uan.brainmher.R;
import com.uan.brainmher.application.ui.activities.carer.CarerRegistration;
import com.uan.brainmher.infraestructure.database.LoginManager;
import com.uan.brainmher.databinding.ActivityLoginBinding;

public class Login extends AppCompatActivity {
    private ActivityLoginBinding binding;

    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using View Binding
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        // Keep the screen on and in fullscreen mode.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Button listeners
        binding.linkRegistration.setOnClickListener(v -> register());
        binding.btnLogin.setOnClickListener(v -> emailPassLogin());
    }

    private void register() {
        Intent intent = new Intent(Login.this, CarerRegistration.class);
        startActivity(intent);
    }

    private LoginManager loginInstance() {
        LoginManager loginManager = new LoginManager();
        return loginManager;
    }

    private void emailPassLogin() {
        String[] credentials = validateFields();
        if (credentials != null) {
            loginInstance().loginEmailPassword(this, credentials[0], credentials[1]);
        }
    }

    private String[] validateFields() {
        String email = binding.txtEmail.getText().toString().trim();
        String password = binding.txtPassword.getText().toString().trim();
        String[] data = new String[2];
        boolean isValid = true;

        if (email.isEmpty()) {
            binding.txtEmailLayout.setError(getString(com.uan.brainmher.R.string.email_required));
            isValid = false;
        } else {
            binding.txtEmailLayout.setError(null); // Clear any previous error
        }

        if (password.isEmpty()) {
            binding.txtPasswordLayout.setError(getString(R.string.password_required));
            isValid = false;
        } else {
            binding.txtPasswordLayout.setError(null); // Clear any previous error
        }

        if (isValid) {
            data[0] = email;
            data[1] = password;
            return data;
        } else {
            return null;
        }
    }

}
