package com.example.studenthub.events;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.studenthub.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventsActivity extends AppCompatActivity implements OnItemClickListener {

    ImageView ivBack;
    RecyclerView rv;
    ProgressDialog progressDialog;
    EventsAdapter adapter;
    SwipeRefreshLayout srl;
    AlertDialog alert = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        progressDialog = new ProgressDialog(this);

        srl = findViewById(R.id.srl);
        ivBack = findViewById(R.id.ivBack);
        rv = findViewById(R.id.rvEvents);

        ivBack.setOnClickListener(v -> finish());

        loadData();

        srl.setOnRefreshListener(() -> {
            loadData();
            new Handler().postDelayed(() -> {
                srl.setRefreshing(false);
            }, 1000);
        });

    }

    void loadData() {
        progressDialog.show();
        FirebaseFirestore.getInstance().collection("events").get().addOnCompleteListener(task -> {
            progressDialog.hide();
            if (task.isSuccessful()) {
                List<Event> list = task.getResult().toObjects(Event.class);
                setList(list);
            } else {
                Toast.makeText(EventsActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void setList(List<Event> list) {
        adapter = new EventsAdapter(EventsActivity.this, list);
        adapter.setOnItemClickListener(this);
        rv.setAdapter(adapter);
    }

    @Override
    public void onItemClick(Event model) {
        progressDialog.show();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("userEmail", userEmail);
        FirebaseFirestore.getInstance().collection("events").document(model.getId()).collection("rsvp")
                .document(userId).set(data).addOnCompleteListener(task -> {
                    progressDialog.hide();
                    if (task.isSuccessful()) {
                        Toast.makeText(EventsActivity.this, "Done. See you there!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EventsActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}