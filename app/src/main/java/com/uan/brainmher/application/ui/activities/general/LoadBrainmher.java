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
import android.view.WindowManager;
import android.widget.Toast;

import com.uan.brainmher.R;
import com.uan.brainmher.infraestructure.tools.Constants;

public class LoadBrainmher extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_brainmher);

        //iniciarOneSignal();

        //region ScreenOrientationPortrait
        //Screen orientation portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //Keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //endregion
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //firebaseAuth = FirebaseAuth.getInstance();
        //firebaseUser = firebaseAuth.getCurrentUser();

        // Permission before in to redirect by role
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            redirect();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, Constants.MY_PERMISSIONS_REQUEST_CALL_PHONE);
        }
    }

    private void redirect() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(LoadBrainmher.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }, Constants.SPLASH_DURATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                redirect();
            } else {
                Toast.makeText(this, "Permiso para realizar llamadas no concedido", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
