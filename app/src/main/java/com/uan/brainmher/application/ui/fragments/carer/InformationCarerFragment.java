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
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.expandable.ExpandableWidgetHelper;
import com.uan.brainmher.R;
import com.uan.brainmher.databinding.FragmentInformationListBinding;
import com.uan.brainmher.domain.entities.AlzheimerInformation;
import com.uan.brainmher.domain.entities.CarerInformation;
import com.uan.brainmher.domain.repositories.InformationCarerRepository;

import java.util.List;

public class InformationCarerFragment extends Fragment {

    private FragmentInformationListBinding binding;
    private InformationCarerRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInformationListBinding.inflate(inflater, container, false);
        repository = new InformationCarerRepository();
        loadCareInformation("general"); // Puedes ajustar el tipo según el filtro deseado
        return binding.getRoot();
    }

    private void loadCareInformation(String type) {
        repository.getCarerInformation(type, new InformationCarerRepository.OnInformationCarerLoaded() {
            @Override
            public void onSuccess(List<CarerInformation> informationList) {
                displayInformationPanels(informationList);
            }

            @Override
            public void onFailure(Exception e) {
                // Manejo del error
            }
        });
    }

    private void displayInformationPanels(List<CarerInformation> informationList) {
        for (CarerInformation info : informationList) {
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
