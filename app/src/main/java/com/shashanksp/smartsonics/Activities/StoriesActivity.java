package com.shashanksp.smartsonics.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import androidx.appcompat.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashanksp.smartsonics.R;
import com.shashanksp.smartsonics.Utils.StoriesAdapter;
import com.shashanksp.smartsonics.Utils.Story;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class StoriesActivity extends AppCompatActivity {

    private RelativeLayout rootlayout;
    private ListView storiesListView;
    SearchView searchView;
    private StoriesAdapter storiesAdapter;
    private DatabaseReference storiesReference;
    ProgressBar pgBar ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories);

        ImageView homeBtn = findViewById(R.id.homeBtn);

        searchView = findViewById(R.id.searchView);
        setupSearchView();

        pgBar = findViewById(R.id.pgBar);
        pgBar.setVisibility(View.VISIBLE);

        storiesListView = findViewById(R.id.storiesLV);

        // Set up Firebase Database reference to "stories" node
        storiesReference = FirebaseDatabase.getInstance().getReference("stories");

        // Initialize StoriesAdapter
        ArrayList<Story> storiesList = new ArrayList<>();
        storiesAdapter = new StoriesAdapter(this, storiesList);
        storiesListView.setAdapter(storiesAdapter);

        // Load stories from Firebase
        loadStoriesFromFirebase();

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
        if (storiesAdapter != null) {
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
                    pgBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error if data retrieval is unsuccessful
                }
            });
        }
    }
    private void loadStoriesFromFirebaseWithQuery(String query) {
        if (storiesAdapter != null) {
            // Convert the query to lowercase for case-insensitive search
            String lowerCaseQuery = query.toLowerCase();

            // Use a Map to store unique stories
            Map<String, Story> uniqueStoriesMap = new HashMap<>();

            // Create a counter to keep track of the number of ValueEventListener callbacks
            AtomicInteger callbacksCount = new AtomicInteger(0);

            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Increment the counter
                    callbacksCount.incrementAndGet();

                    // Iterate through filtered stories in dataSnapshot
                    for (DataSnapshot storySnapshot : dataSnapshot.getChildren()) {
                        // Get the story object
                        Story story = storySnapshot.getValue(Story.class);
                        if (story != null) {
                            // Convert relevant fields to lowercase for case-insensitive search
                            String lowerCaseArtName = story.getArtName().toLowerCase();
                            String lowerCaseUsername = story.getUsername().toLowerCase();
                            String lowerCaseStoryText = story.getStoryText().toLowerCase();

                            // Check if any part of the fields contains the query
                            if (lowerCaseArtName.contains(lowerCaseQuery) ||
                                    lowerCaseUsername.contains(lowerCaseQuery) ||
                                    lowerCaseStoryText.contains(lowerCaseQuery)) {
                                // Use a unique identifier (e.g., story key) as the map key
                                String storyKey = storySnapshot.getKey();
                                uniqueStoriesMap.put(storyKey, story);
                            }
                        }
                    }

                    // If all callbacks have been received, update the adapter and views
                    if (callbacksCount.get() == 3) {
                        List<Story> uniqueStoriesList = new ArrayList<>(uniqueStoriesMap.values());
                        storiesAdapter.clear();
                        for (Story story : uniqueStoriesList) {
                            storiesAdapter.add(story);
                        }
                        storiesAdapter.notifyDataSetChanged();
                        pgBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error if data retrieval is unsuccessful
                    pgBar.setVisibility(View.GONE);
                }
            };

            // Add the ValueEventListener for each filter
            storiesReference.orderByChild("artName").startAt(query).endAt(query + "\uf8ff")
                    .addListenerForSingleValueEvent(valueEventListener);

            storiesReference.orderByChild("username").startAt(query).endAt(query + "\uf8ff")
                    .addListenerForSingleValueEvent(valueEventListener);

            storiesReference.orderByChild("storyText").addListenerForSingleValueEvent(valueEventListener);
        }
    }






    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    // Handle the search query when the user submits
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    if (newText.isEmpty()) {
                        // If the search query is empty, load all stories
                        loadStoriesFromFirebase();
                    } else {
                        // If the search query is not empty, load filtered stories
                        loadStoriesFromFirebaseWithQuery(newText);
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        });
    }
}