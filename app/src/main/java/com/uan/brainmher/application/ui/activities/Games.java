package com.uan.brainmher.application.ui.activities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.uan.brainmher.R;
import com.uan.brainmher.application.ui.fragments.games.Memorama;
import com.uan.brainmher.application.ui.fragments.games.PhysicalExercisePractic;

public class Games extends AppCompatActivity implements Memorama.Memoramai {

    //region Reference
    private FrameLayout container;
    private LinearLayout progresCont;
    private ProgressBar progressBar;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);

        // Binding views
        container = findViewById(R.id.contanedor_games);
        progresCont = findViewById(R.id.progres_cont);
        progressBar = findViewById(R.id.progressBar_init);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Back arrow
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        iniciarProgres(getIntent().getExtras().getString("Game", "Memorama"));
    }

    private void iniciarProgres(final String typeGame) {
        // Display progress
        progresCont.setVisibility(View.VISIBLE);
        container.setVisibility(View.GONE);

        ObjectAnimator anim = ObjectAnimator.ofInt(progressBar, "progress", 0, 100);
        anim.setDuration(3000);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.start();

        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                // Start fragment
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                if ("Memorama".equals(typeGame)) {
                    ft.replace(R.id.contanedor_games, new Memorama(Games.this));
                } else {
                    int time = getIntent().getExtras().getInt("Time");
                    String img = getIntent().getExtras().getString("Image");
                    ft.replace(R.id.contanedor_games, new PhysicalExercisePractic(Games.this, img, time));
                }
                ft.commit();

                // Hide progress and show container
                progresCont.setVisibility(View.GONE);
                container.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void reloadGame(String typeGame) {
        iniciarProgres(typeGame);
    }

    @Override
    public void callOnbackPressed() {
        finish();
    }
}