package com.shashanksp.smartsonics.Utils;

public class Story {
    public Story(){}
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getArtName() {
        return artName;
    }

    public void setArtName(String artName) {
        this.artName = artName;
    }

    public String getStoryText() {
        return storyText;
    }

    public void setStoryText(String storyText) {
        this.storyText = storyText;
    }

    public String username;
    public String artName;
    public String storyText;

    public Story(String username, String artName, String storyText) {
        this.username = username;
        this.artName = artName;
        this.storyText = storyText;
    }
}
