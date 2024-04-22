package com.example.studenthub.events;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.studenthub.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventsActivity extends AppCompatActivity implements OnItemClickListener {

    ImageView ivBack, ivAddEvent;
    RecyclerView rv;
    ProgressDialog progressDialog;
    EventsAdapter adapter;
    SwipeRefreshLayout srl;
    AlertDialog alert = null;

    ArrayList<Event> eventList;
    ArrayList<Event> filteredEventList;
    EditText etSearch;
    boolean isShowingHistory = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        if (getIntent() != null) {
            isShowingHistory = getIntent().getBooleanExtra("isShowingHistory", false);
        }

        progressDialog = new ProgressDialog(this);

        srl = findViewById(R.id.srl);
        ivBack = findViewById(R.id.ivBack);
        rv = findViewById(R.id.rvEvents);
        etSearch = findViewById(R.id.etSearch);
        ivAddEvent = findViewById(R.id.ivAddEvent);

        eventList = new ArrayList<>();
        filteredEventList = new ArrayList<>();


        ivBack.setOnClickListener(v -> finish());
        ivAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EventsActivity.this, AddEventActivity.class));
            }
        });

        loadData();

        srl.setOnRefreshListener(() -> {
            loadData();
            new Handler().postDelayed(() -> {
                srl.setRefreshing(false);
            }, 1000);
        });

        searchFunction();

    }

    void loadData() {
        progressDialog.show();

        Query ref;

        if (!isShowingHistory) {
            ref = FirebaseFirestore.getInstance().collection("events");
        } else {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            ref = FirebaseFirestore.getInstance().collection("events")
                    .whereEqualTo("ownerId", uid);
        }

        ref.get().addOnCompleteListener(task -> {
            progressDialog.hide();
            if (task.isSuccessful()) {
                List<Event> list = task.getResult().toObjects(Event.class);
                eventList.clear();
                eventList.addAll(list);
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
    public void onItemClick(Event model, boolean isAddedByYou) {
        if (!isAddedByYou) {
            rsvp(model);
        } else {
            editEvent(model);
        }
    }

    private void editEvent(Event model) {
        Intent i = new Intent(EventsActivity.this, AddEventActivity.class);
        i.putExtra("Event", model);
        startActivity(i);
    }

    private void rsvp(Event model) {
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

    private void searchFunction() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchText = s.toString().toLowerCase().trim();
                if (searchText.isEmpty()) {
                    setList(eventList);
                } else if (searchText.length() > 2) {
                    filteredEventList.clear();
                    for (Event event : eventList) {
                        if (event.getTitle().toLowerCase().contains(searchText) ||
                                event.getDescription().toLowerCase().contains(searchText) ||
                                event.getLocation().toLowerCase().contains(searchText)) {
                            filteredEventList.add(event);
                        }
                    }
                    setList(filteredEventList);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}