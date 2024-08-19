package com.uan.brainmher.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.uan.brainmher.R;
import com.uan.brainmher.databinding.ActivityCaregiverRegistrationBinding;

import java.util.Calendar;

public class CaregiverRegistration extends AppCompatActivity {
    private ActivityCaregiverRegistrationBinding binding;
    private ActivityResultLauncher<Intent> startActivityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using View Binding
        binding = ActivityCaregiverRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Uri uriImage = Uri.parse("android.resource://" + getPackageName() + "/"+ R.drawable.img_add_image);
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
        binding.btnSave.setOnClickListener(v -> handleSaveButtonClick());

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

    // Save button method
    private void handleSaveButtonClick() {
        Toast.makeText(this, "Save button clicked", Toast.LENGTH_SHORT).show();
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
}
