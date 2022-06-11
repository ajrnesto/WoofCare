package com.woofcare.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.woofcare.R;

import java.util.Objects;

public class ScheduleFragment extends Fragment {

    View view;
    Context context;

    TextInputLayout tilSortBy;
    ExtendedFloatingActionButton ebtnAddEvent;

    String argsPetUid;
    boolean hasPetSelected = false;

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

        ebtnAddEvent.setOnClickListener(view -> {
            AddEventFragment addEventFragment = new AddEventFragment();
            ((FragmentActivity) requireContext()).getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.zoom_in_enter, R.anim.zoom_in_exit, R.anim.zoom_out_enter, R.anim.zoom_out_exit)
                    .replace(R.id.frameLayout, addEventFragment, "ADD_EVENT")
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void initialize() {
        context = getContext();
        tilSortBy = view.findViewById(R.id.tilSortBy);
        ebtnAddEvent = view.findViewById(R.id.ebtnAddEvent);

        if (getArguments() == null) {
            hasPetSelected = false;
            Toast.makeText(context, "No pet selected", Toast.LENGTH_SHORT).show();
        }
        else {
            hasPetSelected = true;
            argsPetUid = requireArguments().getString("pet_uid");
            Toast.makeText(context, "Selected: "+argsPetUid, Toast.LENGTH_SHORT).show();
        }
    }
}