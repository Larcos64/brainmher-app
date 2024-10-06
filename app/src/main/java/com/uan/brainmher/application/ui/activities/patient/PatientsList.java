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
import com.uan.brainmher.application.ui.helpers.NavigationViewHelper;
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

public class PatientsList extends AppCompatActivity implements IMainCarer, AddPatientsFragment.OnFragmentInteractionListener {

    private ActivityListPatientBinding binding;
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

        // Configurar el Drawer con NavigationViewHelper
        NavigationViewHelper.configureDrawer(this, binding.drawerLayout, binding.toolbar);

        // Configurar el NavigationView
        NavigationViewHelper.configureNavigationView(this, binding.navigationView);

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