package com.uan.brainmher.application.ui.fragments.games;

import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.uan.brainmher.R;

import pl.droidsonroids.gif.GifImageView;

public class PhysicalExercisePractic extends Fragment {

    private static final String TAG = "PhysicalExercisePractic";

    // Views
    private TextView countDown;
    private GifImageView gifImageView;

    // Data
    private Memorama.Memoramai memoramai;
    private String img;
    private Integer time;

    public PhysicalExercisePractic() {
        // Default constructor required
    }

    /**
     * Set the callback interface for handling events.
     */
    public void setCallback(Memorama.Memoramai memoramai) {
        this.memoramai = memoramai;
    }

    /**
     * Set the image URL for the physical exercise.
     */
    public void setImage(String img) {
        this.img = img;
    }

    /**
     * Set the duration of the physical exercise in milliseconds.
     */
    public void setTime(Integer time) {
        this.time = time;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_physical_exercise_practic, container, false);

        // Bind views
        countDown = view.findViewById(R.id.text_view_countdown);
        gifImageView = view.findViewById(R.id.fragment_pep_gif);

        // Ensure required data is set
        if (img != null && time != null) {
            Glide.with(this).load(img).fitCenter().into(gifImageView);
            startCountDownTimer();
        } else {
            Log.e(TAG, "Missing required data: img or time is null");
        }

        return view;
    }

    private void startCountDownTimer() {
        new CountDownTimer(time, 1000) {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                countDown.setText(convertSecondsToHHMMSSString(seconds));
            }

            @Override
            public void onFinish() {
                showEndDialog();
            }
        }.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String convertSecondsToHHMMSSString(int nSecondTime) {
        return java.time.LocalTime.MIN.plusSeconds(nSecondTime).toString();
    }

    private void showEndDialog() {
        if (getContext() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            View dialogView = getLayoutInflater().inflate(R.layout.physical_exercise_end_template, null);
            builder.setView(dialogView);

            Button btnReload = dialogView.findViewById(R.id.physicla_exercise_winp_reload);
            Button btnBackToMenu = dialogView.findViewById(R.id.physical_exercise_winp_btnonback);

            AlertDialog dialog = builder.create();
            dialog.show();

            btnReload.setOnClickListener(v -> {
                dialog.dismiss();
                if (memoramai != null) {
                    memoramai.reloadGame("Physical");
                } else {
                    Log.e(TAG, "Memoramai callback is null");
                }
            });

            btnBackToMenu.setOnClickListener(v -> {
                dialog.dismiss();
                if (memoramai != null) {
                    memoramai.callOnbackPressed();
                } else {
                    Log.e(TAG, "Memoramai callback is null");
                }
            });
        } else {
            Log.e(TAG, "Context is null, cannot show dialog");
        }
    }
}