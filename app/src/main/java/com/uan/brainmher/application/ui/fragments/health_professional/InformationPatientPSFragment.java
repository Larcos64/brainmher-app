package com.uan.brainmher.application.ui.fragments.health_professional;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.uan.brainmher.R;
import com.uan.brainmher.databinding.FragmentPsInformationPatientBinding;
import com.uan.brainmher.domain.entities.Patient;
import com.uan.brainmher.domain.repositories.PatientsRepository;
import com.uan.brainmher.infraestructure.tools.CircularProgressUtil;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class InformationPatientPSFragment extends Fragment {

    private FragmentPsInformationPatientBinding binding;
    private Patient patient;
    private Uri uriImage;

    private PatientsRepository patientsRepository;
    private CircularProgressUtil circularProgressUtil;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPsInformationPatientBinding.inflate(inflater, container, false);
        patientsRepository = new PatientsRepository();
        circularProgressUtil = new CircularProgressUtil(getActivity());

        // Cargar datos del paciente
        loadPatientData();
        initEditData();

        // Configurar listeners de UI
        setupUIListeners();

        return binding.getRoot();
    }

    private void loadPatientData() {
        Bundle args = getArguments();
        if (args != null && args.containsKey("patient")) {
            patient = (Patient) args.getSerializable("patient");
        } else {
            SharedPreferences preferences = requireActivity().getPreferences(0);
            String json = preferences.getString("serialipatient", null);
            if (json != null) {
                patient = new Gson().fromJson(json, Patient.class);
            }
        }

        if (patient != null) {
            Glide.with(requireContext()).load(patient.getUriImg()).fitCenter().into(binding.ivPsPatientPhoto);
            initViewData();
            savePatientToPreferences();
        }
    }

    private void savePatientToPreferences() {
        SharedPreferences preferences = requireActivity().getPreferences(0);
        SharedPreferences.Editor editor = preferences.edit();
        String json = new Gson().toJson(patient);
        editor.putString("serialipatient", json);
        editor.apply();
    }

    private void setupUIListeners() {
        binding.btnSave.setOnClickListener(v -> {
            if (validateFields()) {
                savePatient();
            } else {
                Toast.makeText(getActivity(), R.string.complete_field, Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnEditCancel.setOnClickListener(v -> toggleEditMode());

        binding.editIvDatebirthPatient.setOnClickListener(v -> showDatePicker(date -> binding.editTxtDatebirthPatient.setText(date)));
        binding.editIvDatediagnosisPatient.setOnClickListener(v -> showDatePicker(date -> binding.editTxtDatediagnosisPatient.setText(date)));
    }

    private void showDatePicker(OnDateSelectedListener listener) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                    listener.onDateSelected(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        // Limitar las fechas a solo fechas pasadas
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();
    }

    private boolean validateFields() {
        String name = binding.editTxtNamePatient.getText().toString();
        String lastName = binding.editTxtLastnamePatient.getText().toString();
        String email = binding.editTxtEmailPatient.getText().toString();
        return !name.isEmpty() && !lastName.isEmpty() && !email.isEmpty();
    }

    private void savePatient() {
        if (patient.getPatientUID() == null || patient.getPatientUID().isEmpty()) {
            Toast.makeText(getActivity(), "ID NO ENCONTRADO", Toast.LENGTH_SHORT).show();
            return;
        }

        circularProgressUtil.showProgress(getString(R.string.registering));

        // Recopilar solo los campos modificados
        Map<String, Object> fieldsToUpdate = new HashMap<>();
        fieldsToUpdate.put("firstName", binding.editTxtNamePatient.getText().toString());
        fieldsToUpdate.put("lastName", binding.editTxtLastnamePatient.getText().toString());
        fieldsToUpdate.put("gender", ((RadioButton) requireView().findViewById(binding.editRgGenderPatient.getCheckedRadioButtonId())).getText().toString());
        fieldsToUpdate.put("birthday", binding.editTxtDatebirthPatient.getText().toString());
        fieldsToUpdate.put("email", binding.editTxtEmailPatient.getText().toString());
        fieldsToUpdate.put("diagnostic", binding.editTxtDiagnosticPatient.getText().toString());
        fieldsToUpdate.put("dateDiagnostic", binding.editTxtDatediagnosisPatient.getText().toString());

        // Llamar al nuevo método para actualizar los campos
        patientsRepository.updatePatientFields(patient.getPatientUID(), fieldsToUpdate, new PatientsRepository.OnPatientLoadedListener() {
            @Override
            public void onSuccess(Patient updatedPatient) {
                // Recuperar los datos más recientes del documento
                patientsRepository.getPatientById(patient.getPatientUID(), new PatientsRepository.OnPatientLoadedListener() {
                    @Override
                    public void onSuccess(Patient updatedPatient) {
                        circularProgressUtil.hideProgress();
                        patient = updatedPatient; // Actualizar el objeto en memoria
                        initViewData(); // Actualizar la vista con los nuevos datos
                        Toast.makeText(getActivity(), R.string.was_saved_succesfully, Toast.LENGTH_SHORT).show();
                        toggleEditMode();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        circularProgressUtil.hideProgress();
                        Log.e("Error", e.getMessage(), e);
                        Toast.makeText(getActivity(), R.string.saving_error, Toast.LENGTH_SHORT).show();
                    }
                });
            }


            @Override
            public void onFailure(Exception e) {
                circularProgressUtil.hideProgress();
                Log.e("Error", e.getMessage(), e);
                Toast.makeText(getActivity(), R.string.saving_error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initViewData() {
        binding.tvPsPatientName.setText(String.format("%s %s", patient.getFirstName(), patient.getLastName()));
        binding.tvPsNamePatient.setText(patient.getFirstName());
        binding.tvPsLastnamePatient.setText(patient.getLastName());
        binding.tvPsGenderPatient.setText(patient.getGender());
        binding.tvPsDatebirthPatient.setText(patient.getBirthday());
        binding.tvPsEmaiPatient.setText(patient.getEmail());
        binding.tvPsPassPatient.setText(patient.getPassword());
        binding.tvPsDiagnosisPatient.setText(patient.getDiagnostic());
        binding.tvPsDiagnosisdatePatient.setText(patient.getDateDiagnostic());
    }

    private void initEditData() {
        binding.editTxtNamePatient.setText(patient.getFirstName());
        binding.editTxtLastnamePatient.setText(patient.getLastName());
        binding.editTxtDatebirthPatient.setText(patient.getBirthday());
        binding.editTxtEmailPatient.setText(patient.getEmail());
        binding.editTxtPasswordPatient.setText(patient.getPassword());
        binding.editTxtDiagnosticPatient.setText(patient.getDiagnostic());
        binding.editTxtDatediagnosisPatient.setText(patient.getDateDiagnostic());

        String gender = patient.getGender();
        if (gender != null && gender.equalsIgnoreCase("Masculino")) {
            binding.editRbMalePatient.setChecked(true);
        } else {
            binding.editRbFemalePatient.setChecked(true);
        }
    }

    private void toggleEditMode() {
        boolean isEditing = binding.llViewData.getVisibility() == View.VISIBLE;

        if (isEditing) {
            binding.llViewData.setVisibility(View.GONE);
            binding.llEditData.setVisibility(View.VISIBLE);

            binding.btnEditCancel.setIconResource(R.drawable.ic_cancel_black);
            binding.btnEditCancel.setText(getString(R.string.cancel));
            binding.btnEditCancel.setIconTintResource(R.color.red);
            binding.btnEditCancel.setTextColor(requireContext().getColor(R.color.red));
            binding.btnSave.setVisibility(View.VISIBLE);
        } else {
            binding.llViewData.setVisibility(View.VISIBLE);
            binding.llEditData.setVisibility(View.GONE);

            binding.btnEditCancel.setIconResource(R.drawable.ic_edit_black);
            binding.btnEditCancel.setText(getString(R.string.update));
            binding.btnEditCancel.setIconTintResource(R.color.colorPrimaryDark);
            binding.btnEditCancel.setTextColor(requireContext().getColor(R.color.colorPrimaryDark));
            binding.btnSave.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    interface OnDateSelectedListener {
        void onDateSelected(String date);
    }
}