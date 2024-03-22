package com.example.studenthub.accommodation;

import static com.google.common.io.Files.getFileExtension;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class AddAccommodationActivity extends AppCompatActivity {

    private EditText titleEditText, priceEditText, descEditText, locationEditText;
    private Spinner roomTypeSpinner;
    private Button addButton, selectImageButton;
    private ImageView selectedImageView, ivBack;
    private Uri imageUri;
    private ProgressDialog progressDialog;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_accommodation);

        progressDialog = new ProgressDialog(this);
        // Initialize views
        ivBack = findViewById(R.id.ivBack);
        titleEditText = findViewById(R.id.titleEditText);
        priceEditText = findViewById(R.id.priceEditText);
        descEditText = findViewById(R.id.descriptionEditText);
        locationEditText = findViewById(R.id.locationEditText);
        roomTypeSpinner = findViewById(R.id.roomTypeSpinner);
        addButton = findViewById(R.id.btAddAcc);
        selectImageButton = findViewById(R.id.selectImageButton);
        selectedImageView = findViewById(R.id.iv);

        ivBack.setOnClickListener(v -> finish());

        List<String> roomTypes = new ArrayList<>();
        roomTypes.add("Standard Room");
        roomTypes.add("Deluxe Room");

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roomTypes);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomTypeSpinner.setAdapter(categoryAdapter);
        addButton.setOnClickListener(v -> addAccommodation());

        selectImageButton.setOnClickListener(v -> openFileChooser());
    }

    private void addAccommodation() {
        // Retrieve data from input fields
        String name = titleEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        String desc = descEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        String selectedRoomType = (String) roomTypeSpinner.getSelectedItem();

        // Validate inputs
        if (name.isEmpty() || priceString.isEmpty() || location.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri == null) {
            Toast.makeText(this, "Please select Image", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceString);
        String ownerContact = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String ownerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference ref = FirebaseFirestore.getInstance().collection("accommodations").document();
        Map<String, Object> data = new HashMap<>();
        data.put("id", ref.getId());
        data.put("price", price);
        data.put("name", name);
        data.put("description", desc);
        data.put("roomType", selectedRoomType);
        data.put("location", location);
        data.put("available", true);
        data.put("ownerContact", ownerContact);
        data.put("ownerId", ownerId);

        progressDialog.show();

        StorageReference fileReference = FirebaseStorage.getInstance().getReference("accommodations").child(System.currentTimeMillis()
                + "." + getFileExtension(imageUri.toString()));
        fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {

            fileReference.getDownloadUrl().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String imageUrl = task.getResult().toString();
                    data.put("imageUrl", imageUrl);
                    ref.set(data).addOnCompleteListener(AddAccommodationActivity.this, task2 -> {
                        progressDialog.hide();
                        if (task2.isSuccessful()) {
                            Toast.makeText(AddAccommodationActivity.this, "Successfully Added", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddAccommodationActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    progressDialog.hide();
                    Toast.makeText(AddAccommodationActivity.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                }
            });

        }).addOnFailureListener(e -> {
            progressDialog.hide();
            Toast.makeText(AddAccommodationActivity.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
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
