package com.shashanksp.smartsonics.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashanksp.smartsonics.Models.User;
import com.shashanksp.smartsonics.R;

public class EnterContentActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private String guideId;
    private String artId;
    private String username;
    EditText ArtName,details;
    Button submitBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_content);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        ArtName = findViewById(R.id.Artname);
        details = findViewById(R.id.detailsEdt);
        submitBtn = findViewById(R.id.submitBtn);
        String userEmail = getUserEmailFromPrefs();

        retrieveUserDetails(userEmail);
        artId = getIntent().getStringExtra("artId");

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (artId.isEmpty() || details.getText().toString().isEmpty()) {
                    Toast.makeText(EnterContentActivity.this,"Enter the details"+guideId+artId,Toast.LENGTH_LONG).show();
                }else{
                    DatabaseReference artReference = databaseReference.child(artId).child(username + "," + guideId);
                    artReference.child("Details").setValue(details.getText().toString());
                    Toast.makeText(EnterContentActivity.this,"Details Entered Successfully",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(EnterContentActivity.this,HomeScanActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }
    private String getUserEmailFromPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("email", "");
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
                        guideId = user.guideId;
                        username = user.username;
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