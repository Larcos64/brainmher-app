package com.uan.brainmher.application.ui.fragments.health_professional;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.uan.brainmher.databinding.FragmentCuMemorizameBinding;
import com.uan.brainmher.domain.entities.Patient;

public class MemorizameFragment extends Fragment {

    private FragmentCuMemorizameBinding binding;
    private Patient patientSendFragment = new Patient();
    private Bundle args;

    public MemorizameFragment(Bundle args) {
        this.args = args;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCuMemorizameBinding.inflate(inflater, container, false);

        args = getArguments();
        if (args != null) {
            patientSendFragment = (Patient) args.getSerializable("patient");
        }

        // Configurar listeners para los CardViews usando ViewBinding
        binding.cvFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFragment(1);
            }
        });

        binding.cvPets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFragment(2);
            }
        });

        binding.cvHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFragment(3);
            }
        });

        binding.cvPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFragment(4);
            }
        });

        return binding.getRoot(); // Devuelve la vista raíz inflada por ViewBinding
    }

    // Método para abrir el fragmento adecuado
    private void openFragment(int fragmentType) {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        /*
        Fragment change = new MemorizameFamilyFragment(fragmentType);
        change.setArguments(args);
        transaction.replace(binding.getRoot().getId(), change).addToBackStack(null).commit();
        */
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Liberar el binding cuando la vista se destruye
    }
}