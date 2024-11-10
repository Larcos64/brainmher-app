package com.uan.brainmher.domain.repositories;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.uan.brainmher.domain.entities.CognitiveExercisesAssignment;
import com.uan.brainmher.infraestructure.tools.Constants;

public class CognitiveExercisesAssignmentRepository {

    private final FirebaseFirestore db;

    public CognitiveExercisesAssignmentRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public Query getCognitiveAssignmentsQuery(String uidPatient) {
        return db.collection(Constants.CognitiveExercisesAssignments)
                .whereEqualTo("uidPatient", uidPatient);
    }

    public Task<Void> updateAssignmentRating(String idDoc, Integer newRating) {
        DocumentReference documentReference = db.collection(Constants.CognitiveExercisesAssignments).document(idDoc);
        return documentReference.update("rating", newRating);
    }

    public Task<DocumentReference> addAssignment(CognitiveExercisesAssignment assignment) {
        return db.collection(Constants.CognitiveExercisesAssignments).add(assignment);
    }

    public Task<Void> deleteAssignment(String idDoc) {
        DocumentReference documentReference = db.collection(Constants.CognitiveExercisesAssignments).document(idDoc);
        return documentReference.delete();
    }
}