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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.uan.brainmher.R;
import com.uan.brainmher.application.ui.fragments.games.Memorama;
import com.uan.brainmher.application.ui.fragments.games.PhysicalExercisePractic;

public class Games extends AppCompatActivity implements Memorama.Memoramai {

    private static final String TAG = "GamesActivity";

    //region Views
    private FrameLayout container;
    private LinearLayout progresCont;
    private ProgressBar progressBar;
    private ObjectAnimator progressAnimator;
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

        // Fullscreen mode
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Back arrow setup
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Handle back navigation with dispatcher
        handleOnBackPressed();

        // Start progress
        String typeGame = getIntent().getStringExtra("Game");
        iniciarProgres(typeGame != null ? typeGame : "Memorama");
    }

    private void iniciarProgres(final String typeGame) {
        // Show progress bar and hide container
        progresCont.setVisibility(View.VISIBLE);
        container.setVisibility(View.GONE);

        // Setup animation
        progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", 0, 100);
        progressAnimator.setDuration(3000);
        progressAnimator.setInterpolator(new DecelerateInterpolator());
        progressAnimator.start();

        progressAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                // Ensure the FragmentManager is in a valid state
                if (!isFinishing() && !getSupportFragmentManager().isStateSaved()) {
                    showGameFragment(typeGame);
                } else {
                    Log.w(TAG, "FragmentManager not in a valid state.");
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }

    private void showGameFragment(String typeGame) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Fragment fragment;
        if ("Memorama".equals(typeGame)) {
            fragment = createMemoramaFragment();
        } else {
            fragment = createPhysicalExerciseFragment();
        }

        ft.replace(R.id.contanedor_games, fragment);
        ft.commit();

        // Hide progress bar and show container
        progresCont.setVisibility(View.GONE);
        container.setVisibility(View.VISIBLE);
    }

    private Memorama createMemoramaFragment() {
        Memorama fragment = new Memorama();
        fragment.setCallback(this); // Pass the callback directly
        return fragment;
    }

    private PhysicalExercisePractic createPhysicalExerciseFragment() {
        PhysicalExercisePractic fragment = new PhysicalExercisePractic();
        fragment.setCallback(this); // Pass the callback directly
        fragment.setImage(getIntent().getStringExtra("Image"));
        fragment.setTime(getIntent().getIntExtra("Time", 0));
        return fragment;
    }

    private void handleOnBackPressed() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish(); // Finish the activity
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Handle back arrow press
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void reloadGame(String typeGame) {
        iniciarProgres(typeGame);
    }

    @Override
    public void callOnbackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressAnimator != null) {
            progressAnimator.cancel(); // Cancel animation to prevent memory leaks
        }
    }
}
