package com.uan.brainmher.application.ui.adapters.patient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.uan.brainmher.domain.entities.Memorizame;
import com.uan.brainmher.databinding.ItemMemorizameRecyclerBinding;

import java.util.List;

public class PatientMemorizameAdapter extends RecyclerView.Adapter<PatientMemorizameAdapter.PatientMemorizameAdapterHolder> {

    // Variables
    private final List<Memorizame> memorizameList;
    private final Context context;
    private final ISelectionMemorizame iSelectionMemorizame;

    // Constructor
    public PatientMemorizameAdapter(List<Memorizame> memorizameList, Context context, ISelectionMemorizame iSelectionMemorizame) {
        this.memorizameList = memorizameList;
        this.context = context;
        this.iSelectionMemorizame = iSelectionMemorizame;
    }

    @NonNull
    @Override
    public PatientMemorizameAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout con View Binding
        ItemMemorizameRecyclerBinding binding = ItemMemorizameRecyclerBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new PatientMemorizameAdapterHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientMemorizameAdapterHolder holder, int position) {
        // Establecer datos y eventos
        Memorizame item = memorizameList.get(position);
        holder.bind(item);

        holder.binding.getRoot().setOnClickListener(v -> iSelectionMemorizame.clickItem(item));
    }

    @Override
    public int getItemCount() {
        return memorizameList.size();
    }

    // ViewHolder usando View Binding
    public class PatientMemorizameAdapterHolder extends RecyclerView.ViewHolder {

        private final ItemMemorizameRecyclerBinding binding;

        public PatientMemorizameAdapterHolder(@NonNull ItemMemorizameRecyclerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Memorizame item) {
            binding.textNumberPatient.setText("Empieza");
            Glide.with(context)
                    .load(item.getUriImg())
                    .fitCenter()
                    .into(binding.imgMemorizamePatient);
        }
    }

    // Interfaz de selección
    public interface ISelectionMemorizame {
        void clickItem(Memorizame memorizame);
    }
}
