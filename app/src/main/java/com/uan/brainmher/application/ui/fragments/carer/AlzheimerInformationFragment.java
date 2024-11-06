package com.uan.brainmher.application.ui.fragments.carer;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayoutMediator;
import com.uan.brainmher.R;
import com.uan.brainmher.application.ui.adapters.carer.AlzheiherInformationAdapter;
//import com.uan.brainmher.application.ui.fragments.GeneralInformationFragment;
//import com.uan.brainmher.application.ui.fragments.PhasesEAFragment;
import com.uan.brainmher.application.ui.interfaces.IMainCarer;
import com.uan.brainmher.databinding.FragmentAlzheimerInformationBinding;

public class AlzheimerInformationFragment extends Fragment {

    private FragmentAlzheimerInformationBinding binding;
    private IMainCarer mIMainCarer;

    public AlzheimerInformationFragment() {
        // Constructor vacío
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAlzheimerInformationBinding.inflate(inflater, container, false);
        setupTabLayoutAndViewPager();
        return binding.getRoot();
    }

    private void setupTabLayoutAndViewPager() {
        AlzheiherInformationAdapter adapter = new AlzheiherInformationAdapter(this);
        binding.viewpager.setAdapter(adapter);

        new TabLayoutMediator(binding.tablayout, binding.viewpager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.general_information);
                    tab.setIcon(R.drawable.question_answer);
                    break;
                case 1:
                    tab.setText(R.string.phases_ea);
                    tab.setIcon(R.drawable.ic_description);
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
        binding = null; // Liberar binding para evitar fugas de memoria
    }
}