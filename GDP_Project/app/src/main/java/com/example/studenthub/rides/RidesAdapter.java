package com.example.studenthub.rides;


import static com.example.studenthub.utils.Utils.dateFormat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studenthub.R;

import java.util.List;


public class RidesAdapter extends RecyclerView.Adapter<RidesAdapter.RidesViewHolder> {

    private OnItemClickListener onItemClickListener;

    private final List<Ride> dataList;
    private final Context context;

    public RidesAdapter(Context context, List<Ride> list) {
        this.context = context;
        this.dataList = list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public RidesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_rides, parent, false);
        return new RidesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RidesViewHolder holder, int position) {
        Ride currentItem = dataList.get(position);

        String desc = "Booking: " + (position + 1);
        if (currentItem.getType().equals("postRides")) {
            desc += "\n\nPickup: " + currentItem.getPickupLocation() + "\nPickup Time: " +
                    dateFormat(currentItem.getPickupTime()) + "\n\nDrop: " + currentItem.getDropLocation()
                    + "\nDrop Time: " + dateFormat(currentItem.getDropTime()) + "\n\nPassengers: " + currentItem.getNoOfPassengers();

        } else {
            desc += "\n\nPickup: " + currentItem.getPickupLocation() + "\nDrop: " +
                    currentItem.getDropLocation() + "\nPickup Time: " + dateFormat(currentItem.getPickupTime());
        }

        holder.tvDesc.setText(desc);

        holder.btEdit.setOnClickListener(v -> {
            onItemClickListener.onItemClick(currentItem);
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    static class RidesViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDesc;
        public Button btEdit;

        public RidesViewHolder(View itemView) {
            super(itemView);
            tvDesc = itemView.findViewById(R.id.tv);
            btEdit = itemView.findViewById(R.id.btEdit);
        }
    }
}
