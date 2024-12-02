package com.uan.brainmher.application.ui.fragments.carer;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.uan.brainmher.R;
import com.uan.brainmher.domain.entities.CarerEvent;
import com.uan.brainmher.databinding.FragmentCuDiaryBinding;
import com.uan.brainmher.databinding.FragmentCuAddEventBinding;
import com.uan.brainmher.databinding.FragmentCuViewEventsBinding;
import com.uan.brainmher.domain.repositories.CarerEventRepository;
import com.uan.brainmher.infraestructure.tools.CircularProgressUtil;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DiaryFragment extends Fragment {

    private FragmentCuDiaryBinding binding;
    private final Calendar calendarInstance = Calendar.getInstance();
    private CarerEvent carerEvent;
    private CarerEventRepository carerEventRepository;
    private String uidCarer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCuDiaryBinding.inflate(inflater, container, false);

        initializeDependencies();
        setupCalendarListener();

        return binding.getRoot();
    }

    private void initializeDependencies() {
        carerEvent = new CarerEvent();
        carerEventRepository = new CarerEventRepository();
        uidCarer = carerEventRepository.getCurrentUserUid();
    }

    private void setupCalendarListener() {
        binding.calendarViewCarer.setOnDateChangeListener((calendarView, year, month, day) -> showOptionsDialog(day, month + 1, year));
    }

    private void showOptionsDialog(int day, int month, int year) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.BackgroundRounded);

        CharSequence[] items = {
                getString(R.string.add_event_carer),
                getString(R.string.view_event_carer),
                getString(R.string.exit)
        };

        builder.setTitle(getString(R.string.select_option))
                .setItems(items, (dialogInterface, i) -> {
                    if (i == 0) {
                        showAddEventDialog(day, month, year);
                    } else if (i == 1) {
                        showViewEventDialog(day, month, year);
                    }
                })
                .create()
                .show();
    }

    private void showAddEventDialog(int day, int month, int year) {
        FragmentCuAddEventBinding dialogBinding = FragmentCuAddEventBinding.inflate(getLayoutInflater());

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext(), R.style.BackgroundRounded);
        dialogBuilder.setView(dialogBinding.getRoot());
        dialogBuilder.setTitle(getString(R.string.add_event_carer) + " (" + day + "/" + month + "/" + year + ")");

        AlertDialog dialog = dialogBuilder.create(); // Crear el diálogo aquí

        setupAddEventDialogListeners(dialogBinding, day, month, year, dialog);

        dialog.show();
    }

    private void setupAddEventDialogListeners(FragmentCuAddEventBinding dialogBinding, int day, int month, int year, AlertDialog dialog) {
        dialogBinding.eventStartHour.setOnClickListener(v -> getTimePicker(dialogBinding.editCarerEventStart, true, day, month, year).show());
        dialogBinding.eventEndHour.setOnClickListener(v -> getTimePicker(dialogBinding.editCarerEventEnd, false, day, month, year).show());

        dialogBinding.btnAddEvent.setOnClickListener(v -> {
            if (validateEventInput(dialogBinding)) {
                carerEvent.setEventName(dialogBinding.editCarerEventName.getText().toString().trim());
                carerEvent.setEventLocation(dialogBinding.editCarerEventLocation.getText().toString().trim());
                carerEvent.setEventDate(year + "-" + month + "-" + day);
                carerEvent.setEventStartHour(dialogBinding.editCarerEventStart.getText().toString().trim());
                carerEvent.setEventEndHour(dialogBinding.editCarerEventEnd.getText().toString().trim());
                carerEvent.setEventDescription(dialogBinding.editCarerEventDescription.getText().toString().trim());

                dialog.dismiss();
                carerEventRepository.addEvent(uidCarer, carerEvent, requireContext()); // Pasar el diálogo
            }
        });

        dialogBinding.btnCancelEvent.setOnClickListener(v -> dialog.dismiss());
    }

    private boolean validateEventInput(FragmentCuAddEventBinding dialogBinding) {
        boolean isValid = true;

        if (dialogBinding.editCarerEventName.getText().toString().trim().isEmpty()) {
            dialogBinding.tilCarerEventName.setError(getString(R.string.complete_field));
            isValid = false;
        }
        if (dialogBinding.editCarerEventStart.getText().toString().trim().isEmpty()) {
            dialogBinding.tilCarerEventStart.setError(getString(R.string.complete_field));
            isValid = false;
        }

        return isValid;
    }

    private void showViewEventDialog(int day, int month, int year) {
        FragmentCuViewEventsBinding dialogBinding = FragmentCuViewEventsBinding.inflate(getLayoutInflater());

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext(), R.style.BackgroundRounded);
        dialogBuilder.setView(dialogBinding.getRoot());
        dialogBuilder.setTitle(getString(R.string.events_for_date) + " " + day + "-" + month + "-" + year);

        String date = year + "-" + month + "-" + day;
        loadEventList(dialogBinding.lvCarerEvents, dialogBinding.tvEventCarerList, date, dialogBinding);

        dialogBuilder.setPositiveButton(getString(R.string.exit), null);
        dialogBuilder.create().show();
    }

    private void loadEventList(AdapterView listView, TextView textView, String date, FragmentCuViewEventsBinding dialogBinding) {
        carerEventRepository.getEventsByDate(uidCarer, date, events -> {
            if (events.isEmpty()) {
                textView.setText(R.string.no_events_for_date);
                listView.setAdapter(null); // Limpia la lista si no hay eventos
            } else {
                List<String> eventDetails = new ArrayList<>();
                for (CarerEvent event : events) {
                    eventDetails.add(event.getEventName() + " - " + event.getEventStartHour());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, eventDetails);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener((parent, view, position, id) ->
                        showDeleteEventDialog(events.get(position), date, dialogBinding)); // Pasar el binding directamente
            }
        });
    }

    private void showDeleteEventDialog(CarerEvent event, String date, FragmentCuViewEventsBinding dialogBinding) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getString(R.string.delete) + ": " + event.getEventName())
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    carerEventRepository.deleteEvent(uidCarer, event.getEventId(),
                            () -> {
                                Toast.makeText(getContext(), R.string.record_deleted, Toast.LENGTH_SHORT).show();
                                // Recargar la lista de eventos tras eliminar
                                loadEventList(dialogBinding.lvCarerEvents, dialogBinding.tvEventCarerList, date, dialogBinding);
                            },
                            () -> Toast.makeText(getContext(), R.string.elimination_failed, Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }

    private TimePickerDialog getTimePicker(TextInputEditText editText, boolean isStartTime, int day, int month, int year) {
        int hour = calendarInstance.get(Calendar.HOUR_OF_DAY);
        int minute = calendarInstance.get(Calendar.MINUTE);

        return new TimePickerDialog(requireContext(), (view, hourOfDay, minute1) -> {
            calendarInstance.set(year, month, day, hourOfDay, minute1, 0);
            String timeText = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendarInstance.getTime());
            editText.setText(timeText);

            if (isStartTime) {
                carerEvent.setEventStartTime(calendarInstance.getTimeInMillis());
            }
        }, hour, minute, false);
    }
}