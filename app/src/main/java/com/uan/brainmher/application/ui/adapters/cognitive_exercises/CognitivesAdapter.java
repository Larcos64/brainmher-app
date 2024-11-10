package com.uan.brainmher.application.ui.adapters.cognitive_exercises;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uan.brainmher.R;
import com.uan.brainmher.domain.entities.CognitiveExercisesAssignment;
import com.uan.brainmher.domain.entities.CognitiveExercises;
import com.uan.brainmher.domain.repositories.CognitiveExercisesRepository;
import com.uan.brainmher.infraestructure.tools.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CognitivesAdapter extends FirestoreRecyclerAdapter<CognitiveExercises, CognitivesAdapter.ViewHolder> {

    private final Context context;
    private final String uid;
    private final CognitiveExercisesRepository repository;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseUser firebaseUser;

    public CognitivesAdapter(@NonNull FirestoreRecyclerOptions<CognitiveExercises> options, Context context, String uid) {
        super(options);
        this.context = context;
        this.uid = uid;
        this.repository = new CognitiveExercisesRepository();
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseUser = firebaseAuth.getCurrentUser();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View layout;
        MaterialCardView cardCognitive;
        ImageView iv_cognitive;
        TextView tv_cognitive_name;
        TextView tv_cognitive_description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView;
            cardCognitive = itemView.findViewById(R.id.cardCognitive);
            iv_cognitive = itemView.findViewById(R.id.iv_cognitive);
            tv_cognitive_name = itemView.findViewById(R.id.tv_cognitive_name);
            tv_cognitive_description = itemView.findViewById(R.id.tv_cognitive_description);
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull CognitiveExercises model) {
        Glide.with(context).load(model.getUriImageExcercise()).fitCenter().into(holder.iv_cognitive);
        holder.tv_cognitive_name.setText(model.getNameExcercise());
        holder.tv_cognitive_description.setText(model.getDescriptionExcercise());

        final DocumentSnapshot excerciseDocument = getSnapshots().getSnapshot(position);
        final String idDoc = excerciseDocument.getId();
        final CognitiveExercises cognitivesExcercises = excerciseDocument.toObject(CognitiveExercises.class);

        List<String> listAssignments = cognitivesExcercises != null && cognitivesExcercises.getAssignments() != null
                ? cognitivesExcercises.getAssignments()
                : new ArrayList<>();

        holder.cardCognitive.setChecked(listAssignments.contains(uid));

        holder.cardCognitive.setOnClickListener(view -> holder.cardCognitive.toggle());

        holder.cardCognitive.setOnCheckedChangeListener((card, isChecked) -> {
            if (cognitivesExcercises == null) return;
            DocumentReference documentExcerciseReference = repository.getExerciseDocument(idDoc);

            if (isChecked) {
                List<String> assigns = Arrays.asList(uid);
                documentExcerciseReference.update("assignments", assigns).addOnSuccessListener(aVoid ->
                        documentExcerciseReference.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                                CognitiveExercises updatedExercise = task.getResult().toObject(CognitiveExercises.class);
                                if (updatedExercise != null) {
                                    setAssignment(uid, updatedExercise);
                                }
                            }
                        }).addOnFailureListener(e -> showToast(R.string.failed_management))
                ).addOnFailureListener(e -> showToast(R.string.failed_management));
            } else {
                removeAssignment(holder, documentExcerciseReference, cognitivesExcercises);
            }
        });
    }

    private void setAssignment(String uid, CognitiveExercises exercise) {
        CognitiveExercisesAssignment assignment = new CognitiveExercisesAssignment();
        assignment.setUidPatient(uid);
        assignment.setIdExcercise(exercise.getIdExcercise());
        assignment.setNameExcercise(exercise.getNameExcercise());
        assignment.setDescriptionExcercise(exercise.getDescriptionExcercise());
        assignment.setUriImageExcercise(exercise.getUriImageExcercise());
        assignment.setLevel(0);
        assignment.setBestScore(0);
        assignment.setStatement("Sin terminar");
        assignment.setRating(0);

        repository.saveAssignment(assignment).addOnSuccessListener(aVoid ->
                showToast(R.string.exercise_assignment)
        ).addOnFailureListener(e -> showToast(R.string.failed_exercise_assignment));
    }

    private void removeAssignment(ViewHolder holder, DocumentReference documentExcerciseReference, CognitiveExercises cognitivesExcercises) {
        repository.getAssignments(uid, cognitivesExcercises.getIdExcercise()).addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String idDocAssignment = documentSnapshot.getId();
                    documentExcerciseReference.update("assignments", FieldValue.arrayRemove(uid));
                    repository.deleteAssignment(idDocAssignment);
                    showToast(R.string.unassigned_exercise);
                }
            } else {
                showToast(R.string.error_to_deallocate);
            }
        }).addOnFailureListener(e -> showToast(R.string.error_to_deallocate));
    }

    private void showToast(int messageResId) {
        Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cognitives_card, parent, false);
        return new ViewHolder(view);
    }
}