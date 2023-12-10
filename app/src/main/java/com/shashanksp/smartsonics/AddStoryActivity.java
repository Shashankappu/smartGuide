package com.shashanksp.smartsonics;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        username = getIntent().getStringExtra("username");
        Log.d("crashh", "username reference: " + username);
        username = extractUsername(username);
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
    private String extractUsername(String username) {
        int atIndex = username.indexOf('@');
        if (atIndex != -1) {
            return username.substring(0, atIndex);
        }
        return username;
    }
    private static class Story {
        public String username;
        public String artName;
        public String storyText;

        public Story(String username, String artName, String storyText) {
            this.username = username;
            this.artName = artName;
            this.storyText = storyText;
        }
    }
    // Generate a unique key using timestamp
    private String generateUniqueId() {
        return String.valueOf(System.currentTimeMillis());
    }
}