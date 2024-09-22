package com.uan.brainmher.application.ui.activities.general;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.uan.brainmher.R;
import com.uan.brainmher.infraestructure.database.LoginManager;
import com.uan.brainmher.infraestructure.tools.Constants;

public class LoadBrainmher extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private boolean isRedirecting = false; // Flag para evitar redirección múltiple

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_brainmher);

        // Configuraciones de pantalla
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        firebaseAuth = FirebaseAuth.getInstance();

        // Listener para detectar cambios en el estado de autenticación
        firebaseAuthListener();
        redirect();
    }

    private void firebaseAuthListener() {
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null && !isRedirecting) {
                    // Si el usuario está autenticado y no hemos redirigido aún
                    isRedirecting = true;
                    LoginManager loginManager = new LoginManager();
                    loginManager.redirectByRole(LoadBrainmher.this, firebaseUser);
                } else if (firebaseUser == null && !isRedirecting) {
                    // Si no hay usuario autenticado y no hemos redirigido aún
                    redirectToLogin();
                }
            }
        };
    }

    private void redirect() {
        new Handler().postDelayed(() -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null && !isRedirecting) {
                // Si hay un usuario autenticado y no hemos redirigido aún
                isRedirecting = true;
                loginInstance().redirectByRole(LoadBrainmher.this, currentUser);
            } else if (!isRedirecting) {
                // Si no hay usuario, redirigir al Login
                redirectToLogin();
            }
        }, Constants.SPLASH_DURATION);
    }

    private void redirectToLogin() {
        isRedirecting = true;
        Intent intent = new Intent(LoadBrainmher.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private LoginManager loginInstance() {
        return new LoginManager();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}
