package com.example.studenthub.events;

import static com.google.common.io.Files.getFileExtension;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.studenthub.R;
import com.example.studenthub.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddEventActivity extends AppCompatActivity {

    private EditText titleEditText, descriptionEditText, locationEditText;
    private Button btAddEvent, selectImageButton, btDate;
    private ImageView selectedImageView, ivBack;
    private Uri imageUri;
    private ProgressDialog progressDialog;
    private Date pickedDate;

    private TextView tvTitle, tvDelete;
    private boolean isEditMode = false;
    private Event event;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        if (getIntent() != null) {
            event = getIntent().getParcelableExtra("Event");
            if (event != null) {
                isEditMode = true;
            }
        }

        progressDialog = new ProgressDialog(this);
        // Initialize views
        ivBack = findViewById(R.id.ivBack);
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        locationEditText = findViewById(R.id.locationEditText);
        btAddEvent = findViewById(R.id.btAddEvent);
        btDate = findViewById(R.id.btDate);
        selectImageButton = findViewById(R.id.selectImageButton);
        selectedImageView = findViewById(R.id.iv);

        btAddEvent.setOnClickListener(v -> {
            if (!isEditMode) {
                addEvent();
            } else {
                updateEvent();
            }
        });
        btDate.setOnClickListener(v -> pickDateAndTime());
        ivBack.setOnClickListener(v -> finish());

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        preFillData();
    }

    private void preFillData() {
        if (event == null) {
            return;
        }
        titleEditText.setText(event.getTitle());
        descriptionEditText.setText(event.getDescription());
        locationEditText.setText(event.getLocation());
        pickedDate = event.getDate();
        btDate.setText(Utils.dateFormat(pickedDate));
        Glide.with(this).load(event.getImageUrl()).into(selectedImageView);
        selectImageButton.setText(R.string.update_image);
        btAddEvent.setText(R.string.update);
        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.update);
        tvDelete = findViewById(R.id.tvDelete);
        tvDelete.setVisibility(View.VISIBLE);
        tvDelete.setOnClickListener(v -> deleteEvent());
    }

    private void deleteEvent() {
        progressDialog.show();
        DocumentReference ref = FirebaseFirestore.getInstance().collection("events").document(event.getId());
        ref.delete().addOnCompleteListener(AddEventActivity.this, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(AddEventActivity.this, "Successfully Deleted", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(AddEventActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addEvent() {
        // Retrieve data from input fields
        String title = titleEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        // Validate inputs
        if (title.isEmpty() || location.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pickedDate == null) {
            Toast.makeText(this, "Please select date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri == null) {
            Toast.makeText(this, "Please select Image", Toast.LENGTH_SHORT).show();
            return;
        }

        String ownerContact = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String ownerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference ref = FirebaseFirestore.getInstance().collection("events").document();
        Map<String, Object> data = new HashMap<>();
        data.put("id", ref.getId());
        data.put("description", description);
        data.put("title", title);
        data.put("location", location);
        data.put("date", pickedDate);
        data.put("ownerId", ownerId);
        data.put("ownerContact", ownerContact);

        progressDialog.show();

        StorageReference fileReference = FirebaseStorage.getInstance().getReference("events").child(System.currentTimeMillis()
                + "." + getFileExtension(imageUri.toString()));
        fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {

            fileReference.getDownloadUrl().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String imageUrl = task.getResult().toString();
                    data.put("imageUrl", imageUrl);
                    ref.set(data).addOnCompleteListener(AddEventActivity.this, task2 -> {
                        progressDialog.hide();
                        if (task2.isSuccessful()) {
                            Toast.makeText(AddEventActivity.this, "Successfully Added", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddEventActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    progressDialog.hide();
                    Toast.makeText(AddEventActivity.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                }
            });

        }).addOnFailureListener(e -> {
            progressDialog.hide();
            Toast.makeText(AddEventActivity.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
        });
    }
    private void updateEvent() {
        // Retrieve data from input fields
        String title = titleEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        // Validate inputs
        if (title.isEmpty() || location.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pickedDate == null) {
            Toast.makeText(this, "Please select date", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference ref = FirebaseFirestore.getInstance().collection("events").document(event.getId());
        Map<String, Object> data = new HashMap<>();
        data.put("description", description);
        data.put("title", title);
        data.put("location", location);
        data.put("date", pickedDate);

        progressDialog.show();

        if (imageUri != null) {
            StorageReference fileReference = FirebaseStorage.getInstance().getReference("events").child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri.toString()));
            fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {

                fileReference.getDownloadUrl().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String imageUrl = task.getResult().toString();
                        data.put("imageUrl", imageUrl);
                        ref.update(data).addOnCompleteListener(AddEventActivity.this, task2 -> {
                            progressDialog.hide();
                            if (task2.isSuccessful()) {
                                Toast.makeText(AddEventActivity.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(AddEventActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        progressDialog.hide();
                        Toast.makeText(AddEventActivity.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                    }
                });

            }).addOnFailureListener(e -> {
                progressDialog.hide();
                Toast.makeText(AddEventActivity.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
            });
        } else {
            ref.update(data).addOnCompleteListener(AddEventActivity.this, task2 -> {
                progressDialog.hide();
                if (task2.isSuccessful()) {
                    Toast.makeText(AddEventActivity.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddEventActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            });
        }
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

    private void pickDateAndTime() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create a date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddEventActivity.this,
                (view, year1, month1, dayOfMonth) -> {
                    // Set the selected date to the calendar object
                    calendar.set(Calendar.YEAR, year1);
                    calendar.set(Calendar.MONTH, month1);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    // Create a time picker dialog
                    TimePickerDialog timePickerDialog = new TimePickerDialog(AddEventActivity.this,
                            (view1, hourOfDay1, minute1) -> {
                                // Set the selected time to the calendar object
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay1);
                                calendar.set(Calendar.MINUTE, minute1);
                                pickedDate = calendar.getTime();
                                btDate.setText(Utils.dateFormat(pickedDate));
                            }, hourOfDay, minute, true);
                    timePickerDialog.show();

                }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();

    }
}
