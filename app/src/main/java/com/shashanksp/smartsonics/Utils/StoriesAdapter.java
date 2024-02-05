package com.shashanksp.smartsonics.Utils;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shashanksp.smartsonics.Models.Story;
import com.shashanksp.smartsonics.R;

import java.util.ArrayList;
import java.util.List;

public class StoriesAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Story> storiesList;
    public StoriesAdapter(Context context, List<Story> stories) {
        super();
    }



    public StoriesAdapter(Context context, ArrayList<Story> storiesList) {
        this.context = context;
        this.storiesList = storiesList;
    }

    @Override
    public int getCount() {
        return storiesList.size();
    }

    @Override
    public Object getItem(int position) {
        return storiesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public void add(Story story) {
        storiesList.add(story);
        notifyDataSetChanged();
    }
    public void clear() {
        storiesList.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.stories_item, parent, false);
            holder = new ViewHolder();
            holder.usernameTextView = convertView.findViewById(R.id.usernameTextView);
            holder.artnameTextView = convertView.findViewById(R.id.artnameTextView);
            holder.storyContentTextView = convertView.findViewById(R.id.storyContentTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Get the current story
        Story currentStory = storiesList.get(position);

        // Set data to the views
        holder.usernameTextView.setText(currentStory.getUsername());
        holder.artnameTextView.setText(currentStory.getArtName());
        holder.storyContentTextView.setText(currentStory.getStoryText());

        return convertView;
    }

    // ViewHolder pattern for better performance
    private static class ViewHolder {
        TextView usernameTextView;
        TextView artnameTextView;
        TextView storyContentTextView;
    }
}

