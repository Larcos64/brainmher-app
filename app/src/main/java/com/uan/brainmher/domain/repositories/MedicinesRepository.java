package com.uan.brainmher.domain.repositories;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uan.brainmher.infraestructure.tools.Constants;

public class MedicinesRepository {

    private final FirebaseFirestore db;
    private final StorageReference storageReference;

    public MedicinesRepository() {
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    public void deleteMedication(String patientUID, String medicationUID, Runnable onSuccess, OnFailureListener onFailure) {
        // Eliminar la imagen asociada en Firebase Storage
        StorageReference deleteImage = storageReference.child(Constants.Medicines + "/" + medicationUID + ".jpg");

        deleteImage.delete()
                .addOnSuccessListener(aVoid -> {
                    // Eliminar el documento en Firestore
                    db.collection(Constants.Medicines)
                            .document(patientUID)
                            .collection(Constants.Medicine)
                            .document(medicationUID)
                            .delete()
                            .addOnSuccessListener(aVoid2 -> {
                                db.collection(Constants.Medicines).document(patientUID).delete();
                                onSuccess.run();
                            })
                            .addOnFailureListener(onFailure);
                })
                .addOnFailureListener(onFailure);
    }
}