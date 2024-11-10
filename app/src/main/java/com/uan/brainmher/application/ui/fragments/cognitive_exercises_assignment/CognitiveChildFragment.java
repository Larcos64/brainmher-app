package com.uan.brainmher.application.ui.fragments.cognitive_exercises_assignment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;
import com.uan.brainmher.R;
import com.uan.brainmher.application.ui.adapters.cognitive_exercises.CognitiveExercisesAdapter;
import com.uan.brainmher.application.ui.interfaces.ICommunicateFragment;
import com.uan.brainmher.domain.entities.CognitiveExercisesAssignment;
import com.uan.brainmher.domain.repositories.CognitiveExercisesAssignmentRepository;

public class CognitiveChildFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private CognitiveExercisesAdapter cognitiveExcercisesAdapter;
    private RecyclerView listCognitiveExcercises;
    private ICommunicateFragment interfaceComunicateFragments;
    private View vista;
    private Activity activity;
    private CognitiveExercisesAdapter.ISelectionItem iSelectionItem;
    private final CognitiveExercisesAssignmentRepository repository = new CognitiveExercisesAssignmentRepository();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_cognitive_child, container, false);
        listCognitiveExcercises = vista.findViewById(R.id.list_cognitive_excercises);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        eventLogicSelectItem();
        fillRecycler();
        return vista;
    }

    private void eventLogicSelectItem() {
        iSelectionItem = () -> interfaceComunicateFragments.inicarJuego();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (cognitiveExcercisesAdapter != null) {
            cognitiveExcercisesAdapter.startListening();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (cognitiveExcercisesAdapter != null) {
            cognitiveExcercisesAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (cognitiveExcercisesAdapter != null) {
            cognitiveExcercisesAdapter.stopListening();
        }
    }

    private void fillRecycler() {
        listCognitiveExcercises.setLayoutManager(new LinearLayoutManager(getContext()));
        listCognitiveExcercises.setItemAnimator(null);

        Query query = repository.getCognitiveAssignmentsQuery(firebaseUser.getUid());
        FirestoreRecyclerOptions<CognitiveExercisesAssignment> options = new FirestoreRecyclerOptions.Builder<CognitiveExercisesAssignment>()
                .setQuery(query, CognitiveExercisesAssignment.class)
                .build();

        cognitiveExcercisesAdapter = new CognitiveExercisesAdapter(options, getActivity(), iSelectionItem);
        cognitiveExcercisesAdapter.notifyDataSetChanged();
        listCognitiveExcercises.setAdapter(cognitiveExcercisesAdapter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
            interfaceComunicateFragments = (ICommunicateFragment) activity;
        }
    }
}
