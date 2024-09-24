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
import com.uan.brainmher.domain.repositories.CarerRepository;
import com.uan.brainmher.domain.repositories.HealthcareProfessionalRepository;
import com.uan.brainmher.infraestructure.tools.Constants;

import de.hdodenhof.circleimageview.CircleImageView;

public class HealthProfessionalActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityHealthProfessionalBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private HealthcareProfessionalRepository healthcareProfessionalRepository;
    private CarerRepository carerRepository;
    private String userRole;
    private Bundle args = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializamos el ViewBinding
        binding = ActivityHealthProfessionalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setSupportActionBar(binding.toolbarHealthProfessional);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // Configurar Navigation Drawer
        binding.secondNavigationViewHp.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, binding.secondDrawerLayoutHp, binding.toolbarHealthProfessional, R.string.drawer_open, R.string.drawer_close);
        binding.secondDrawerLayoutHp.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Inicializamos los repositorios
        healthcareProfessionalRepository = new HealthcareProfessionalRepository();
        carerRepository = new CarerRepository();

        // Configuramos los datos del usuario en el Navigation Drawer
        final TextView nameUser = binding.secondNavigationViewHp.getHeaderView(0).findViewById(R.id.lbl_name_user);
        final TextView emailUser = binding.secondNavigationViewHp.getHeaderView(0).findViewById(R.id.lbl_email_user);
        final CircleImageView imageUser = binding.secondNavigationViewHp.getHeaderView(0).findViewById(R.id.img_users_navigation);

        // Cargamos los datos del Healthcare Professional
        loadHealthcareProfessionalData(firebaseUser.getUid(), nameUser, emailUser, imageUser);

        // Cargamos los datos del Carer
        loadCarerData(firebaseUser.getUid(), nameUser, emailUser, imageUser);

        // Configurar BottomNavigationView y NavController
        BottomNavigationView bottomNavigationView = binding.navigationHealthProfessional;
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.content_health_professional);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        }

        // Guardar paciente en SharedPreferences
        savePatientInPreferences();

        // Manejo del botón de retroceso
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    private void loadHealthcareProfessionalData(String userId, TextView nameUser, TextView emailUser, CircleImageView imageUser) {
        healthcareProfessionalRepository.getHealthcareProfessional(userId, new HealthcareProfessionalRepository.OnHealthcareProfessionalLoadedListener() {
            @Override
            public void onSuccess(HealthcareProfessional hp) {
                if (hp != null) {
                    nameUser.setText(hp.getFirstName() + " " + hp.getLastName());
                    emailUser.setText(hp.getEmail());
                    Glide.with(HealthProfessionalActivity.this).load(hp.getUriImg()).fitCenter().into(imageUser);
                    userRole = hp.getRole(); // Asignamos el rol del usuario
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("HealthProfessional", "Failed to load Healthcare Professional", e);
            }
        });
    }

    private void loadCarerData(String userId, TextView nameUser, TextView emailUser, CircleImageView imageUser) {
        carerRepository.getCarer(userId, new CarerRepository.OnCarerLoadedListener() {
            @Override
            public void onSuccess(Carer carer) {
                if (carer != null) {
                    nameUser.setText(carer.getFirstName() + " " + carer.getLastName());
                    emailUser.setText(carer.getEmail());
                    Glide.with(HealthProfessionalActivity.this).load(carer.getUriImg()).fitCenter().into(imageUser);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("HealthProfessional", "Failed to load Carer", e);
            }
        });
    }

    private void savePatientInPreferences() {
        args = getIntent().getExtras();
        if (args != null) {
            Patient patientSendFragment = (Patient) args.getSerializable("patient");
            args.putSerializable("patient", patientSendFragment);

            SharedPreferences preferences = getPreferences(0);
            SharedPreferences.Editor editor = preferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(patientSendFragment);
            editor.putString("serialipatient", json);
            editor.apply();
        }
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
        if (item.getItemId() == R.id.btn_profile) {
            Intent navigation = new Intent(HealthProfessionalActivity.this, NavigationOptions.class);
            navigation.putExtra("option", "profile");
            navigation.putExtra("user_uid", firebaseUser.getUid());
            navigation.putExtra("user_role", userRole);
            navigation.putExtra("profile_type", "personal");
            startActivity(navigation);
        } else if (item.getItemId() == R.id.btn_logout) {
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
