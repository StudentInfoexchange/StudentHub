package com.example.studenthub.accommodation;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.studenthub.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservedAccommodationActivity extends AppCompatActivity implements OnItemClickListener {

    ImageView ivBack;
    RecyclerView rv;
    ProgressDialog progressDialog;
    AccommodationAdapter adapter;
    ArrayList<Accommodation> accommodationList;
    SwipeRefreshLayout srl;
    AlertDialog alert = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserved_accommodation);

        progressDialog = new ProgressDialog(this);
        accommodationList = new ArrayList<>();

        ivBack = findViewById(R.id.ivBack);
        srl = findViewById(R.id.srl);
        rv = findViewById(R.id.rvAccommodation);
        loadData();
        ivBack.setOnClickListener(v -> onBackPressed());

        srl.setOnRefreshListener(() -> {
            loadData();
            new Handler().postDelayed(() -> {
                srl.setRefreshing(false);
            }, 1000);
        });
    }

    void loadData() {
        progressDialog.show();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference ref = FirebaseFirestore.getInstance().collection("users")
                .document(uid).collection("accommodations");

        ref.get().addOnCompleteListener(task -> {
            progressDialog.hide();
            if (task.isSuccessful()) {
                List<Accommodation> list = task.getResult().toObjects(Accommodation.class);
                Log.d("Accommodation LIST", list.toString());
                accommodationList.clear();
                accommodationList.addAll(list);
                setList(list);

            } else {
                Toast.makeText(ReservedAccommodationActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void setList(List<Accommodation> list) {
        adapter = new AccommodationAdapter(ReservedAccommodationActivity.this, list, true);
        adapter.setOnItemClickListener(this);
        rv.setAdapter(adapter);
    }

    @Override
    public void onItemClick(Accommodation model, boolean isAddedByYou) {
        releaseAccommodation(model);
    }

    private void releaseAccommodation(Accommodation model) {
        progressDialog.show();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        WriteBatch batch = FirebaseFirestore.getInstance().batch();
        DocumentReference accommodationsDoc = FirebaseFirestore.getInstance()
                .collection("accommodations").document(model.getId());
        DocumentReference userDocAccommodations = FirebaseFirestore.getInstance()
                .collection("users").document(uid).collection("accommodations").document(model.getId());

        Map<String, Object> data = new HashMap<>();
        data.put("available", true);

        batch.update(accommodationsDoc, data);
        batch.delete(userDocAccommodations);

        batch.commit().addOnCompleteListener(task -> {
            progressDialog.hide();
            if (task.isSuccessful()) {
                Toast.makeText(ReservedAccommodationActivity.this, "Successfully Released.", Toast.LENGTH_SHORT).show();
                loadData();
            } else {
                Toast.makeText(ReservedAccommodationActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

}