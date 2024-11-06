package com.uan.brainmher.application.ui.adapters.carer;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

//import com.uan.brainmher.application.ui.fragments.PhasesEAFragment;
import com.uan.brainmher.application.ui.fragments.carer.GeneralInformationFragment;

public class AlzheiherInformationAdapter extends FragmentStateAdapter {

    public AlzheiherInformationAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new GeneralInformationFragment();
            case 1:
                //return new PhasesEAFragment();
            default:
                throw new IllegalArgumentException("Invalid tab position");
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Número total de tabs
    }
}