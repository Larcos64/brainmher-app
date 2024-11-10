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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;
import com.uan.brainmher.R;
import com.uan.brainmher.application.ui.adapters.cognitive_exercises.CognitivesAdapter;
import com.uan.brainmher.databinding.FragmentPsTherapyCognitiveBinding;
import com.uan.brainmher.domain.entities.CognitiveExercises;
import com.uan.brainmher.domain.entities.Patient;
import com.uan.brainmher.domain.repositories.CognitiveExercisesRepository;

public class CognitiveTherapyPSFragment extends Fragment {

    private CognitivesAdapter cognitivesAdapter;
    private FragmentPsTherapyCognitiveBinding binding;
    private String uID;
    private Patient patient = new Patient();
    private final Bundle bundle;
    private final CognitiveExercisesRepository repository = new CognitiveExercisesRepository();

    public CognitiveTherapyPSFragment(Bundle bundle) {
        this.bundle = bundle;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPsTherapyCognitiveBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        SharedPreferences preferences = requireActivity().getPreferences(0);
        Gson gson = new Gson();
        String json = preferences.getString("serialipatient", "");
        patient = gson.fromJson(json, Patient.class);
        uID = patient != null ? patient.getPatientUID() : "";

        setupRecyclerView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (cognitivesAdapter != null) {
            cognitivesAdapter.startListening();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (cognitivesAdapter != null) {
            cognitivesAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (cognitivesAdapter != null) {
            cognitivesAdapter.stopListening();
        }
    }

    private void setupRecyclerView() {
        binding.listCognitives.setLayoutManager(new LinearLayoutManager(getContext()));
        Query query = repository.getCognitiveExercisesQuery();

        FirestoreRecyclerOptions<CognitiveExercises> options = new FirestoreRecyclerOptions.Builder<CognitiveExercises>()
                .setQuery(query, CognitiveExercises.class)
                .build();

        cognitivesAdapter = new CognitivesAdapter(options, getActivity(), uID);
        cognitivesAdapter.notifyDataSetChanged();
        binding.listCognitives.setAdapter(cognitivesAdapter);
    }
}