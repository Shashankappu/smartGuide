package com.shashanksp.smartsonics.Activities;

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
import com.shashanksp.smartsonics.R;

public class CreateAccountActivity extends AppCompatActivity {
    Button registerbtn;
    TextView signinbtn;
    EditText guideIDEdt;
    EditText emailEdt,usernameEdt;
    EditText pwdEdt;
    EditText cnfpwdEdt;
    boolean isGuide;
    String guideId;
    private FirebaseAuth mAuth;
    private static final String PREF_GUIDE_ID = "guideId";
    private static final String PREF_IS_GUIDE = "isGuide";
    private static final String USER_EMAIL = "anonymous";
    private static final String USERNAME = "anonymous user";

    @Override
    protected void onStart() {
        super.onStart();
        // Check if the user is already logged in
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        guideId = getIntent().getStringExtra("guideId");
        isGuide = getIntent().getBooleanExtra("isGuide", false);
//        guideId = getGuideIdFromPrefs();
//        isGuide = getIsGuideFromPrefs();
        if (isLoggedIn) {
            // User is logged in, start the HomeActivity and finish LoginActivity
            Intent intent = new Intent(CreateAccountActivity.this, HomeScanActivity.class);
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
        setContentView(R.layout.activity_create_account);
        registerbtn = findViewById(R.id.register_button);
        signinbtn = findViewById(R.id.Signin_txtbtn);
        guideIDEdt = findViewById(R.id.GuideID_edt);
        emailEdt = findViewById(R.id.email_edittext);
        usernameEdt = findViewById(R.id.username_edittext);
        pwdEdt = findViewById(R.id.password_edittext);
        cnfpwdEdt= findViewById(R.id.confirmpassword_text);
        mAuth = FirebaseAuth.getInstance();
        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGuide) {
                    if (validateGuideId(guideIDEdt.getText().toString().trim())) {
                        if (validatePassword(pwdEdt.getText().toString(), cnfpwdEdt.getText().toString())) {
                            if (emailEdt.getText().toString().isEmpty() && usernameEdt.getText().toString().isEmpty()) {
                                Toast.makeText(CreateAccountActivity.this, "Invalid Email or username", Toast.LENGTH_LONG).show();
                            } else {
                                saveUsernameToPrefs(usernameEdt.getText().toString());
                                Register(emailEdt.getText().toString(), pwdEdt.getText().toString());
                            }
                        }else{
                            Toast.makeText(CreateAccountActivity.this, "password does not match", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(CreateAccountActivity.this, "Invalid Guide Id", Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (validatePassword(pwdEdt.getText().toString(), cnfpwdEdt.getText().toString())) {
                        if (emailEdt.getText().toString().isEmpty() && usernameEdt.getText().toString().isEmpty()) {
                            Toast.makeText(CreateAccountActivity.this, "Invalid Email or username", Toast.LENGTH_LONG).show();
                        } else {
                            saveUsernameToPrefs(usernameEdt.getText().toString());
                            Register(emailEdt.getText().toString(), pwdEdt.getText().toString());
                        }
                    }
                    else{
                        Toast.makeText(CreateAccountActivity.this, "password does not match", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        signinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CreateAccountActivity.this,SignInActivity.class);
                startActivity(i);
                finish();
            }
        });

        final RadioGroup radio = (RadioGroup) findViewById(R.id.radiogrp);
        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = radio.findViewById(checkedId);
                int index = radio.indexOfChild(radioButton);
                switch (index) {
                    case 0: // guide button
                        guideIDEdt.setVisibility(View.VISIBLE);
                        isGuide = true;
                        break;
                    case 1: // traveller button
                        guideIDEdt.setVisibility(View.GONE);
                        isGuide = false;
                        break;
                }
            }
        });

    }
    private boolean validateGuideId(String guideId){
        String regex = "[a-zA-Z]\\d[a-zA-Z]{2}\\d{4,5}";
        // Check if guideId matches the pattern
        return guideId.matches(regex);
    }
    private boolean validatePassword(String pass, String cnfpass) {
        if(!pass.isEmpty() && !cnfpass.isEmpty()){
            return pass.equals(cnfpass);
        }
        Toast.makeText(CreateAccountActivity.this,"Password Don't match",Toast.LENGTH_LONG).show();
        return false;
    }
    private void Register(String email,String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Register", "createUserWithEmail:success");
                            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isLoggedIn", true);
                            editor.apply();
                            Intent intent = new Intent(CreateAccountActivity.this,HomeScanActivity.class);
                            if(isGuide){
                                intent.putExtra("isGuide",true);
                                intent.putExtra("guideId",guideIDEdt.getText().toString());
                                saveIsGuideToPrefs(true);
                                saveGuideIdToPrefs(guideIDEdt.getText().toString());
                            }else {
                                intent.putExtra("isGuide",false);
                                saveIsGuideToPrefs(false);
                            }
                            saveUseremailToPrefs(email);
                            intent.putExtra("useremail",email);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Register", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(CreateAccountActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
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

    private void saveIsGuideToPrefs(boolean isGuide) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_IS_GUIDE, isGuide);
        editor.apply();
    }

    private void saveUseremailToPrefs(String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_EMAIL, email);
        editor.apply();
    }
    private void saveUsernameToPrefs(String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME, email);
        editor.apply();
    }
}
