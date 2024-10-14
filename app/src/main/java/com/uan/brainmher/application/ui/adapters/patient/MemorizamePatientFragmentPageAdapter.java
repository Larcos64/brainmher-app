package com.uan.brainmher.application.ui.adapters.patient;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.uan.brainmher.application.ui.fragments.patients.FamilyChildFragment;
import com.uan.brainmher.application.ui.fragments.patients.HomeChildFragment;
import com.uan.brainmher.application.ui.fragments.patients.PetsChildFragment;
import com.uan.brainmher.application.ui.fragments.patients.PlacesChildFragment;

/**
 * Adaptador para gestionar las pestañas de los fragmentos: Family, Home, Places y Pets.
 */
public class MemorizamePatientFragmentPageAdapter extends FragmentStateAdapter {

    public MemorizamePatientFragmentPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FamilyChildFragment();
            case 1:
                return new HomeChildFragment();
            case 2:
                return new PlacesChildFragment();
            case 3:
                return new PetsChildFragment();
            default:
                return new Fragment(); // Fallback fragment por defecto
        }
    }

    @Override
    public int getItemCount() {
        return 4; // Número total de fragmentos
    }
}