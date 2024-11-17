package com.uan.brainmher.application.ui.fragments.patients;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uan.brainmher.R;
import com.uan.brainmher.application.ui.adapters.medicine.MedicamentsAdapter;
import com.uan.brainmher.databinding.FragmentMedicamentsChildBinding;
import com.uan.brainmher.domain.entities.AlertReceiver;
import com.uan.brainmher.domain.entities.MedicationAssignment;
import com.uan.brainmher.domain.entities.Notification;
import com.uan.brainmher.infraestructure.tools.Constants;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MedicamentsChildFragment extends Fragment {

    private FragmentMedicamentsChildBinding binding;
    private MedicamentsAdapter medicamentsAdapter;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private Notification notificationData;
    private AlarmManager alarmManager;
    private final Calendar calendarInstance = Calendar.getInstance();

    public MedicamentsChildFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMedicamentsChildBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        notificationData = new Notification();
        alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);

        initRecycler();
        setUpAlarms();
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        medicamentsAdapter.startListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        medicamentsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (medicamentsAdapter != null) {
            medicamentsAdapter.stopListening();
        }
    }

    private void initRecycler() {
        binding.listNotificationsMedicaments.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.listNotificationsMedicaments.setItemAnimator(null);

        Query query = db.collection(Constants.Medicines).document(user.getUid()).collection(Constants.Medicine);

        FirestoreRecyclerOptions<MedicationAssignment> options = new FirestoreRecyclerOptions.Builder<MedicationAssignment>()
                .setQuery(query, MedicationAssignment.class).build();

        medicamentsAdapter = new MedicamentsAdapter(options, getActivity(), new MedicamentsAdapter.ISelectItemMedicaments() {
            @Override
            public void clickSelect(MedicationAssignment medicationAssignment) {
                Toast.makeText(getActivity(), "Seleccionó " + medicationAssignment.getMedicamentName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void check(MedicationAssignment medicationAssignment) {
                Toast.makeText(getActivity(), "Chequeado " + medicationAssignment.getMedicamentName(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.listNotificationsMedicaments.setAdapter(medicamentsAdapter);
        medicamentsAdapter.notifyDataSetChanged();
    }

    private void setUpAlarms() {
        db.collection(Constants.Medicines).document(user.getUid()).collection(Constants.Medicine)
                .get()
                .addOnSuccessListener(this::processAlarms);
    }

    private void processAlarms(QuerySnapshot snapshots) {
        if (!snapshots.isEmpty()) {
            Map<String, Long> frequencyMap = createFrequencyMap();
            for (QueryDocumentSnapshot document : snapshots) {
                MedicationAssignment assignment = document.toObject(MedicationAssignment.class);
                if ("Activada".equals(assignment.getStatement())) {
                    long intervalMillis = frequencyMap.getOrDefault(assignment.getFrequency(), -1L);
                    if (intervalMillis != -1) {
                        initNotify(assignment.getHours());
                        startAlarm(assignment.hashCode(), intervalMillis);
                    }
                } else {
                    cancelAlarm(assignment.hashCode());
                }
            }
        }
    }

    private Map<String, Long> createFrequencyMap() {
        Map<String, Long> map = new HashMap<>();
        map.put("2 Minutos", 1000L * 60 * 2);
        map.put("5 Minutos", 1000L * 60 * 5);
        map.put("30 Minutos", 1000L * 60 * 30);
        map.put("6 Horas", 1000L * 60 * 60 * 6);
        map.put("8 Horas", 1000L * 60 * 60 * 8);
        map.put("12 Horas", 1000L * 60 * 60 * 12);
        map.put("48 Horas", 1000L * 60 * 60 * 48);
        return map;
    }

    private void initNotify(String hour) {
        String[] hourSplit = hour.split(":");
        int hourNotify = Integer.parseInt(hourSplit[0].trim());
        int minuteNotify = Integer.parseInt(hourSplit[1].split(" ")[0].trim());

        calendarInstance.set(Calendar.HOUR_OF_DAY, hourNotify);
        calendarInstance.set(Calendar.MINUTE, minuteNotify);
        calendarInstance.set(Calendar.SECOND, 0);

        notificationData.setStartHour(hourNotify);
        notificationData.setStartMinute(minuteNotify);
        notificationData.setStartTime(calendarInstance.getTimeInMillis());
    }

    private void startAlarm(int requestCode, long intervalMillis) {
        Intent intent = new Intent(getContext(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), requestCode, intent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (calendarInstance.before(Calendar.getInstance())) {
            calendarInstance.add(Calendar.DATE, 1);
        }

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, notificationData.getStartTime(), intervalMillis, pendingIntent);
    }

    private void cancelAlarm(int requestCode) {
        Intent intent = new Intent(getContext(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), requestCode, intent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        alarmManager.cancel(pendingIntent);
        Toast.makeText(getContext(), "Alarma cancelada", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
