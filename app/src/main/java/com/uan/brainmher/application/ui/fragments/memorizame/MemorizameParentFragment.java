package com.uan.brainmher.application.ui.fragments.memorizame;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.gson.Gson;
import com.uan.brainmher.databinding.FragmentMemorizameParentBinding;
import com.uan.brainmher.domain.entities.Patient;
import com.uan.brainmher.application.ui.interfaces.IMainCarer;

public class MemorizameParentFragment extends Fragment implements IMainCarer {

    private Bundle args;
    private FragmentMemorizameParentBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        args = getArguments();
        Patient patientSendFragment;
        args = getActivity().getIntent().getExtras();
        if (args != null) {
            patientSendFragment = (Patient) args.getSerializable("patient");
            args.putSerializable("patient", patientSendFragment);

            SharedPreferences preferences = getActivity().getPreferences(0);
            SharedPreferences.Editor editor = preferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(patientSendFragment);
            editor.putString("serialipatient", json);
            editor.apply(); // Uso de apply() en lugar de commit() por ser más eficiente
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMemorizameParentBinding.inflate(inflater, container, false);

        // Iniciar fragmento MemorizameFragment
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment change = new MemorizameFragment(args);
        change.setArguments(args);
        transaction.replace(binding.containerMemorizameParent.getId(), change).commit();

        return binding.getRoot(); // Devuelve la vista raíz inflada por ViewBinding
    }

    @Override
    public void inflateFragment(String fragmentTag) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        Fragment change;
        if (fragmentTag.equals("memorizamepru")) {
            change = new MemorizameFragment(args);
            transaction.replace(binding.containerMemorizameParent.getId(), change).commit();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Liberar el binding cuando la vista se destruye
    }
}
