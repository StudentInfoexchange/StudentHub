package com.example.studenthub.chat;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studenthub.R;

import java.util.List;

public class ChatOverViewAdapter extends RecyclerView.Adapter<ChatOverViewAdapter.ViewHolder> {
    private List<ChatOverview> messages;

    public ChatOverViewAdapter(List<ChatOverview> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_overview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatOverview message = messages.get(position);
        holder.bind(message, holder.cv.getContext());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMsg, tvTime, tvTitle, tvIcon;
        private CardView cv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMsg = itemView.findViewById(R.id.tvMsg);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvIcon = itemView.findViewById(R.id.tvIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            cv = itemView.findViewById(R.id.cvChat);
        }

        public void bind(ChatOverview message, Context ct) {
            tvMsg.setText(message.getMsg());
            tvMsg.setText(message.getMsg());
            tvTitle.setText("Chat");
            try {
                tvIcon.setText(String.format("%s", message.getMsg().charAt(0)).toUpperCase());
            } catch (Exception e) {

            }
            tvTime.setText(DateUtils.getRelativeTimeSpanString(ct, message.getUpdatedAt().getTime()));
            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ct, ChatActivity.class);
                    intent.putExtra("groupInfo", new Group(null, message.getGroupId(), null, ""));
                    ct.startActivity(intent);
                }
            });
        }
    }
}
