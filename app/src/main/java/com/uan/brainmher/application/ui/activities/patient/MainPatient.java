package com.uan.brainmher.application.ui.activities.patient;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.uan.brainmher.R;
import com.uan.brainmher.application.ui.activities.Games;
import com.uan.brainmher.application.ui.adapters.patient.PatientFragmentPageAdapter;
import com.uan.brainmher.application.ui.helpers.CustomViewPager;
import com.uan.brainmher.application.ui.helpers.NavigationViewHelper;
import com.uan.brainmher.application.ui.interfaces.ICommunicateFragment;
import com.uan.brainmher.databinding.ActivityMainPatientBinding;

import java.util.Timer;
import java.util.TimerTask;

public class MainPatient extends AppCompatActivity implements ICommunicateFragment {
    private ActivityMainPatientBinding binding;
    private int[] galeria = {R.drawable.motivational_1, R.drawable.motivational_2, R.drawable.motivational_3,
            R.drawable.motivational_4, R.drawable.motivational_5, R.drawable.motivational_6, R.drawable.motivational_7,
            R.drawable.motivational_8, R.drawable.motivational_9, R.drawable.motivational_10, R.drawable.motivational_11,
            R.drawable.motivational_12, R.drawable.motivational_13, R.drawable.motivational_14, R.drawable.motivational_15,
            R.drawable.motivational_16, R.drawable.motivational_17, R.drawable.motivational_18, R.drawable.motivational_19,
            R.drawable.motivational_20, R.drawable.motivational_21, R.drawable.motivational_22, R.drawable.motivational_23};
    private int position;
    private static final int DURATION = 9000;
    private Timer timer = null;
    private PatientFragmentPageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using View Binding
        binding = ActivityMainPatientBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        // Setup Drawer
        NavigationViewHelper.configureDrawer(this, binding.drawerLayout, binding.toolbar);
        NavigationViewHelper.configureNavigationView(this, binding.navigationView);

        // Setup ViewPager2 and Adapter
        adapter = new PatientFragmentPageAdapter(this);
        binding.viewPager.getViewPager().setAdapter(adapter);
        binding.viewPager.getViewPager().setOffscreenPageLimit(3);

        // Set navigation item selected listener
        binding.navigationPatient.setOnItemSelectedListener(mOnItemSelectedListener);

        // Slider setup
        setupImageSlider();

        // Start slider
        startSlider();
    }

    // Listener for bottom navigation items
    private BottomNavigationView.OnItemSelectedListener mOnItemSelectedListener = new BottomNavigationView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int itemId = item.getItemId();

            if (itemId == R.id.home_patient) {
                binding.viewPager.getViewPager().setCurrentItem(0);
                return true;
            } else if (itemId == R.id.memorizame_patient) {
                binding.viewPager.getViewPager().setCurrentItem(1);
                return true;
            } else if (itemId == R.id.notifications_patient) {
                binding.viewPager.getViewPager().setCurrentItem(2);
                return true;
            }

            return false;
        }
    };

    private void setupImageSlider() {
        binding.ivMotivational.setFactory(new ViewSwitcher.ViewFactory() {
            public View makeView() {
                ImageView imageView = new ImageView(MainPatient.this);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                return imageView;
            }
        });

        Animation fadeIn = AnimationUtils.loadAnimation(MainPatient.this, R.anim.fade_in);
        Animation fadeOut = AnimationUtils.loadAnimation(MainPatient.this, R.anim.fade_out);
        binding.ivMotivational.setInAnimation(fadeIn);
        binding.ivMotivational.setOutAnimation(fadeOut);
    }

    private void startSlider() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        binding.ivMotivational.setImageResource(galeria[position]);
                        position++;
                        if (position == galeria.length)
                            position = 0;
                    }
                });
            }
        }, 0, DURATION);
    }

    @Override
    public void inicarJuego() {
        Intent pasar = new Intent(MainPatient.this, Games.class);
        pasar.putExtra("Game", "Memorama");
        startActivity(pasar);
    }
}