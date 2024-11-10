package com.uan.brainmher.domain.repositories;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.uan.brainmher.domain.entities.CognitiveExercisesAssignment;
import com.uan.brainmher.infraestructure.tools.Constants;

public class CognitiveExercisesRepository {

    private final FirebaseFirestore db;

    public CognitiveExercisesRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public Query getCognitiveExercisesQuery() {
        return db.collection(Constants.CognitiveExercises);
    }

    public DocumentReference getExerciseDocument(String idDoc) {
        return db.collection(Constants.CognitiveExercises).document(idDoc);
    }

    public Task<Void> saveAssignment(CognitiveExercisesAssignment assignment) {
        return db.collection(Constants.CognitiveExercisesAssignments).document("Cognitive").set(assignment);
    }

    public Task<QuerySnapshot> getAssignments(String uid, int idExercise) {
        return db.collection(Constants.CognitiveExercisesAssignments)
                .whereEqualTo("uidPatient", uid)
                .whereEqualTo("idExcercise", idExercise)
                .get();
    }

    public void deleteAssignment(String idDocAssignment) {
        db.collection(Constants.CognitiveExercisesAssignments).document(idDocAssignment).delete();
    }
}
