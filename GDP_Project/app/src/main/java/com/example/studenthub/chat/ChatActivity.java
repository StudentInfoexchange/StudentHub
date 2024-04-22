package com.example.studenthub.chat;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studenthub.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView chatUserName;
    private EditText messageEditText;
    private Button sendButton;
    CollectionReference messagesRef;
    private ChatAdapter adapter;
    private List<ChatMessage> messages;
    private ImageView ivBack;
    private Group groupInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (getIntent() != null && getIntent().hasExtra("groupInfo")) {
            groupInfo = (Group) getIntent().getParcelableExtra("groupInfo");
        }

        messagesRef = FirebaseFirestore.getInstance().collection("messages").document(groupInfo.getId()).collection("data");

        recyclerView = findViewById(R.id.recyclerView);
        messageEditText = findViewById(R.id.etMsg);
        sendButton = findViewById(R.id.btSend);
        ivBack = findViewById(R.id.ivBack);
        chatUserName = findViewById(R.id.tvUserName);

        if (TextUtils.isEmpty(groupInfo.getRegarding())) {
            chatUserName.setVisibility(View.GONE);
        }

        chatUserName.setText(groupInfo.getRegarding());
        ivBack.setOnClickListener(v -> finish());

        messages = new ArrayList<>();
        adapter = new ChatAdapter(messages);
        recyclerView.setAdapter(adapter);

        messagesRef.orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.w("", "Listen failed.", e);
                        return;
                    }
                    messages.clear();
                    if (snapshot != null) {
                        for (DocumentSnapshot document : snapshot.getDocuments()) {
                            ChatMessage message = document.toObject(ChatMessage.class);
                            messages.add(message);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });

        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String message = messageEditText.getText().toString().trim();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        if (!TextUtils.isEmpty(message)) {

            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            DocumentReference groupRef = FirebaseFirestore.getInstance().collection("group").document(groupInfo.getId());
            WriteBatch batch = FirebaseFirestore.getInstance().batch();
            Map<String, Object> groupData = new HashMap<>();
            groupData.put("updatedAt", new Date());
            groupData.put("recentMsg", message);
            DocumentReference msgRef = messagesRef.document();
            ChatMessage chatMessage = new ChatMessage(message, currentUserId, System.currentTimeMillis(), email);
            batch.set(msgRef, chatMessage);
            batch.update(groupRef, groupData);
            batch.commit();
            messageEditText.setText("");
        }
    }

}
