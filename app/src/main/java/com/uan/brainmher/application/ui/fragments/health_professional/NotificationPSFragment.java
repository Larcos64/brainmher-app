package com.uan.brainmher.application.ui.fragments.health_professional;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.uan.brainmher.R;
import com.uan.brainmher.application.ui.adapters.medicine.MedicinesAdapter;
import com.uan.brainmher.domain.entities.MedicationAssignment;
import com.uan.brainmher.databinding.FragmentPsNotificationBinding;
import com.uan.brainmher.domain.entities.Patient;
import com.uan.brainmher.domain.repositories.NotificationRepository;
import com.uan.brainmher.infraestructure.tools.CircularProgressUtil;
import com.uan.brainmher.infraestructure.tools.Constants;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationPSFragment extends Fragment {

    private FragmentPsNotificationBinding binding;
    private NotificationRepository notificationsRepository;
    private Patient patient;
    private Uri uriImage;
    private MedicinesAdapter adapterMedicine;
    private Calendar calendarInstance = Calendar.getInstance();

    public NotificationPSFragment() {}
    private ActivityResultLauncher<Intent> startActivityLauncher;
    private CircleImageView imgMedicine;
    private CircularProgressUtil circularProgressUtil;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPsNotificationBinding.inflate(inflater, container, false);

        notificationsRepository = new NotificationRepository();
        circularProgressUtil = new CircularProgressUtil(requireActivity());

        loadPatientData();
        setupRecyclerView();
        setupEventListeners();
        setupActivityLauncher();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapterMedicine.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterMedicine.stopListening();
    }

    private void setupActivityLauncher() {
        startActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        uriImage = result.getData().getData();
                        if (uriImage != null) {
                            Glide.with(getActivity()).load(uriImage).into(imgMedicine);
                        }
                    }
                }
        );
    }

    private void loadPatientData() {
        SharedPreferences preferences = requireActivity().getPreferences(Activity.MODE_PRIVATE);
        String json = preferences.getString("serialipatient", "");
        patient = new Gson().fromJson(json, Patient.class);
    }

    private void setupRecyclerView() {
        binding.rcMedicine.setLayoutManager(new LinearLayoutManager(getContext()));

        FirestoreRecyclerOptions<MedicationAssignment> options =
                notificationsRepository.getMedicationOptions(patient.getPatientUID());

        adapterMedicine = new MedicinesAdapter(
                options,
                getActivity(),
                medication -> showUpdateDialog(medication),  // Lambda para la selección
                medication -> { /* Lógica para eliminar */ });
        binding.rcMedicine.setAdapter(adapterMedicine);
    }

    private void setupEventListeners() {
        binding.floatingMedicineAdd.setOnClickListener(v -> showCreateDialog());
    }

    private void showCreateDialog() {
        showMedicineDialog("create", new MedicationAssignment());
    }

    private void showUpdateDialog(MedicationAssignment medication) {
        showMedicineDialog("update", medication);
    }

    private void showMedicineDialog(String option, MedicationAssignment medication) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.BackgroundRounded);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.item_add_medicine, null);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();

        setupDialogViews(dialogView, option, medication, alertDialog);
        alertDialog.show();
    }

    private void setupDialogViews(View dialogView, String option, MedicationAssignment medication, AlertDialog alertDialog) {
        TextInputEditText editName = dialogView.findViewById(R.id.edit_name_medicine);
        TextInputEditText editDescription = dialogView.findViewById(R.id.edit_desciption_medicine);
        TextInputEditText editDose = dialogView.findViewById(R.id.edit_dose_medicine);
        AutoCompleteTextView editFrequency = dialogView.findViewById(R.id.edit_frequency);
        TextView txtStartHour = dialogView.findViewById(R.id.txt_start_hour);

        imgMedicine = dialogView.findViewById(R.id.img_medicine);

        Button btnSave = dialogView.findViewById(R.id.btn_save_medicine);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancelar_medicine);

        setupDropdownMenu(editFrequency);

        imgMedicine.setOnClickListener(v -> openGallery());  // Abre la galería al hacer clic
        btnCancel.setOnClickListener(v -> alertDialog.dismiss());

        btnSave.setOnClickListener(v -> saveMedicine(option, medication, alertDialog, editName, editDescription, editDose, editFrequency, txtStartHour));
    }

    private void setupDropdownMenu(AutoCompleteTextView editFrequency) {
        String[] frequencies = {"2 Minutos", "5 Minutos", "30 Minutos", "6 Horas", "8 Horas", "12 Horas", "48 Horas"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.dropdown_menu_popup_item, frequencies);
        editFrequency.setAdapter(adapter);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityLauncher.launch(Intent.createChooser(intent, getString(R.string.select_photo)));
    }

    private void saveMedicine(String option, MedicationAssignment medication, AlertDialog alertDialog,
                              TextInputEditText editName, TextInputEditText editDescription,
                              TextInputEditText editDose, AutoCompleteTextView editFrequency,
                              TextView txtStartHour) {

        String name = editName.getText().toString();
        String description = editDescription.getText().toString();
        String dose = editDose.getText().toString();
        String frequency = editFrequency.getText().toString();
        String startHour = txtStartHour.getText().toString();

        if (isValidInput(name, description, dose, frequency, startHour)) {
            medication.setMedicamentName(name);
            medication.setMedicamentDescription(description);
            medication.setDose(dose);
            medication.setFrequency(frequency);
            medication.setHours(startHour);

            if (option.equals("create")) {
                createMedication(medication, uriImage);
            } else {
                updateMedication(medication, uriImage, alertDialog);
            }
        } else {
            Toast.makeText(getContext(), "Completa todos los datos", Toast.LENGTH_SHORT).show();
        }
    }

    private void createMedication(MedicationAssignment medication, Uri imageUri) {
        notificationsRepository.createMedication(
                patient,
                medication,
                imageUri,
                new NotificationRepository.ProgressCallback() {
                    @Override
                    public void onStart() {
                        circularProgressUtil.showProgress(getString(R.string.registering));
                    }

                    @Override
                    public void onComplete() {
                        circularProgressUtil.hideProgress();
                    }
                },
                aVoid -> Toast.makeText(getContext(), "Medicamento creado con éxito", Toast.LENGTH_SHORT).show()
        );
    }

    private void updateMedication(MedicationAssignment medication, Uri newImageUri, AlertDialog alertDialog) {
        notificationsRepository.updateMedication(
                patient,
                medication,
                newImageUri,
                new NotificationRepository.ProgressCallback() {
                    @Override
                    public void onStart() {
                        circularProgressUtil.showProgress(getString(R.string.updating));
                    }

                    @Override
                    public void onComplete() {
                        circularProgressUtil.hideProgress();
                        Toast.makeText(getContext(), "Medicamento actualizado con éxito", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                },
                aVoid -> {}
        );
    }

    private boolean isValidInput(String... inputs) {
        for (String input : inputs) {
            if (input.isEmpty()) return false;
        }
        return true;
    }
}