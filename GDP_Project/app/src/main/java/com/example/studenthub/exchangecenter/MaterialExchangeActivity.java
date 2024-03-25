package com.example.studenthub.material_exchange;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studenthub.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MaterialExchangeActivity extends AppCompatActivity implements OnItemClickListener {

    ImageView ivBack, ivAdd, ivRefresh;
    RecyclerView rv;
    ProgressDialog progressDialog;
    MaterialListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_exchange);

        progressDialog = new ProgressDialog(this);

        ivBack = findViewById(R.id.ivBack);
        ivAdd = findViewById(R.id.ivAdd);
        ivRefresh = findViewById(R.id.ivRefresh);
        rv = findViewById(R.id.rv);

        ivBack.setOnClickListener(v -> finish());
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MaterialExchangeActivity.this, AddMaterialActivity.class));
            }
        });

        ivRefresh.setOnClickListener(v -> loadData());

    }

    void loadData() {
        progressDialog.show();
        FirebaseFirestore.getInstance().collection("MaterialExchange").get().addOnCompleteListener(task -> {
            progressDialog.hide();
            if (task.isSuccessful()) {
                List<Material> list = task.getResult().toObjects(Material.class);
                setList(list);
            } else {
                Toast.makeText(MaterialExchangeActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void setList(List<Material> list) {

        HashMap<String, List<Material>> groupedMaterials = new HashMap<>();

        for (Material material : list) {
            String type = material.getType();
            if (!groupedMaterials.containsKey(type)) {
                List<Material> filteredList = list.stream().filter(m -> m.getType().equals(type)).collect(Collectors.toList());
                groupedMaterials.put(type, filteredList);
            }
        }

        adapter = new MaterialListAdapter(MaterialExchangeActivity.this, groupedMaterials);
        rv.setAdapter(adapter);
    }

    @Override
    public void onItemClick(Material model) {
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
                        Toast.makeText(MaterialExchangeActivity.this, "Done. See you there!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MaterialExchangeActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}