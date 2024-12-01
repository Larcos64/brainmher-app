package com.uan.brainmher.application.ui.activities.patient;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.uan.brainmher.R;
import com.uan.brainmher.application.ui.fragments.patients.AddPatientsFragment;
import com.uan.brainmher.application.ui.fragments.patients.PatientsListFragment;
import com.uan.brainmher.application.ui.helpers.NavigationViewHelper;
import com.uan.brainmher.application.ui.interfaces.IMainCarer;
import com.uan.brainmher.domain.entities.Carer;
import com.uan.brainmher.databinding.ActivityListPatientBinding;

public class PatientsList extends AppCompatActivity implements IMainCarer, AddPatientsFragment.OnFragmentInteractionListener {

    private ActivityListPatientBinding binding;
    private Carer carer = new Carer();
    private boolean isFabTapped = false;

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