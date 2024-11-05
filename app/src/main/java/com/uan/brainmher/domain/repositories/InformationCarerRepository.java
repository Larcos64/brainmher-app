package com.uan.brainmher.domain.repositories;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.uan.brainmher.domain.entities.InformationCarer;

import java.util.ArrayList;
import java.util.List;

public class InformationCarerRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface OnInformationCarerLoaded {
        void onSuccess(List<InformationCarer> informationList);
        void onFailure(Exception e);
    }

    public void getInformationCarer(String type, OnInformationCarerLoaded callback) {
        db.collection("InformationCarer")
                .whereEqualTo("type", type)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<InformationCarer> informationList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        InformationCarer info = document.toObject(InformationCarer.class);
                        informationList.add(info);
                    }
                    callback.onSuccess(informationList);
                })
                .addOnFailureListener(callback::onFailure);
    }
}