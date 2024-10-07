package com.uan.brainmher.domain.repositories;

import com.google.firebase.firestore.FirebaseFirestore;
import com.uan.brainmher.domain.entities.HealthcareProfessional;
import com.uan.brainmher.infraestructure.tools.Constants;

public class HealthcareProfessionalRepository {

    private FirebaseFirestore db;

    public HealthcareProfessionalRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void getHealthcareProfessional(String userId, OnHealthcareProfessionalLoadedListener listener) {
        db.collection(Constants.HealthcareProfessional)
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        HealthcareProfessional hp = documentSnapshot.toObject(HealthcareProfessional.class);
                        listener.onSuccess(hp);
                    } else {
                        listener.onFailure(new Exception("Healthcare Professional not found"));
                    }
                })
                .addOnFailureListener(listener::onFailure);
    }

    public interface OnHealthcareProfessionalLoadedListener {
        void onSuccess(HealthcareProfessional healthcareProfessional);
        void onFailure(Exception e);
    }
}
