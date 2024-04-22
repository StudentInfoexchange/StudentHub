package com.example.studenthub.material_exchange;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studenthub.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

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
    EditText etSearch;
    ArrayList<Material> materialList;
    ArrayList<Material> filteredMaterialList;

    boolean isShowingHistory = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_exchange);

        if (getIntent() != null) {
            isShowingHistory = getIntent().getBooleanExtra("isShowingHistory", false);
        }

        progressDialog = new ProgressDialog(this);

        ivBack = findViewById(R.id.ivBack);
        ivAdd = findViewById(R.id.ivAdd);
        ivRefresh = findViewById(R.id.ivRefresh);
        rv = findViewById(R.id.rv);
        etSearch = findViewById(R.id.etSearch);

        materialList = new ArrayList<>();
        filteredMaterialList = new ArrayList<>();

        ivBack.setOnClickListener(v -> finish());
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MaterialExchangeActivity.this, AddMaterialActivity.class));
            }
        });

        ivRefresh.setOnClickListener(v -> loadData());

        searchFunction();

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
                    setList(materialList);
                } else if (searchText.length() > 2) {
                    filteredMaterialList.clear();
                    for (Material material : materialList) {
                        if (material.getTitle().toLowerCase().contains(searchText) ||
                                material.getType().toLowerCase().contains(searchText) ||
                                material.getSubCategory().toLowerCase().contains(searchText)) {
                            filteredMaterialList.add(material);
                        }
                    }
                    setList(filteredMaterialList);
                }
            }
        });
    }

    void loadData() {
        progressDialog.show();
        Query ref;
        if (!isShowingHistory) {
            ref = FirebaseFirestore.getInstance().collection("MaterialExchange");
        } else {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            ref = FirebaseFirestore.getInstance().collection("MaterialExchange")
                    .whereEqualTo("studentId", uid);
        }
        ref.get().addOnCompleteListener(task -> {
            progressDialog.hide();
            if (task.isSuccessful()) {

                List<Material> list = task.getResult().toObjects(Material.class);
                materialList.clear();
                materialList.addAll(list);
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