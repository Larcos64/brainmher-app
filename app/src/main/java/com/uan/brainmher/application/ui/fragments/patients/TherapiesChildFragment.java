package com.uan.brainmher.application.ui.fragments.patients;

import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.uan.brainmher.R;
import com.uan.brainmher.databinding.FragmentTherapiesChildBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class TherapiesChildFragment extends Fragment {

    private FragmentTherapiesChildBinding binding;

    public TherapiesChildFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment using View Binding
        binding = FragmentTherapiesChildBinding.inflate(inflater, container, false);

        // Load image with Glide
        Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/brainmher-de54d.appspot.com/o/Excercises%2FCognitive%2Fmemorama.png?alt=media&token=2cb66808-5b0f-4255-9859-6b9ecf650afa")
                .fitCenter()
                .into(binding.ivTherapyMiniature);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set click listener for the button
        binding.btnShowHideTherapy.setOnClickListener(v -> toggleExpandableView());
    }

    private void toggleExpandableView() {
        if (binding.expandableTherapyView.getVisibility() == View.GONE) {
            TransitionManager.beginDelayedTransition(binding.cardActivityTherapy, new AutoTransition());
            binding.expandableTherapyView.setVisibility(View.VISIBLE);
            binding.btnShowHideTherapy.setText(R.string.btn_hide_info);
            binding.btnShowHideTherapy.setWidth(240);
        } else {
            TransitionManager.beginDelayedTransition(binding.cardActivityTherapy, new AutoTransition());
            binding.expandableTherapyView.setVisibility(View.GONE);
            binding.btnShowHideTherapy.setText(R.string.btn_show_info);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}