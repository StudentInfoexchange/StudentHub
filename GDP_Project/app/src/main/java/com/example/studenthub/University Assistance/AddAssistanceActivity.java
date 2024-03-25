package com.example.studenthub.assistance;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studenthub.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddAssistanceActivity extends AppCompatActivity {

    private EditText descEditText;
    private Spinner categorySpinner;
    private Button btAdd;
    private ProgressDialog progressDialog;
    private ImageView ivBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assistance);

        progressDialog = new ProgressDialog(this);
        // Initialize views
        ivBack = findViewById(R.id.ivBack);
        descEditText = findViewById(R.id.tvDesc);
        btAdd = findViewById(R.id.btAdd);
        categorySpinner = findViewById(R.id.categorySpinner);
        ivBack.setOnClickListener(v -> finish());

        ArrayAdapter<String> categoryAdapter = getStringArrayAdapter();
        categorySpinner.setAdapter(categoryAdapter);

        btAdd.setOnClickListener(v -> addAssistance());

    }

    @NonNull
    private ArrayAdapter<String> getStringArrayAdapter() {
        List<String> categories = new ArrayList<>();
        categories.add("Transcript preparation");
        categories.add("Diploma and degree certificate processing");
        categories.add("Letter of enrollment");
        categories.add("Academic recommendation letter writing");
        categories.add("Certification application assistance");
        categories.add("Verification document preparation");
        categories.add("Internship completion certificates");
        categories.add("Research project documentation");

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return categoryAdapter;
    }

    private void addAssistance() {
        // Retrieve data from input fields
        String desc = descEditText.getText().toString().trim();
        String category = (String) categorySpinner.getSelectedItem();

        // Validate inputs
        if (desc.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String contact = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String studentId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference ref = FirebaseFirestore.getInstance().collection("assistance").document();
        Map<String, Object> data = new HashMap<>();
        data.put("byUid", studentId);
        data.put("id", ref.getId());
        data.put("description", desc);
        data.put("contact", contact);
        data.put("date", new Date());
        data.put("category", category);

        progressDialog.show();

        ref.set(data).addOnCompleteListener(AddAssistanceActivity.this, task2 -> {
            progressDialog.hide();
            if (task2.isSuccessful()) {
                Toast.makeText(AddAssistanceActivity.this, "Successfully Added", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(AddAssistanceActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
