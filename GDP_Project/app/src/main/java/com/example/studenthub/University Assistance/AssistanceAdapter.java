package com.example.studenthub.assistance;


import static com.example.studenthub.utils.Utils.dateFormat;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studenthub.R;
import com.example.studenthub.events.OnItemClickListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;


public class AssistanceAdapter extends RecyclerView.Adapter<AssistanceAdapter.AssistanceViewHolder> {

    private OnItemClickListener onItemClickListener;

    private final List<Assistance> dataList;
    private final Context context;

    public AssistanceAdapter(Context context, List<Assistance> list) {
        this.context = context;
        this.dataList = list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public AssistanceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_assistance, parent, false);
        return new AssistanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AssistanceViewHolder holder, int position) {
        Assistance currentItem = dataList.get(position);
        holder.tvDescription.setText(currentItem.getDescription());
        holder.tvCategory.setText(currentItem.getCategory());
        holder.tvContact.setText("Contact: " + currentItem.getContact());
        String date = dateFormat(currentItem.getDate());
        holder.tvDate.setText(DateUtils.getRelativeTimeSpanString(context, currentItem.getDate().getTime()));
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        boolean isAddedByYou = TextUtils.equals(currentItem.getByUid(), uid);
        if (isAddedByYou) {
            holder.btEdit.setVisibility(View.VISIBLE);
        }
        holder.btEdit.setOnClickListener(v -> {
            Context ct = holder.tvCategory.getContext();
            Intent i = new Intent(ct, AddAssistanceActivity.class);
            i.putExtra("Assistance", currentItem);
            ct.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class AssistanceViewHolder extends RecyclerView.ViewHolder {
        public TextView tvCategory, tvDescription, tvDate, tvContact;
        public Button btEdit;

        public AssistanceViewHolder(View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDescription = itemView.findViewById(R.id.tvDesc);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvContact = itemView.findViewById(R.id.tvContact);
            btEdit = itemView.findViewById(R.id.btEdit);
        }
    }
}
