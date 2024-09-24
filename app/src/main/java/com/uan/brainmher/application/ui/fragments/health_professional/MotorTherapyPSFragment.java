package com.uan.brainmher.application.ui.fragments.health_professional;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
// import com.uan.brainmher.adapters.CognitivesAdapter;
// import com.uan.brainmher.data.CognitivesExcercises;
// import com.uan.brainmher.data.Patient;
import com.uan.brainmher.databinding.FragmentPsTherapyMotorBinding;

public class MotorTherapyPSFragment extends Fragment {
    private FragmentPsTherapyMotorBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public MotorTherapyPSFragment() {
        // Constructor vacío necesario
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inicializamos ViewBinding
        binding = FragmentPsTherapyMotorBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Liberar el binding para evitar fugas de memoria
        binding = null;
    }
}