package com.uan.brainmher.domain.entities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.onesignal.notifications.internal.common.NotificationHelper;
import com.uan.brainmher.R;

public class AlertReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "medication_alert_channel";
    private static final String CHANNEL_NAME = "Medication Alerts";
    private static final String CHANNEL_DESC = "Notifications for medication reminders";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Create the notification manager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create the notification channel for API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESC);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Build the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications) // Reemplaza con tu ícono de notificación
                .setContentTitle("Recordatorio de Medicación")
                .setContentText("Es hora de tomar tu medicamento.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Issue the notification
        if (notificationManager != null) {
            notificationManager.notify(1, notificationBuilder.build());
        }
    }
}