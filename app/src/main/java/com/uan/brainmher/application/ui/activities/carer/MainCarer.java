package com.uan.brainmher.application.ui.activities.carer;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uan.brainmher.R;
import com.uan.brainmher.domain.entities.Carer;
import com.uan.brainmher.databinding.ActivityMainCarerBinding;
import com.uan.brainmher.application.ui.interfaces.IMainCarer;
import com.uan.brainmher.infraestructure.tools.Constants;

import de.hdodenhof.circleimageview.CircleImageView;
import com.google.android.material.navigation.NavigationView;
import com.uan.brainmher.application.ui.activities.general.Login;
import com.uan.brainmher.application.ui.activities.general.NavigationOptions;

public class MainCarer extends AppCompatActivity implements IMainCarer, NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainCarerBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private Carer carer = new Carer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using View Binding
        binding = ActivityMainCarerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Function to read the items of BottomNavigation
        BottomNavigationView bottomnNavigationView = findViewById(R.id.navigation_carer);
        NavController navController = Navigation.findNavController(this, R.id.content_carer);
        NavigationUI.setupWithNavController(bottomnNavigationView, navController);

        setSupportActionBar(binding.toolbarCareer);
        binding.navigationViewCareer.setNavigationItemSelectedListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, binding.drawerLayoutCareer, binding.toolbarCareer, R.string.drawer_open, R.string.drawer_close);
        binding.drawerLayoutCareer.addDrawerListener(drawerToggle);

        final TextView nameUser = binding.navigationViewCareer.getHeaderView(0).findViewById(R.id.lbl_name_user);
        final TextView emailUser = binding.navigationViewCareer.getHeaderView(0).findViewById(R.id.lbl_email_user);
        final CircleImageView imageUser = binding.navigationViewCareer.getHeaderView(0).findViewById(R.id.img_users_navigation);
        db = FirebaseFirestore.getInstance();
        db.collection(Constants.Carers).document(firebaseUser.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            carer = documentSnapshot.toObject(Carer.class);
                            nameUser.setText(carer.getFirstName() + " " + carer.getLastName());
                            emailUser.setText(carer.getEmail());
                            Glide.with(MainCarer.this).load(carer.getUriImg()).fitCenter().into(imageUser);
                        }
                    }
                });
        drawerToggle.syncState();
    }

    @Override
    public void inflateFragment(String fragmentTag) {
        NavController navController = Navigation.findNavController(this, R.id.content_carer);
        //navController.navigate(R.id.action_homeFragment_to_otherFragment); // Ajustar fragmentos y navegación.
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        closeDrawer();
        int itemId = item.getItemId();
        if (itemId == R.id.btn_profile) {
            Intent navigation = new Intent(MainCarer.this, NavigationOptions.class);
            navigation.putExtra("option", "profile");
            navigation.putExtra("user_uid", carer.getCarerUId());
            navigation.putExtra("user_role", carer.getRole());
            navigation.putExtra("profile_type", "personal");
            startActivity(navigation);
        } else if (itemId == R.id.btn_logout) {
            firebaseAuth.signOut();
            Intent intent = new Intent(MainCarer.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        return true;
    }

    private void closeDrawer() {
        binding.drawerLayoutCareer.closeDrawer(GravityCompat.START);
    }
}
