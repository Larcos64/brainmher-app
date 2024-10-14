package com.uan.brainmher.application.ui.adapters.stretching_exercises;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.uan.brainmher.R;
import com.uan.brainmher.domain.entities.StretchingExercise;

import java.util.List;

public class StretchingExercisesAdapter extends RecyclerView.Adapter<StretchingExercisesAdapter.StretchingViewHolder> {

    // Variables
    private final List<StretchingExercise> stretchingList;
    private final ISelectionStretching iSelectionStretching;

    // Constructor
    public StretchingExercisesAdapter(
            List<StretchingExercise> stretchingList,
            ISelectionStretching iSelectionStretching
    ) {
        this.stretchingList = stretchingList;
        this.iSelectionStretching = iSelectionStretching;
    }

    @NonNull
    @Override
    public StretchingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.listview_exercise_carer, parent, false);
        return new StretchingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StretchingViewHolder holder, int position) {
        StretchingExercise exercise = stretchingList.get(position);
        holder.bind(exercise);

        // Handle item click
        holder.itemView.setOnClickListener(v -> iSelectionStretching.clickItem(exercise));
    }

    @Override
    public int getItemCount() {
        return stretchingList.size();
    }

    // ViewHolder class
    public static class StretchingViewHolder extends RecyclerView.ViewHolder {

        private final ImageView photo;
        private final TextView name;

        public StretchingViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.img_stretching);
            name = itemView.findViewById(R.id.text_name_exercise);
        }

        public void bind(StretchingExercise exercise) {
            name.setText(exercise.getNameExercise());
            Glide.with(itemView.getContext())
                    .load(exercise.getUriGif())
                    .fitCenter()
                    .into(photo);
        }
    }

    // Interface for item selection
    public interface ISelectionStretching {
        void clickItem(StretchingExercise stretchingExercise);
    }
}