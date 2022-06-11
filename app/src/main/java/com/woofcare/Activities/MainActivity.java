package com.woofcare.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.woofcare.Fragments.AddScheduleFragment;
import com.woofcare.Fragments.AddMemoryFragment;
import com.woofcare.Fragments.AddPetFragment;
import com.woofcare.Fragments.MemoriesFragment;
import com.woofcare.Fragments.PetsFragment;
import com.woofcare.Fragments.ScheduleFragment;
import com.woofcare.Fragments.ViewMemoryFragment;
import com.woofcare.R;
import com.woofcare.Utils.Utils;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final FirebaseDatabase WOOF_CARE_DB = FirebaseDatabase.getInstance();
    private static final FirebaseAuth AUTH = FirebaseAuth.getInstance();
    private static final FirebaseUser USER = AUTH.getCurrentUser();
    private DatabaseReference refUser;

    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    NavigationView navView;
    View headerView;
    MaterialToolbar toolbar;
    MenuItem miPets, miMemories, miSchedule;
    TextView tvUserName, tvUserEmail;
    MaterialButton btnLogout;
    RoundedImageView ivProfile;

    // action bar
    MaterialButton btnActionBar;
    MaterialCardView cvActionBar;
    TextView tvActivityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.zoom_in_enter, R.anim.zoom_in_exit);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
        initializeMenuDrawer();
        setStartupFragment();

        addBackstackListener();
    }

    private void initialize() {
        btnActionBar = findViewById(R.id.btnActionBar);
        cvActionBar = findViewById(R.id.cvActionBar);
        tvActivityTitle = findViewById(R.id.tvActivityTitle);
        btnActionBar.setOnClickListener(view -> onBackPressed());
    }

    private void addBackstackListener() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(() -> {
            AddPetFragment addPetFragment = (AddPetFragment) getSupportFragmentManager().findFragmentByTag("ADD_PET");
            AddMemoryFragment addMemoryFragment = (AddMemoryFragment) getSupportFragmentManager().findFragmentByTag("ADD_MEMORY");
            AddScheduleFragment addScheduleFragment = (AddScheduleFragment) getSupportFragmentManager().findFragmentByTag("ADD_EVENT");
            ViewMemoryFragment viewMemoryFragment = (ViewMemoryFragment) getSupportFragmentManager().findFragmentByTag("VIEW_MEMORY");

            if (addPetFragment != null && addPetFragment.isVisible()) {
                tvActivityTitle.setText("");
                cvActionBar.setVisibility(View.VISIBLE);
            }
            else if (addMemoryFragment != null && addMemoryFragment.isVisible()) {
                tvActivityTitle.setText("");
                cvActionBar.setVisibility(View.VISIBLE);
            }
            else if (viewMemoryFragment != null && viewMemoryFragment.isVisible()) {
                tvActivityTitle.setText("");
                cvActionBar.setVisibility(View.VISIBLE);
            }
            else if (addScheduleFragment != null && addScheduleFragment.isVisible()) {
                tvActivityTitle.setText("");
                cvActionBar.setVisibility(View.VISIBLE);
            }
            else {
                cvActionBar.setVisibility(View.GONE);
            }
        });
    }

    private void setStartupFragment() {
        Fragment fragment = new PetsFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void initializeMenuDrawer() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navView = findViewById(R.id.navView);
        toolbar = findViewById(R.id.toolbar);
        headerView = navView.getHeaderView(0);
        tvUserName = headerView.findViewById(R.id.tvUserName);
        tvUserEmail = headerView.findViewById(R.id.tvUserEmail);
        btnLogout = headerView.findViewById(R.id.btnSignOut);
        ivProfile = headerView.findViewById(R.id.ivProfile);
        miPets = findViewById(R.id.miPets);
        miMemories = findViewById(R.id.miMemories);
        miSchedule = findViewById(R.id.miSchedule);

        // header
        refUser = WOOF_CARE_DB.getReference("user_"+USER.getUid());
        refUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String firstName = Objects.requireNonNull(snapshot.child("firstName").getValue()).toString();
                String lastName = Objects.requireNonNull(snapshot.child("lastName").getValue()).toString();
                tvUserName.setText(firstName + " " + lastName);

                if (snapshot.child("photoUrl").exists()) {
                    Picasso.get().load(snapshot.child("photoUrl").getValue().toString()).fit().centerCrop().into(ivProfile);
                }
                else {
                    Picasso.get().load(Utils.getDefaultPhotoUrl()).fit().centerCrop().into(ivProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        tvUserEmail.setText(USER.getEmail());
        btnLogout.setOnClickListener(view -> {
            Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, AuthenticationActivity.class));
            finish();
        });
        // header

        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        navView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.miPets) {
                Fragment fragment = new PetsFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout,fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
            else if (item.getItemId() == R.id.miMemories) {
                Fragment fragment = new MemoriesFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout,fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
            else if (item.getItemId() == R.id.miSchedule) {
                Fragment fragment = new ScheduleFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout,fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
            else {
                Toast.makeText(this, "Not added yet", Toast.LENGTH_SHORT).show();
            }
            drawerLayout.close();
            return true;
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_out_enter, R.anim.zoom_out_exit);
    }
}