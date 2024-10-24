package com.uan.brainmher.application.ui.adapters.medicine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.uan.brainmher.R;
import com.uan.brainmher.domain.entities.MedicationAssignment;

public class MedicinesAdapter extends FirestoreRecyclerAdapter<MedicationAssignment, MedicinesAdapter.MedicinesViewHolder> {

    private final Context context;
    private final OnItemClick onItemClick;
    private final OnItemDelete onItemDelete;

    public MedicinesAdapter(@NonNull FirestoreRecyclerOptions<MedicationAssignment> options,
                            Context context, OnItemClick onItemClick, OnItemDelete onItemDelete) {
        super(options);
        this.context = context;
        this.onItemClick = onItemClick;
        this.onItemDelete = onItemDelete;
    }

    @Override
    protected void onBindViewHolder(@NonNull MedicinesViewHolder holder, int position, @NonNull MedicationAssignment model) {
        final DocumentSnapshot medicineDocument = getSnapshots().getSnapshot(holder.getBindingAdapterPosition());

        holder.setData(medicineDocument.toObject(MedicationAssignment.class));

        holder.layout.setOnClickListener(v -> {
            if (holder.getBindingAdapterPosition() != RecyclerView.NO_POSITION) {
                onItemClick.click(medicineDocument.toObject(MedicationAssignment.class));
            }
        });

        holder.imageDelete.setOnClickListener(v -> {
            if (holder.getBindingAdapterPosition() != RecyclerView.NO_POSITION) {
                onItemDelete.delete(medicineDocument.toObject(MedicationAssignment.class));
            }
        });
    }

    @NonNull
    @Override
    public MedicinesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine, parent, false);
        return new MedicinesViewHolder(view);
    }

    public class MedicinesViewHolder extends RecyclerView.ViewHolder {
        private final View layout;
        private final TextView txtMediName, txtMediDescription;
        private final ImageView imgMedicine, imageDelete;

        public MedicinesViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView;
            txtMediName = itemView.findViewById(R.id.txt_medicine_name);
            txtMediDescription = itemView.findViewById(R.id.txt_medicine_description);
            imgMedicine = itemView.findViewById(R.id.iv_medicine_miniature);
            imageDelete = itemView.findViewById(R.id.img_delete_medicine);
        }

        public void setData(MedicationAssignment item) {
            txtMediName.setText(item.getMedicamentName());
            txtMediDescription.setText(item.getMedicamentDescription());
            Glide.with(context).load(item.getUriImg()).centerCrop().into(imgMedicine);
        }
    }

    @FunctionalInterface
    public interface OnItemClick {
        void click(MedicationAssignment medicationAssignment);
    }

    @FunctionalInterface
    public interface OnItemDelete {
        void delete(MedicationAssignment medicationAssignment);
    }
}