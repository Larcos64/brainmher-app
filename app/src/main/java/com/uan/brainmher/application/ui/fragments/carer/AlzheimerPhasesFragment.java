package com.uan.brainmher.application.ui.fragments.carer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.uan.brainmher.R;
import com.uan.brainmher.databinding.FragmentInformationListBinding;
import com.uan.brainmher.domain.entities.AlzheimerInformation;
import com.uan.brainmher.domain.repositories.AlzheimerInformationRepository;

import java.util.List;

public class AlzheimerPhasesFragment extends Fragment {
    private FragmentInformationListBinding binding;
    private AlzheimerInformationRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInformationListBinding.inflate(inflater, container, false);
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
            TextView moreInfoLink = panelView.findViewById(R.id.more_info_link);

            button.setText(info.getTitle());

            // Convertir la descripción en HTML
            Spanned formattedDescription = Html.fromHtml(info.getDescription(), Html.FROM_HTML_MODE_COMPACT);
            descriptionView.setText(formattedDescription);

            // Configura el enlace si existe una URL
            if (info.getLink() != null && !info.getLink().isEmpty()) {
                moreInfoLink.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(info.getLink()));
                    startActivity(intent);
                });
            }

            button.setOnClickListener(v -> {
                if (descriptionView.getVisibility() == View.VISIBLE) {
                    // Oculta descripción y enlace
                    descriptionView.setVisibility(View.GONE);
                    moreInfoLink.setVisibility(View.GONE);
                    button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                } else {
                    // Muestra descripción y, si existe, el enlace
                    descriptionView.setVisibility(View.VISIBLE);
                    if (info.getLink() != null && !info.getLink().isEmpty()) {
                        moreInfoLink.setVisibility(View.VISIBLE);
                    }
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
