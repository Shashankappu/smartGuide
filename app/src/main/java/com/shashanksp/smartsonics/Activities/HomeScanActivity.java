package com.shashanksp.smartsonics.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.shashanksp.smartsonics.Models.User;
import com.shashanksp.smartsonics.R;


public class HomeScanActivity extends AppCompatActivity {

    private boolean isExpanded = false;
    private String userEmail;
    private Boolean isGuide;
    private FloatingActionButton mainFabBtn,addstoryBtn,viewstoryBtn;
    private  Button scanBtn;
    private ImageView logoutBtn;
    private TextView addstoryTV,viewstoryTV;
    private String artId;

    private Animation fromBottomAnim;
    private Animation toBottomAnim;
    private Animation rotateClockwiseAnim;
    private Animation rotateAntiClockwiseAnim;

    private static final String USER_EMAIL = "email";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_scan);
        permissionCheck();

        initializeViews();
        initializeAnimations();

        userEmail = getUserEmailFromPrefs();
        Toast.makeText(HomeScanActivity.this, "hello "+ userEmail, Toast.LENGTH_SHORT).show();

        mainFabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFabMenu();
            }
        });

        initializeCodeScanner();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearSharedPreferences();
                startActivity(new Intent(HomeScanActivity.this, SignInActivity.class));
                finish();
            }
        });

        addstoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScanActivity.this, AddStoryActivity.class));
                finish();
            }
        });
        viewstoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScanActivity.this, StoriesActivity.class));
                finish();
            }
        });

        retrieveUserDetails(userEmail);
    }
    
    private void initializeViews() {
        addstoryTV = findViewById(R.id.addstoryTV);
        viewstoryTV = findViewById(R.id.viewstoryTV);
        viewstoryBtn = findViewById(R.id.viewstoryFabBtn);
        addstoryBtn = findViewById(R.id.addstoryFabBtn);
        mainFabBtn = findViewById(R.id.mainFabBtn);
        scanBtn = findViewById(R.id.scanBtn);
        logoutBtn = findViewById(R.id.logoutbtn);
    }

    private void initializeAnimations() {
        fromBottomAnim = getAnimation(R.anim.from_bottom_fab);
        toBottomAnim = getAnimation(R.anim.to_bottom_fab);
        rotateClockwiseAnim = getAnimation(R.anim.rotate_clock_wise);
        rotateAntiClockwiseAnim = getAnimation(R.anim.rotate_anti_clock_wise);
    }

    private void initializeCodeScanner() {
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        CodeScanner mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.startPreview();
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(HomeScanActivity.this, "Click on Continue", Toast.LENGTH_SHORT).show();
                        handleDecodedResult(result.getText());
                        artId = result.getText().trim();
                    }
                });
            }
        });

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!artId.isEmpty()) {
                    retrieveUserDetails(userEmail);
                    startNextActivity();
                } else {
                    Toast.makeText(HomeScanActivity.this, "Could not find any QR Code", Toast.LENGTH_LONG).show();
                    restartScanActivity();
                }
            }
        });
    }

    private void toggleFabMenu() {
        if (isExpanded) {
            shrinkFab();
        } else {
            expandFab();
        }
    }

    private void shrinkFab() {
        mainFabBtn.startAnimation(rotateAntiClockwiseAnim);
        addstoryBtn.startAnimation(toBottomAnim);
        viewstoryBtn.startAnimation(toBottomAnim);
        viewstoryTV.startAnimation(toBottomAnim);
        addstoryTV.startAnimation(toBottomAnim);
        isExpanded = false;
    }

    private void expandFab() {
        mainFabBtn.startAnimation(rotateClockwiseAnim);
        addstoryBtn.startAnimation(fromBottomAnim);
        viewstoryBtn.startAnimation(fromBottomAnim);
        viewstoryTV.startAnimation(fromBottomAnim);
        addstoryTV.startAnimation(fromBottomAnim);
        isExpanded = true;
    }

    private void handleDecodedResult(String resultText) {
        artId = resultText;
    }

    private void startNextActivity() {
        Intent intent;
        if (isGuide) {
            intent = new Intent(HomeScanActivity.this, EnterContentActivity.class);
        } else {
            intent = new Intent(HomeScanActivity.this, GuidelistActivity.class);
        }
        intent.putExtra("artId", artId);
        Toast.makeText(HomeScanActivity.this, "Logging in as Guide? "+isGuide, Toast.LENGTH_SHORT).show();
        startActivity(intent);
        finish();
    }

    private void restartScanActivity() {
        startActivity(new Intent(HomeScanActivity.this, HomeScanActivity.class));
        finish();
    }

    private Animation getAnimation(int resId) {
        return AnimationUtils.loadAnimation(HomeScanActivity.this, resId);
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


    private void clearSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // This will clear all the data in the SharedPreferences
        editor.apply();
    }
    private String getUserEmailFromPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString(USER_EMAIL, "");
    }

    private void retrieveUserDetails(final String email) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        String userId = email.replace(".", ",");

        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        isGuide = user.isGuide;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error if needed
            }
        });
    }

}
