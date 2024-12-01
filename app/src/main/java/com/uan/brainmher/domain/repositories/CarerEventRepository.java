package com.uan.brainmher.domain.repositories;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uan.brainmher.R;
import com.uan.brainmher.domain.entities.CarerEvent;
import com.uan.brainmher.infraestructure.tools.CircularProgressUtil;
import com.uan.brainmher.infraestructure.tools.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CarerEventRepository {

    private final FirebaseFirestore db;
    private final String currentUserUid;
    private CircularProgressUtil circularProgressUtil;

    public CarerEventRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.currentUserUid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
    }

    public String getCurrentUserUid() {
        return currentUserUid;
    }

    public void addEvent(String uidCarer, CarerEvent carerEvent, Context context) {
        if (context != null) {
            circularProgressUtil = new CircularProgressUtil((Activity) context);
            circularProgressUtil.showProgress(context.getString(R.string.registering));
        } else {
            logInfo("LoginManager: Context is null, cannot initialize CircularProgressUtil");
            return;
        }

        CollectionReference eventsRef = db.collection(Constants.Carers).document(uidCarer).collection(Constants.Events);

        eventsRef.add(carerEvent)
                .addOnSuccessListener(documentReference -> {
                    String eventId = documentReference.getId();

                    // Actualizar el campo "eventId" dentro del mismo documento
                    documentReference.update("eventId", eventId)
                            .addOnSuccessListener(aVoid -> {
                                logInfo("Event ID updated successfully for event: " + eventId);
                                if (circularProgressUtil != null) {
                                    circularProgressUtil.hideProgress();
                                    Toast.makeText(context, R.string.was_saved_succesfully, Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                logError("Error updating event ID", e);
                            });
                })
                .addOnFailureListener(e -> {
                    logError("Error adding event", e);
                });
    }



    private void updateEventId(String uidCarer, String eventId) {
        db.collection(Constants.Carers).document(uidCarer)
                .collection(Constants.Events).document(eventId)
                .update("eventId", eventId)
                .addOnSuccessListener(aVoid -> logInfo("Event ID updated successfully"))
                .addOnFailureListener(e -> logError("Error updating event ID", e));
    }

    public void getEventsByDate(String uidCarer, String date, Consumer<List<CarerEvent>> onSuccess) {
        CollectionReference eventsRef = db.collection(Constants.Carers).document(uidCarer).collection(Constants.Events);

        eventsRef.whereEqualTo("eventDate", date).get()
                .addOnSuccessListener(querySnapshot -> {
                    List<CarerEvent> events = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                        CarerEvent event = documentSnapshot.toObject(CarerEvent.class);
                        event.setEventId(documentSnapshot.getId());
                        events.add(event);
                    }
                    onSuccess.accept(events);
                })
                .addOnFailureListener(e -> logError("Error fetching events by date", e));
    }

    public void deleteEvent(String uidCarer, String eventId) {
        db.collection(Constants.Carers).document(uidCarer)
                .collection(Constants.Events).document(eventId)
                .delete()
                .addOnSuccessListener(aVoid -> logInfo("Event deleted successfully"))
                .addOnFailureListener(e -> logError("Error deleting event", e));
    }

    private void logInfo(String message) {
        // Replace with your preferred logging library or method
        System.out.println(message);
    }

    private void logError(String message, Exception e) {
        // Replace with your preferred logging library or method
        System.err.println(message + ": " + e.getMessage());
    }
}