package com.shashanksp.smartsonics;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StoriesActivity extends AppCompatActivity {

    private ListView storiesListView;
    private StoriesAdapter storiesAdapter;
    private DatabaseReference storiesReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories);

        ImageView homeBtn = findViewById(R.id.homeBtn);
        storiesListView = findViewById(R.id.storiesLV);

        // Set up Firebase Database reference to "stories" node
        storiesReference = FirebaseDatabase.getInstance().getReference("stories");

        // Initialize StoriesAdapter
        ArrayList<Story> storiesList = new ArrayList<>();
        storiesAdapter = new StoriesAdapter(this, storiesList);
        storiesListView.setAdapter(storiesAdapter);

        // Load stories from Firebase
        loadStoriesFromFirebase();
        Log.d("crashh", "stories loaded ");
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StoriesActivity.this, HomeScanActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void loadStoriesFromFirebase() {
        // Add a ValueEventListener to retrieve data from Firebase
        storiesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear the current list before adding new data
                storiesAdapter.clear();

                // Iterate through all stories in dataSnapshot
                for (DataSnapshot storySnapshot : dataSnapshot.getChildren()) {
                    // Get the story object and add it to the adapter
                    Story story = storySnapshot.getValue(Story.class);
                    if (story != null) {
                        storiesAdapter.add(story);
                    }
                }

                // Notify the adapter that the data set has changed
                storiesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error if data retrieval is unsuccessful
            }
        });
    }
}
