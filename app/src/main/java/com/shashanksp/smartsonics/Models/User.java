package com.shashanksp.smartsonics.Models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public String email;
    public boolean isGuide;
    public String username;
    public String guideId;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, boolean isGuide, String username, String guideId) {
        this.email = email;
        this.isGuide = isGuide;
        this.username = username;
        this.guideId = guideId;
    }
}
