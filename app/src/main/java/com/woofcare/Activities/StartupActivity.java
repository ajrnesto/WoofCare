package com.woofcare.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;
import com.woofcare.R;

public class StartupActivity extends AppCompatActivity {

    private static final FirebaseDatabase WOOF_CARE_DB = FirebaseDatabase.getInstance();
    private static final FirebaseAuth AUTH = FirebaseAuth.getInstance();
    private static final FirebaseUser USER = AUTH.getCurrentUser();
    private DatabaseReference refUser;

    RoundedImageView ivLogo, ivHello, ivMemories, ivTasks, ivStart;
    MaterialButton btnNext, btnSkip;
    TextView tvHello, tvHello2, tvMemories, tvTasks, tvStart;
    MaterialCardView cvSlideshow;

    int slide = 0;

    @Override
    protected void onStart() {
        super.onStart();
        if (USER != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        slide = 0;
        ivHello.setVisibility(View.VISIBLE);
        tvHello.setText("Hello Human");
        tvHello2.setVisibility(View.VISIBLE);
        ivMemories.setVisibility(View.INVISIBLE);
        tvMemories.setVisibility(View.INVISIBLE);
        ivTasks.setVisibility(View.INVISIBLE);
        tvTasks.setVisibility(View.INVISIBLE);
        ivStart.setVisibility(View.INVISIBLE);
        tvStart.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.zoom_in_enter, R.anim.zoom_in_exit);
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blue_oracle, this.getTheme()));
        } else {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blue_oracle));
        }
        setContentView(R.layout.activity_startup);

        initialize();
        startLoading();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_out_enter, R.anim.zoom_out_exit);
    }

    private void initialize() {
        ivLogo = findViewById(R.id.ivLogo);
        ivHello = findViewById(R.id.ivHello);
        ivMemories = findViewById(R.id.ivMemories);
        ivTasks = findViewById(R.id.ivTasks);
        ivStart = findViewById(R.id.ivStart);
        tvHello = findViewById(R.id.tvHello);
        tvHello2 = findViewById(R.id.tvHello2);
        tvMemories = findViewById(R.id.tvMemories);
        tvTasks = findViewById(R.id.tvTasks);
        tvStart = findViewById(R.id.tvStart);
        cvSlideshow = findViewById(R.id.cvSlideshow);
        btnNext = findViewById(R.id.btnNext);
        btnSkip = findViewById(R.id.btnSkip);
    }

    private void startLoading() {
        ivLogo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.intro));

        final Handler handler = new Handler();
        handler.postDelayed(this::loadingComplete, 2000);
    }

    private void loadingComplete() {
        ivLogo.setVisibility(View.GONE);
        cvSlideshow.setVisibility(View.VISIBLE);

        btnSkip.setOnClickListener(view -> {
            startActivity(new Intent(this, AuthenticationActivity.class));
        });

        btnNext.setOnClickListener(view -> {
            switch (slide) {
                case 0:
                    tvHello.setText("Welcome to WoofCare");
                    // save your precious memories
                    ivHello.setVisibility(View.INVISIBLE);
                    tvHello2.setVisibility(View.INVISIBLE);
                    ivMemories.setVisibility(View.VISIBLE);
                    tvMemories.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    // manage tasks and schedules
                    ivMemories.setVisibility(View.INVISIBLE);
                    tvMemories.setVisibility(View.INVISIBLE);
                    ivTasks.setVisibility(View.VISIBLE);
                    tvTasks.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    // get started
                    ivTasks.setVisibility(View.INVISIBLE);
                    tvTasks.setVisibility(View.INVISIBLE);
                    ivStart.setVisibility(View.VISIBLE);
                    tvStart.setVisibility(View.VISIBLE);
                    btnNext.setText("Get started");
                    break;
                case 3:
                    startActivity(new Intent(this, AuthenticationActivity.class));
                    break;
            }
            slide++;
        });
    }
}