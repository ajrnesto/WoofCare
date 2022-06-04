package com.woofcare.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.woofcare.Adapters.PhotoAdapter;
import com.woofcare.Objects.Photo;
import com.woofcare.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

public class ViewMemoryFragment extends Fragment {

    private static final FirebaseDatabase WOOF_CARE_DB = FirebaseDatabase.getInstance();
    private static final FirebaseAuth AUTH = FirebaseAuth.getInstance();
    private static final FirebaseUser USER = AUTH.getCurrentUser();
    private DatabaseReference dbMemoryPhotos;
    private ValueEventListener velMemoryPhotos;

    View view;
    Context context;

    TextView tvTitle, tvTimestamp, tvJournal;
    RecyclerView rvPhotos;

    String uid, title, journal;
    long timestamp;

    ArrayList<Photo> arrPhotos;
    PhotoAdapter photoAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_view_memory, container, false);
        
        initialize();
        loadMemory();
        loadPhotos();
        
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        dbMemoryPhotos.removeEventListener(velMemoryPhotos);
    }

    private void initialize() {
        context = getContext();
        tvTitle = view.findViewById(R.id.tvTitle);
        tvTimestamp = view.findViewById(R.id.tvTimestamp);
        tvJournal = view.findViewById(R.id.tvJournal);
        rvPhotos = view.findViewById(R.id.rvPhotos);
    }

    private void loadMemory() {
        uid = requireArguments().getString("memory_uid");
        title = requireArguments().getString("memory_title");
        journal = requireArguments().getString("memory_journal");
        timestamp = requireArguments().getLong("memory_timestamp");

        tvTitle.setText(title);
        tvJournal.setText(journal);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        tvTimestamp.setText(sdf.format(timestamp));
    }

    private void loadPhotos() {
        arrPhotos = new ArrayList<>();
        rvPhotos = view.findViewById(R.id.rvPhotos);
        rvPhotos.setHasFixedSize(false);
        rvPhotos.setLayoutManager(new GridLayoutManager(context, 2));

        dbMemoryPhotos = WOOF_CARE_DB.getReference("user_"+ Objects.requireNonNull(USER).getUid()+"_memoryPhotos").child(uid);
        velMemoryPhotos = dbMemoryPhotos.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrPhotos.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Photo photo = dataSnapshot.getValue(Photo.class);
                    arrPhotos.add(photo);
                }

                photoAdapter = new PhotoAdapter(context, arrPhotos);
                rvPhotos.setAdapter(photoAdapter);
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}