package com.uan.brainmher.application.ui.adapters.motor_exercises_assignment;

import android.content.Context;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uan.brainmher.R;
import com.uan.brainmher.databinding.PhysicalExerciseTemplateBinding;
import com.uan.brainmher.domain.entities.MotorExcercisesAssignment;
import com.uan.brainmher.application.ui.fragments.motor_exercises_assignment.MotorChildFragment;

public class MotorExercisesAdapter extends FirestoreRecyclerAdapter<MotorExcercisesAssignment, MotorExercisesAdapter.ViewHolder> {

    private FirebaseFirestore db;
    private MotorChildFragment.MotorChildFragmentI motorChildFragmentI;
    private Context context;
    private Integer newRating;

    public MotorExercisesAdapter(@NonNull FirestoreRecyclerOptions<MotorExcercisesAssignment> options, Context context, MotorChildFragment.MotorChildFragmentI motorChildFragmentI) {
        super(options);
        this.context = context;
        this.motorChildFragmentI = motorChildFragmentI;
        db = FirebaseFirestore.getInstance();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final PhysicalExerciseTemplateBinding binding;

        public ViewHolder(@NonNull PhysicalExerciseTemplateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull final MotorExercisesAdapter.ViewHolder holder, final int position, @NonNull final MotorExcercisesAssignment model) {
        final DocumentSnapshot excerciseDocument = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String idDoc = excerciseDocument.getId();

        // Binding views with data
        Glide.with(context).load(model.getUriGifExcercise()).fitCenter().into(holder.binding.physicalExerciseImagevGif);
        holder.binding.physicalExerciseTxtvName.setText(model.getNameExcercise());
        holder.binding.physicalExerciseTxtvResume.setText(model.getDescriptionExcercise());
        holder.binding.dataFinished.setText(model.getFinished());
        holder.binding.dataRatingMotor.setText(String.valueOf(model.getRating()));
        holder.binding.ratingBarMotor.setRating(model.getRating());

        // Expand/collapse functionality
        holder.binding.btnExpandMotor.setOnClickListener(view -> {
            if (holder.binding.expandableViewMotor.getVisibility() == View.GONE) {
                TransitionManager.beginDelayedTransition(holder.binding.cardActivityMotor, new AutoTransition());
                holder.binding.expandableViewMotor.setVisibility(View.VISIBLE);
                holder.binding.btnExpandMotor.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black);
            } else {
                TransitionManager.beginDelayedTransition(holder.binding.cardActivityMotor, new AutoTransition());
                holder.binding.expandableViewMotor.setVisibility(View.GONE);
                holder.binding.btnExpandMotor.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black);
            }
        });

        // Rating bar change listener
        holder.binding.ratingBarMotor.setOnRatingBarChangeListener((ratingBar, v, b) -> {
            newRating = (int) ratingBar.getRating();
            holder.binding.dataRatingMotor.setText(String.valueOf(newRating));
            updateRating(idDoc, newRating);
        });

        // Item click listener
        holder.itemView.setOnClickListener(view -> motorChildFragmentI.alert("eliminar", excerciseDocument.toObject(MotorExcercisesAssignment.class)));
    }

    private void updateRating(String idDoc, Integer rating) {
        DocumentReference documentReference = db.collection("MotorExcercisesAssignments").document(idDoc);
        documentReference.update("rating", rating)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, R.string.rate_saved, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, R.string.rate_no_saved, Toast.LENGTH_SHORT).show());
    }

    @NonNull
    @Override
    public MotorExercisesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate using View Binding
        PhysicalExerciseTemplateBinding binding = PhysicalExerciseTemplateBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }
}
