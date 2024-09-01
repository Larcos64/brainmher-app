package com.uan.brainmher.application.ui.activities.general;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import com.uan.brainmher.R;
import com.uan.brainmher.databinding.ActivityNavigationOptionsBinding;
import com.uan.brainmher.application.ui.fragments.general.ProfileFragment;

public class NavigationOptions extends AppCompatActivity {

    private ActivityNavigationOptionsBinding binding;
    String option, uid, role, profile_type;
    Bundle args = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNavigationOptionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarNavigationOption);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            option = getIntent().getExtras().getString("option");
            uid = getIntent().getExtras().getString("user_uid");
            role = getIntent().getExtras().getString("user_role");
            profile_type = getIntent().getExtras().getString("profile_type");
            switch (option) {
                case "profile":
                    args.putString("userUid", uid);
                    args.putString("userRole", role);
                    args.putString("profileType", profile_type);
                    handleFrame(new ProfileFragment(), args);
                    break;
            }
        }

        // Retroceso
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Acción personalizada para el botón de retroceso
                finish();
            }
        });
    }

    // Método que se encarga de gestionar el llenado del frame con fragmentos
    private void handleFrame(Fragment fragment, Bundle args) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragment.setArguments(args);
        fragmentTransaction.replace(R.id.content_navigation_option, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
