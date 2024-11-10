package com.uan.brainmher.application.ui.fragments.patients;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.tabs.TabLayout;
import com.uan.brainmher.R;
import com.uan.brainmher.application.ui.fragments.cognitive_exercises_assignment.CognitiveChildFragment;
import com.uan.brainmher.application.ui.fragments.motor_exercises_assignment.MotorChildFragment;
import com.uan.brainmher.databinding.FragmentHomePatientBinding;

public class HomePatientFragment extends Fragment {
    private MotorChildFragment.MotorChildFragmentI motorChildFragmentI;
    private FragmentHomePatientBinding binding;

    public static HomePatientFragment newInstance(MotorChildFragment.MotorChildFragmentI motorChildFragmentI) {
        return new HomePatientFragment(motorChildFragmentI);
    }

    public HomePatientFragment(MotorChildFragment.MotorChildFragmentI motorChildFragmentI) {
        this.motorChildFragmentI = motorChildFragmentI;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout using View Binding
        binding = FragmentHomePatientBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buildStructure();
    }

    // Build the fragment structure
    private void buildStructure() {
        // Configuramos el adaptador del ViewPager
        PatientFragmentPageAdapter adapter = new PatientFragmentPageAdapter(getChildFragmentManager());
        binding.viewPagerHome.setAdapter(adapter);
        binding.viewPagerHome.setOffscreenPageLimit(adapter.getCount() - 1);
        binding.tabsHome.setupWithViewPager(binding.viewPagerHome);

        // Asignamos íconos para las pestañas
        binding.tabsHome.getTabAt(0).setIcon(R.drawable.ic_lightbulb_outline_black);
        binding.tabsHome.getTabAt(1).setIcon(R.drawable.ic_accessibility_black);
    }

    private class PatientFragmentPageAdapter extends FragmentPagerAdapter {

        public PatientFragmentPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // Configuración de fragmentos según la pestaña
            switch (position) {
                case 0:
                    return new CognitiveChildFragment();  // Ejercicios Cognitivos
                case 1:
                    return new MotorChildFragment(motorChildFragmentI);  // Ejercicios Motrices
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2; // Número de pestañas
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.tab_cognitive_exercises);
                case 1:
                    return getString(R.string.tab_motor_exercises);
                default:
                    return null;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Evitamos fugas de memoria
    }
}