package com.woofcare.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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
import com.woofcare.Adapters.EventAdapter;
import com.woofcare.Objects.Event;
import com.woofcare.R;

import java.util.ArrayList;
import java.util.Objects;

public class MemoriesFragment extends Fragment implements com.woofcare.Adapters.EventAdapter.OnEventListener {

    private static final FirebaseDatabase WOOF_CARE_DB = FirebaseDatabase.getInstance();
    private static final FirebaseAuth AUTH = FirebaseAuth.getInstance();
    private static final FirebaseUser USER = AUTH.getCurrentUser();
    private DatabaseReference dbEvents;
    private ValueEventListener velEvents;

    View view;
    Context context;

    ExtendedFloatingActionButton ebtnAddEvent;
    CircularProgressIndicator loadingBar;
    TextView tvEmpty;

    ArrayList<Event> arrEvents;
    com.woofcare.Adapters.EventAdapter eventAdapter;
    EventAdapter.OnEventListener onEventListener = this;
    RecyclerView rvEvents;

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Memories");
        NavigationView navigationView = (NavigationView) requireActivity().findViewById(R.id.navView);
        navigationView.setCheckedItem(R.id.miMemories);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_memories, container, false);

        initialize();
        loadRecyclerView();
        
        return view;
    }

    private void initialize() {
        context = getContext();

        ebtnAddEvent = view.findViewById(R.id.ebtnAddEvent);
        loadingBar = view.findViewById(R.id.loadingBar);
        tvEmpty = view.findViewById(R.id.tvEmpty);
    }

    private void loadRecyclerView() {
        arrEvents = new ArrayList<>();
        rvEvents = view.findViewById(R.id.rvEvents);
        rvEvents.setLayoutManager(new LinearLayoutManager(context));

        dbEvents = WOOF_CARE_DB.getReference("user_"+ Objects.requireNonNull(USER).getUid()+"_events");
        velEvents = dbEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrEvents.clear();

                if (!snapshot.exists()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                }
                else {
                    tvEmpty.setVisibility(View.GONE);
                }

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Event Event = dataSnapshot.getValue(Event.class);
                    arrEvents.add(Event);
                    eventAdapter.notifyDataSetChanged();
                }

                loadingBar.hide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        eventAdapter = new EventAdapter(getContext(), arrEvents, onEventListener);
        rvEvents.setAdapter(eventAdapter);
        eventAdapter.notifyDataSetChanged();
    }

    @Override
    public void onEventClick(int position) {

    }
}