package com.shashanksp.smartsonics;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {
    Button loginbtn;
    TextView signupbtn;
    EditText guideIDEdt;
    String guideId;
    private FirebaseAuth mAuth;
    EditText emailEdt;
    boolean isGuide;
    EditText pwdEdt;
    private static final String PREF_GUIDE_ID = "guideId";
    private static final String PREF_IS_GUIDE = "isGuide";
    @Override
    protected void onStart() {
        super.onStart();
        // Check if the user is already logged in
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        guideId = getIntent().getStringExtra("guideId");
        isGuide = getIntent().getBooleanExtra("isGuide", false);
        // Save guideId and isGuide to SharedPreferences
//        guideId = getGuideIdFromPrefs();
//        isGuide = getIsGuideFromPrefs();
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            // User is logged in, start the HomeActivity and finish LoginActivity
            Intent intent = new Intent(SignInActivity.this, HomeScanActivity.class);
            if(isGuide){
                saveGuideIdToPrefs(guideId);
                saveIsGuideToPrefs(isGuide);
            }else{
                saveIsGuideToPrefs(false);
            }
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        loginbtn = findViewById(R.id.login_button);
        signupbtn = findViewById(R.id.createacc_txtbtn);
        guideIDEdt = findViewById(R.id.GuideID_edt);
        emailEdt = findViewById(R.id.email_edittext);
        pwdEdt = findViewById(R.id.password_edittext);
        mAuth = FirebaseAuth.getInstance();
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(emailEdt.getText().toString().equals("") || pwdEdt.getText().toString().equals(""))) {
                    login(emailEdt.getText().toString(), pwdEdt.getText().toString());
                }else{
                    Toast.makeText(SignInActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignInActivity.this,CreateAccountActivity.class);
                startActivity(i);
            }
        });

        final RadioGroup radio = (RadioGroup) findViewById(R.id.radiogrp);
        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                View radioButton = radio.findViewById(checkedId);
                int index = radio.indexOfChild(radioButton);
                switch (index) {
                    case 0: // first button
                        guideIDEdt.setVisibility(View.VISIBLE);
                        isGuide= true;
                        break;
                    case 1: // secondbutton
                        guideIDEdt.setVisibility(View.GONE);
                        isGuide = false;
                        break;
                }
            }
        });

    }

    private void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Login", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Store the login status in SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isLoggedIn", true);

                            Intent intent = new Intent(SignInActivity.this, HomeScanActivity.class);
                            if(isGuide){
                                intent.putExtra("isGuide",true);
                                intent.putExtra("guideId",guideIDEdt.getText().toString());
                                saveGuideIdToPrefs(guideIDEdt.getText().toString());
                                saveIsGuideToPrefs(isGuide);
                            }else {
                                intent.putExtra("isGuide", false);
                                saveIsGuideToPrefs(false);
                            }
                            editor.apply();
                            startActivity(intent);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Login", "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
    private void saveGuideIdToPrefs(String guideId) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_GUIDE_ID, guideId);
        editor.apply();
    }
    private String getGuideIdFromPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString(PREF_GUIDE_ID, "");
    }
    private boolean getIsGuideFromPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(PREF_IS_GUIDE, false);
    }

    private void saveIsGuideToPrefs(boolean isGuide) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_IS_GUIDE, isGuide);
        editor.apply();
    }
}