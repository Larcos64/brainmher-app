package com.uan.brainmher.domain.repositories;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uan.brainmher.domain.entities.MedicationAssignment;
import com.uan.brainmher.domain.entities.Patient;
import com.uan.brainmher.infraestructure.tools.Constants;

import java.util.UUID;

public class NotificationRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    public interface ProgressCallback {
        void onStart();
        void onComplete();
    }

    public FirestoreRecyclerOptions<MedicationAssignment> getMedicationOptions(String patientUID) {
        Query query = db.collection(Constants.Medicines)
                .document(patientUID)
                .collection(Constants.Medicine);

        return new FirestoreRecyclerOptions.Builder<MedicationAssignment>()
                .setQuery(query, MedicationAssignment.class)
                .build();
    }

    public void createMedication(Patient patient, MedicationAssignment medication, Uri imageUri,
                                 ProgressCallback progressCallback, OnSuccessListener<Void> onSuccessListener) {

        progressCallback.onStart();

        String uuid = UUID.randomUUID().toString();
        medication.setMedicamentUID(uuid);

        StorageReference imgRef = storageReference.child(Constants.Medicines + "/" + uuid + ".jpg");
        imgRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    medication.setUriImg(uri.toString());
                    db.collection(Constants.Medicines)
                            .document(patient.getPatientUID())
                            .collection(Constants.Medicine)
                            .document(uuid)
                            .set(medication)
                            .addOnSuccessListener(aVoid -> {
                                progressCallback.onComplete();
                                onSuccessListener.onSuccess(aVoid);
                            });
                })
        );
    }

    public void updateMedication(Patient patient, MedicationAssignment medication, Uri newImageUri,
                                 ProgressCallback progressCallback, OnSuccessListener<Void> onSuccessListener) {
        progressCallback.onStart();

        if (newImageUri != null) {
            // Subir nueva imagen si fue seleccionada
            String imgPath = Constants.Medicines + "/" + medication.getMedicamentUID() + ".jpg";
            StorageReference imgRef = storageReference.child(imgPath);
            imgRef.putFile(newImageUri).addOnSuccessListener(taskSnapshot ->
                    imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        medication.setUriImg(uri.toString());
                        saveMedicationToFirestore(patient, medication, progressCallback, onSuccessListener);
                    })
            );
        } else {
            // No se seleccionó nueva imagen, actualizar solo los datos
            saveMedicationToFirestore(patient, medication, progressCallback, onSuccessListener);
        }
    }

    private void saveMedicationToFirestore(Patient patient, MedicationAssignment medication,
                                           ProgressCallback progressCallback, OnSuccessListener<Void> onSuccessListener) {
        db.collection(Constants.Medicines)
                .document(patient.getPatientUID())
                .collection(Constants.Medicine)
                .document(medication.getMedicamentUID())
                .set(medication)
                .addOnSuccessListener(aVoid -> {
                    progressCallback.onComplete();
                    onSuccessListener.onSuccess(aVoid);
                });
    }
}