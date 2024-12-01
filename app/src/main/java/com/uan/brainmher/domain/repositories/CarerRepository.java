package com.uan.brainmher.domain.repositories;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uan.brainmher.domain.entities.Carer;
import com.uan.brainmher.infraestructure.tools.Constants;

public class CarerRepository {

    private FirebaseFirestore db;
    private final StorageReference storageReference;

    public CarerRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.storageReference = FirebaseStorage.getInstance().getReference();
    }

    public void getCarer(String userId, OnCarerLoadedListener listener) {
        Log.d("CarerRepository", "Querying Carer data for userId: " + userId);
        db.collection(Constants.Carers)
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Carer carer = documentSnapshot.toObject(Carer.class);
                        listener.onSuccess(carer);
                    } else {
                        listener.onFailure(new Exception("Carer not found"));
                    }
                })
                .addOnFailureListener(listener::onFailure);
    }

    public void getCarerById(String carerId, OnSuccessListener<DocumentSnapshot> onSuccessListener, OnFailureListener onFailureListener) {
        db.collection(Constants.Carers)
                .document(carerId)
                .get()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void updateCarer(Carer carer, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        db.collection(Constants.Carers)
                .document(carer.getCarerUId())
                .set(carer)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void uploadImage(Uri uri, String carerId, OnSuccessListener<Uri> onSuccessListener, OnFailureListener onFailureListener) {
        StorageReference imgRef = storageReference.child("Users/Carers/" + carerId + ".jpg");
        imgRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener))
                .addOnFailureListener(onFailureListener);
    }

    public void deleteImage(String carerId, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        StorageReference imgRef = storageReference.child("Users/Carers/" + carerId + ".jpg");
        imgRef.delete().addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }

    public interface OnCarerLoadedListener {
        void onSuccess(Carer carer);
        void onFailure(Exception e);
    }
}
