package com.example.studenthub.material_exchange;

import static com.google.common.io.Files.getFileExtension;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.studenthub.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddMaterialActivity extends AppCompatActivity {

    private EditText titleEditText, priceEditText;
    private Spinner categorySpinner, subcategorySpinner;
    private Button addButton, selectImageButton;
    private ImageView selectedImageView, ivBack;
    private Uri imageUri;
    private ProgressDialog progressDialog;
    private TextView tvTitle, tvDelete;
    private boolean isEditMode = false;
    private Material material;
    private List<String> subcategories;
    private List<String> categories;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_material);

        if (getIntent() != null) {
            material = getIntent().getParcelableExtra("Material");
            if (material != null) {
                isEditMode = true;
            }
        }

        progressDialog = new ProgressDialog(this);
        // Initialize views
        ivBack = findViewById(R.id.ivBack);
        titleEditText = findViewById(R.id.titleEditText);
        priceEditText = findViewById(R.id.priceEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        subcategorySpinner = findViewById(R.id.subcategorySpinner);
        addButton = findViewById(R.id.addButton);
        selectImageButton = findViewById(R.id.selectImageButton);
        selectedImageView = findViewById(R.id.iv);

        ivBack.setOnClickListener(v -> finish());


        categories = new ArrayList<>();
        categories.add("Books");
        categories.add("Furniture");

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedCategory = (String) parentView.getItemAtPosition(position);
                subcategories = new ArrayList<>();
                if (selectedCategory.equals("Books")) {
                    subcategories.add("IS");
                    subcategories.add("ACS");
                } else if (selectedCategory.equals("Furniture")) {
                    subcategories.add("Sofa");
                    subcategories.add("Table");
                }
                ArrayAdapter<String> subcategoryAdapter = new ArrayAdapter<>(AddMaterialActivity.this, android.R.layout.simple_spinner_item, subcategories);
                subcategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                subcategorySpinner.setAdapter(subcategoryAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        addButton.setOnClickListener(v -> {
            if (!isEditMode) {
                addMaterial();
            } else {
                updateMaterial();
            }
        });

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        preFillData();
    }

    private void updateMaterial() {

        String title = titleEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        String category = (String) categorySpinner.getSelectedItem();
        String subcategory = (String) subcategorySpinner.getSelectedItem();

        // Validate inputs
        if (title.isEmpty() || priceString.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceString);

        DocumentReference ref = FirebaseFirestore.getInstance().collection("MaterialExchange").document(material.getId());
        Map<String, Object> data = new HashMap<>();
        data.put("price", price);
        data.put("title", title);
        data.put("date", new Date());
        data.put("type", category);
        data.put("subCategory", subcategory);

        progressDialog.show();

        if (imageUri != null) {
            StorageReference fileReference = FirebaseStorage.getInstance().getReference("material_images").child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri.toString()));
            fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {

                fileReference.getDownloadUrl().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String imageUrl = task.getResult().toString();
                        data.put("imageUrl", imageUrl);
                        ref.update(data).addOnCompleteListener(AddMaterialActivity.this, task2 -> {
                            progressDialog.hide();
                            if (task2.isSuccessful()) {
                                Toast.makeText(AddMaterialActivity.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(AddMaterialActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        progressDialog.hide();
                        Toast.makeText(AddMaterialActivity.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                    }
                });

            }).addOnFailureListener(e -> {
                progressDialog.hide();
                Toast.makeText(AddMaterialActivity.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
            });
        } else {
            ref.update(data).addOnCompleteListener(AddMaterialActivity.this, task2 -> {
                progressDialog.hide();
                if (task2.isSuccessful()) {
                    Toast.makeText(AddMaterialActivity.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddMaterialActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void preFillData() {
        if (material == null) {
            return;
        }
        titleEditText.setText(material.getTitle());
        priceEditText.setText("" + material.getPrice());
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).equals(material.getType())) {
                categorySpinner.setSelection(i);
                break;
            }
        }

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            for (int j = 0; j < subcategories.size(); j++) {
                if (subcategories.get(j).equals(material.getSubCategory())) {
                    subcategorySpinner.setSelection(j);
                    break;
                }
            }
        }, 200);

        Glide.with(this).load(material.getImageUrl()).into(selectedImageView);
        selectImageButton.setText(R.string.update_image);
        addButton.setText(R.string.update);
        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.update);
        tvDelete = findViewById(R.id.tvDelete);
        tvDelete.setVisibility(View.VISIBLE);
        tvDelete.setOnClickListener(v -> deleteMaterial());
    }

    private void deleteMaterial() {
        progressDialog.show();
        DocumentReference ref = FirebaseFirestore.getInstance().collection("MaterialExchange").document(material.getId());
        ref.delete().addOnCompleteListener(AddMaterialActivity.this, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(AddMaterialActivity.this, "Successfully Deleted", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(AddMaterialActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMaterial() {
        // Retrieve data from input fields
        String title = titleEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        String category = (String) categorySpinner.getSelectedItem();
        String subcategory = (String) subcategorySpinner.getSelectedItem();

        // Validate inputs
        if (title.isEmpty() || priceString.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri == null) {
            Toast.makeText(this, "Please select Image", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceString);
        String contact = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String studentId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference ref = FirebaseFirestore.getInstance().collection("MaterialExchange").document();
        Map<String, Object> data = new HashMap<>();
        data.put("id", ref.getId());
        data.put("price", price);
        data.put("title", title);
        data.put("contact", contact);
        data.put("date", new Date());
        data.put("type", category);
        data.put("subCategory", subcategory);
        data.put("studentId", studentId);

        progressDialog.show();

        StorageReference fileReference = FirebaseStorage.getInstance().getReference("material_images").child(System.currentTimeMillis()
                + "." + getFileExtension(imageUri.toString()));
        fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {

            fileReference.getDownloadUrl().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String imageUrl = task.getResult().toString();
                    data.put("imageUrl", imageUrl);
                    ref.set(data).addOnCompleteListener(AddMaterialActivity.this, task2 -> {
                        progressDialog.hide();
                        if (task2.isSuccessful()) {
                            Toast.makeText(AddMaterialActivity.this, "Successfully Added", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddMaterialActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    progressDialog.hide();
                    Toast.makeText(AddMaterialActivity.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                }
            });

        }).addOnFailureListener(e -> {
            progressDialog.hide();
            Toast.makeText(AddMaterialActivity.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            selectedImageView.setImageURI(imageUri);
        }
    }
}
