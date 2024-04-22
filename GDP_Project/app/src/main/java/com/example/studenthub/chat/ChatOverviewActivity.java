package com.example.studenthub.chat;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

public class ChatOverviewActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageView ivBack;

    private List<ChatOverview> overviewMsg;
    private ChatOverViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_overview);

        recyclerView = findViewById(R.id.rv);
        overviewMsg = new ArrayList<>();
        adapter = new ChatOverViewAdapter(overviewMsg);
        recyclerView.setAdapter(adapter);

        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());


    }

    void setData() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("group")
                .whereArrayContains("members", uid)
                .orderBy("updatedAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.w("", "Listen failed.", e);
                        return;
                    }
                    overviewMsg.clear();
                    if (snapshot != null) {
                        for (DocumentSnapshot document : snapshot.getDocuments()) {
                            ChatOverview message = new ChatOverview(document.getString("id"), document.getString("recentMsg"), document.getString("regarding"), document.getDate("updatedAt"));
                            overviewMsg.add(message);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setData();
    }
}
