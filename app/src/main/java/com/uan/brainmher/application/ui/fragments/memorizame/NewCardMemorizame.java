package com.uan.brainmher.application.ui.fragments.memorizame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.uan.brainmher.R;
import com.uan.brainmher.application.ui.helpers.MemorizameFormHelper;
import com.uan.brainmher.application.ui.helpers.NavigationHelper;
import com.uan.brainmher.domain.entities.Patient;
import com.uan.brainmher.databinding.FragmentNewCardMemorizameBinding;
import com.uan.brainmher.domain.entities.Memorizame;
import com.uan.brainmher.domain.repositories.MemorizameRepository;
import com.uan.brainmher.infraestructure.tools.CircularProgressUtil;

public class NewCardMemorizame extends Fragment {

    private int flagInt;
    private String folderCategory = "";
    private Uri uriImage;
    private Memorizame memorizame;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private Patient patient;
    private FragmentNewCardMemorizameBinding binding;
    private MemorizameRepository memorizameRepository;
    private CircularProgressUtil circularProgressUtil;
    private MemorizameFormHelper formHelper;

    public NewCardMemorizame(int flagInt) {
        this.flagInt = flagInt;
    }

    private ActivityResultLauncher<Intent> startActivityLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        memorizameRepository = new MemorizameRepository();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNewCardMemorizameBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        Bundle bundle = getArguments();
        if (bundle != null) {
            patient = (Patient) bundle.getSerializable("patient");
            memorizame = new Memorizame();
            memorizame.setPatientUID(patient.getPatientUID());
        }

        circularProgressUtil = new CircularProgressUtil(getActivity());

        // Inicializar el helper con el binding y el memorizame actual
        formHelper = new MemorizameFormHelper(requireContext(), memorizame, binding);

        setCategoryAndTitle();
        formHelper.setupDropdownMenu();
        setupSaveButton();

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
        formHelper.setupImagePicker(startActivityLauncher);

        return view;
    }

    private void setCategoryAndTitle() {
        switch (flagInt) {
            case 1:
                folderCategory = "Family";
                binding.tvTitleQuestion.setText(R.string.add_family_question);
                break;
            case 2:
                folderCategory = "Pets";
                binding.tvTitleQuestion.setText(R.string.add_pets_question);
                break;
            case 3:
                folderCategory = "Home";
                binding.tvTitleQuestion.setText(R.string.add_home_question);
                break;
            case 4:
                folderCategory = "Places";
                binding.tvTitleQuestion.setText(R.string.add_places_question);
                break;
        }
    }

    private void setupSaveButton() {
        binding.buttonCreateMemorizame.setOnClickListener(v -> {
            if (formHelper.setPojoMemorizame(uriImage, patient)) {
                saveMemorizame();  // Llama a guardar solo si setPojoMemorizame() retorna true
            } else {
                Toast.makeText(requireContext(), getString(R.string.complete_field), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveMemorizame() {
        circularProgressUtil.showProgress(getResources().getString(R.string.registering));

        memorizameRepository.createMemorizame(memorizame, folderCategory, uriImage, null, false, new MemorizameRepository.OnMemorizameCreatedListener() {
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
        Glide.with(requireContext()).load(R.drawable.img_add_image).fitCenter().into(binding.civProfileImage);
        binding.editQuestion.setText("");
        binding.editAnswer1.setText("");
        binding.editAnswer2.setText("");
        binding.editAnswer3.setText("");
        binding.editAnswer4.setText("");
        binding.editCorrectAnswer.setText("");
        NavigationHelper.navigateToFragment(requireActivity().getSupportFragmentManager(), MemorizameFamilyFragment.class, R.id.container_memorizame_parent);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavigationHelper.navigateToFragment(requireActivity().getSupportFragmentManager(), MemorizameFamilyFragment.class, R.id.container_memorizame_parent);
            }
        });
    }
}