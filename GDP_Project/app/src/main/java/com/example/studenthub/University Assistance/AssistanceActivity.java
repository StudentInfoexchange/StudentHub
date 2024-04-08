package com.example.studenthub.assistance;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
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

public class AssistanceActivity extends AppCompatActivity {

    ImageView ivBack, ivAdd;
    RecyclerView rv;
    ProgressDialog progressDialog;
    AssistanceAdapter adapter;
    SwipeRefreshLayout srl;
    AlertDialog alert = null;
    ArrayList<Assistance> assistanceList;

    ArrayList<Assistance> filteredAssistanceList;

    boolean isShowingHistory = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistance);

        if (getIntent() != null) {
            isShowingHistory = getIntent().getBooleanExtra("isShowingHistory", false);
        }

        progressDialog = new ProgressDialog(this);

        srl = findViewById(R.id.srl);
        ivBack = findViewById(R.id.ivBack);
        rv = findViewById(R.id.rv);

        ivAdd = findViewById(R.id.ivAdd);

        assistanceList = new ArrayList<>();
        filteredAssistanceList = new ArrayList<>();

        ivBack.setOnClickListener(v -> finish());
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AssistanceActivity.this, AddAssistanceActivity.class));
            }
        });

        loadData();

        srl.setOnRefreshListener(() -> {
            loadData();
            new Handler().postDelayed(() -> {
                srl.setRefreshing(false);
            }, 1000);
        });

        // searchFunction();

    }

    void loadData() {
        progressDialog.show();
        Query ref;
        if (!isShowingHistory) {
            ref = FirebaseFirestore.getInstance().collection("assistance");
        } else {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            ref = FirebaseFirestore.getInstance().collection("assistance")
                    .whereEqualTo("byUid", uid);
        }

        ref.get().addOnCompleteListener(task -> {
            progressDialog.hide();
            if (task.isSuccessful()) {
                List<Assistance> list = task.getResult().toObjects(Assistance.class);
                assistanceList.clear();
                assistanceList.addAll(list);
                setList(list);
            } else {
                Toast.makeText(AssistanceActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void setList(List<Assistance> list) {
        adapter = new AssistanceAdapter(AssistanceActivity.this, list);
        rv.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}