package com.woofcare.Fragments;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.woofcare.Notifications.BroadcastReceiver;
import com.woofcare.Objects.Pet;
import com.woofcare.Objects.Schedule;
import com.woofcare.R;
import com.woofcare.Utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

public class AddScheduleFragment extends Fragment {

    private static final FirebaseDatabase WOOF_CARE_DB = FirebaseDatabase.getInstance();
    private static final FirebaseUser USER = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbPets, dbNewSchedule;

    View view;
    TextInputEditText etTitle, etDetails, etDate, etTime;
    MaterialButton btnSave;
    TextInputLayout tilPets;

    ArrayList<Pet> arrPets;
    ArrayList<String> pets;
    String[] categories;
    ArrayAdapter<String> adapterCategories;
    AutoCompleteTextView menuCategories;
    ArrayAdapter<String> adapterPets;
    AutoCompleteTextView menuPets;

    int petAdapterPosition = 0;

    MaterialDatePicker.Builder<Long> builderDatePicker;
    MaterialDatePicker<Long> datePicker;
    MaterialTimePicker timePicker;
    long timePickerGetSelection = 0;

    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_event, container, false);

        initialize();
        initializeSpinner();
        initializeDatePicker();
        createNotificationChannel();

        etDate.setOnClickListener(view -> {
            etDate.setEnabled(false);
            datePicker.show(getParentFragmentManager(), "Date Picker");
        });

        etTime.setOnClickListener(view -> {
            etTime.setEnabled(false);
            timePicker.show(getParentFragmentManager(), "Time Picker");
        });

        btnSave.setOnClickListener(view -> saveSchedule());

        return view;
    }

    private void initialize() {
        etTitle = view.findViewById(R.id.etTitle);
        etDetails = view.findViewById(R.id.etDetails);
        etDate = view.findViewById(R.id.etDate);
        etTime = view.findViewById(R.id.etTime);
        btnSave = view.findViewById(R.id.btnSave);
        menuCategories = view.findViewById(R.id.menuCategories);
        menuPets = view.findViewById(R.id.menuPets);
        tilPets = view.findViewById(R.id.tilPets);

        context = getContext();
    }

    private void initializeSpinner() {
        arrPets = new ArrayList<>();
        pets = new ArrayList<>();
        categories = new String[] {"Food", "Exercise", "Medication", "Vaccination"};
        adapterCategories = new ArrayAdapter<>(getContext(), R.layout.list_item, categories);
        menuCategories.setAdapter(adapterCategories);

        dbPets = WOOF_CARE_DB.getReference("user_"+USER.getUid()+"_pets");
        dbPets.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Pet pet = dataSnapshot.getValue(Pet.class);
                    arrPets.add(pet);
                    pets.add(pet.getName());
                }
                adapterPets = new ArrayAdapter<>(getContext(), R.layout.list_item, pets);
                menuPets.setAdapter(adapterPets);
                menuPets.setEnabled(true);
                tilPets.setEnabled(true);
                tilPets.setHint("Assign to Pet");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        menuPets.setOnItemClickListener((adapterView, view, position, id) -> {
            petAdapterPosition = position;
        });
    }

    private void initializeDatePicker() {
        builderDatePicker = MaterialDatePicker.Builder.datePicker();
        builderDatePicker.setTitleText("Birthdate").setSelection(System.currentTimeMillis());
        datePicker = builderDatePicker.build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
            etDate.setText(sdf.format(datePicker.getSelection()));
            etDate.setEnabled(true);
        });
        datePicker.addOnNegativeButtonClickListener(view -> {
            etDate.setEnabled(true);
        });
        datePicker.addOnCancelListener(dialogInterface -> {
            etDate.setEnabled(true);
        });

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(calendar.get(Calendar.HOUR))
                .setMinute(calendar.get(Calendar.MINUTE))
                .setTitleText("Time of Reservation")
                .setTheme(R.style.WoofCare_TimePicker)
                .build();
        timePicker.addOnPositiveButtonClickListener(view -> {
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
            calendar.set(Calendar.MINUTE, timePicker.getMinute());
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            timePickerGetSelection = calendar.getTimeInMillis();

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
            etTime.setText(sdf.format(timePickerGetSelection));
            etTime.setEnabled(true);
        });
        timePicker.addOnNegativeButtonClickListener(view -> {
            etTime.setEnabled(true);
        });
        timePicker.addOnCancelListener(dialogInterface -> {
            etTime.setEnabled(true);
        });
    }

    private void saveSchedule() {
        if (datePicker == null) {
            return;
        }
        Calendar calendarFromDatePicker = Calendar.getInstance();
        Calendar calendarFromTimePicker = Calendar.getInstance();
        Calendar calendarMergedFromDateAndTimePicker = Calendar.getInstance();

        calendarFromDatePicker.setTimeInMillis(datePicker.getSelection());
        calendarFromTimePicker.setTimeInMillis(timePickerGetSelection);

        calendarMergedFromDateAndTimePicker.set(Calendar.YEAR, calendarFromDatePicker.get(Calendar.YEAR));
        calendarMergedFromDateAndTimePicker.set(Calendar.MONTH, calendarFromDatePicker.get(Calendar.MONTH));
        calendarMergedFromDateAndTimePicker.set(Calendar.DAY_OF_YEAR, calendarFromDatePicker.get(Calendar.DAY_OF_YEAR));
        calendarMergedFromDateAndTimePicker.set(Calendar.HOUR_OF_DAY, calendarFromTimePicker.get(Calendar.HOUR_OF_DAY));
        calendarMergedFromDateAndTimePicker.set(Calendar.MINUTE, calendarFromTimePicker.get(Calendar.MINUTE));
        calendarMergedFromDateAndTimePicker.set(Calendar.SECOND, 0);

        // instead of pushing new uid, use timestamp as uid to automatically sort by timestamp
        long timestamp = calendarMergedFromDateAndTimePicker.getTimeInMillis();
        dbNewSchedule = WOOF_CARE_DB.getReference("user_"+USER.getUid()+"_schedules").child(String.valueOf(timestamp));

        String uid = String.valueOf(timestamp);
        String petUid = arrPets.get(petAdapterPosition).getUid();
        String title = etTitle.getText().toString();
        String details = etDetails.getText().toString();
        String category = menuCategories.getText().toString();

        Schedule newSchedule = new Schedule(uid, petUid, title, details, timestamp, category);
        dbNewSchedule.setValue(newSchedule);

        // add alarm
        Set<String> cachedAlarms = Utils.getStringSet(context, "alarms");
        if (!cachedAlarms.contains(String.valueOf(timestamp))) {
            Intent intent = new Intent(context, BroadcastReceiver.class);
            intent.putExtra("title", title);
            intent.putExtra("details", details);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent);

            cachedAlarms.add(String.valueOf(timestamp));
            Utils.setStringSet(context, "alarms", cachedAlarms);
        }

        // end add alarm

        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        Toast.makeText(context, "New event has been scheduled", Toast.LENGTH_SHORT).show();
        requireActivity().onBackPressed();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "WoofCare Notifications";
            String description = "WoofCare Notifications";
            NotificationChannel channel = new NotificationChannel("woofcare_notifications", name, NotificationManager.IMPORTANCE_MAX);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}