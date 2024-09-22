package com.uan.brainmher.application.ui.fragments.patients;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uan.brainmher.R;
import com.uan.brainmher.application.ui.adapters.patient.PatientsAdapter;
import com.uan.brainmher.domain.entities.Carer;
import com.uan.brainmher.domain.entities.HealthcareProfessional;
import com.uan.brainmher.domain.entities.Patient;
import com.uan.brainmher.databinding.FragmentPatientsBinding;
import com.uan.brainmher.infraestructure.database.LoginManager;
import com.uan.brainmher.infraestructure.tools.CircularProgressUtil;
import com.uan.brainmher.infraestructure.tools.Constants;
import com.uan.brainmher.application.ui.activities.health_professional.HealthProfessionalActivity;

public class PatientsListFragment extends Fragment {

    //region Variables
    RecyclerView recyclerView;
    private FragmentPatientsBinding binding;
    private PatientsAdapter adapter;
    private PatientsAdapter.ISelectionPatient iSelectionPatient;
    private PatientsAdapter.IDeletePatient iDeletePatient;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String uIdLoggedIn, uidPatient;
    private HealthcareProfessional health_professional = new HealthcareProfessional();
    private Carer carer = new Carer();
    private CircularProgressUtil circularProgressUtil;
    private LoginManager loginManager;
    //endregion

    public PatientsListFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentPatientsBinding.inflate(inflater, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        uIdLoggedIn = user.getUid();
        db = FirebaseFirestore.getInstance();
        loginManager = new LoginManager();

        circularProgressUtil = new CircularProgressUtil(getActivity());

        initializeUI();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void initializeUI() {
        eventSelectedItem();
        eventDeleteItem();
        initRecyclerView();
    }

    //region Events Onclick
    private void eventSelectedItem() {
        iSelectionPatient = patient -> {
            Intent goPatient = new Intent(getActivity(), HealthProfessionalActivity.class);
            Bundle patientSend = new Bundle();
            patientSend.putSerializable("patient", patient);
            goPatient.putExtras(patientSend);
            startActivity(goPatient);
        };
    }

    private void eventDeleteItem() {
        iDeletePatient = patient -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.BackgroundRounded);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Log.d("PatientsListFragment", "DialogIF");
                showDeleteDialogLollipop(patient, builder);
            } else {
                Log.d("PatientsListFragment", "DialogELSE");
                showDeleteDialogPreLollipop(patient, builder);
            }
        };
    }

    private void showDeleteDialogLollipop(final Patient patient, AlertDialog.Builder builder) {
        try {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_one_textview_two_buttons, null);
            builder.setView(dialogView);
            AlertDialog alertDialog = builder.create();

            Button btnNo = dialogView.findViewById(R.id.btn1);
            Button btnYes = dialogView.findViewById(R.id.btn2);
            TextView tvInformation = dialogView.findViewById(R.id.textView);

            btnNo.setText(R.string.no);
            btnNo.setOnClickListener(view -> alertDialog.dismiss());

            btnYes.setText(R.string.yes);
            btnYes.setOnClickListener(view -> deletePatient(patient, alertDialog));

            String namePatient = patient.getFirstName() + " " + patient.getLastName();
            tvInformation.setText(getString(R.string.message_delete, namePatient));
            alertDialog.show();

        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    private void showDeleteDialogPreLollipop(final Patient patient, AlertDialog.Builder builder) {
        builder.setTitle(getString(R.string.alert));
        builder.setMessage(getString(R.string.message_delete, patient.getFirstName()));
        builder.setPositiveButton(getString(R.string.delete), (dialog, which) -> deletePatient(patient, null));
        builder.show();
    }

    private void deletePatient(final Patient patient, AlertDialog alertDialog) {
        circularProgressUtil.showProgress(getString(R.string.deleting));

        db.collection(Constants.Patients).document(patient.getPatientUID()).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), getResources().getString(R.string.record_deleted), Toast.LENGTH_SHORT).show();
                    deletePatientData(patient.getPatientUID(), alertDialog);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), getResources().getString(R.string.elimination_failed), Toast.LENGTH_SHORT).show();
                    circularProgressUtil.hideProgress();
                });
    }

    private void deletePatientData(String uidPatient, AlertDialog alertDialog) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference deleteImage = storageReference.child("Users/Patients/" + uidPatient + ".jpg");

        deleteImage.delete().addOnSuccessListener(aVoid -> {
            // Imagen eliminada
        });

        // Cierra el diálogo si estaba abierto
        if (alertDialog != null) {
            alertDialog.dismiss();
        }

        circularProgressUtil.hideProgress();
    }

    private void initRecyclerView() {
        String uid = user.getUid();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Log.d("PatientListFragment UID", uid);

        Query query = db.collection(Constants.Patients)
                .whereArrayContains("assigns", uid)
                .orderBy("firstName");

        FirestoreRecyclerOptions<Patient> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Patient>()
                .setQuery(query, Patient.class).build();

        adapter = new PatientsAdapter(firestoreRecyclerOptions, getActivity(), iSelectionPatient, iDeletePatient);
        binding.recyclerView.setAdapter(adapter);

        adapter.startListening();
    }
}