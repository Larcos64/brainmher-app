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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.uan.brainmher.R;
import pl.droidsonroids.gif.GifImageView;

public class PhysicalExercisePractic extends Fragment {

    private TextView countDown;
    private GifImageView gifImageView;
    private Memorama.Memoramai memoramai;
    private String img;
    private Integer time;

    public PhysicalExercisePractic(Memorama.Memoramai memoramai, String img, Integer time) {
        this.memoramai = memoramai;
        this.img = img;
        this.time = time;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_physical_exercise_practic, container, false);
        countDown = view.findViewById(R.id.text_view_countdown);
        gifImageView = view.findViewById(R.id.fragment_pep_gif);

        Glide.with(this).load(img).fitCenter().into(gifImageView);

        new CountDownTimer(time, 1000) {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onTick(long millisUntilFinished) {
                int seg = (int) (millisUntilFinished / 1000);
                countDown.setText(ConvertSecondToHHMMSSString(seg));
            }

            @Override
            public void onFinish() {
                showEndDialog();
            }
        }.start();

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String ConvertSecondToHHMMSSString(int nSecondTime) {
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
                memoramai.reloadGame("Physical");
            });

            btnBackToMenu.setOnClickListener(v -> {
                dialog.dismiss();
                memoramai.callOnbackPressed();
            });
        }
    }
}