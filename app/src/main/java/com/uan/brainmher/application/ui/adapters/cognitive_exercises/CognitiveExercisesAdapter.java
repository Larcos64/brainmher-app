package com.uan.brainmher.application.ui.adapters.cognitive_exercises;

import android.content.Context;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.uan.brainmher.R;
import com.uan.brainmher.domain.entities.CognitiveExercisesAssignment;
import com.uan.brainmher.domain.repositories.CognitiveExercisesAssignmentRepository;

public class CognitiveExercisesAdapter extends FirestoreRecyclerAdapter<CognitiveExercisesAssignment, CognitiveExercisesAdapter.ViewHolder> {

    private final Context context;
    private final ISelectionItem iSelectionItem;
    private final CognitiveExercisesAssignmentRepository repository;
    private Integer newRating;

    public CognitiveExercisesAdapter(@NonNull FirestoreRecyclerOptions<CognitiveExercisesAssignment> options, Context context,
                                      ISelectionItem iSelectionItem) {
        super(options);
        this.context = context;
        this.iSelectionItem = iSelectionItem;
        this.repository = new CognitiveExercisesAssignmentRepository();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View layout;
        ImageView ivImage;
        TextView tvName, tvDescription, tvLevel, tvScore, tvStatement, tvRating;
        RatingBar ratingBar;
        MaterialCardView cardExcercise;
        Button btnExpand;
        LinearLayout expandableView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView;
            ivImage = itemView.findViewById(R.id.iv_cognitive_miniature);
            tvName = itemView.findViewById(R.id.data_cognitive_name);
            tvDescription = itemView.findViewById(R.id.data_cognitive_description);
            tvLevel = itemView.findViewById(R.id.data_cognitive_level);
            tvScore = itemView.findViewById(R.id.data_cognitive_best_score);
            tvStatement = itemView.findViewById(R.id.data_cognitive_statement);
            tvRating = itemView.findViewById(R.id.data_cognitive_rating);
            ratingBar = itemView.findViewById(R.id.cognitiveRatingBar);
            cardExcercise = itemView.findViewById(R.id.cardCognitiveExcercise);
            btnExpand = itemView.findViewById(R.id.btnCognitiveExpand);
            expandableView = itemView.findViewById(R.id.expandableCognitiveView);
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull CognitiveExercisesAssignment model) {
        final DocumentSnapshot cognitiveDocument = getSnapshots().getSnapshot(position);
        final String idDoc = cognitiveDocument.getId();

        // Set data to views
        Glide.with(context).load(model.getUriImageExcercise()).fitCenter().into(holder.ivImage);
        holder.tvName.setText(model.getNameExcercise());
        holder.tvDescription.setText(model.getDescriptionExcercise());
        holder.tvLevel.setText(String.valueOf(model.getLevel()));
        holder.tvScore.setText(String.valueOf(model.getBestScore()));
        holder.tvStatement.setText(model.getStatement());
        holder.tvRating.setText(String.valueOf(model.getRating()));
        holder.ratingBar.setRating(model.getRating());

        holder.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                newRating = (int) rating;
                holder.tvRating.setText(String.valueOf(newRating));
                updateRating(idDoc, newRating);
            }
        });

        holder.cardExcercise.setOnClickListener(view -> iSelectionItem.clickSelect());

        holder.btnExpand.setOnClickListener(view -> {
            if (holder.expandableView.getVisibility() == View.GONE) {
                TransitionManager.beginDelayedTransition(holder.cardExcercise, new AutoTransition());
                holder.expandableView.setVisibility(View.VISIBLE);
                holder.btnExpand.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black);
            } else {
                TransitionManager.beginDelayedTransition(holder.cardExcercise, new AutoTransition());
                holder.expandableView.setVisibility(View.GONE);
                holder.btnExpand.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black);
            }
        });
    }

    private void updateRating(String idDoc, Integer newRating) {
        repository.updateAssignmentRating(idDoc, newRating)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, R.string.rate_saved, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, R.string.rate_no_saved, Toast.LENGTH_SHORT).show());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cognitive_exercise_card, parent, false);
        return new ViewHolder(view);
    }

    public interface ISelectionItem {
        void clickSelect();
    }
}