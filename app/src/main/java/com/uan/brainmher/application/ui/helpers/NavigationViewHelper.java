package com.uan.brainmher.application.ui.helpers;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.uan.brainmher.R;
import com.uan.brainmher.application.ui.activities.general.Login;
import com.uan.brainmher.application.ui.activities.general.NavigationOptions;
import com.uan.brainmher.domain.entities.Carer;
import com.uan.brainmher.domain.entities.HealthcareProfessional;
import com.uan.brainmher.domain.repositories.CarerRepository;
import com.uan.brainmher.domain.repositories.HealthcareProfessionalRepository;

import de.hdodenhof.circleimageview.CircleImageView;

public class NavigationViewHelper {

    public static void configureNavigationView(Activity activity, String userRole, String userId,
                                               NavigationView navigationView,
                                               TextView nameUser, TextView emailUser, CircleImageView imageUser) {

        switch (userRole) {
            case "Carers":
                CarerRepository carerRepository = new CarerRepository();
                loadCarerData(activity, carerRepository, userId, nameUser, emailUser, imageUser);
                break;
            case "HealthcareProfessional":
                HealthcareProfessionalRepository healthcareProfessionalRepository = new HealthcareProfessionalRepository();
                loadHealthcareProfessionalData(activity, healthcareProfessionalRepository, userId, nameUser, emailUser, imageUser);
                break;
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.btn_profile) {
                Intent navigation = new Intent(activity, NavigationOptions.class);
                navigation.putExtra("option", "profile");
                navigation.putExtra("user_uid", userId);
                navigation.putExtra("user_role", userRole);
                navigation.putExtra("profile_type", "personal");
                activity.startActivity(navigation);
            } else if (item.getItemId() == R.id.btn_logout) {
                // Cerrar sesión de Firebase
                FirebaseAuth.getInstance().signOut();

                // Redirigir al login
                Intent intent = new Intent(activity, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            }
            return true;
        });
    }

    private static void loadHealthcareProfessionalData(Activity activity, HealthcareProfessionalRepository repository,
                                                       String userId, TextView nameUser,
                                                       TextView emailUser, CircleImageView imageUser) {
        repository.getHealthcareProfessional(userId, new HealthcareProfessionalRepository.OnHealthcareProfessionalLoadedListener() {
            @Override
            public void onSuccess(HealthcareProfessional hp) {
                if (hp != null) {
                    nameUser.setText(hp.getFirstName() + " " + hp.getLastName());
                    emailUser.setText(hp.getEmail());
                    Glide.with(activity).load(hp.getUriImg()).fitCenter().into(imageUser);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("NavigationViewHelper", "Failed to load Healthcare Professional", e);
            }
        });
    }

    private static void loadCarerData(Activity activity, CarerRepository repository,
                                      String userId, TextView nameUser,
                                      TextView emailUser, CircleImageView imageUser) {
        repository.getCarer(userId, new CarerRepository.OnCarerLoadedListener() {
            @Override
            public void onSuccess(Carer carer) {
                if (carer != null) {
                    nameUser.setText(carer.getFirstName() + " " + carer.getLastName());
                    emailUser.setText(carer.getEmail());
                    Glide.with(activity).load(carer.getUriImg()).fitCenter().into(imageUser);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("NavigationViewHelper", "Failed to load Carer", e);
            }
        });
    }
}