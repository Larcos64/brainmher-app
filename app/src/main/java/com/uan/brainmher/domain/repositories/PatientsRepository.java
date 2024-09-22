package com.uan.brainmher.domain.repositories;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private FirebaseAuth firebaseAuth;

    public PatientsRepository() {
        db = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void createPatient(Patient patient, Uri uriImage, OnPatientCreatedListener listener) {
        firebaseAuth.createUserWithEmailAndPassword(patient.getEmail(), patient.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser newUser = task.getResult().getUser();
                        String uIDPatient = newUser.getUid();
                        patient.setPatientUID(uIDPatient);

                        if (uriImage != null) {
                            uploadPatientImage(patient, uriImage, listener);
                        } else {
                            savePatientData(patient, listener);
                        }
                    } else {
                        listener.onFailure(task.getException());
                    }
                });
    }

    private void uploadPatientImage(Patient patient, Uri uriImage, OnPatientCreatedListener listener) {
        StorageReference imgRef = firebaseStorage.getReference().child("Users/Patients/" + patient.getPatientUID() + ".jpg");
        imgRef.putFile(uriImage)
                .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl()
                        .addOnSuccessListener(downloadUri -> {
                            patient.setUriImg(downloadUri.toString());
                            savePatientData(patient, listener);
                        })
                        .addOnFailureListener(listener::onFailure))
                .addOnFailureListener(listener::onFailure);
    }

    private void savePatientData(Patient patient, OnPatientCreatedListener listener) {
        db.collection(Constants.Patients).document(patient.getPatientUID())
                .set(patient)
                .addOnSuccessListener(aVoid -> listener.onSuccess(patient))
                .addOnFailureListener(listener::onFailure);
    }

    public interface OnPatientCreatedListener {
        void onSuccess(Patient patient);
        void onFailure(Exception e);
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