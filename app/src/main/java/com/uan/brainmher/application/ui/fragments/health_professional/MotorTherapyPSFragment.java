package com.uan.brainmher.application.ui.fragments.health_professional;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;
import com.uan.brainmher.application.ui.adapters.motor_excercises.MotorAdapter;
import com.uan.brainmher.domain.entities.MotorExercises;
import com.uan.brainmher.domain.entities.Patient;
import com.uan.brainmher.databinding.FragmentPsTherapyMotorBinding;
import com.uan.brainmher.domain.repositories.MotorExcercisesRepository;

public class MotorTherapyPSFragment extends Fragment {

    private MotorAdapter motorAdapter;
    private FragmentPsTherapyMotorBinding binding;
    private String uID;
    private Patient patient = new Patient();
    private final Bundle bundle;
    private MotorExcercisesRepository repository;

    public MotorTherapyPSFragment(Bundle bundle) {
        this.bundle = bundle;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPsTherapyMotorBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        SharedPreferences preferences = requireActivity().getPreferences(0);
        Gson gson = new Gson();
        String json = preferences.getString("serialipatient", "");
        patient = gson.fromJson(json, Patient.class);
        uID = patient.getPatientUID();

        repository = new MotorExcercisesRepository(requireContext());

        fillRecycler();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        motorAdapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        motorAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        motorAdapter.stopListening();
    }

    private void fillRecycler() {
        binding.listMotor.setLayoutManager(new LinearLayoutManager(getContext()));

        Query query = repository.getMotorExercisesQuery();
        FirestoreRecyclerOptions<MotorExercises> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<MotorExercises>()
                .setQuery(query, MotorExercises.class).build();

        motorAdapter = new MotorAdapter(firestoreRecyclerOptions, getActivity(), uID);
        motorAdapter.notifyDataSetChanged();
        binding.listMotor.setAdapter(motorAdapter);
    }
}