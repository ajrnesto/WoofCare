package com.woofcare.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.woofcare.R;
import com.woofcare.Utils.Utils;

import java.util.Objects;

public class AuthenticationActivity extends AppCompatActivity {

    private static final FirebaseDatabase WOOF_CARE_DB = FirebaseDatabase.getInstance();
    private static final FirebaseAuth AUTH = FirebaseAuth.getInstance();
    private DatabaseReference refUser;

    MaterialCardView cvLogin, cvSignup;
    TextInputEditText etLoginEmail, etLoginPassword;
    TextInputEditText etSignupFirstName, etSignupLastName, etSignupEmail, etSignupPassword;
    MaterialButton btnLogin, btnShowSignup, btnSignup, btnShowLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.zoom_in_enter, R.anim.zoom_in_exit);
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.gray, this.getTheme()));
        } else {
            getWindow().setStatusBarColor(getResources().getColor(R.color.gray));
        }
        setContentView(R.layout.activity_authentication);

        initialize();

        btnSignup.setOnClickListener(view -> {
            registerUser();
        });

        btnShowLogin.setOnClickListener(view -> {
            cvLogin.setVisibility(View.VISIBLE);
            cvSignup.setVisibility(View.GONE);
        });

        btnShowSignup.setOnClickListener(view -> {
            cvLogin.setVisibility(View.GONE);
            cvSignup.setVisibility(View.VISIBLE);
        });

        btnLogin.setOnClickListener(view -> {
            loginUser();
        });

        btnShowSignup.setOnClickListener(view -> {
            cvLogin.setVisibility(View.GONE);
            cvSignup.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_out_enter, R.anim.zoom_out_exit);
    }

    private void initialize() {
        // login
        cvLogin = findViewById(R.id.cvLogin);
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnShowSignup = findViewById(R.id.btnShowSignup);
        // sign up
        cvSignup = findViewById(R.id.cvSignup);
        etSignupFirstName = findViewById(R.id.etSignupFirstName);
        etSignupLastName = findViewById(R.id.etSignupLastName);
        etSignupEmail = findViewById(R.id.etSignupEmail);
        etSignupPassword = findViewById(R.id.etSignupPassword);
        btnSignup = findViewById(R.id.btnSignup);
        btnShowLogin = findViewById(R.id.btnShowLogin);
    }

    private void registerUser() {
        btnSignup.setEnabled(false);
        String firstName = Objects.requireNonNull(etSignupFirstName.getText()).toString();
        String lastName = Objects.requireNonNull(etSignupLastName.getText()).toString();
        String email = Objects.requireNonNull(etSignupEmail.getText()).toString();
        String password = Objects.requireNonNull(etSignupPassword.getText()).toString();

        if (TextUtils.isEmpty(firstName) ||
                TextUtils.isEmpty(lastName) ||
                TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password)) {
            Utils.basicDialog(this, "All fields are required!", "Okay");
            btnSignup.setEnabled(true);
            return;
        }

        if (password.length() < 6) {
            Utils.basicDialog(this, "The password should be at least 6 characters", "Okay");
            btnSignup.setEnabled(true);
            return;
        }

        AUTH.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        refUser = WOOF_CARE_DB.getReference("user_"+ Objects.requireNonNull(AUTH.getCurrentUser()).getUid());
                        refUser.child("uid").setValue(AUTH.getCurrentUser().getUid());
                        refUser.child("firstName").setValue(firstName);
                        refUser.child("lastName").setValue(lastName);
                        Toast.makeText(AuthenticationActivity.this, "Logging in...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    }
                    else {
                        Utils.basicDialog(this, "Something went wrong! Please try again.", "Try again");
                        btnSignup.setEnabled(true);
                    }
                });
    }

    private void loginUser() {
        btnLogin.setEnabled(false);
        String email = Objects.requireNonNull(etLoginEmail.getText()).toString();
        String password = Objects.requireNonNull(etLoginPassword.getText()).toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Utils.basicDialog(this, "All fields are required!", "Okay");
            btnLogin.setEnabled(true);
            return;
        }

        AUTH.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Sign in as "+email, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                    }
                    else {
                        Utils.basicDialog(this, "Email or password is incorrect", "Try again");
                        btnLogin.setEnabled(true);
                    }
                });
    }
}