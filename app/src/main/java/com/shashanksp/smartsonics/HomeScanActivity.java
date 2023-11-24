package com.shashanksp.smartsonics;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;



public class HomeScanActivity extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    private String artId, guideId;
    private boolean isGuide;
    private static final String PREF_GUIDE_ID = "guideId";
    private static final String PREF_IS_GUIDE = "isGuide";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_scan);
        permissionCheck();

        Button scanBtn = findViewById(R.id.scanBtn);
        ImageView logoutBtn = findViewById(R.id.logoutbtn);

        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);

        guideId = getIntent().getStringExtra("guideId");
        isGuide = getIntent().getBooleanExtra("isGuide",false);
        // Get guideId and isGuide from SharedPreferences
//        guideId = getGuideIdFromPrefs();
//        isGuide = getIsGuideFromPrefs();


        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(HomeScanActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
                        artId = result.getText();
                    }
                });
            }
        });

        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                if (isGuide) {
                    i = new Intent(HomeScanActivity.this, EnterContentActivity.class);
                } else {
                    i = new Intent(HomeScanActivity.this, GuidelistActivity.class);
                }
                i.putExtra("guideId", guideId);
                i.putExtra("artId", artId);
                startActivity(i);
                finish();
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear the login status in SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.apply();
                clearSharedPreferences();
                // Redirect to the LoginActivity
                Intent intent = new Intent(HomeScanActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    protected void permissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 12);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != 12) {
            permissionCheck();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Get guideId and isGuide from SharedPreferences and set them
        guideId = getGuideIdFromPrefs();
        isGuide = getIsGuideFromPrefs();
        mCodeScanner.startPreview();
    }


    @Override
    protected void onPause() {
        // Save guideId and isGuide to SharedPreferences when the activity is paused
        saveGuideIdToPrefs(guideId);
        saveIsGuideToPrefs(isGuide);
        mCodeScanner.releaseResources();
        super.onPause();
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
