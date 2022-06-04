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
import androidx.fragment.app.FragmentActivity;
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
import com.woofcare.Adapters.MemoryAdapter;
import com.woofcare.Objects.Memory;
import com.woofcare.R;

import java.util.ArrayList;
import java.util.Objects;

public class MemoriesFragment extends Fragment implements MemoryAdapter.OnMemoryListener {

    private static final FirebaseDatabase WOOF_CARE_DB = FirebaseDatabase.getInstance();
    private static final FirebaseAuth AUTH = FirebaseAuth.getInstance();
    private static final FirebaseUser USER = AUTH.getCurrentUser();
    private DatabaseReference dbMemories;
    private ValueEventListener velMemories;

    View view;
    Context context;

    ExtendedFloatingActionButton ebtnAddMemory;
    CircularProgressIndicator loadingBar;
    TextView tvEmpty;

    ArrayList<Memory> arrMemories;
    MemoryAdapter memoryAdapter;
    MemoryAdapter.OnMemoryListener onMemoryListener = this;
    RecyclerView rvMemories;

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

        ebtnAddMemory.setOnClickListener(view -> {
            AddMemoryFragment addMemoryFragment = new AddMemoryFragment();
            ((FragmentActivity) requireContext()).getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.zoom_in_enter, R.anim.zoom_in_exit, R.anim.zoom_out_enter, R.anim.zoom_out_exit)
                    .replace(R.id.frameLayout, addMemoryFragment, "ADD_MEMORY")
                    .addToBackStack(null)
                    .commit();
        });
        
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        dbMemories.removeEventListener(velMemories);
    }

    private void initialize() {
        context = getContext();

        ebtnAddMemory = view.findViewById(R.id.ebtnAddMemory);
        loadingBar = view.findViewById(R.id.loadingBar);
        tvEmpty = view.findViewById(R.id.tvEmpty);
    }

    private void loadRecyclerView() {
        arrMemories = new ArrayList<>();
        rvMemories = view.findViewById(R.id.rvMemories);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rvMemories.setLayoutManager(linearLayoutManager);

        dbMemories = WOOF_CARE_DB.getReference("user_"+ Objects.requireNonNull(USER).getUid()+"_memories");
        velMemories = dbMemories.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrMemories.clear();

                if (!snapshot.exists()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    rvMemories.setVisibility(View.INVISIBLE);
                }
                else {
                    tvEmpty.setVisibility(View.GONE);
                    rvMemories.setVisibility(View.VISIBLE);
                }

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Memory Memory = dataSnapshot.getValue(Memory.class);
                    arrMemories.add(Memory);
                    memoryAdapter.notifyDataSetChanged();
                }

                loadingBar.hide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        memoryAdapter = new MemoryAdapter(getContext(), arrMemories, onMemoryListener);
        rvMemories.setAdapter(memoryAdapter);
        memoryAdapter.notifyDataSetChanged();

        rvMemories.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == 0) {
                    ebtnAddMemory.show();
                }
                else {
                    ebtnAddMemory.hide();
                }
            }
        });
    }

    @Override
    public void onMemoryClick(int position) {
        Bundle memoryBundle = new Bundle();
        memoryBundle.putString("memory_uid", arrMemories.get(position).getUid());
        memoryBundle.putString("memory_title", arrMemories.get(position).getTitle());
        memoryBundle.putString("memory_journal", arrMemories.get(position).getJournal());
        memoryBundle.putLong("memory_timestamp", arrMemories.get(position).getTimestamp());

        ViewMemoryFragment viewMemoryFragment = new ViewMemoryFragment();
        viewMemoryFragment.setArguments(memoryBundle);
        ((FragmentActivity) requireContext()).getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.zoom_in_enter, R.anim.zoom_in_exit, R.anim.zoom_out_enter, R.anim.zoom_out_exit)
                .replace(R.id.frameLayout, viewMemoryFragment, "VIEW_MEMORY")
                .addToBackStack(null)
                .commit();
    }
}