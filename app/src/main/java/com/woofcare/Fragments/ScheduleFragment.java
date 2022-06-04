package com.woofcare.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.woofcare.R;

import java.util.Objects;

public class ScheduleFragment extends Fragment {

    View view;
    Context context;

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

        return view;
    }

    private void initialize() {
        context = getContext();

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