package com.uan.brainmher.application.ui.activities.carer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uan.brainmher.R;
import com.uan.brainmher.application.ui.activities.patient.PatientsList;
import com.uan.brainmher.application.ui.helpers.NavigationViewHelper;
import com.uan.brainmher.domain.entities.Carer;
import com.uan.brainmher.databinding.ActivityMainCarerBinding;
import com.uan.brainmher.application.ui.interfaces.IMainCarer;
import com.uan.brainmher.infraestructure.tools.Constants;

import de.hdodenhof.circleimageview.CircleImageView;
import com.google.android.material.navigation.NavigationView;
import com.uan.brainmher.application.ui.activities.general.Login;
import com.uan.brainmher.application.ui.activities.general.NavigationOptions;

public class MainCarer extends AppCompatActivity implements IMainCarer {

    private ActivityMainCarerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using View Binding
        binding = ActivityMainCarerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Function to read the items of BottomNavigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation_carer);
        NavController navController = Navigation.findNavController(this, R.id.content_carer);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // Declara los IDs en variables locales
        int homeId = R.id.home;
        // int testId = R.id.test;
        // int emergencyId = R.id.emergency;
        int informationId = R.id.information;

        // Configura el manejo de clics en los elementos del BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == homeId) {
                navController.navigate(R.id.home); // Ajusta según corresponda
                return true;
            } /* else if (itemId == testId) {
                navController.navigate(R.id.home); // Ajusta según corresponda
                return true;
            } else if (itemId == emergencyId) {
                navController.navigate(R.id.alzheimerInformationFragment); // Ajusta según corresponda
                return true;
            } */ else if (itemId == informationId) {
                navController.navigate(R.id.alzheimerInformationFragment); // Aquí navegamos a AlzheimerInformationFragment
                return true;
            }
            return false;
        });

        setSupportActionBar(binding.toolbar);

        // Configurar el Drawer con NavigationViewHelper
        NavigationViewHelper.configureDrawer(this, binding.drawerLayout, binding.toolbar);

        // Configurar el NavigationView
        NavigationViewHelper.configureNavigationView(this, binding.navigationView);
    }

    @Override
    public void inflateFragment(String fragmentTag) {
        NavController navController = Navigation.findNavController(this, R.id.content_carer);

        if (fragmentTag.equals(getString(R.string.manage))) {
            Intent intent = new Intent(MainCarer.this, PatientsList.class);
            startActivity(intent);
        } else if (fragmentTag.equals(getString(R.string.my_care))) {
            navController.navigate(R.id.careFragment);
        }
    }
}
