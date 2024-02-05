package com.shashanksp.smartsonics.Activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.shashanksp.smartsonics.R;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String email,pass;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onRestart() {
        super.onRestart();
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            startHomeActivity();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // if the user logged in previously
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            startHomeActivity();
        }

        EditText emailEdt = findViewById(R.id.email_edittext);
        EditText pwdEdt = findViewById(R.id.password_edittext);
        mAuth = FirebaseAuth.getInstance();
        
        Button loginBtn = findViewById(R.id.login_button);
        TextView signupBtn = findViewById(R.id.createacc_txtbtn);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!emailEdt.getText().toString().isEmpty() && !pwdEdt.getText().toString().isEmpty()) {
                    login(emailEdt.getText().toString(), pwdEdt.getText().toString());
                } else {
                    Toast.makeText(SignInActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, CreateAccountActivity.class));
            }
        });
    }

    private void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Login successful, retrieve user details and start HomeActivity
                            saveUserDetailsToPrefs(email);
                            startHomeActivity();
                        } else {
                            // Login failed, display a message
                            Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    
    private void saveUserDetailsToPrefs(String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("email",email);
        editor.apply();
    }

    private void startHomeActivity() {
        Intent intent = new Intent(SignInActivity.this, HomeScanActivity.class);
        startActivity(intent);
        finish();
    }
}
