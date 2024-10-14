package com.uan.brainmher.application.ui.fragments.carer;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.uan.brainmher.R;
import com.uan.brainmher.application.ui.adapters.carer.CarerCareFragmentPageAdapter;
import com.uan.brainmher.application.ui.adapters.patient.MemorizamePatientFragmentPageAdapter;
import com.uan.brainmher.application.ui.fragments.motor_exercises_assignment.MotorChildFragment;
import com.uan.brainmher.application.ui.interfaces.IMainCarer;
import com.uan.brainmher.databinding.FragmentCarerCareBinding;

public class CareFragment extends Fragment {

    private FragmentCarerCareBinding binding;
    private IMainCarer mIMainCarer;
    private MotorChildFragment.MotorChildFragmentI motorChildFragmentI;

    // Constructor vacío necesario para el NavController
    public CareFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_carer_care, container, false);  // Inflar directamente el layout
        setupTabLayout(view);  // Pasar la vista inflada
        return view;
    }

    private void setupTabLayout(View view) {
        ViewPager2 viewPager = view.findViewById(R.id.viewpagerh);
        TabLayout tabLayout = view.findViewById(R.id.tabs_care);

        CarerCareFragmentPageAdapter adapter = new CarerCareFragmentPageAdapter(requireActivity());
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setIcon(R.drawable.ic_accessibility_black);
                    tab.setText(R.string.exercises);
                    break;
                case 1:
                    tab.setIcon(R.drawable.ic_info);
                    tab.setText(R.string.info);
                    break;
                case 2:
                    tab.setIcon(R.drawable.ic_description);
                    tab.setText(R.string.support);
                    break;
            }
        }).attach();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IMainCarer) {
            mIMainCarer = (IMainCarer) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement IMainCarer");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;  // Evita fugas de memoria
    }
}