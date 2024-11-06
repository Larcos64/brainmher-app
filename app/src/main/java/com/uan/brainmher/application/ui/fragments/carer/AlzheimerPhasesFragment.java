package com.uan.brainmher.application.ui.fragments.carer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.uan.brainmher.R;
import com.uan.brainmher.databinding.FragmentAlzheimerPhasesBinding;
import com.uan.brainmher.domain.entities.AlzheimerInformation;
import com.uan.brainmher.domain.repositories.AlzheimerInformationRepository;

import java.util.List;

public class AlzheimerPhasesFragment extends Fragment {
    private FragmentAlzheimerPhasesBinding binding;
    private AlzheimerInformationRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAlzheimerPhasesBinding.inflate(inflater, container, false);
        repository = new AlzheimerInformationRepository();
        loadAlzheimerInformation("phase"); // Ajustar el tipo según el filtro deseado
        return binding.getRoot();
    }

    private void loadAlzheimerInformation(String type) {
        repository.getAlzheimerInformation(type, new AlzheimerInformationRepository.OnAlzheimerInformationLoaded() {
            @Override
            public void onSuccess(List<AlzheimerInformation> informationList) {
                displayInformationPanels(informationList);
            }

            @Override
            public void onFailure(Exception e) {
                // Manejo del error
            }
        });
    }

    private void displayInformationPanels(List<AlzheimerInformation> informationList) {
        for (AlzheimerInformation info : informationList) {
            View panelView = LayoutInflater.from(getContext()).inflate(R.layout.panel_information, binding.container, false);

            MaterialButton button = panelView.findViewById(R.id.expand_button);
            TextView descriptionView = panelView.findViewById(R.id.description_text);

            button.setText(info.getTitle());
            descriptionView.setText(info.getDescription());

            button.setOnClickListener(v -> {
                if (descriptionView.getVisibility() == View.VISIBLE) {
                    descriptionView.setVisibility(View.GONE);
                    button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                } else {
                    descriptionView.setVisibility(View.VISIBLE);
                    button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
                }
            });

            binding.container.addView(panelView);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
