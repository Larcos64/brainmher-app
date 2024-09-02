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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uan.brainmher.R;
import com.uan.brainmher.databinding.ActivityMainCarerBinding;
import com.uan.brainmher.databinding.FragmentProfileBinding;
import com.uan.brainmher.domain.entities.Carer;
import com.uan.brainmher.infraestructure.tools.CircularProgressUtil;
import com.uan.brainmher.infraestructure.tools.Constants;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    StorageReference storageReference;
    private Carer carer = new Carer();

    String uidString, role, profile_type, nameSring, lastNameString, residenceCountryString, birthDayString,emailString, passwordString, seleccionRG, phoneString, profession;
    Uri uriImage;
    private CircularProgressUtil circularProgressUtil;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        circularProgressUtil = new CircularProgressUtil(getActivity());

        getUserData();

        return view;
    }

    private void getUserData() {
        db = FirebaseFirestore.getInstance();
        Bundle bundle = getArguments();
        if (bundle != null) {
            profile_type = bundle.getString("profileType");
            if (profile_type.equals("professional")) {
                //binding.tvProfileTitle.setText(getResources().getString(R.string.lbl_professionl_profile));
            } else if (profile_type.equals("personal")) {
                binding.tvProfileTitle.setText(getResources().getString(R.string.lbl_my_profile));
            }

            final String uID = bundle.getString("userUid");
            role = bundle.getString("userRole");

            if ("Carers".equals(role)) {
                db.collection(Constants.Carers).document(uID).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    carer = documentSnapshot.toObject(Carer.class);
                                    setDataCarer(carer);
                                }
                                binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        boolean flag2 = setPojoCarers();
                                        if (flag2) {
                                            circularProgressUtil.showProgress();

                                            if (uriImage != null) {
                                                deleteImage();
                                                final StorageReference imgRef = storageReference.child("Users/Carers/" + carer.getCarerUId() + ".jpg");
                                                imgRef.putFile(uriImage)
                                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                                                                while (!uri.isComplete()) ;
                                                                Uri url = uri.getResult();
                                                                carer.setUriImg(url.toString());
                                                                db.collection(Constants.Carers).document(carer.getCarerUId()).set(carer)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Toast.makeText(getActivity(), getResources().getString(R.string.was_saved_succesfully), Toast.LENGTH_SHORT).show();
                                                                                circularProgressUtil.hideProgress();
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Log.d("message: ", e.toString());
                                                                            }
                                                                        });
                                                            }
                                                        });
                                            } else {
                                                db.collection(Constants.Carers).document(carer.getCarerUId()).set(carer)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(getActivity(), getResources().getString(R.string.was_saved_succesfully), Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(getActivity(), getResources().getString(R.string.complete_field), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
            }
        }
    }

    private void setDataCarer(Carer carer) {
        // Establecer datos en los campos
        uidString = carer.getCarerUId();
        Glide.with(ProfileFragment.this).load(carer.getUriImg()).fitCenter().into(binding.civProfile);
        binding.txtName.setText(carer.getFirstName());
        binding.txtLastname.setText(carer.getLastName());
        binding.txtResidenceCountry.setText(carer.getResidenceCountry(), false);
        binding.txtProfession.setText(carer.getProfession());

        String selectedGender = carer.getGender();
        switch (selectedGender) {
            case "Femenino":
                binding.rbFemale.setChecked(true);
                break;
            case "Masculino":
                binding.rbMale.setChecked(true);
                break;
        }

        binding.txtBirthDate.setText(carer.getBirthday());
        binding.txtTelephone.setText(Long.toString(carer.getPhoneNumber()));
        binding.txtPassword.setText(carer.getPassword());
        binding.txtEmail.setText(carer.getEmail());
    }

    private boolean setPojoCarers() { //region get text of form
        boolean flag = true;

        uidString = carer.getCarerUId();
        nameSring = binding.txtName.getText().toString().trim();
        lastNameString = binding.txtLastname.getText().toString().trim();
        residenceCountryString = binding.txtResidenceCountry.getText().toString().trim();
        if (binding.rgGender.getCheckedRadioButtonId() != -1) {
            int radioButtonId = binding.rgGender.getCheckedRadioButtonId();
            View radioButton = binding.rgGender.findViewById(radioButtonId);
            int indice = binding.rgGender.indexOfChild(radioButton);
            RadioButton rb = (RadioButton)  binding.rgGender.getChildAt(indice);
            seleccionRG = rb.getText().toString();
        }
        birthDayString = binding.txtBirthDate.getText().toString().trim();
        phoneString = binding.txtTelephone.getText().toString().trim();
        emailString = binding.txtEmail.getText().toString().trim();
        passwordString = binding.txtPassword.getText().toString().trim();
        profession = binding.txtProfession.getText().toString().trim();

        if (!nameSring.isEmpty()&&!lastNameString.isEmpty()&&!residenceCountryString.isEmpty()
                &&!seleccionRG.isEmpty()&&!birthDayString.isEmpty()&&!phoneString.isEmpty()
                &&!emailString.isEmpty()&&!passwordString.isEmpty()&&!profession.isEmpty()&&emailString.length()>=7) {
            carer.setCarerUId(uidString);
            carer.setFirstName(nameSring);
            carer.setLastName(lastNameString);
            carer.setResidenceCountry(residenceCountryString);
            carer.setGender(seleccionRG);
            carer.setBirthday(birthDayString);
            carer.setPhoneNumber(Long.parseLong(phoneString));
            carer.setEmail(emailString);
            carer.setPassword(passwordString);
            carer.setProfession(profession);
            carer.setRole(Constants.Carers);
        }else{
            flag = false;
        }
        return flag;
    }

    private void deleteImage() {
        switch (role) {
            case "Carers":
                FirebaseStorage.getInstance().getReference();
                storageReference.child("Users/Carers/" + carer.getCarerUId() + ".jpg");
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });
                break;
        }
    }

}
