package com.uan.brainmher.infraestructure.tools;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import androidx.core.content.ContextCompat;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.uan.brainmher.R;

public class CircularProgressUtil {
    private Activity activity;
    private View progressView;
    private CircularProgressIndicator circularProgressIndicator;

    public CircularProgressUtil(Activity activity) {
        this.activity = activity;
        // Inflar el layout de progreso circular
        progressView = LayoutInflater.from(activity).inflate(R.layout.layout_circular_progress, null);
        circularProgressIndicator = progressView.findViewById(R.id.circularProgressIndicator);

        // Configurar colores programáticamente
        //setIndicatorColor(ContextCompat.getColor(activity, R.color.teal_200), ContextCompat.getColor(activity, R.color.track_gray));
    }

    // Método para configurar colores programáticamente
    public void setIndicatorColor(int indicatorColor, int trackColor) {
        circularProgressIndicator.setIndicatorColor(indicatorColor);
        circularProgressIndicator.setTrackColor(trackColor);
    }

    // Mostrar el progreso con animación de desvanecimiento (fade in)
    public void showProgress() {
        if (progressView.getParent() == null) {
            FrameLayout decorView = (FrameLayout) activity.getWindow().getDecorView().findViewById(android.R.id.content);
            decorView.addView(progressView);
        }
        Animation fadeIn = AnimationUtils.loadAnimation(activity, R.anim.fade_in);
        circularProgressIndicator.startAnimation(fadeIn);
        circularProgressIndicator.show();
    }

    // Ocultar el progreso con animación de desvanecimiento (fade out)
    public void hideProgress() {
        if (circularProgressIndicator != null && progressView.getParent() != null) {
            Animation fadeOut = AnimationUtils.loadAnimation(activity, R.anim.fade_out);
            circularProgressIndicator.startAnimation(fadeOut);
            circularProgressIndicator.hide();
            ((FrameLayout) progressView.getParent()).removeView(progressView);
        }
    }
}