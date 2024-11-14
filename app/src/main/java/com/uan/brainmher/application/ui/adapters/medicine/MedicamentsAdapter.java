package com.uan.brainmher.application.ui.adapters.medicine;

import android.content.Context;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.uan.brainmher.domain.entities.MedicationAssignment;
import com.uan.brainmher.databinding.ItemMedicamentCardBinding;

public class MedicamentsAdapter extends FirestoreRecyclerAdapter<MedicationAssignment, MedicamentsAdapter.MeicamentsViewHolder> {

    private final Context context;
    private final ISelectItemMedicaments iSelectItemMedicaments;

    public MedicamentsAdapter(@NonNull FirestoreRecyclerOptions<MedicationAssignment> options, Context context, ISelectItemMedicaments iSelectItemMedicaments) {
        super(options);
        this.context = context;
        this.iSelectItemMedicaments = iSelectItemMedicaments;
    }

    @Override
    protected void onBindViewHolder(@NonNull MeicamentsViewHolder holder, int position, @NonNull MedicationAssignment model) {
        final DocumentSnapshot medicamentDocument = getSnapshots().getSnapshot(position);
        holder.bindData(medicamentDocument.toObject(MedicationAssignment.class));

        holder.binding.btnShowHideMedicament.setOnClickListener(view -> {
            iSelectItemMedicaments.clickSelect(medicamentDocument.toObject(MedicationAssignment.class));
            if (holder.binding.expandableMedicamentView.getVisibility() == View.GONE) {
                TransitionManager.beginDelayedTransition(holder.binding.cardActivityMedicaments, new AutoTransition());
                holder.binding.expandableMedicamentView.setVisibility(View.VISIBLE);
            } else {
                TransitionManager.beginDelayedTransition(holder.binding.cardActivityMedicaments, new AutoTransition());
                holder.binding.expandableMedicamentView.setVisibility(View.GONE);
            }
        });
    }

    @NonNull
    @Override
    public MeicamentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMedicamentCardBinding binding = ItemMedicamentCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MeicamentsViewHolder(binding);
    }

    public class MeicamentsViewHolder extends RecyclerView.ViewHolder {
        private final ItemMedicamentCardBinding binding;

        public MeicamentsViewHolder(@NonNull ItemMedicamentCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindData(MedicationAssignment item) {
            // Configura los datos del medicamento en las vistas correspondientes
            binding.lblMedicamentName.setText(item.getMedicamentName());
            binding.lblDataDescription.setText(item.getMedicamentDescription());
            binding.dataFrequency.setText(item.getFrequency());
            binding.dataDose.setText(item.getDose());
            binding.dataHours.setText(item.getHours());
            binding.dataStatementMedicament.setText(item.getStatement());
            Glide.with(context).load(item.getUriImg()).centerCrop().into(binding.ivMedicamentMiniature);

            // Verificación de null en statement
            String statement = item.getStatement();
            binding.txtSwitchList.setText("Activada".equals(statement) ? "Activada" : "Desactivada");
        }
    }

    public interface ISelectItemMedicaments {
        void clickSelect(MedicationAssignment medicationAssignment);
        void check(MedicationAssignment medicationAssignment);
    }
}
