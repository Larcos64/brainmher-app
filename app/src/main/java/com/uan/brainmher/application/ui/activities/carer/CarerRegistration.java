package com.uan.brainmher.application.ui.activities.carer;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uan.brainmher.R;
import com.uan.brainmher.domain.entities.Carer;
import com.uan.brainmher.databinding.ActivityCarerRegistrationBinding;
import com.uan.brainmher.infraestructure.tools.Constants;

import java.util.Calendar;

public class CarerRegistration extends AppCompatActivity {
    private ActivityCarerRegistrationBinding binding;
    private ActivityResultLauncher<Intent> startActivityLauncher;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private StorageReference storageReference;

    private Carer carer = new Carer();
    private Uri uriImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using View Binding
        binding = ActivityCarerRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        setupListeners();
        verifyFields();

        uriImage = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.img_add_image);
        Glide.with(this).load(uriImage).fitCenter().into(binding.civProfileImage);

        setSupportActionBar(binding.toolbarRegistrationCarer);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize ActivityResultLauncher
        startActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        // Update the URI of the new selected image
                        uriImage = imageUri;

                        // Load the selected image into the ImageView
                        Glide.with(CarerRegistration.this)
                                .load(imageUri)
                                .fitCenter()
                                .into(binding.civProfileImage);
                    }
                });

        setupDropdown(binding.ddlResidenceCountry, R.array.residence_countries);
    }

    private void setupListeners() {
        binding.civProfileImage.setOnClickListener(v -> handleProfileImageClick());
        binding.ivBirthDate.setOnClickListener(v -> handleDateImageClick(v));
        binding.btnSave.setOnClickListener(v -> {
            if (setPojoCarers()) {
                handleSaveButtonClick();
            } else {
                Toast.makeText(CarerRegistration.this, R.string.complete_field, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Dropdown ddlIdentificationType
    private void setupDropdown(AutoCompleteTextView autoCompleteTextView, int optionsArrayResId) {
        String[] options = getResources().getStringArray(optionsArrayResId);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, options);
        autoCompleteTextView.setAdapter(adapter);
    }

    // Save button method
    private void handleProfileImageClick() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityLauncher.launch(intent.createChooser(intent, getString(R.string.select_photo)));
    }

    private void handleDateImageClick(View view) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(CarerRegistration.this, (datePicker, year, monthOfYear, dayOfMonth) -> {
            if (view.getId() == binding.ivBirthDate.getId()) {
                binding.txtBirthDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
            }
        }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Save button method
    private void handleSaveButtonClick() {
        firebaseAuth.createUserWithEmailAndPassword(carer.getEmail(), carer.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            String uIDCarer = user.getUid();
                            carer.setCarerUId(uIDCarer);

                            if (uriImage != null) {
                                final StorageReference imgRef = storageReference.child("Users/Carers/" + carer.getCarerUId() + ".jpg");
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
                                                            carer.setUriImg(downloadUri.toString());

                                                            db.collection(Constants.Carers).document(carer.getCarerUId()).set(carer)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Toast.makeText(CarerRegistration.this, getResources().getString(R.string.was_saved_succesfully), Toast.LENGTH_SHORT).show();
                                                                            Intent intent = new Intent(CarerRegistration.this, MainCarer.class);
                                                                            startActivity(intent);
                                                                            finish();
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
                            Log.d("message: ", "Registration failed");
                        }
                    }
                });
    }


    private boolean setPojoCarers() {
        String nameSring = binding.txtName.getText().toString().trim();
        String lastNameString = binding.txtLastname.getText().toString().trim();
        String residenceCountry = binding.ddlResidenceCountry.getText().toString().trim();

        String seleccionRG = "";
        if (binding.rgGender.getCheckedRadioButtonId() != -1) {
            int radioButtonId = binding.rgGender.getCheckedRadioButtonId();
            RadioButton rb = binding.getRoot().findViewById(radioButtonId);
            seleccionRG = rb.getText().toString();
        }

        String birthDateString = binding.txtBirthDate.getText().toString().trim();
        String phoneString = binding.txtTelephone.getText().toString().trim();
        String emailString = binding.txtEmail.getText().toString().trim();
        String passwordString = binding.txtPassword.getText().toString().trim();
        String profession = binding.txtProfession.getText().toString().trim();

        boolean isValid = !nameSring.isEmpty() && !lastNameString.isEmpty() && !residenceCountry.isEmpty()
                && !seleccionRG.isEmpty() && !birthDateString.isEmpty() && !phoneString.isEmpty() && !emailString.isEmpty()
                && !passwordString.isEmpty() && !profession.isEmpty() && emailString.length() >= 7;

        if (isValid) {
            carer.setFirstName(nameSring);
            carer.setLastName(lastNameString);
            carer.setResidenceCountry(residenceCountry);
            carer.setGender(seleccionRG);
            carer.setBirthday(birthDateString);
            carer.setPhoneNumber(Long.parseLong(phoneString));
            carer.setEmail(emailString);
            carer.setPassword(passwordString);
            carer.setProfession(profession);
            carer.setRole(Constants.Carers);
            return true;
        } else {
            return false;
        }
    }

    private void verifyFields() {
        binding.txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.txtPassword.setError(null);
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
                binding.txtPassword.setError(getString(R.string.val_min_password));
                return false;
            }
        }
        return true;
    }
}
