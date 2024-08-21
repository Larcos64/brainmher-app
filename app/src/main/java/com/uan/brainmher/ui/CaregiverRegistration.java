package com.uan.brainmher.ui;

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
import com.uan.brainmher.data.Carer;
import com.uan.brainmher.databinding.ActivityCaregiverRegistrationBinding;
import com.uan.brainmher.tools.Constants;

import java.util.Calendar;

public class CaregiverRegistration extends AppCompatActivity {
    private ActivityCaregiverRegistrationBinding binding;
    private ActivityResultLauncher<Intent> startActivityLauncher;

    String nameSring, lastNameString, typeIDString, idString, birthDateString, nativeCityString, actualCityString, addressString, emailString, userString, passwordString, seleccionRG, phoneString, profession, workC;

    FirebaseAuth auth;
    FirebaseUser users;
    FirebaseFirestore db;
    StorageReference storageReference;

    Carer carer = new Carer();
    String uIDCarer;
    Uri uriImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using View Binding
        binding = ActivityCaregiverRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        uriImage = Uri.parse("android.resource://" + getPackageName() + "/"+ R.drawable.img_add_image);
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
                        // Load the selected image into the ImageView
                        Glide.with(CaregiverRegistration.this)
                                .load(imageUri)
                                .fitCenter()
                                .into(binding.civProfileImage);
                    }
                });

        // onClick ivBirthDate
        binding.civProfileImage.setOnClickListener(v -> handleProfileImageClick());

        setupDropdown(binding.ddlIdentificationType, R.array.identification_types);

        // TextWatcher txtPassword
        binding.txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validatePassword(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // onClick btnSaveButton
        binding.btnSave.setOnClickListener(v -> {
            if (setPojoCarers()) {
                handleSaveButtonClick();
            } else {
                Toast.makeText(CaregiverRegistration.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            }
        });

        // onClick ivBirthDate
        binding.ivBirthDate.setOnClickListener(v -> handleBirthDateImageClick());
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

    // Validate password method
    private void validatePassword(String password) {
        if (password.length() < 7) {
            binding.txtPassword.setError(getString(R.string.val_min_password));
        } else {
            binding.txtPassword.setError(null);
        }
    }

    // Calendar image view method
    private void handleBirthDateImageClick() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(CaregiverRegistration.this, (view, year, monthOfYear, dayOfMonth) ->
                binding.txtBirthDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year), mYear, mMonth, mDay);
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
        auth.createUserWithEmailAndPassword(carer.getEmail(), carer.getPassword())
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
                                                                            Toast.makeText(CaregiverRegistration.this, getResources().getString(R.string.was_saved_succesfully), Toast.LENGTH_SHORT).show();
                                                                            Intent intent = new Intent(CaregiverRegistration.this, MainCarer.class);
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
        nameSring = binding.txtName.getText().toString().trim();
        lastNameString = binding.txtLastname.getText().toString().trim();
        typeIDString = binding.ddlIdentificationType.getText().toString().trim();
        idString = binding.txtDocumet.getText().toString().trim();

        if (binding.rgGender.getCheckedRadioButtonId() != -1) {
            int radioButtonId = binding.rgGender.getCheckedRadioButtonId();
            RadioButton rb = binding.getRoot().findViewById(radioButtonId);
            seleccionRG = rb.getText().toString();
        }

        birthDateString = binding.txtBirthDate.getText().toString().trim();
        phoneString = binding.txtTelephone.getText().toString().trim();
        nativeCityString = binding.txtNativeCity.getText().toString().trim();
        addressString = binding.txtAddress.getText().toString().trim();
        actualCityString = binding.txtCity.getText().toString().trim();
        emailString = binding.txtEmail.getText().toString().trim();
        userString = binding.txtUser.getText().toString().trim();
        passwordString = binding.txtPassword.getText().toString().trim();
        profession = binding.txtProfession.getText().toString().trim();
        workC = binding.txtWorkplace.getText().toString().trim();

        boolean isValid = !nameSring.isEmpty() && !lastNameString.isEmpty() && !typeIDString.isEmpty()
                && !idString.isEmpty() && !seleccionRG.isEmpty() && !birthDateString.isEmpty()
                && !phoneString.isEmpty() && !nativeCityString.isEmpty() && !actualCityString.isEmpty()
                && !addressString.isEmpty() && !emailString.isEmpty() && !userString.isEmpty()
                && !passwordString.isEmpty() && !profession.isEmpty() && !workC.isEmpty()
                && emailString.length() >= 7;

        if (isValid) {
            carer.setFirstName(nameSring);
            carer.setLastName(lastNameString);
            carer.setIdentificationType(typeIDString);
            carer.setIdentification(idString);
            carer.setGender(seleccionRG);
            carer.setBirthday(birthDateString);
            carer.setPhoneNumber(Long.parseLong(phoneString));
            carer.setNativeCity(nativeCityString);
            carer.setActualCity(actualCityString);
            carer.setAddress(addressString);
            carer.setEmail(emailString);
            carer.setUserName(userString);
            carer.setPassword(passwordString);
            carer.setProfession(profession);
            carer.setEmploymentPlace(workC);
            carer.setRole(Constants.Carers);
            return true;
        } else {
            return false;
        }
    }

}
