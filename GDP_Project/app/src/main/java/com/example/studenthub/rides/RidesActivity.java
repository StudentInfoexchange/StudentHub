package com.example.studenthub.rides;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class RidesActivity extends AppCompatActivity implements OnItemClickListener {

    ImageView ivBack;
    RecyclerView rv;
    ProgressDialog progressDialog;
    RidesAdapter adapter;
    SwipeRefreshLayout srl;
    ArrayList<Ride> ridesList;
    ArrayList<Ride> filteredRideList;
    String type = "bookedRides";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rides);

        if (getIntent() != null) {
            type = getIntent().getStringExtra("type");
        }

        progressDialog = new ProgressDialog(this);

        srl = findViewById(R.id.srl);
        ivBack = findViewById(R.id.ivBack);
        rv = findViewById(R.id.rvRides);

        ridesList = new ArrayList<>();
        filteredRideList = new ArrayList<>();

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

        Query ref;
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref = FirebaseFirestore.getInstance().collection(type)
                .orderBy("lastModified", Query.Direction.DESCENDING)
                .whereEqualTo("ownerId", uid);

        ref.get().addOnCompleteListener(task -> {
            progressDialog.hide();
            if (task.isSuccessful()) {
                List<Ride> list = task.getResult().toObjects(Ride.class);
                ridesList.clear();
                ridesList.addAll(list);
                setList(list);
            } else {
                task.getException().printStackTrace();
                Toast.makeText(RidesActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void setList(List<Ride> list) {
        adapter = new RidesAdapter(RidesActivity.this, list);
        adapter.setOnItemClickListener(this);
        rv.setAdapter(adapter);
    }

    @Override
    public void onItemClick(Ride model) {
        editEvent(model);
    }

    private void editEvent(Ride model) {
        if (model.getType().equals("bookedRides")) {
            Intent i = new Intent(RidesActivity.this, BookRideActivity.class);
            i.putExtra("Ride", model);
            startActivity(i);
        } else {
            Intent i = new Intent(RidesActivity.this, PostRideActivity.class);
            i.putExtra("Ride", model);
            startActivity(i);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}