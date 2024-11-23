package com.uan.brainmher.domain.repositories;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.uan.brainmher.domain.entities.CarerInformation;

import java.util.ArrayList;
import java.util.List;

public class InformationCarerRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface OnInformationCarerLoaded {
        void onSuccess(List<CarerInformation> informationList);
        void onFailure(Exception e);
    }

    public void getCarerInformation(String type, OnInformationCarerLoaded callback) {
        db.collection("CarerInformation")
                .whereEqualTo("type", type)
                .orderBy("order") // Ordena por el campo 'order' de forma ascendente
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<CarerInformation> informationList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        CarerInformation info = document.toObject(CarerInformation.class);
                        informationList.add(info);
                    }
                    callback.onSuccess(informationList);
                })
                .addOnFailureListener(callback::onFailure);
    }
}