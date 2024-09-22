package com.uan.brainmher.application.ui.fragments.patients;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uan.brainmher.R;
import com.uan.brainmher.application.ui.activities.carer.CarerRegistration;
import com.uan.brainmher.application.ui.activities.carer.MainCarer;
import com.uan.brainmher.application.ui.activities.patient.PatientsList;
import com.uan.brainmher.databinding.FragmentAddPatientsBinding;
import com.uan.brainmher.domain.entities.Carer;
import com.uan.brainmher.domain.entities.HealthcareProfessional;
import com.uan.brainmher.domain.entities.Patient;
import com.uan.brainmher.infraestructure.tools.CircularProgressUtil;
import com.uan.brainmher.infraestructure.tools.Constants;
import com.uan.brainmher.infraestructure.database.LoginManager;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AddPatientsFragment extends Fragment {

    private FragmentAddPatientsBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private String uIDHPoCarer;
    private Uri uriImage;
    private Patient patient = new Patient();
    private HealthcareProfessional health_professional = new HealthcareProfessional();
    private Carer carer = new Carer();
    private boolean flag = true;
    private CircularProgressUtil circularProgressUtil;

    private ActivityResultLauncher<Intent> startActivityLauncher;
    private LoginManager loginManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddPatientsBinding.inflate(inflater, container, false);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        uIDHPoCarer = firebaseUser.getUid();
        db = FirebaseFirestore.getInstance();
        loginManager = new LoginManager();

        setupListeners();
        verifyFields();

        circularProgressUtil = new CircularProgressUtil(getActivity());

        startActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        // Actualizar URI de la imagen seleccionada
                        uriImage = imageUri;

                        // Cargar imagen seleccionada en el ImageView
                        Glide.with(requireContext())
                                .load(imageUri)
                                .fitCenter()
                                .into(binding.civProfileImage);
                    }
                });

        return binding.getRoot();
    }

    private void setupListeners() {
        binding.civProfileImage.setOnClickListener(v -> handleProfileImageClick());
        binding.ivBirthDate.setOnClickListener(v -> handleDateImageClick(v));
        binding.ivDiagnosisDate.setOnClickListener(v -> handleDateImageClick(v));
        binding.btnSave.setOnClickListener(v -> {
            if (setPojoPatients()) {
                circularProgressUtil.showProgress(getString(R.string.registering));
                handleSaveButtonClick();
            } else {
                Toast.makeText(requireContext(), R.string.complete_field, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleDateImageClick(View view) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (datePicker, year, monthOfYear, dayOfMonth) -> {
            // Actualiza el texto del TextView correspondiente
            if (view.getId() == binding.ivBirthDate.getId()) {
                binding.txtBirthDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
            } else if (view.getId() == binding.ivDiagnosisDate.getId()) {
                binding.txtDiagnosisDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
            }
        }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }

    private void verifyFields() {
        binding.txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tilPasswordPatient.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateFields("password");
            }
        });
    }

    private boolean validateFields(String field) {
        if ("password".equals(field)) {
            String password = binding.txtPassword.getText().toString().trim();
            if (password.length() < 7) {
                binding.tilPasswordPatient.setError(getString(R.string.val_min_password));
                return false;
            }
        }
        return true;
    }

    private void handleProfileImageClick() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityLauncher.launch(intent.createChooser(intent, getString(R.string.select_photo)));
    }

    private boolean setPojoPatients() {
        String nameSring = binding.txtName.getText().toString().trim();
        String lastNameString = binding.txtLastname.getText().toString().trim();

        String seleccionRG = "";
        if (binding.rgGender.getCheckedRadioButtonId() != -1) {
            int radioButtonId = binding.rgGender.getCheckedRadioButtonId();
            RadioButton rb = binding.getRoot().findViewById(radioButtonId);
            seleccionRG = rb.getText().toString();
        }

        String birthDateString = binding.txtBirthDate.getText().toString().trim();
        String emailString = binding.txtEmail.getText().toString().trim();
        String passwordString = binding.txtPassword.getText().toString().trim();
        String diagnosticString = binding.txtDiagnostic.getText().toString();
        String dateDiagnosticString = binding.txtDiagnosisDate.getText().toString();

        boolean isValid = !nameSring.isEmpty() && !lastNameString.isEmpty() && !seleccionRG.isEmpty()
                && !birthDateString.isEmpty() && !emailString.isEmpty() && !passwordString.isEmpty() && passwordString.length() >= 7
                && !diagnosticString.isEmpty() && !dateDiagnosticString.isEmpty();

        if (isValid) {
            patient.setFirstName(nameSring);
            patient.setLastName(lastNameString);
            patient.setGender(seleccionRG);
            patient.setBirthday(birthDateString);
            patient.setEmail(emailString);
            patient.setPassword(passwordString);
            patient.setDiagnostic(diagnosticString);
            patient.setDateDiagnostic(dateDiagnosticString);
            patient.setRole(Constants.Patients);
            patient.setRole(Constants.Patients);
            String[] assignsArray = {firebaseUser.getUid()};
            List<String> assigns = Arrays.asList(assignsArray);
            patient.setAssigns(assigns);
            return true;
        } else {
            return false;
        }
    }

    private void handleSaveButtonClick() {
        firebaseAuth.createUserWithEmailAndPassword(patient.getEmail(), patient.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser newUser = task.getResult().getUser();
                            String uIDPatient = newUser.getUid();
                            Log.d("AddPatient uIDPatient: ", uIDPatient);
                            patient.setPatientUID(uIDPatient);

                            if (uriImage != null) {
                                final StorageReference imgRef = storageReference.child("Users/Patients/" + patient.getPatientUID() + ".jpg");
                                imgRef.putFile(uriImage)
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                                uriTask.addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Uri> task) {
                                                        if (task.isSuccessful()) {
                                                            Uri downloadUri = task.getResult();
                                                            patient.setUriImg(downloadUri.toString());

                                                            db.collection(Constants.Patients).document(patient.getPatientUID()).set(patient)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Toast.makeText(requireContext(), getResources().getString(R.string.was_saved_succesfully), Toast.LENGTH_SHORT).show();

                                                                            loginManager.reAuthenticateAndRedirect(
                                                                                    uIDHPoCarer,
                                                                                    requireContext(),
                                                                                    PatientsList.class
                                                                            );

                                                                            circularProgressUtil.hideProgress();
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Log.d("message: ", e.toString());
                                                                        }
                                                                    });
                                                        } else {
                                                            Log.d("message: ", "Failed to get download URL");
                                                        }
                                                    }
                                                });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("message: ", e.toString());
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(requireContext(), R.string.registration_failed + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("message: ", R.string.registration_failed + task.getException().getMessage());
                        }
                    }
                });
    }

    public interface OnFragmentInteractionListener{
        public void onFragmentInteraction(Uri uri);
    }
}