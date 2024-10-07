package com.uan.brainmher.application.ui.fragments.cognitive_exercises_assignment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.uan.brainmher.databinding.FragmentCognitiveChildBinding;

public class CognitiveChildFragment extends Fragment {

    private FragmentCognitiveChildBinding binding;

    public static CognitiveChildFragment newInstance() {
        return new CognitiveChildFragment();
    }

    public CognitiveChildFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout using View Binding
        binding = FragmentCognitiveChildBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Aquí no se añade lógica adicional, ya que el fragmento debe estar vacío
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Evita fugas de memoria
    }
}
