package com.uan.brainmher.domain.repositories;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
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

    public interface StartCallback {
        void onStart();
    }

    public interface CompleteCallback {
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
                                 StartCallback onStart, CompleteCallback onComplete, OnFailureListener failureListener) {
        onStart.onStart();

        // Generar un UUID único para el medicamento
        String uuid = UUID.randomUUID().toString();
        medication.setMedicamentUID(uuid);

        if (imageUri != null) {
            // Si hay una imagen, subirla a Firebase Storage
            StorageReference imgRef = storageReference.child(Constants.Medicines + "/" + uuid + ".jpg");
            imgRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot ->
                            imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                medication.setUriImg(uri.toString());
                                saveMedicationToFirestore(patient, medication, onComplete, failureListener);
                            })
                    )
                    .addOnFailureListener(failureListener); // Error al subir la imagen
        } else {
            // Si no hay imagen, guardar el medicamento directamente en Firestore
            saveMedicationToFirestore(patient, medication, onComplete, failureListener);
        }
    }

    // Método auxiliar para guardar el medicamento en Firestore
    private void saveMedicationToFirestore(Patient patient, MedicationAssignment medication,
                                           CompleteCallback onComplete, OnFailureListener failureListener) {
        db.collection(Constants.Medicines)
                .document(patient.getPatientUID())
                .collection(Constants.Medicine)
                .document(medication.getMedicamentUID())
                .set(medication)
                .addOnSuccessListener(aVoid -> onComplete.onComplete()) // Operación completada
                .addOnFailureListener(failureListener); // Error al guardar en Firestore
    }

    public void updateMedication(Patient patient, MedicationAssignment medication, Uri newImageUri,
                                 StartCallback onStart, CompleteCallback onComplete, OnSuccessListener<Void> onSuccessListener) {

        onStart.onStart();

        if (newImageUri != null) {
            // Subir nueva imagen si fue seleccionada
            String imgPath = Constants.Medicines + "/" + medication.getMedicamentUID() + ".jpg";
            StorageReference imgRef = storageReference.child(imgPath);
            imgRef.putFile(newImageUri).addOnSuccessListener(taskSnapshot ->
                    imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        medication.setUriImg(uri.toString());
                        saveMedicationToFirestore(patient, medication, onComplete, onSuccessListener);
                    })
            );
        } else {
            // No se seleccionó nueva imagen, actualizar solo los datos
            saveMedicationToFirestore(patient, medication, onComplete, onSuccessListener);
        }
    }

    private void saveMedicationToFirestore(Patient patient, MedicationAssignment medication,
                                           CompleteCallback onComplete, OnSuccessListener<Void> onSuccessListener) {
        db.collection(Constants.Medicines)
                .document(patient.getPatientUID())
                .collection(Constants.Medicine)
                .document(medication.getMedicamentUID())
                .set(medication)
                .addOnSuccessListener(aVoid -> {
                    onComplete.onComplete();
                    onSuccessListener.onSuccess(aVoid);
                });
    }

    public void deleteMedication(String patientUID, String medicationUID, StartCallback onStart, CompleteCallback onComplete, OnFailureListener failureListener) {
        onStart.onStart();

        // Eliminar documento en Firestore
        db.collection(Constants.Medicines)
                .document(patientUID)
                .collection(Constants.Medicine)
                .document(medicationUID)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Al completar la eliminación del documento, eliminar la imagen
                    StorageReference deleteImage = storageReference.child(Constants.Medicines + "/" + medicationUID + ".jpg");
                    deleteImage.delete()
                            .addOnSuccessListener(aVoid2 -> {
                                // Imagen eliminada con éxito, llamar al callback de completado
                                onComplete.onComplete();
                            })
                            .addOnFailureListener(failureListener); // Error al eliminar la imagen
                })
                .addOnFailureListener(failureListener);
    }

}