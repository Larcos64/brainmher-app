package com.uan.brainmher.application.ui.activities.health_professional;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.uan.brainmher.R;
import com.uan.brainmher.application.ui.activities.general.Login;
import com.uan.brainmher.application.ui.activities.general.NavigationOptions;
import com.uan.brainmher.application.ui.activities.patient.PatientsList;
import com.uan.brainmher.databinding.ActivityHealthProfessionalBinding;
import com.uan.brainmher.domain.entities.Carer;
import com.uan.brainmher.domain.entities.HealthcareProfessional;
import com.uan.brainmher.domain.entities.Patient;
import com.uan.brainmher.infraestructure.tools.Constants;

import de.hdodenhof.circleimageview.CircleImageView;

public class HealthProfessionalActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityHealthProfessionalBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private HealthcareProfessional hp = new HealthcareProfessional();
    private Carer carer = new Carer();
    private Bundle args = new Bundle();
    private Patient patientSendFragment = new Patient();
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializamos el ViewBinding
        binding = ActivityHealthProfessionalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configuramos la orientación de la pantalla y otras opciones
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setSupportActionBar(binding.toolbarHealthProfessional);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // Configuramos el Navigation Drawer
        binding.secondNavigationViewHp.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, binding.secondDrawerLayoutHp, binding.toolbarHealthProfessional, R.string.drawer_open, R.string.drawer_close);
        binding.secondDrawerLayoutHp.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Configuramos los datos del usuario en el Navigation Drawer
        final TextView nameUser = binding.secondNavigationViewHp.getHeaderView(0).findViewById(R.id.lbl_name_user);
        final TextView emailUser = binding.secondNavigationViewHp.getHeaderView(0).findViewById(R.id.lbl_email_user);
        final CircleImageView imageUser = binding.secondNavigationViewHp.getHeaderView(0).findViewById(R.id.img_users_navigation);

        db = FirebaseFirestore.getInstance();
        db.collection(Constants.HealthcareProfesional).document(firebaseUser.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            hp = documentSnapshot.toObject(HealthcareProfessional.class);
                            if (hp != null) {
                                nameUser.setText(hp.getFirstName() + " " + hp.getLastName());
                                emailUser.setText(hp.getEmail());
                                Glide.with(HealthProfessionalActivity.this).load(hp.getUriImg()).fitCenter().into(imageUser);
                                userRole = carer.getRole();
                            }
                        }
                    }
                });

        db.collection(Constants.Carers).document(firebaseUser.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            carer = documentSnapshot.toObject(Carer.class);
                            if (carer != null) {
                                nameUser.setText(carer.getFirstName() + " " + carer.getLastName());
                                emailUser.setText(carer.getEmail());
                                Glide.with(HealthProfessionalActivity.this).load(carer.getUriImg()).fitCenter().into(imageUser);
                            }
                        }
                    }
                });

        // Configuramos el BottomNavigationView
        BottomNavigationView bottomNavigationView = binding.navigationHealthProfessional;

        // Obtén el NavHostFragment y configura el NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.content_health_professional);
        Log.d("NAVHOST: ", navHostFragment.toString());
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            Log.d("NAVCONTROLLER: ", navController.toString());
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        }

        // Guardamos el paciente en SharedPreferences
        args = getIntent().getExtras();
        if (args != null) {
            patientSendFragment = (Patient) args.getSerializable("patient");
            args.putSerializable("patient", patientSendFragment);

            SharedPreferences preferences = getPreferences(0);
            SharedPreferences.Editor editor = preferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(patientSendFragment);
            editor.putString("serialipatient", json);
            editor.apply();
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Acción personalizada para el botón de retroceso
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_ps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.ps_action_patient_list) {
            startSettings();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startSettings() {
        startActivity(new Intent(HealthProfessionalActivity.this, PatientsList.class));
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        closeDrawer();
        int itemId = item.getItemId();
        if (itemId == R.id.btn_profile) {
            Intent navigation = new Intent(HealthProfessionalActivity.this, NavigationOptions.class);
            navigation.putExtra("option", "profile");
            navigation.putExtra("user_uid", firebaseUser.getUid());
            navigation.putExtra("user_role", userRole);
            navigation.putExtra("profile_type", "personal");
            startActivity(navigation);
        } else if (itemId == R.id.btn_logout) {
            firebaseAuth.signOut();
            Intent intent = new Intent(HealthProfessionalActivity.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        return true;
    }

    private void closeDrawer() {
        binding.secondDrawerLayoutHp.closeDrawer(GravityCompat.START);
    }
}
