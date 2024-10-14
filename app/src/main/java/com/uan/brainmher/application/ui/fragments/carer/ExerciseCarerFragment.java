package com.uan.brainmher.application.ui.fragments.carer;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.uan.brainmher.R;
import com.uan.brainmher.application.ui.adapters.stretching_exercises.StretchingExercisesAdapter;
import com.uan.brainmher.databinding.FragmentExerciseCarerBinding;
import com.uan.brainmher.domain.entities.StretchingExercise;
import com.uan.brainmher.domain.repositories.StretchingExercisesRepository;

import java.util.ArrayList;
import java.util.List;

public class ExerciseCarerFragment extends Fragment {

    private FragmentExerciseCarerBinding binding;
    private StretchingExercisesAdapter adapter;
    private StretchingExercisesAdapter.ISelectionStretching iSelectionStretching;
    private final StretchingExercisesRepository repository = new StretchingExercisesRepository();
    private List<StretchingExercise> stretchingList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentExerciseCarerBinding.inflate(inflater, container, false);
        setupRecyclerView();
        setupStretchingSelection();
        loadStretchingExercises();
        return binding.getRoot();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setHasFixedSize(true);
    }

    private void setupStretchingSelection() {
        iSelectionStretching = stretchingExercise -> showStretchingDialog(stretchingExercise);
    }

    private void showStretchingDialog(StretchingExercise stretchingExercise) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.BackgroundRounded);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                View dialogView = LayoutInflater.from(getActivity()).inflate(
                        R.layout.dialog_one_textview_one_button_one_image, null);
                builder.setView(dialogView);

                AlertDialog alertDialog = builder.create();

                Button btnClose = dialogView.findViewById(R.id.btn1);
                btnClose.setText(R.string.close);
                btnClose.setOnClickListener(view -> alertDialog.dismiss());

                TextView tvName = dialogView.findViewById(R.id.textViewBold);
                tvName.setText(stretchingExercise.getNameExercise());

                TextView tvDescription = dialogView.findViewById(R.id.textView);
                tvDescription.setText(stretchingExercise.getDescriptionExercise());

                ImageView gifView = dialogView.findViewById(R.id.gif_stretching);
                Glide.with(requireContext()).load(stretchingExercise.getUriGif()).fitCenter().into(gifView);

                alertDialog.show();
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }
        } else {
            builder.setTitle(R.string.alert)
                    .setMessage(stretchingExercise.getNameExercise() + " " + stretchingExercise.getDescriptionExercise())
                    .show();
        }
    }

    private void loadStretchingExercises() {
        repository.getStretchingExercises(new StretchingExercisesRepository.OnStretchingExercisesLoaded() {
            @Override
            public void onSuccess(List<StretchingExercise> exercises) {
                stretchingList = exercises;
                adapter = new StretchingExercisesAdapter(stretchingList, iSelectionStretching);
                binding.recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("ExerciseCarerFragment", "Error loading exercises", e);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}