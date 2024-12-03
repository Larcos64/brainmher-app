package com.uan.brainmher.application.ui.fragments.general;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.uan.brainmher.R;
import com.uan.brainmher.databinding.FragmentProfileBinding;
import com.uan.brainmher.domain.entities.Carer;
import com.uan.brainmher.domain.repositories.CarerRepository;
import com.uan.brainmher.infraestructure.tools.CircularProgressUtil;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private CarerRepository carerRepository;
    private CircularProgressUtil circularProgressUtil;
    private Carer carer = new Carer();
    private Uri uriImage;
    private String role, profile_type;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        circularProgressUtil = new CircularProgressUtil(getActivity());
        carerRepository = new CarerRepository();

        getUserData();

        return binding.getRoot();
    }

    private void getUserData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            profile_type = bundle.getString("profileType");
            role = bundle.getString("userRole");

            if ("personal".equals(profile_type)) {
                binding.tvProfileTitle.setText(getString(R.string.lbl_my_profile));
            }

            String uID = bundle.getString("userUid");

            if ("Carers".equals(role)) {
                carerRepository.getCarerById(uID, documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        carer = documentSnapshot.toObject(Carer.class);
                        setDataCarer(carer);
                    }
                    setupUpdateButton();
                }, e -> Log.d("ProfileFragment", "Error fetching data: " + e.toString()));
            }
        }
    }

    private void setupUpdateButton() {
        binding.btnUpdate.setOnClickListener(view -> {
            if (setPojoCarers()) {
                circularProgressUtil.showProgress(getString(R.string.updating));

                if (uriImage != null) {
                    carerRepository.deleteImage(carer.getCarerUId(),
                            aVoid -> carerRepository.uploadImage(uriImage, carer.getCarerUId(), uri -> {
                                carer.setUriImg(uri.toString());
                                saveCarerData();
                            }, this::showError),
                            this::showError);
                } else {
                    saveCarerData();
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.complete_field), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveCarerData() {
        carerRepository.updateCarer(carer, aVoid -> {
            circularProgressUtil.hideProgress();
            Toast.makeText(getActivity(), getString(R.string.was_saved_succesfully), Toast.LENGTH_SHORT).show();
        }, this::showError);
    }

    private void setDataCarer(Carer carer) {
        Glide.with(this).load(carer.getUriImg()).fitCenter().into(binding.civProfile);
        binding.txtName.setText(carer.getFirstName());
        binding.txtLastname.setText(carer.getLastName());
        binding.txtResidenceCountry.setText(carer.getResidenceCountry(), false);
        binding.txtProfession.setText(carer.getProfession());

        String selectedGender = carer.getGender();
        if ("Femenino".equals(selectedGender)) {
            binding.rbFemale.setChecked(true);
        } else if ("Masculino".equals(selectedGender)) {
            binding.rbMale.setChecked(true);
        }

        binding.txtBirthDate.setText(carer.getBirthday());
        binding.txtTelephone.setText(String.valueOf(carer.getPhoneNumber()));
        // binding.txtPassword.setText(carer.getPassword());
        binding.txtEmail.setText(carer.getEmail());
    }

    private boolean setPojoCarers() {
        String name = binding.txtName.getText().toString().trim();
        String lastName = binding.txtLastname.getText().toString().trim();
        String residenceCountry = binding.txtResidenceCountry.getText().toString().trim();
        String gender = getSelectedGender();
        String birthDate = binding.txtBirthDate.getText().toString().trim();
        String phone = binding.txtTelephone.getText().toString().trim();
        String email = binding.txtEmail.getText().toString().trim();
        // String password = binding.txtPassword.getText().toString().trim();
        String profession = binding.txtProfession.getText().toString().trim();

        if (name.isEmpty() || lastName.isEmpty() || residenceCountry.isEmpty() || gender.isEmpty() ||
                birthDate.isEmpty() || phone.isEmpty() || email.isEmpty() || /* password.isEmpty() ||*/ profession.isEmpty()) {
            return false;
        }

        carer.setFirstName(name);
        carer.setLastName(lastName);
        carer.setResidenceCountry(residenceCountry);
        carer.setGender(gender);
        carer.setBirthday(birthDate);
        carer.setPhoneNumber(Long.parseLong(phone));
        carer.setEmail(email);
        // carer.setPassword(password);
        carer.setProfession(profession);

        return true;
    }

    private String getSelectedGender() {
        int selectedId = binding.rgGender.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton radioButton = binding.rgGender.findViewById(selectedId);
            return radioButton.getText().toString();
        }
        return "";
    }

    private void showError(Exception e) {
        circularProgressUtil.hideProgress();
        Log.d("ProfileFragment", "Error: " + e.toString());
    }
}