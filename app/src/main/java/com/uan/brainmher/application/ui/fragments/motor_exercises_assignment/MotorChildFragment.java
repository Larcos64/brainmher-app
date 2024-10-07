package com.uan.brainmher.application.ui.fragments.motor_exercises_assignment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.uan.brainmher.application.ui.adapters.motor_exercises_assignment.MotorExercisesAdapter;
import com.uan.brainmher.domain.entities.MotorExcercisesAssignment;
import com.uan.brainmher.databinding.FragmentMotorChildBinding;
import com.uan.brainmher.infraestructure.tools.Constants;

public class MotorChildFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private MotorExercisesAdapter motorExercisesAdapter;
    private MotorChildFragmentI motorChildFragmentI;

    // View binding variable
    private FragmentMotorChildBinding binding;

    public MotorChildFragment() {
        // Required empty public constructor
    }

    public MotorChildFragment(MotorChildFragmentI motorChildFragmentI) {
        this.motorChildFragmentI = motorChildFragmentI;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout using View Binding
        binding = FragmentMotorChildBinding.inflate(inflater, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // Fill the recycler view
        fillRecycler();

        return binding.getRoot(); // Return the root view from binding
    }

    @Override
    public void onResume() {
        super.onResume();
        motorExercisesAdapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        motorExercisesAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        motorExercisesAdapter.stopListening();
    }

    private void fillRecycler() {
        binding.rvMotorExercises.setLayoutManager(new LinearLayoutManager(getContext()));
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query query = db.collection(Constants.MotorExercisesAssignments)
                .whereEqualTo("uidPatient", firebaseUser.getUid());

        FirestoreRecyclerOptions<MotorExcercisesAssignment> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<MotorExcercisesAssignment>()
                        .setQuery(query, MotorExcercisesAssignment.class).build();

        motorExercisesAdapter = new MotorExercisesAdapter(firestoreRecyclerOptions, getActivity(), motorChildFragmentI);
        motorExercisesAdapter.notifyDataSetChanged();
        binding.rvMotorExercises.setAdapter(motorExercisesAdapter);
    }

    public interface MotorChildFragmentI {
        void alert(String option, MotorExcercisesAssignment listExcercises);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks by setting the binding to null
    }
}
