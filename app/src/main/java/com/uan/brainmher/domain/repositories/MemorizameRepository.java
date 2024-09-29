package com.uan.brainmher.domain.repositories;

import android.net.Uri;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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

    public interface OnMemorizameDeletedListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    // Ahora acepta uuidGenerated opcional para la actualización de registros
    public void createMemorizame(Memorizame memorizame, String folderCategory, Uri uriImage, @Nullable String uuidGenerated, boolean merge, OnMemorizameCreatedListener listener) {
        if (uriImage != null) {
            saveMemorizameDataWithImage(memorizame, folderCategory, uriImage, uuidGenerated, merge, listener); // Usa uuidGenerated si es que existe
        } else {
            saveMemorizameData(memorizame, folderCategory, uuidGenerated, merge, listener); // Usa uuidGenerated si es que existe
        }
    }

    private void saveMemorizameDataWithImage(Memorizame memorizame, String folderCategory, Uri uriImage, @Nullable String uuidGenerated, boolean merge, OnMemorizameCreatedListener listener) {
        final String finalUuidGenerated;

        if (uuidGenerated == null) {
            finalUuidGenerated = createTransactionID(); // Genera uno nuevo si no existe
        } else {
            finalUuidGenerated = uuidGenerated; // Usa el existente
        }

        StorageReference imgRef = storageReference.child(folderCategory + "/" + finalUuidGenerated + ".jpg");
        imgRef.putFile(uriImage)
                .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl()
                        .addOnSuccessListener(downloadUri -> {
                            memorizame.setUriImg(downloadUri.toString());
                            memorizame.setUuidGenerated(finalUuidGenerated);
                            // Usar el finalUuidGenerated en lugar de uuidGenerated
                            saveMemorizameData(memorizame, folderCategory, finalUuidGenerated, merge, listener);
                        })
                        .addOnFailureListener(listener::onFailure))
                .addOnFailureListener(listener::onFailure);
    }

    public void saveMemorizameData(Memorizame memorizame, String folderCategory, @Nullable String uuidGenerated, boolean merge, OnMemorizameCreatedListener listener) {
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
                    .collection(folderCategory)
                    .document(uuidGenerated) // Usa el UUID proporcionado o generado
                    .set(memorizame, SetOptions.merge())  // Realiza merge en lugar de sobrescribir
                    .addOnSuccessListener(aVoid -> listener.onSuccess(memorizame))
                    .addOnFailureListener(listener::onFailure);
        } else {
            // Sobrescribe todo el documento
            db.collection(Constants.Memorizame)
                    .document(patientUID)
                    .collection(folderCategory)
                    .document(uuidGenerated) // Usa el UUID proporcionado o generado
                    .set(memorizame)  // Sobrescribe completamente
                    .addOnSuccessListener(aVoid -> listener.onSuccess(memorizame))
                    .addOnFailureListener(listener::onFailure);
        }
    }

    // Método para eliminar la imagen de Firebase Storage
    public void deleteImage(String folderCategory, String uuidGenerated, OnMemorizameDeletedListener listener) {
        StorageReference deleteImage = storageReference.child(folderCategory + "/" + uuidGenerated + ".jpg");
        deleteImage.delete()
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }

    // Método para eliminar el documento de Firestore
    public void deleteMemorizame(String patientUID, String folderCategory, String uuidGenerated, OnMemorizameDeletedListener listener) {
        db.collection(Constants.Memorizame).document(patientUID)
                .collection(folderCategory).document(uuidGenerated)
                .delete()
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }

    private String createTransactionID() {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }
}