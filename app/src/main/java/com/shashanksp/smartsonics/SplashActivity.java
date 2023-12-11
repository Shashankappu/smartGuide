package com.shashanksp.smartsonics;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private static final String PREF_GUIDE_ID = "guideId";
    private static final String PREF_IS_GUIDE = "isGuide";
    private static final String USER_EMAIL = "anonymous";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

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
    private String getusernameFromPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString(USER_EMAIL, "");
    }

    private void saveIsGuideToPrefs(boolean isGuide) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_IS_GUIDE, isGuide);
        editor.apply();
    }

    private boolean getIsGuideFromPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(PREF_IS_GUIDE, false);
    }
    private void clearSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // This will clear all the data in the SharedPreferences
        editor.apply();
    }
}