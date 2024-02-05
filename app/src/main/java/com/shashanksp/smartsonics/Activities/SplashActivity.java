package com.shashanksp.smartsonics.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.shashanksp.smartsonics.R;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent;
                    if(isLoggedIn){
                        intent = new Intent(SplashActivity.this, HomeScanActivity.class);
                    } else {
                        intent = new Intent(SplashActivity.this, SignInActivity.class);
                    }
                    startActivity(intent);
                    finish();
                }
            }
        };
        //start thread
        timerThread.start();
    }
}