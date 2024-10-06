package com.uan.brainmher.application.ui.helpers;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.uan.brainmher.R;
import com.uan.brainmher.application.ui.activities.general.Login;
import com.uan.brainmher.application.ui.activities.general.NavigationOptions;
import com.uan.brainmher.domain.entities.Carer;
import com.uan.brainmher.domain.entities.HealthcareProfessional;
import com.uan.brainmher.domain.entities.Patient;
import com.uan.brainmher.domain.repositories.CarerRepository;
import com.uan.brainmher.domain.repositories.HealthcareProfessionalRepository;
import com.uan.brainmher.domain.repositories.PatientsRepository;
import com.uan.brainmher.infraestructure.helpers.SharedPreferencesManager;

import de.hdodenhof.circleimageview.CircleImageView;

public class NavigationViewHelper {

    // Método para configurar el header del NavigationView, primero intenta cargar desde SharedPreferences
    public static void configureHeaderView(Activity activity, NavigationView navigationView) {
        TextView nameUser = navigationView.getHeaderView(0).findViewById(R.id.lbl_name_user);
        TextView emailUser = navigationView.getHeaderView(0).findViewById(R.id.lbl_email_user);
        CircleImageView imageUser = navigationView.getHeaderView(0).findViewById(R.id.img_users_navigation);

        // Intentar cargar los datos desde SharedPreferences
        String userName = SharedPreferencesManager.getInstance(activity).getString("user_name", null);
        String userEmail = SharedPreferencesManager.getInstance(activity).getString("user_email", null);
        String userImageUri = SharedPreferencesManager.getInstance(activity).getString("user_image", "");
        String userRole = SharedPreferencesManager.getInstance(activity).getString("user_role", null);

        // Obtener el UID del usuario desde FirebaseAuth
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (userName != null && userEmail != null) {
            // Si los datos existen en SharedPreferences, se utilizan
            nameUser.setText(userName);
            emailUser.setText(userEmail);
            if (!userImageUri.isEmpty()) {
                Glide.with(activity).load(userImageUri).fitCenter().into(imageUser);
            }
        } else if (firebaseUser != null) {
            // Si no hay datos en SharedPreferences, cargar con base en el userRole y firebaseUser.getUid()
            String userId = firebaseUser.getUid();
            if (userRole != null && userId != null) {
                loadDataBasedOnRole(activity, userRole, userId, nameUser, emailUser, imageUser);
            } else {
                Log.e("NavigationViewHelper", "User role or user ID is missing.");
            }
        } else {
            Log.e("NavigationViewHelper", "FirebaseUser is null.");
        }
    }

    // Método para configurar el NavigationView
    public static void configureNavigationView(Activity activity, NavigationView navigationView) {
        // Configurar el header del NavigationView
        configureHeaderView(activity, navigationView);

        // Configurar los listeners de los ítems del NavigationView
        navigationView.setNavigationItemSelectedListener(item -> {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String userRole = SharedPreferencesManager.getInstance(activity).getString("user_role", null);

            if (firebaseUser != null) {
                String userId = firebaseUser.getUid(); // Obtener el UID directamente de FirebaseAuth

                if (item.getItemId() == R.id.btn_profile) {
                    if (userRole != null) {
                        Intent navigation = new Intent(activity, NavigationOptions.class);
                        navigation.putExtra("option", "profile");
                        navigation.putExtra("user_uid", userId);
                        navigation.putExtra("user_role", userRole);
                        navigation.putExtra("profile_type", "personal");
                        activity.startActivity(navigation);
                    } else {
                        Log.e("NavigationViewHelper", "User role not found in SharedPreferences.");
                    }
                } else if (item.getItemId() == R.id.btn_logout) {
                    // Cerrar sesión de Firebase
                    FirebaseAuth.getInstance().signOut();

                    // Redirigir al login
                    Intent intent = new Intent(activity, Login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                }
            } else {
                Log.e("NavigationViewHelper", "FirebaseUser is null.");
            }
            return true;
        });
    }

    // Método para cargar los datos basados en el userRole
    private static void loadDataBasedOnRole(Activity activity, String userRole, String userId,
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
            case "Patients":
                PatientsRepository patientsRepository = new PatientsRepository();
                loadPatientData(activity, patientsRepository, userId, nameUser, emailUser, imageUser);
                break;
            default:
                Log.e("NavigationViewHelper", "Invalid user role: " + userRole);
                break;
        }
    }

    // Método para configurar el ActionBarDrawerToggle
    public static void configureDrawer(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar) {
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    // Solo carga los datos desde el backend si no están en SharedPreferences
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

    // Solo carga los datos desde el backend si no están en SharedPreferences
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

    // Solo carga los datos desde el backend si no están en SharedPreferences
    private static void loadPatientData(Activity activity, PatientsRepository repository,
                                      String userId, TextView nameUser,
                                      TextView emailUser, CircleImageView imageUser) {
        repository.getPatient(userId, new PatientsRepository.OnPatientLoadedListener() {
            @Override
            public void onSuccess(Patient patient) {
                if (patient != null) {
                    nameUser.setText(patient.getFirstName() + " " + patient.getLastName());
                    emailUser.setText(patient.getEmail());
                    Glide.with(activity).load(patient.getUriImg()).fitCenter().into(imageUser);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("NavigationViewHelper", "Failed to load Patient", e);
            }
        });
    }
}