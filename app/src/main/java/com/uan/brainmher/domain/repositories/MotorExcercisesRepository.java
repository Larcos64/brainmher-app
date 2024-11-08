package com.uan.brainmher.domain.repositories;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.uan.brainmher.R;
import com.uan.brainmher.domain.entities.MotorExercises;
import com.uan.brainmher.domain.entities.MotorExcercisesAssignment;
import com.uan.brainmher.infraestructure.tools.Constants;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class MotorExcercisesRepository {

    private final FirebaseFirestore db;
    private final Context context;

    public MotorExcercisesRepository(Context context) {
        this.db = FirebaseFirestore.getInstance();
        this.context = context;
    }

    public void assignExercise(String idDoc, String uid, MotorExercises model, Consumer<Boolean> callback) {
        List<String> assigns = Arrays.asList(uid);
        DocumentReference documentReference = db.collection(Constants.MotorExercises).document(idDoc);
        documentReference.update("assignments", assigns)
                .addOnSuccessListener(aVoid -> callback.accept(true))
                .addOnFailureListener(e -> callback.accept(false));
    }

    public void getExerciseDetails(String idDoc, Consumer<MotorExercises> callback) {
        DocumentReference documentReference = db.collection(Constants.MotorExercises).document(idDoc);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                callback.accept(task.getResult().toObject(MotorExercises.class));
            } else {
                callback.accept(null);
            }
        }).addOnFailureListener(e -> callback.accept(null));
    }

    public void setAssignment(String uid, MotorExercises exercise) {
        MotorExcercisesAssignment assignment = new MotorExcercisesAssignment();
        assignment.setUidPatient(uid);
        assignment.setIdExercise(exercise.getIdExercise());
        assignment.setNameExercise(exercise.getNameExercise());
        assignment.setDescriptionExercise(exercise.getDescriptionExercise());
        assignment.setLongDescriptionExercise(exercise.getLongDescriptionExercise());
        assignment.setTimeExercise(exercise.getTimeExercise());
        assignment.setUriGifExercise(exercise.getUriGifExercise());
        assignment.setFinished("No");
        assignment.setRating(0);

        db.collection(Constants.MotorExercisesAssignments).document("Motor").set(assignment)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, R.string.exercise_assignment, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, R.string.failed_exercise_assignment, Toast.LENGTH_SHORT).show());
    }

    public void unassignExercise(String idDoc, String uid, MotorExercises exercise, Consumer<Boolean> callback) {
        db.collection(Constants.MotorExercisesAssignments)
                .whereEqualTo("uidPatient", uid)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String idDocAssignment = documentSnapshot.getId();
                            db.collection(Constants.MotorExercises).document(idDoc).update("assignments", FieldValue.arrayRemove(uid));
                            db.collection(Constants.MotorExercisesAssignments).document(idDocAssignment).delete();
                        }
                        callback.accept(true);
                    } else {
                        callback.accept(false);
                    }
                }).addOnFailureListener(e -> callback.accept(false));
    }

    public Query getMotorExercisesQuery() {
        return db.collection(Constants.MotorExercises).orderBy("idExercise");
    }
}