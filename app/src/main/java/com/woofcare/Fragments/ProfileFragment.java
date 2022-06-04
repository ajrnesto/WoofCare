package com.woofcare.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.woofcare.R;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    View view;

    @Override
    public void onResume() {
        super.onResume();
        /*Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Profile");
        NavigationView navigationView = (NavigationView) requireActivity().findViewById(R.id.navView);
        navigationView.setCheckedItem(R.id.miProfile);*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        return view;
    }
}