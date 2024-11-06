package com.uan.brainmher.domain.repositories;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.uan.brainmher.domain.entities.AlzheimerInformation;

import java.util.ArrayList;
import java.util.List;

public class AlzheimerInformationRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface OnAlzheimerInformationLoaded {
        void onSuccess(List<AlzheimerInformation> informationList);
        void onFailure(Exception e);
    }

    public void getAlzheimerInformation(String type, OnAlzheimerInformationLoaded callback) {
        db.collection("AlzheimerInformation")
                .whereEqualTo("type", type)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<AlzheimerInformation> informationList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        AlzheimerInformation info = document.toObject(AlzheimerInformation.class);
                        informationList.add(info);
                    }
                    callback.onSuccess(informationList);
                })
                .addOnFailureListener(callback::onFailure);
    }
}
