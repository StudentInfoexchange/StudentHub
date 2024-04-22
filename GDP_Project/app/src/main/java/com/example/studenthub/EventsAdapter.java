package com.example.studenthub.events;


import static com.example.studenthub.utils.Utils.dateFormat;

import android.content.Context;
import android.text.TextUtils;
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
import com.google.firebase.auth.FirebaseAuth;

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
        Event currentItem = dataList.get(position);
        holder.tvDescription.setText(currentItem.getDescription());
        holder.tvName.setText(currentItem.getTitle());
        String date = dateFormat(currentItem.getDate());
        holder.tvDate.setText("" + date);
        holder.tvLocation.setText(currentItem.getLocation());

        Glide.with(context).load(currentItem.getImageUrl()).into(holder.iv);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        boolean isAddedByYou = TextUtils.equals(currentItem.getOwnerId(), uid);
        String btRSVPText;
        if (isAddedByYou) {
            btRSVPText = "Edit";
        } else {
            btRSVPText = "RSVP";
        }

        holder.btRSVP.setText(btRSVPText);

        holder.btRSVP.setOnClickListener(v -> {
            onItemClickListener.onItemClick(currentItem, isAddedByYou);
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
