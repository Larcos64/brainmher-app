package com.uan.brainmher.application.ui.fragments.health_professional;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CognitiveTherapyPSFragment extends Fragment {

    public CognitiveTherapyPSFragment() {
        // Constructor vacío requerido
    }

    @Nullable
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Aquí puedes recuperar el bundle si lo necesitas
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            // Procesar los argumentos según sea necesario
        }
    }
}