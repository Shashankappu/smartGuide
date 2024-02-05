package com.shashanksp.smartsonics.Activities;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashanksp.smartsonics.Models.User;
import com.shashanksp.smartsonics.R;
import com.shashanksp.smartsonics.Models.Story;
public class AddStoryActivity extends AppCompatActivity {
    EditText storiesEdt,artnameEdt;
    Button addBtn,clearBtn;
    String username;

    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_add_story);
        ImageView homeBtn = findViewById(R.id.homeBtn);
        storiesEdt = findViewById(R.id.storiesEdt);
        artnameEdt = findViewById(R.id.artnameEdt);
        addBtn = findViewById(R.id.addBtn);
        clearBtn = findViewById(R.id.clearBtn);
        getUsernameFromFirebase();
        databaseReference = FirebaseDatabase.getInstance().getReference("stories");

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddStoryActivity.this,HomeScanActivity.class);
                startActivity(i);
                finish();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addstorytoFirebase(username);
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearEdts();
            }
        });

    }
    private void clearEdts(){
        storiesEdt.getText().clear();
        artnameEdt.getText().clear();
    }
    private void addstorytoFirebase(String username){
        String storyText = storiesEdt.getText().toString();
        String artName = artnameEdt.getText().toString();

        if (!storyText.isEmpty() && !artName.isEmpty()) {
            // Create a unique key for the story
            String storyId = databaseReference.push().getKey();
            if (storyId == null) {
                // If push().getKey() returns null, generate a unique key using another method
                storyId = generateUniqueId();
            }

            // Create a Story object
            Story story = new Story(username, artName, storyText);
            // Add the story to the database under a unique key
            databaseReference.child(storyId).setValue(story);
            // Clear the EditTexts after adding the story
            clearEdts();
            Toast.makeText(this, "Story Added Successfully", Toast.LENGTH_SHORT).show();
        } else {
            // Handle case where story text or art name is empty
            // You may want to show a Toast or provide some feedback to the user
            Toast.makeText(this, "Story text and art name cannot be empty", Toast.LENGTH_SHORT).show();
        }
        Intent i = new Intent(AddStoryActivity.this,HomeScanActivity.class);
        startActivity(i);
        finish();
    }

    // Generate a unique key using timestamp
    private String generateUniqueId() {
        return String.valueOf(System.currentTimeMillis());
    }

    private void getUsernameFromFirebase() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("email", "");

        if (!userEmail.isEmpty()) {
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
            String userId = userEmail.replace(".", ",");

            usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            username = user.username;
                            //addstorytoFirebase(username);
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

}