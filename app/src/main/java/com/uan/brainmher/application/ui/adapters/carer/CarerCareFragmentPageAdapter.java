package com.uan.brainmher.application.ui.adapters.carer;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.uan.brainmher.application.ui.fragments.carer.ExerciseCarerFragment;
import com.uan.brainmher.application.ui.fragments.carer.InformationCarerFragment;

public class CarerCareFragmentPageAdapter extends FragmentStateAdapter {

    public CarerCareFragmentPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ExerciseCarerFragment();  // motorChildFragmentI passed if needed
            case 1:
                return new InformationCarerFragment();
            case 2:
                //return new WarningCarerFragment();
            default:
                throw new IllegalArgumentException("Invalid item index.");
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Número total de fragmentos
    }
}
