package com.shashanksp.smartsonics.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shashanksp.smartsonics.Models.User;
import com.shashanksp.smartsonics.R;

public class CreateAccountActivity extends AppCompatActivity {
    private EditText guideIDEdt, emailEdt, usernameEdt, pwdEdt, cnfpwdEdt;
    private boolean isGuide;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        Button registerBtn = findViewById(R.id.register_button);
        TextView signinBtn = findViewById(R.id.Signin_txtbtn);
        guideIDEdt = findViewById(R.id.GuideID_edt);
        emailEdt = findViewById(R.id.email_edittext);
        usernameEdt = findViewById(R.id.username_edittext);
        pwdEdt = findViewById(R.id.password_edittext);
        cnfpwdEdt= findViewById(R.id.confirmpassword_text);

        mAuth = FirebaseAuth.getInstance();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    Register(emailEdt.getText().toString().trim(), pwdEdt.getText().toString().trim());
                }
            }
        });

        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CreateAccountActivity.this, SignInActivity.class);
                startActivity(i);
                finish();
            }
        });

        RadioGroup radioGroup = findViewById(R.id.radiogrp);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                isGuide = checkedId == R.id.guide_radio_button;
                guideIDEdt.setVisibility(isGuide ? View.VISIBLE : View.GONE);
            }
        });
    }

    private boolean validateFields() {
        if (isGuide) {
            if (!validateGuideId(guideIDEdt.getText().toString().trim())) {
                Toast.makeText(this, "Invalid Guide Id", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        if (!validatePassword(pwdEdt.getText().toString(), cnfpwdEdt.getText().toString())) {
            Toast.makeText(this, "Password does not match", Toast.LENGTH_LONG).show();
            return false;
        }

        if (emailEdt.getText().toString().isEmpty() || usernameEdt.getText().toString().isEmpty()) {
            Toast.makeText(this, "Invalid Email or Username", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private boolean validateGuideId(String guideId) {
        String regex = "[a-zA-Z]\\d[a-zA-Z]{2}\\d{4,5}";
        // Check if guideId matches the pattern
        return guideId.matches(regex);
    }

    private boolean validatePassword(String pass, String cnfpass) {
        if (!pass.isEmpty() && !cnfpass.isEmpty()) {
            return pass.equals(cnfpass);
        }
        return false;
    }

    private void Register(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registration successful, save user details and start HomeActivity
                            saveUserDetailsToFirebase(email, isGuide, usernameEdt.getText().toString(), guideIDEdt.getText().toString());
                            saveUserDetailsToPrefs(email);
                            startHomeActivity();
                        } else {
                            // Registration failed, display a message
                            Toast.makeText(CreateAccountActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUserDetailsToFirebase(String email, boolean isGuide, String username, String guideId) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        String userId = email.replace(".", ",");
        User user = new User(email, isGuide, username, guideId);
        usersRef.child(userId).setValue(user);
        Toast.makeText(CreateAccountActivity.this, "username is :" + username, Toast.LENGTH_SHORT).show();
    }

    private void saveUserDetailsToPrefs(String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("email",email);
        editor.apply();
    }

    private void startHomeActivity() {
        Intent intent = new Intent(CreateAccountActivity.this, HomeScanActivity.class);
        startActivity(intent);
        finish();
    }
}
