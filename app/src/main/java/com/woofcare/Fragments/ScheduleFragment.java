package com.woofcare.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.woofcare.Adapters.ScheduleAdapter;
import com.woofcare.Objects.Pet;
import com.woofcare.Objects.Schedule;
import com.woofcare.R;

import java.util.ArrayList;
import java.util.Objects;

public class ScheduleFragment extends Fragment implements ScheduleAdapter.OnScheduleListener {

    private static final FirebaseDatabase WOOF_CARE_DB = FirebaseDatabase.getInstance();
    private static final FirebaseAuth AUTH = FirebaseAuth.getInstance();
    private static final FirebaseUser USER = AUTH.getCurrentUser();
    private static final DatabaseReference DB_SCHEDULES = WOOF_CARE_DB.getReference("user_"+ Objects.requireNonNull(USER).getUid()+"_schedules");
    private DatabaseReference dbPets;
    private ValueEventListener velMemories;
    private Query qrySchedules;

    View view;
    Context context;

    ExtendedFloatingActionButton ebtnAddEvent;
    TextView tvEmpty;
    CircularProgressIndicator loadingBar;
    MaterialCheckBox chkPastSchedules;

    // spinner
    ArrayList<Pet> arrPets;
    ArrayList<String> arrFilters;
    ArrayAdapter<String> adapterFilter;
    TextInputLayout tilFilter;
    AutoCompleteTextView menuFilter;
    int filterAdapterPosition = 0;

    String argsPetUid;
    boolean hasPetSelected = false;

    ArrayList<Schedule> arrSchedules;
    ScheduleAdapter scheduleAdapter;
    ScheduleAdapter.OnScheduleListener onScheduleListener = this;
    RecyclerView rvSchedules;

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Schedule");
        NavigationView navigationView = requireActivity().findViewById(R.id.navView);
        navigationView.setCheckedItem(R.id.miSchedule);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_schedule, container, false);

        initialize();
        initializeSpinner();
        loadRecyclerView(0);

        ebtnAddEvent.setOnClickListener(view -> {
            AddScheduleFragment addScheduleFragment = new AddScheduleFragment();
            ((FragmentActivity) requireContext()).getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.zoom_in_enter, R.anim.zoom_in_exit, R.anim.zoom_out_enter, R.anim.zoom_out_exit)
                    .replace(R.id.frameLayout, addScheduleFragment, "ADD_EVENT")
                    .addToBackStack(null)
                    .commit();
        });

        menuFilter.setOnItemClickListener((adapterView, view, position, id) -> {
            filterAdapterPosition = position;
            loadRecyclerView(position);
        });

        chkPastSchedules.setOnCheckedChangeListener((compoundButton, b) -> loadRecyclerView(filterAdapterPosition));

        return view;
    }

    private void initialize() {
        context = getContext();
        tilFilter = view.findViewById(R.id.tilFilter);
        menuFilter = view.findViewById(R.id.menuFilter);
        loadingBar = view.findViewById(R.id.loadingBar);
        ebtnAddEvent = view.findViewById(R.id.ebtnAddEvent);
        chkPastSchedules = view.findViewById(R.id.chkPastSchedules);
        tvEmpty = view.findViewById(R.id.tvEmpty);
    }

    private void initializeSpinner() {
        arrPets = new ArrayList<>();
        arrFilters = new ArrayList<>();

        // add default
        arrFilters.add("None");
        // add categories
        arrFilters.add("Food");
        arrFilters.add("Exercise");
        arrFilters.add("Medication");
        arrFilters.add("Vaccination");
        // add pets
        dbPets = WOOF_CARE_DB.getReference("user_"+USER.getUid()+"_pets");
        dbPets.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Pet pet = dataSnapshot.getValue(Pet.class);
                    arrPets.add(pet);
                    arrFilters.add(pet.getName());
                }
                adapterFilter = new ArrayAdapter<>(getContext(), R.layout.list_item, arrFilters);
                menuFilter.setAdapter(adapterFilter);
                menuFilter.setEnabled(true);
                tilFilter.setEnabled(true);

                checkForPetUidArguments();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // assign adapter
        adapterFilter = new ArrayAdapter<>(getContext(), R.layout.list_item, arrFilters);
        menuFilter.setAdapter(adapterFilter);

        // set default selected value as "none"
        menuFilter.setText(arrFilters.get(0), false);
    }

    private void loadRecyclerView(int filterIndex) {
        arrSchedules = new ArrayList<>();
        rvSchedules = view.findViewById(R.id.rvSchedules);
        rvSchedules.setLayoutManager(new LinearLayoutManager(context));

        velMemories = getQuery(filterIndex).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrSchedules.clear();

                if (!snapshot.exists()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    rvSchedules.setVisibility(View.INVISIBLE);
                }
                else {
                    tvEmpty.setVisibility(View.GONE);
                    rvSchedules.setVisibility(View.VISIBLE);
                }

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Schedule schedule = dataSnapshot.getValue(Schedule.class);

                    arrSchedules.add(schedule);
                    scheduleAdapter.notifyDataSetChanged();
                }

                loadingBar.hide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        scheduleAdapter = new ScheduleAdapter(getContext(), arrSchedules, onScheduleListener, chkPastSchedules.isChecked());
        rvSchedules.setAdapter(scheduleAdapter);
        scheduleAdapter.notifyDataSetChanged();
    }

    private Query getQuery(int filterIndex) {
        if (filterIndex == 1) {
            return DB_SCHEDULES.orderByChild("category").equalTo("Food");
        }
        else if (filterIndex == 2) {
            return DB_SCHEDULES.orderByChild("category").equalTo("Exercise");
        }
        else if (filterIndex == 3) {
            return DB_SCHEDULES.orderByChild("category").equalTo("Medication");
        }
        else if (filterIndex == 4) {
            return DB_SCHEDULES.orderByChild("category").equalTo("Vaccination");
        }
        else if (filterIndex > 4) {
            return DB_SCHEDULES.orderByChild("petUid").equalTo(arrPets.get(filterIndex - 5).getUid());
        }
        else {
            return DB_SCHEDULES;
        }
    }

    private void checkForPetUidArguments() {
        if (getArguments() == null) {
            hasPetSelected = false;
            return;
        }

        hasPetSelected = true;
        argsPetUid = requireArguments().getString("pet_uid");

        for (int i = 0; i < arrPets.size(); i++) {
            if (arrPets.get(i).getUid().equals(argsPetUid)){
                String selectedPetName = arrPets.get(i).getName();
                menuFilter.setText(selectedPetName, false);
                loadRecyclerView(i+5);
            }
        }
    }

    @Override
    public void onScheduleClick(int position) {

    }
}