package com.example.studenthub.events;


import static com.example.studenthub.utils.Utils.dateFormat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.studenthub.R;

import java.util.List;


public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventsViewHolder> {

    private OnItemClickListener onItemClickListener;

    private final List<Event> dataList;
    private final Context context;

    public EventsAdapter(Context context, List<Event> list) {
        this.context = context;
        this.dataList = list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public EventsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_event, parent, false);
        return new EventsViewHolder(view);
    }

    @Override
   public void onBindViewHolder(EventsViewHolder holder, int position) {
    // Retrieve the current item from the data list
    Event currentItem = dataList.get(position);

    // Set the description
    String description = currentItem.getDescription();
    holder.tvDescription.setText(description);

    // Set the title
    String title = currentItem.getTitle();
    holder.tvName.setText(title);

    // Set the formatted date
    String formattedDate = dateFormat(currentItem.getDate());
    holder.tvDate.setText(formattedDate);

    // Set the location
    String location = currentItem.getLocation();
    holder.tvLocation.setText(location);

    // Load image using Glide
    String imageUrl = currentItem.getImageUrl();
    Glide.with(context)
         .load(imageUrl)
         .into(holder.iv);

    // Set click listener for RSVP button
    holder.btRSVP.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Invoke onItemClick method of the OnItemClickListener interface
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(currentItem);
            }
        }
    });
}


    @Override
    public int getItemCount() {
        return dataList.size();
    }


    static class EventsViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvDescription, tvDate, tvLocation;
        public ImageView iv;
        public Button btRSVP;

        public EventsViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvDate);
            iv = itemView.findViewById(R.id.iv);
            btRSVP = itemView.findViewById(R.id.btRSVP);
            tvLocation = itemView.findViewById(R.id.tvLocation);
        }
    }
}
