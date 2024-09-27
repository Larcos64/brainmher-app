package com.uan.brainmher.application.ui.fragments.memorizame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.uan.brainmher.domain.repositories.MemorizameRepository;
import com.uan.brainmher.domain.repositories.PatientsRepository;
import com.uan.brainmher.infraestructure.tools.CircularProgressUtil;
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

    private ActivityResultLauncher<Intent> startActivityLauncher;
    private MemorizameRepository memorizameRepository;
    private CircularProgressUtil circularProgressUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        memorizameRepository = new MemorizameRepository(); // Asegúrate de inicializarlo aquí
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

        circularProgressUtil = new CircularProgressUtil(getActivity());

        startActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        uriImage = result.getData().getData();
                        Glide.with(requireContext())
                                .load(uriImage)
                                .fitCenter()
                                .into(binding.civProfileImage);
                    }
                });

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
                Toast.makeText(requireContext(), getString(R.string.was_saved_succesfully), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupImagePicker() {
        binding.civProfileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityLauncher.launch(Intent.createChooser(intent, getString(R.string.select_photo)));
        });
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
            memorizame.setPatientUID(patient.getPatientUID());
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

    private void saveMemorizame() {
        memorizameRepository.createMemorizame(memorizame, categoria, uriImage, new MemorizameRepository.OnMemorizameCreatedListener() {
            @Override
            public void onSuccess(Memorizame createdMemorizame) {
                circularProgressUtil.hideProgress();
                Toast.makeText(getActivity(), getResources().getString(R.string.was_saved_succesfully), Toast.LENGTH_SHORT).show();
                clearForm();
            }

            @Override
            public void onFailure(Exception e) {
                circularProgressUtil.hideProgress();
                Toast.makeText(getActivity(), getResources().getString(R.string.saving_error) + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        // Solo regresa al fragmento anterior (MemorizameFamilyFragment) sin reemplazar nada
        if (fragmentManager.getBackStackEntryCount() > 0) {
            Log.d("REGRESA", "POR IF");
            fragmentManager.popBackStack();
        } else {
            // En caso de que no haya nada en la pila de fragmentos, podrías manejar una acción alternativa
            // Como cargar directamente MemorizameFamilyFragment
            Log.d("REGRESA", "POR ELSE");
            fragmentManager.beginTransaction()
                    .replace(R.id.container_memorizame_parent, new MemorizameParentFragment())
                    .commit();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Aquí puedes manejar el retroceso y navegar al fragmento anterior
                navigateToMemorizameParent();
            }
        });
    }
}