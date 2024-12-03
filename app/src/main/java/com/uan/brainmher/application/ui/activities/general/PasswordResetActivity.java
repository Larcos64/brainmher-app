package com.uan.brainmher.application.ui.activities.general;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.uan.brainmher.databinding.ActivityPasswordResetBinding;

public class PasswordResetActivity extends AppCompatActivity {

    private ActivityPasswordResetBinding binding;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using View Binding
        binding = ActivityPasswordResetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Instancia de FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        // Listener para el botón de envío de correo
        binding.sendEmailButton.setOnClickListener(v -> {
            String email = binding.emailEditText.getText().toString().trim();
            if (!email.isEmpty()) {
                sendPasswordResetEmail(email);
            } else {
                Toast.makeText(this, "Por favor ingresa un correo válido.", Toast.LENGTH_SHORT).show();
            }
        });

        // Listener para el botón de regresar al login
        binding.backToLoginButton.setOnClickListener(v -> {
            Intent intent = new Intent(PasswordResetActivity.this, Login.class);
            startActivity(intent);
            finish(); // Finaliza la actividad actual
        });
    }

    private void sendPasswordResetEmail(String email) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Correo enviado. Cambia tu contraseña y regresa para iniciar sesión.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(PasswordResetActivity.this, Login.class);
                        startActivity(intent); // Redirige al login automáticamente
                        finish(); // Cierra la actividad actual
                    } else {
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}