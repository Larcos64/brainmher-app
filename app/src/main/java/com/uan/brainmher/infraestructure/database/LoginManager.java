package com.uan.brainmher.infraestructure.database;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onesignal.OneSignal;
import com.uan.brainmher.R;
import com.uan.brainmher.domain.entities.Carer;
import com.uan.brainmher.infraestructure.tools.CircularProgressUtil;
import com.uan.brainmher.infraestructure.tools.Constants;
import com.uan.brainmher.application.ui.activities.carer.MainCarer;

public class LoginManager {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = firebaseAuth.getCurrentUser();

    Carer carer = new Carer();
    String role;
    private CircularProgressUtil circularProgressUtil;

    public void loginEmailPassword(final Context context, final String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    redirectByRole(context, user);
                }
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.auth_fail), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(context, context.getResources().getString(R.string.auth_fail), Toast.LENGTH_SHORT).show();
        });
    }

    public void redirectByRole(final Context context, final FirebaseUser useruID) {
        if (context != null) {
            circularProgressUtil = new CircularProgressUtil((Activity) context);
        } else {
            Log.e("LoginManager", "Context is null, cannot initialize CircularProgressUtil");
            return;
        }

        final String userId = OneSignal.getUser().getOnesignalId();
        handleRedirection(context, useruID, userId);
    }

    private void handleRedirection(final Context context, final FirebaseUser useruID, final String userId) {
        if (useruID == null) return;

        circularProgressUtil.showProgress(context.getString(R.string.entering));

        final String uID = useruID.getUid();

        DocumentReference docRefCr = db.collection(Constants.Carers).document(uID);
        docRefCr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    carer = documentSnapshot.toObject(Carer.class);

                    if(carer.getPlayerId() == null){
                        carer.setPlayerId(userId);
                        db.collection(Constants.Carers).document(uID).set(carer);
                    }

                    role = carer.getRole();
                    if (!role.isEmpty()) {
                        Intent intent = new Intent(context, MainCarer.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                        if (circularProgressUtil != null) {
                            circularProgressUtil.hideProgress();
                        }

                        context.startActivity(intent);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                Log.d("ERROR:", e.toString());
            }
        });
    }

    public boolean userLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }
}
