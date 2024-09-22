package com.uan.brainmher.domain.repositories;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uan.brainmher.application.ui.adapters.patient.PatientsAdapter;
import com.uan.brainmher.domain.entities.Patient;
import com.uan.brainmher.infraestructure.tools.Constants;

public class PatientsRepository {

    private FirebaseFirestore db;
    private FirebaseStorage firebaseStorage;

    public PatientsRepository() {
        db = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
    }

    // Consultar la lista de pacientes asignados a un cuidador
    public FirestoreRecyclerOptions<Patient> getPatientsByCarer(String uid) {
        Query query = db.collection(Constants.Patients)
                .whereArrayContains("assigns", uid)
                .orderBy("firstName");

        return new FirestoreRecyclerOptions.Builder<Patient>()
                .setQuery(query, Patient.class)
                .build();
    }

    // Eliminar un paciente por su UID
    public void deletePatient(String patientUID, OnPatientDeletedListener listener) {
        db.collection(Constants.Patients).document(patientUID).delete()
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e));
    }

    // Eliminar la imagen del paciente en Firebase Storage
    public void deletePatientImage(String uidPatient) {
        StorageReference deleteImage = firebaseStorage.getReference()
                .child("Users/Patients/" + uidPatient + ".jpg");

        deleteImage.delete().addOnSuccessListener(aVoid -> {
            Log.d("PatientsRepository", "Patient image deleted successfully");
        }).addOnFailureListener(e -> {
            Log.e("PatientsRepository", "Failed to delete patient image", e);
        });
    }

    // Listener para manejar el resultado de la eliminación del paciente
    public interface OnPatientDeletedListener {
        void onSuccess();
        void onFailure(Exception e);
    }
}