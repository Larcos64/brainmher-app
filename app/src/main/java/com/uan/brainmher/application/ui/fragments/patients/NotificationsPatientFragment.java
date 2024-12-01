package com.uan.brainmher.application.ui.fragments.patients;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.uan.brainmher.R;
import com.uan.brainmher.databinding.FragmentHomePatientBinding;
import com.uan.brainmher.databinding.FragmentNotificationsPatientBinding;

public class NotificationsPatientFragment extends Fragment {

    private FragmentNotificationsPatientBinding binding;

    public static Fragment newInstance() {
        return new NotificationsPatientFragment();
    }

    public NotificationsPatientFragment() {
        // Constructor público requerido
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsPatientBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buildStructure(view);
    }

    private void buildStructure(View view) {
        ViewPager2 viewPager = view.findViewById(R.id.view_pager_notifications);

        PatientFragmentPageAdapter adapter = new PatientFragmentPageAdapter(this);
        viewPager.setAdapter(adapter);

        Log.d("ENTRA viewPager", viewPager.toString());

        TabLayout tabLayout = view.findViewById(R.id.tabs_notifications);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    Log.d("ENTRA tab", tab.toString());

                    tab.setText(getString(R.string.tab_medicaments));
                    tab.setIcon(R.drawable.ic_local_pharmacy_black);
                    break;
                case 1:
                    tab.setText(getString(R.string.tab_therapies));
                    tab.setIcon(R.drawable.ic_group_work_black);
                    break;
            }
        }).attach();

        // Agregar badges a las pestañas
        BadgeDrawable badgeMedicaments = tabLayout.getTabAt(0).getOrCreateBadge();
        badgeMedicaments.setVisible(true);
        badgeMedicaments.setNumber(1);

        BadgeDrawable badgeTherapies = tabLayout.getTabAt(1).getOrCreateBadge();
        badgeTherapies.setVisible(true);
        badgeTherapies.setNumber(1);
    }


    private class PatientFragmentPageAdapter extends FragmentStateAdapter {

        public PatientFragmentPageAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new MedicamentsChildFragment();
                case 1:
                    return new TherapiesChildFragment();
                default:
                    throw new IllegalStateException("Invalid position");
            }
        }

        @Override
        public int getItemCount() {
            return 2; // Número de pestañas
        }
    }
}