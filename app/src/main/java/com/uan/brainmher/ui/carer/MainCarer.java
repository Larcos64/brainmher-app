package com.uan.brainmher.ui.carer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.uan.brainmher.R;
import com.uan.brainmher.databinding.ActivityMainCarerBinding;
import com.uan.brainmher.interfaces.IMainCarer;

public class MainCarer extends AppCompatActivity implements IMainCarer {

    private ActivityMainCarerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using View Binding
        binding = ActivityMainCarerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Function to read the items of BottomNavigation
        BottomNavigationView bottomnNavigationView = findViewById(R.id.navigation_carer);
        NavController navController = Navigation.findNavController(this, R.id.content_carer);
        NavigationUI.setupWithNavController(bottomnNavigationView, navController);
    }

    @Override
    public void inflateFragment(String fragmentTag) {
        NavController navController = Navigation.findNavController(this, R.id.content_carer);
        //navController.navigate(R.id.action_homeFragment_to_otherFragment); // Ajustar fragmentos y navegación.
    }
}
