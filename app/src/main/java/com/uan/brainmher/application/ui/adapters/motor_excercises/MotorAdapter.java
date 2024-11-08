package com.uan.brainmher.application.ui.adapters.motor_excercises;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.card.MaterialCardView;
import com.uan.brainmher.R;
import com.uan.brainmher.domain.entities.MotorExercises;
import com.uan.brainmher.databinding.ItemMotorCardBinding;
import com.uan.brainmher.domain.repositories.MotorExcercisesRepository;

import java.util.ArrayList;
import java.util.List;

public class MotorAdapter extends FirestoreRecyclerAdapter<MotorExercises, MotorAdapter.ViewHolder> {

    private final Context context;
    private final String uid;
    private final MotorExcercisesRepository repository;

    public MotorAdapter(@NonNull FirestoreRecyclerOptions<MotorExercises> options, Context context, String uid) {
        super(options);
        this.context = context;
        this.uid = uid;
        this.repository = new MotorExcercisesRepository(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemMotorCardBinding binding;

        public ViewHolder(@NonNull ItemMotorCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemMotorCardBinding binding = ItemMotorCardBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull MotorExercises model) {
        holder.binding.tvMotorName.setText(model.getNameExercise());
        holder.binding.tvMotorDescription.setText(model.getDescriptionExercise());
        Glide.with(context).load(model.getUriGifExercise()).fitCenter().into(holder.binding.ivMotor);

        final String idDoc = getSnapshots().getSnapshot(holder.getAdapterPosition()).getId();
        final MaterialCardView cardMotor = holder.binding.cardMotor;

        List<String> listAssignments = model.getAssignments() != null ? model.getAssignments() : new ArrayList<>();
        boolean isAssigned = listAssignments.contains(uid);
        cardMotor.setChecked(isAssigned);

        cardMotor.setOnClickListener(view -> cardMotor.toggle());

        cardMotor.setOnCheckedChangeListener((card, isChecked) -> {
            if (isChecked) {
                repository.assignExercise(idDoc, uid, model, success -> {
                    if (success) {
                        repository.getExerciseDetails(idDoc, exercise -> {
                            if (exercise != null) {
                                repository.setAssignment(uid, exercise);
                            }
                        });
                    } else {
                        Toast.makeText(context, R.string.failed_management, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                repository.unassignExercise(idDoc, uid, model, success -> {
                    if (success) {
                        Toast.makeText(context, R.string.unassigned_exercise, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, R.string.error_to_deallocate, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
