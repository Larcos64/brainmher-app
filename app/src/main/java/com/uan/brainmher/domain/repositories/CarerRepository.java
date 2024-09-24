package com.uan.brainmher.domain.repositories;

import com.google.firebase.firestore.FirebaseFirestore;
import com.uan.brainmher.domain.entities.Carer;
import com.uan.brainmher.infraestructure.tools.Constants;

public class CarerRepository {

    private FirebaseFirestore db;

    public CarerRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void getCarer(String userId, OnCarerLoadedListener listener) {
        db.collection(Constants.Carers)
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Carer carer = documentSnapshot.toObject(Carer.class);
                        listener.onSuccess(carer);
                    } else {
                        listener.onFailure(new Exception("Carer not found"));
                    }
                })
                .addOnFailureListener(listener::onFailure);
    }

    public interface OnCarerLoadedListener {
        void onSuccess(Carer carer);
        void onFailure(Exception e);
    }
}
