package com.uan.brainmher.application.ui.fragments.memorizame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uan.brainmher.R;
import com.uan.brainmher.domain.entities.Patient;
import com.uan.brainmher.databinding.FragmentNewCardMemorizameBinding;
import com.uan.brainmher.domain.entities.Memorizame;
import com.uan.brainmher.infraestructure.tools.Constants;

import java.util.UUID;

public class NewCardMemorizame extends Fragment {

    private int flagInt;
    private String patientUID, question, answer1, answer2, answer3, answer4, categoria = "";
    private Uri uriImage;
    private Memorizame memorizame = new Memorizame();
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private Patient patient;
    private Context context;
    private FragmentNewCardMemorizameBinding binding;

    public static final int REQUEST_CODE2 = 10;

    public NewCardMemorizame(int flagInt) {
        this.flagInt = flagInt;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNewCardMemorizameBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        context = container.getContext();
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        patientUID = firebaseUser.getUid();

        Bundle bundle = getArguments();
        if (bundle != null) {
            patient = (Patient) bundle.getSerializable("patient");
        }

        setCategoryAndTitle();
        setupDropdownMenu();
        setupSaveButton();
        setupImagePicker();

        return view;
    }

    private void setCategoryAndTitle() {
        switch (flagInt) {
            case 1:
                categoria = "Family";
                binding.tvTitleQuestion.setText(R.string.add_family_question);
                break;
            case 2:
                categoria = "Pets";
                binding.tvTitleQuestion.setText(R.string.add_pets_question);
                break;
            case 3:
                categoria = "Home";
                binding.tvTitleQuestion.setText(R.string.add_home_question);
                break;
            case 4:
                categoria = "Places";
                binding.tvTitleQuestion.setText(R.string.add_places_question);
                break;
        }
    }

    private void setupDropdownMenu() {
        String[] correctAnswerArray = {"1", "2", "3", "4"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.dropdown_menu_popup_item, correctAnswerArray);
        binding.editCorrectAnswer.setAdapter(arrayAdapter);
    }

    private void setupSaveButton() {
        binding.buttonCreateMemorizame.setOnClickListener(v -> {
            if (setPojoMemorizame()) {
                saveMemorizame();
                showAlert();
            }
        });
    }

    private void setupImagePicker() {
        binding.civProfileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, getString(R.string.select_photo)), REQUEST_CODE2);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE2 && resultCode == Activity.RESULT_OK && data != null) {
            uriImage = data.getData();
            if (uriImage != null) {
                Glide.with(getActivity()).load(uriImage).fitCenter().into(binding.civProfileImage);
            }
        }
    }

    private boolean setPojoMemorizame() {
        question = binding.editQuestion.getText().toString();
        answer1 = binding.editAnswer1.getText().toString();
        answer2 = binding.editAnswer2.getText().toString();
        answer3 = binding.editAnswer3.getText().toString();
        answer4 = binding.editAnswer4.getText().toString();
        String correct = binding.editCorrectAnswer.getText().toString();

        if (isFormValid(question, answer1, answer2, answer3, answer4, correct)) {
            memorizame.setQuestion(question);
            memorizame.setAnswer1(answer1);
            memorizame.setAnswer2(answer2);
            memorizame.setAnswer3(answer3);
            memorizame.setAnswer4(answer4);
            memorizame.setPatientUID(patientUID);
            setCorrectAnswer(correct);
            return true;
        } else {
            Toast.makeText(getActivity(), uriImage == null ? "Agregue imagen" : getString(R.string.complete_field), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean isFormValid(String... fields) {
        for (String field : fields) {
            if (field.isEmpty()) return false;
        }
        return uriImage != null;
    }

    private void setCorrectAnswer(String correct) {
        switch (correct) {
            case "1":
                memorizame.setCorrectAnswer(answer1);
                break;
            case "2":
                memorizame.setCorrectAnswer(answer2);
                break;
            case "3":
                memorizame.setCorrectAnswer(answer3);
                break;
            case "4":
                memorizame.setCorrectAnswer(answer4);
                break;
        }
    }

    public void saveMemorizame() {
        final String uuidGenerated = createTransactionID();
        memorizame.setUuidGenerated(uuidGenerated);

        if (uriImage != null) {
            final StorageReference imgRef = storageReference.child(categoria + "/" + uuidGenerated + ".jpg");
            imgRef.putFile(uriImage).addOnSuccessListener(taskSnapshot -> {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                uriTask.addOnCompleteListener(uriComplete -> {
                    if (uriComplete.isSuccessful()) {
                        memorizame.setUriImg(uriComplete.getResult().toString());
                        db.collection(Constants.Memorizame).document(patient.getPatientUID())
                                .collection(categoria).document(uuidGenerated).set(memorizame)
                                .addOnSuccessListener(aVoid -> Toast.makeText(getActivity(), "Tarjeta Memorizame guardada exitosamente", Toast.LENGTH_SHORT).show());
                    }
                });
            });
        }
    }

    private String createTransactionID() {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }

    private void showAlert() {
        new AlertDialog.Builder(context, R.style.BackgroundRounded)
                .setView(LayoutInflater.from(context).inflate(R.layout.dialog_one_textview_two_buttons, null))
                .setCancelable(false)
                .setNegativeButton(R.string.no, (dialog, which) -> {
                    clearForm();
                    dialog.dismiss();
                })
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    clearForm();
                    dialog.dismiss();
                })
                .show();
    }

    private void clearForm() {
        Glide.with(context).load(R.drawable.img_add_image).fitCenter().into(binding.civProfileImage);
        binding.editQuestion.setText("");
        binding.editAnswer1.setText("");
        binding.editAnswer2.setText("");
        binding.editAnswer3.setText("");
        binding.editAnswer4.setText("");
        binding.editCorrectAnswer.setText("");
        navigateToMemorizameParent();
    }

    private void navigateToMemorizameParent() {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container_memorizame_parent, new MemorizameParentFragment()).commit();
    }
}