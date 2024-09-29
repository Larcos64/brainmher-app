package com.uan.brainmher.domain.repositories;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uan.brainmher.R;
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

    // Ahora acepta uuidGenerated opcional para la actualización de registros
    public void createMemorizame(Memorizame memorizame, String categoria, Uri uriImage, @Nullable String uuidGenerated, boolean merge, OnMemorizameCreatedListener listener) {
        if (uriImage != null) {
            uploadMemorizameImage(memorizame, categoria, uriImage, uuidGenerated, merge, listener); // Usa uuidGenerated si es que existe
        } else {
            saveMemorizameData(memorizame, categoria, uuidGenerated, merge, listener); // Usa uuidGenerated si es que existe
        }
    }

    private void uploadMemorizameImage(Memorizame memorizame, String categoria, Uri uriImage, @Nullable String uuidGenerated, boolean merge, OnMemorizameCreatedListener listener) {
        // Asegurarse de que uuidGenerated sea final o efectivamente final
        final String finalUuidGenerated;

        if (uuidGenerated == null) {
            finalUuidGenerated = createTransactionID(); // Genera uno nuevo si no existe
        } else {
            finalUuidGenerated = uuidGenerated; // Usa el existente
        }

        StorageReference imgRef = storageReference.child(categoria + "/" + finalUuidGenerated + ".jpg");
        imgRef.putFile(uriImage)
                .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl()
                        .addOnSuccessListener(downloadUri -> {
                            memorizame.setUriImg(downloadUri.toString());
                            memorizame.setUuidGenerated(finalUuidGenerated);
                            // Usar el finalUuidGenerated en lugar de uuidGenerated
                            saveMemorizameData(memorizame, categoria, finalUuidGenerated, merge, listener);
                        })
                        .addOnFailureListener(listener::onFailure))
                .addOnFailureListener(listener::onFailure);
    }

    public void saveMemorizameData(Memorizame memorizame, String categoria, @Nullable String uuidGenerated, boolean merge, OnMemorizameCreatedListener listener) {
        String patientUID = memorizame.getPatientUID();

        // Si uuidGenerated es nulo, lo generamos aquí
        if (uuidGenerated == null) {
            uuidGenerated = createTransactionID();
        }

        // Verifica si patientUID es nulo
        if (patientUID == null) {
            listener.onFailure(new NullPointerException("patientUID is null"));
            return;
        }

        // Usa el método de guardado adecuado dependiendo de si se debe hacer merge o sobrescribir
        if (merge) {
            // Realiza una actualización parcial
            db.collection(Constants.Memorizame)
                    .document(patientUID)
                    .collection(categoria)
                    .document(uuidGenerated) // Usa el UUID proporcionado o generado
                    .set(memorizame, SetOptions.merge())  // Realiza merge en lugar de sobrescribir
                    .addOnSuccessListener(aVoid -> listener.onSuccess(memorizame))
                    .addOnFailureListener(listener::onFailure);
        } else {
            // Sobrescribe todo el documento
            db.collection(Constants.Memorizame)
                    .document(patientUID)
                    .collection(categoria)
                    .document(uuidGenerated) // Usa el UUID proporcionado o generado
                    .set(memorizame)  // Sobrescribe completamente
                    .addOnSuccessListener(aVoid -> listener.onSuccess(memorizame))
                    .addOnFailureListener(listener::onFailure);
        }
    }

    private String createTransactionID() {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }
}