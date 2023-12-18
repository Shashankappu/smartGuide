package com.shashanksp.smartsonics;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private static final String PREF_GUIDE_ID = "guideId";
    private static final String PREF_IS_GUIDE = "isGuide";
    private static final String USER_EMAIL = "anonymous";
    private static final String USERNAME = " ";
    private TextView welcomeTxt;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        username = getUsernameFromPrefs();
        welcomeTxt = findViewById(R.id.welcomeTxt);
        welcomeTxt.setText("Welcome "+ username);

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1500);//splash time
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
                    intent.putExtra("guideId",getGuideIdFromPrefs());
                    intent.putExtra("isGuide",getIsGuideFromPrefs());
                    startActivity(intent);
                    finish();
                }
            }
        };
        //start thread
        timerThread.start();
    }
    private String getGuideIdFromPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString(PREF_GUIDE_ID, "");
    }

    private boolean getIsGuideFromPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(PREF_IS_GUIDE, false);
    }
    private String getUsernameFromPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString(USERNAME,"anonymous user");
    }
    private void clearSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // This will clear all the data in the SharedPreferences
        editor.apply();
    }
}