package com.uan.brainmher.infraestructure.tools;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.uan.brainmher.R;

public class CircularProgressUtil {
    private Activity activity;
    private View progressView;
    private CircularProgressIndicator circularProgressIndicator;
    private TextView progressMessage; // TextView para el mensaje personalizado

    public CircularProgressUtil(Activity activity) {
        this.activity = activity;
        // Inflar el layout de progreso circular
        progressView = LayoutInflater.from(activity).inflate(R.layout.layout_circular_progress, null);
        circularProgressIndicator = progressView.findViewById(R.id.circularProgressIndicator);
        progressMessage = progressView.findViewById(R.id.txtMessage); // Inicializar el TextView del mensaje
    }

    // Método para configurar colores programáticamente
    public void setIndicatorColor(int indicatorColor, int trackColor) {
        circularProgressIndicator.setIndicatorColor(indicatorColor);
        circularProgressIndicator.setTrackColor(trackColor);
    }

    // Mostrar el progreso con mensaje personalizado y animación de desvanecimiento (fade in)
    public void showProgress(String message) {
        if (progressView.getParent() == null) {
            FrameLayout decorView = (FrameLayout) activity.getWindow().getDecorView().findViewById(android.R.id.content);
            decorView.addView(progressView);
        }
        if (message != null) {
            progressMessage.setText(message); // Establecer mensaje personalizado
        }

        Animation fadeIn = AnimationUtils.loadAnimation(activity, R.anim.fade_in);
        progressView.startAnimation(fadeIn); // Animar la vista completa
        circularProgressIndicator.show();
    }

    // Ocultar el progreso con animación de desvanecimiento (fade out)
    public void hideProgress() {
        if (circularProgressIndicator != null && progressView.getParent() != null) {
            Animation fadeOut = AnimationUtils.loadAnimation(activity, R.anim.fade_out);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    // Opcional: puedes realizar acciones aquí cuando inicie la animación de salida
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // Aquí es donde removemos la vista después de que termine la animación
                    circularProgressIndicator.hide();
                    ((FrameLayout) progressView.getParent()).removeView(progressView);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // No necesario para este caso
                }
            });
            progressView.startAnimation(fadeOut); // Animar la vista completa
        }
    }
}
