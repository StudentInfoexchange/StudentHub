package com.example.studenthub.chat;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studenthub.R;
import com.example.studenthub.chat.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<ChatMessage> messages;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.bind(message, holder.cv.getContext());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMsg, tvTime;
        private CardView cv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMsg = itemView.findViewById(R.id.tvMsg);
            tvTime = itemView.findViewById(R.id.tvTime);
            cv = itemView.findViewById(R.id.cvChat);
        }

        public void bind(ChatMessage message, Context ct) {
            tvMsg.setText(message.getMessage());
            String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
            boolean sentByCurrentUser = TextUtils.equals(message.getSenderId(), currentUser);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            if (sentByCurrentUser) {
                layoutParams.gravity = Gravity.END;
                layoutParams.setMarginStart(100);
                cv.setCardBackgroundColor(Color.parseColor("#FF3644BA"));
            } else {
                layoutParams.gravity = Gravity.START;
                layoutParams.setMarginEnd(100);
                cv.setCardBackgroundColor(Color.parseColor("#FFEF5220"));
            }
            cv.setLayoutParams(layoutParams);
            tvTime.setLayoutParams(layoutParams);
            tvTime.setText(DateUtils.getRelativeTimeSpanString(ct, message.getTimestamp()));
        }
    }
}
