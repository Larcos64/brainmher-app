package com.uan.brainmher.domain.repositories;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.uan.brainmher.domain.entities.StretchingExercise;
import com.uan.brainmher.infraestructure.tools.Constants;

import java.util.ArrayList;
import java.util.List;

public class StretchingExercisesRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface OnStretchingExercisesLoaded {
        void onSuccess(List<StretchingExercise> exercises);

        void onFailure(Exception e);
    }

    public void getStretchingExercises(OnStretchingExercisesLoaded callback) {
        db.collection(Constants.Stretching)
                .orderBy("nameExercise")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<StretchingExercise> exercises = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        StretchingExercise exercise = document.toObject(StretchingExercise.class);
                        exercises.add(exercise);
                    }
                    callback.onSuccess(exercises);
                })
                .addOnFailureListener(callback::onFailure);
    }
}