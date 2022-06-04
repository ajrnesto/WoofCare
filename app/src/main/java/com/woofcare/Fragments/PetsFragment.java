package com.woofcare.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.woofcare.Adapters.PetAdapter;
import com.woofcare.Objects.Pet;
import com.woofcare.R;

import java.util.ArrayList;
import java.util.Objects;

public class PetsFragment extends Fragment implements com.woofcare.Adapters.PetAdapter.OnPetListener {

    private static final FirebaseDatabase WOOF_CARE_DB = FirebaseDatabase.getInstance();
    private static final FirebaseAuth AUTH = FirebaseAuth.getInstance();
    private static final FirebaseUser USER = AUTH.getCurrentUser();
    private DatabaseReference dbPets;
    private ValueEventListener velPets;

    View view;
    ExtendedFloatingActionButton ebtnAddPet;
    CircularProgressIndicator loadingBar;
    TextView tvEmpty;

    ArrayList<Pet> arrPets;
    PetAdapter PetAdapter;
    PetAdapter.OnPetListener onPetListener = this;
    RecyclerView rvPets;

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Pets");
        NavigationView navigationView = requireActivity().findViewById(R.id.navView);
        navigationView.setCheckedItem(R.id.miPets);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pets, container, false);

        initialize();
        loadRecyclerView();

        ebtnAddPet.setOnClickListener(view -> {
            AddPetFragment addPetFragment = new AddPetFragment();
            ((FragmentActivity) requireContext()).getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.zoom_in_enter, R.anim.zoom_in_exit, R.anim.zoom_out_enter, R.anim.zoom_out_exit)
                    .replace(R.id.frameLayout, addPetFragment, "ADD_PET")
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        dbPets.removeEventListener(velPets);
    }

    private void initialize() {
        ebtnAddPet = view.findViewById(R.id.ebtnAddPet);
        loadingBar = view.findViewById(R.id.loadingBar);
        tvEmpty = view.findViewById(R.id.tvEmpty);
    }

    private void loadRecyclerView() {
        arrPets = new ArrayList<>();
        rvPets = view.findViewById(R.id.rvPets);
        rvPets.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        rvPets.setLayoutManager(gridLayoutManager);

        dbPets = WOOF_CARE_DB.getReference("user_"+ Objects.requireNonNull(USER).getUid()+"_pets");
        velPets = dbPets.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrPets.clear();

                if (!snapshot.exists()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                }
                else {
                    tvEmpty.setVisibility(View.GONE);
                }

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Pet Pet = dataSnapshot.getValue(Pet.class);
                    arrPets.add(Pet);
                    PetAdapter.notifyDataSetChanged();
                }

                loadingBar.hide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        PetAdapter = new PetAdapter(getContext(), arrPets, onPetListener);
        rvPets.setAdapter(PetAdapter);
        PetAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPetClick(int position) {
        Bundle petUid = new Bundle();
        petUid.putString("pet_uid", arrPets.get(position).getUid());

        ScheduleFragment scheduleFragment = new ScheduleFragment();
        scheduleFragment.setArguments(petUid);
        ((FragmentActivity) requireContext()).getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.zoom_in_enter, R.anim.zoom_in_exit, R.anim.zoom_out_enter, R.anim.zoom_out_exit)
                .replace(R.id.frameLayout, scheduleFragment, "SCHEDULES")
                .addToBackStack(null)
                .commit();
    }
}