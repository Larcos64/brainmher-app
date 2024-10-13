package com.uan.brainmher.application.ui.fragments.patients;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.uan.brainmher.application.ui.adapters.patient.PatientMemorizameAdapter;
import com.uan.brainmher.databinding.FragmentFamilyChildBinding;
import com.uan.brainmher.domain.entities.Memorizame;
import com.uan.brainmher.infraestructure.tools.Constants;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseChildFragment extends Fragment {

    private FragmentFamilyChildBinding binding;
    private PatientMemorizameAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private List<Memorizame> memorizameList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFamilyChildBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        initRecycler();
    }

    private void initRecycler() {
        binding.recyclerViewFamily.setLayoutManager(new GridLayoutManager(requireActivity(), 3));
        CollectionReference collectionReference = db.collection(Constants.Memorizame);

        collectionReference.document(user.getUid()).collection(getCollectionName()).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    memorizameList.clear();
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Memorizame memorizame = snapshot.toObject(Memorizame.class);
                        memorizameList.add(memorizame);
                    }
                    adapter = new PatientMemorizameAdapter(memorizameList, requireActivity(), this::onItemSelected);
                    binding.recyclerViewFamily.setAdapter(adapter);
                    binding.layoutDescriptionFamily.setVisibility(
                            memorizameList.isEmpty() ? View.VISIBLE : View.INVISIBLE);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al obtener datos", e));
    }

    protected abstract String getCollectionName();

    private void onItemSelected(Memorizame memorizame) {
        Toast.makeText(requireActivity(), "Seleccionado: " + memorizame.getQuestion(), Toast.LENGTH_SHORT).show();
        // Aquí puedes manejar la selección del ítem según sea necesario
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Evitar fugas de memoria
    }
}