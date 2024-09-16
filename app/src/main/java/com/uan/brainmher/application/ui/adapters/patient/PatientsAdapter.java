package com.uan.brainmher.application.ui.adapters.patient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.uan.brainmher.R;
import com.uan.brainmher.domain.entities.Patient;
import com.uan.brainmher.application.ui.interfaces.IOnPatientClickListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.uan.brainmher.infraestructure.tools.Constants.Patients;

public class PatientsAdapter extends FirestoreRecyclerAdapter<Patient,PatientsAdapter.PatientViewHolder> {

    //region Variables
    Context context;
    ISelectionPatient iSelectionPatient;
    IDeletePatient iDeletePatient;
    //endregion

    //region Builder

    public PatientsAdapter(@NonNull FirestoreRecyclerOptions<Patient> options, Context context, ISelectionPatient iSelectionPatient, IDeletePatient iDeletePatient) {
        super(options);
        this.context = context;
        this.iSelectionPatient = iSelectionPatient;
        this.iDeletePatient = iDeletePatient;
    }

    //endregion

    //region Overwritten methods of RecyclerView
    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_list_patients, parent, false);
        return new PatientViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull PatientViewHolder holder, int position, @NonNull Patient model) {
        // Obtén la posición del adaptador actual
        int adapterPosition = holder.getBindingAdapterPosition();

        // Verifica que la posición no sea NO_POSITION antes de usarla
        if (adapterPosition != RecyclerView.NO_POSITION) {
            final DocumentSnapshot patientDocument = getSnapshots().getSnapshot(adapterPosition);

            // Establece los datos en las vistas
            holder.setData(patientDocument.toObject(Patient.class));
            holder.layout.setOnClickListener(view -> {
                // Convierte el documento obtenido del adaptador en un objeto Patient
                iSelectionPatient.clickItem(patientDocument.toObject(Patient.class));
            });
            holder.delete.setOnClickListener(view -> {
                // Convierte el documento obtenido del adaptador en un objeto Patient
                iDeletePatient.clickdelete(patientDocument.toObject(Patient.class));
            });
        }
    }
    //endregion

    //region ViewHolder of Recycler
    public class PatientViewHolder extends RecyclerView.ViewHolder {

        CircleImageView photo;
        ImageView delete;
        TextView name, identification;
        Patient item;
        View layout;

        //Reference to views
        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView;
            photo = itemView.findViewById(R.id.imagePatient);
            delete = itemView.findViewById(R.id.btn_delete);
            name = itemView.findViewById(R.id.tvPatientName);
            //identification = itemView.findViewById(R.id.tvPatientId);
        }

        //Set data to views
        public void setData(Patient item) {
            this.item = item;
            Glide.with(context).load(item.getUriImg()).fitCenter().into(photo);
            name.setText(item.getFirstName());
            //identification.setText(item.getIdentification());
        }
    }
    //endregion

    //region Interfaces
    public interface ISelectionPatient {
        void clickItem(Patient patient);
    }

    public interface IDeletePatient {
        void clickdelete(Patient patient);
    }
    //endregion

}