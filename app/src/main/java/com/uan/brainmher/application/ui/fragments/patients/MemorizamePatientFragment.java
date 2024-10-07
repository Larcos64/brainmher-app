package com.uan.brainmher.application.ui.fragments.patients;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.uan.brainmher.application.ui.adapters.patient.PatientFragmentPageAdapter;

import com.google.android.material.tabs.TabLayoutMediator;
import com.uan.brainmher.R;
import com.uan.brainmher.databinding.FragmentPatientMemorizameBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class MemorizamePatientFragment extends Fragment {

    public static MemorizamePatientFragment newInstance() {
        return new MemorizamePatientFragment();
    }

    public MemorizamePatientFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_patient_memorizame, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupTabLayout(view);
    }

    private void setupTabLayout(View view) {
        ViewPager2 viewPager = view.findViewById(R.id.view_pager_memorizame);
        PatientFragmentPageAdapter adapter = new PatientFragmentPageAdapter(requireActivity());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = view.findViewById(R.id.tabs_memorizame);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Family");
                    break;
                case 1:
                    tab.setText("Home");
                    break;
                case 2:
                    tab.setText("Places");
                    break;
            }
        }).attach();
    }
}