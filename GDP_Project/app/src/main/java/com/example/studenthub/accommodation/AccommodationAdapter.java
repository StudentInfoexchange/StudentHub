package com.example.studenthub.accommodation;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.studenthub.R;

import java.util.List;


public class AccommodationAdapter extends RecyclerView.Adapter<AccommodationAdapter.AccommodationViewHolder> {

    private OnItemClickListener onItemClickListener;

    private final List<Accommodation> dataList;
    private final Context context;

    public AccommodationAdapter(Context context, List<Accommodation> list) {
        this.context = context;
        this.dataList = list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public AccommodationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_accommodation, parent, false);
        return new AccommodationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AccommodationViewHolder holder, int position) {
        Accommodation currentItem = dataList.get(position);
        holder.tvDescription.setText(currentItem.getDescription());
        holder.tvName.setText(currentItem.getName());
        holder.tvPrice.setText("$" + currentItem.getPrice());
        holder.tvLocation.setText(currentItem.getLocation());

        Glide.with(context).load(currentItem.getImageUrl()).into(holder.iv);

        String buttonText;
        if (currentItem.isAvailable()) {
            buttonText = "Reserve";
        } else {
            buttonText = "Not Available";
        }
        holder.btReserve.setText(buttonText);
        holder.btReserve.setOnClickListener(view -> {
            if (!currentItem.isAvailable()) {
                Toast.makeText(context, "Not available", Toast.LENGTH_SHORT).show();
                return;
            }
            onItemClickListener.onItemClick(currentItem);
        });
    }

}
