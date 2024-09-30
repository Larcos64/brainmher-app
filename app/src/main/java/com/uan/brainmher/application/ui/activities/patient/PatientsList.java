package com.uan.brainmher.application.ui.activities.patient;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uan.brainmher.R;
import com.uan.brainmher.application.ui.activities.general.Login;
import com.uan.brainmher.application.ui.activities.general.NavigationOptions;
import com.uan.brainmher.application.ui.activities.health_professional.HealthProfessionalActivity;
import com.uan.brainmher.application.ui.fragments.patients.AddPatientsFragment;
import com.uan.brainmher.application.ui.fragments.patients.PatientsListFragment;
import com.uan.brainmher.application.ui.interfaces.IMainCarer;
import com.uan.brainmher.domain.entities.Carer;
//import com.uan.brainmher.data.HealthcareProfessional;
import com.uan.brainmher.databinding.ActivityListPatientBinding;
import com.uan.brainmher.domain.entities.HealthcareProfessional;
import com.uan.brainmher.domain.repositories.CarerRepository;
import com.uan.brainmher.domain.repositories.HealthcareProfessionalRepository;
import com.uan.brainmher.infraestructure.helpers.SharedPreferencesManager;
import com.uan.brainmher.infraestructure.tools.Constants;
//import com.uan.brainmher.fragments.AddPatients;
//import com.uan.brainmher.fragments.hp.PatientsListFragment;
//import com.uan.brainmher.interfaces.IMainCarer;
//import com.uan.brainmher.interfaces.IPatientsListFragmentListener;
//import com.uan.brainmher.tools.Constants;

import de.hdodenhof.circleimageview.CircleImageView;

public class PatientsList extends AppCompatActivity implements IMainCarer, AddPatientsFragment.OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener {

    private ActivityListPatientBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    //private HealthcareProfessional hp = new HealthcareProfessional();
    private Carer carer = new Carer();
    private boolean isFabTapped = false;
    //private IPatientsListFragmentListener fragmentListener;
    private String userRole;
    private HealthcareProfessionalRepository healthcareProfessionalRepository;
    private CarerRepository carerRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializa ViewBinding
        binding = ActivityListPatientBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configuración de pantalla
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setSupportActionBar(binding.toolbar);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // Configurar Navigation Drawer
        binding.navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.drawer_open, R.string.drawer_close);
        binding.drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Configuramos los datos del usuario en el Navigation Drawer
        final TextView nameUser = binding.navigationView.getHeaderView(0).findViewById(R.id.lbl_name_user);
        final TextView emailUser = binding.navigationView.getHeaderView(0).findViewById(R.id.lbl_email_user);
        final CircleImageView imageUser = binding.navigationView.getHeaderView(0).findViewById(R.id.img_users_navigation);

        String userRole = SharedPreferencesManager.getInstance(this).getString("user_role", "default_role");
        switch (userRole) {
            case "Carers":
                carerRepository = new CarerRepository();
                loadCarerData(firebaseUser.getUid(), nameUser, emailUser, imageUser);
                break;
            case "HealthcareProfessional":
                healthcareProfessionalRepository = new HealthcareProfessionalRepository();
                loadHealthcareProfessionalData(firebaseUser.getUid(), nameUser, emailUser, imageUser);
                break;
        }

        if (savedInstanceState == null) {
            handleFrame(new PatientsListFragment());
        }

        // Gestión del clic en el FloatingActionButton
        binding.fabHProfessional.setOnClickListener(v -> handleFab());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Acción personalizada para el botón de retroceso
                finish();
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
                    Glide.with(PatientsList.this).load(carer.getUriImg()).fitCenter().into(imageUser);
                    userRole = carer.getRole();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("HealthProfessional", "Failed to load Carer", e);
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
                    Glide.with(PatientsList.this).load(hp.getUriImg()).fitCenter().into(imageUser);
                    userRole = hp.getRole();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("HealthProfessional", "Failed to load Healthcare Professional", e);
            }
        });
    }

    // Método para gestionar el clic en el FloatingActionButton
    private void handleFab() {
        isFabTapped = !isFabTapped;
        if (isFabTapped) {
            binding.babHProfessional.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
            handleFrame(new AddPatientsFragment());
            binding.fabHProfessional.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_go_back));
        } else {
            binding.babHProfessional.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
            handleFrame(new PatientsListFragment());
            binding.fabHProfessional.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_person_add));
        }
    }

    // Administrador de fragmentos
    private void handleFrame(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.fragmentHProfessional, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // Implementación vacía
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        closeDrawer();
        int itemId = item.getItemId();
        if (itemId == R.id.btn_profile) {
            Intent navigation = new Intent(PatientsList.this, NavigationOptions.class);
            navigation.putExtra("option", "profile");
            navigation.putExtra("user_uid", firebaseUser.getUid());
            navigation.putExtra("user_role", userRole);
            navigation.putExtra("profile_type", "personal");
            startActivity(navigation);
        } else if (itemId == R.id.btn_logout) {
            firebaseAuth.signOut();
            Intent intent = new Intent(PatientsList.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        return true;
    }

    private void closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void inflateFragment(String fragmentTag) {
        if (fragmentTag.equals(getString(R.string.list_patient))) {
            binding.babHProfessional.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
            handleFrame(new PatientsListFragment());
            binding.fabHProfessional.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_person_add));
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        // Implementación vacía
    }
}