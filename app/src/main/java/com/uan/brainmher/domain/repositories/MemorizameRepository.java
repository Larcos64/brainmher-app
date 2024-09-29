package com.uan.brainmher.domain.repositories;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uan.brainmher.domain.entities.Memorizame;
import com.uan.brainmher.infraestructure.tools.Constants;

import java.util.UUID;

public class MemorizameRepository {

    private final FirebaseFirestore db;
    private final StorageReference storageReference;

    public MemorizameRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.storageReference = FirebaseStorage.getInstance().getReference();
    }

    public interface OnMemorizameCreatedListener {
        void onSuccess(Memorizame createdMemorizame);
        void onFailure(Exception e);
    }

    public void createMemorizame(Memorizame memorizame, String categoria, Uri uriImage, OnMemorizameCreatedListener listener) {
        if (uriImage != null) {
            uploadMemorizameImage(memorizame, categoria, uriImage, listener); // Genera uuidGenerated en uploadMemorizameImage
        } else {
            saveMemorizameData(memorizame, categoria, null, listener); // Genera uuidGenerated en saveMemorizameData
        }
    }

    private void uploadMemorizameImage(Memorizame memorizame, String categoria, Uri uriImage, OnMemorizameCreatedListener listener) {
        String uuidGenerated = createTransactionID();

        StorageReference imgRef = storageReference.child(categoria + "/" + uuidGenerated + ".jpg");
        imgRef.putFile(uriImage)
                .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl()
                        .addOnSuccessListener(downloadUri -> {
                            memorizame.setUriImg(downloadUri.toString());
                            // Pasamos el uuidGenerated generado aquí
                            saveMemorizameData(memorizame, categoria, uuidGenerated, listener);
                        })
                        .addOnFailureListener(listener::onFailure))
                .addOnFailureListener(listener::onFailure);
    }

    private void saveMemorizameData(Memorizame memorizame, String categoria, @Nullable String uuidGenerated, OnMemorizameCreatedListener listener) {
        String patientUID = memorizame.getPatientUID();

        Log.d("PATIENTUID", patientUID);
        Log.d("UIDGENERATED", uuidGenerated);

        // Si uuidGenerated es nulo, lo generamos aquí
        if (uuidGenerated == null) {
            uuidGenerated = createTransactionID();
        }

        // Verifica si patientUID es nulo
        if (patientUID == null) {
            listener.onFailure(new NullPointerException("patientUID is null"));
            return;
        }

        db.collection(Constants.Memorizame)
                .document(patientUID)
                .collection(categoria)
                .document(uuidGenerated) // Usamos el UUID generado
                .set(memorizame)
                .addOnSuccessListener(aVoid -> listener.onSuccess(memorizame))
                .addOnFailureListener(listener::onFailure);
    }

    private String createTransactionID() {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }
}