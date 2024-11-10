package com.uan.brainmher.application.ui.adapters.motor_exercises_assignment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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
import com.uan.brainmher.application.ui.activities.Games;
import com.uan.brainmher.databinding.PhysicalExerciseTemplateBinding;
import com.uan.brainmher.domain.entities.MotorExcercisesAssignment;
import com.uan.brainmher.application.ui.fragments.motor_exercises_assignment.MotorChildFragment;
import com.uan.brainmher.infraestructure.tools.Constants;

import pl.droidsonroids.gif.GifImageView;

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
        int bindingPosition = holder.getBindingAdapterPosition();
        if (bindingPosition != RecyclerView.NO_POSITION && bindingPosition < getSnapshots().size()) {
            final DocumentSnapshot exerciseDocument = getSnapshots().getSnapshot(bindingPosition);
            final String idDoc = exerciseDocument.getId();

            // Binding views with data
            Glide.with(context).load(model.getUriGifExercise()).fitCenter().into(holder.binding.physicalExerciseImagevGif);
            holder.binding.physicalExerciseTxtvName.setText(model.getNameExercise());
            holder.binding.physicalExerciseTxtvResume.setText(model.getDescriptionExercise());
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
            holder.itemView.setOnClickListener(view -> showAlertDialog(model));
        }
    }

    private void showAlertDialog(final MotorExcercisesAssignment exercise) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.BackgroundRounded);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.physical_exercise_info_template, null);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();

        Button btn1 = dialogView.findViewById(R.id.btn1);
        Button btn2 = dialogView.findViewById(R.id.btn2);
        TextView tvInformation = dialogView.findViewById(R.id.text_information);
        GifImageView gifImageView = dialogView.findViewById(R.id.gift);

        // Setting up the dialog components
        Glide.with(context).load(exercise.getUriGifExercise()).fitCenter().into(gifImageView);
        tvInformation.setText(exercise.getLongDescriptionExercise());

        btn1.setText(context.getString(R.string.close));
        btn1.setOnClickListener(v -> alertDialog.dismiss());

        btn2.setText(context.getString(R.string.practice));
        btn2.setOnClickListener(v -> {
            try {
                alertDialog.dismiss();
                Intent intent = new Intent(context, Games.class);
                intent.putExtra("Game", "Physical");
                intent.putExtra("Time", exercise.getTimeExercise());
                intent.putExtra("Image", exercise.getUriGifExercise());
                context.startActivity(intent);
            } catch (Exception e) {
                Log.e("MotorExercisesAdapter", "Error al iniciar Games: ", e);
            }
        });

        alertDialog.show();
    }

    private void updateRating(String idDoc, Integer rating) {
        DocumentReference documentReference = db.collection(Constants.MotorExercisesAssignments).document(idDoc);
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
