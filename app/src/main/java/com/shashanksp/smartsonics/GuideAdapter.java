package com.shashanksp.smartsonics;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GuideAdapter extends RecyclerView.Adapter<GuideAdapter.GuideViewHolder> {

    private ArrayList<String> guideIds;
    private OnGuideClickListener onGuideClickListener; // Interface for click events
    private String artId;
    public GuideAdapter(ArrayList<String> guideIds, OnGuideClickListener onGuideClickListener, String artId) {
        this.guideIds = guideIds;
        this.onGuideClickListener = onGuideClickListener;
        this.artId = artId; // Assign the artId
    }

    @NonNull
    @Override
    public GuideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_guide, parent, false);
        return new GuideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GuideViewHolder holder, int position) {
        String guideId = guideIds.get(position);
        holder.bind(guideId);
    }

    @Override
    public int getItemCount() {
        return guideIds.size();
    }

    public class GuideViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView guideIdTextView;

        public GuideViewHolder(@NonNull View itemView) {
            super(itemView);
            guideIdTextView = itemView.findViewById(R.id.guideIdTextView);

            // Set the click listener
            itemView.setOnClickListener(this);
        }

        public void bind(String guideId) {
            guideIdTextView.setText(guideId);
        }

        @Override
        public void onClick(View v) {
            // Handle item click
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                String selectedGuideId = guideIds.get(position);

                // Notify the activity or fragment about the click event
                if (onGuideClickListener != null) {
                    onGuideClickListener.onGuideClick(artId,selectedGuideId);
                }
            }
        }
    }

    // Interface to communicate click events to the activity or fragment
    public interface OnGuideClickListener {
        void onGuideClick(String artId,String guideId);
    }
}
