package com.example.studenthub.rides;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studenthub.R;
import com.example.studenthub.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BookRideActivity extends AppCompatActivity {

    private EditText etPickupLocation, etDropLocation;
    private Button btBookRide, btPickTime;
    private ImageView ivBack, ivPostRide;
    private ProgressDialog progressDialog;
    private Date pickedPickTimeDate;
    private TextView tvTitle, tvDelete;
    private boolean isEditMode = false;
    private Ride ride;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_ride);

        if (getIntent() != null) {
            ride = getIntent().getParcelableExtra("Ride");
            if (ride != null) {
                isEditMode = true;
            }
        }

        progressDialog = new ProgressDialog(this);
        // Initialize views
        ivBack = findViewById(R.id.ivBack);
        etPickupLocation = findViewById(R.id.etPickupLocation);
        etDropLocation = findViewById(R.id.etDropLocation);
        btBookRide = findViewById(R.id.btBook);
        btPickTime = findViewById(R.id.btDatePickupTime);
        ivPostRide = findViewById(R.id.ivPostRide);

        btBookRide.setOnClickListener(v -> {
            if (!isEditMode) {
                bookRide();
            } else {
                updateRide();
            }
        });
        btPickTime.setOnClickListener(v -> pickDateAndTime());
        ivBack.setOnClickListener(v -> finish());
        ivPostRide.setVisibility(View.VISIBLE);
        ivPostRide.setOnClickListener(v -> {
            Intent i = new Intent(BookRideActivity.this, PostRideActivity.class);
            startActivity(i);
        });

        preFillData();
    }

    private void preFillData() {
        if (ride == null) {
            return;
        }
        etDropLocation.setText(ride.getDropLocation());
        etPickupLocation.setText(ride.getPickupLocation());
        pickedPickTimeDate = ride.getPickupTime();
        btPickTime.setText(Utils.dateFormat(pickedPickTimeDate));
        btBookRide.setText(R.string.update);
        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.update);
        tvDelete = findViewById(R.id.tvDelete);
        tvDelete.setVisibility(View.VISIBLE);
        ivPostRide.setVisibility(View.GONE);
        tvDelete.setOnClickListener(v -> deleteBooking());
    }

    private void deleteBooking() {
        progressDialog.show();
        DocumentReference ref = FirebaseFirestore.getInstance().collection("bookedRides").document(ride.getId());
        ref.delete().addOnCompleteListener(BookRideActivity.this, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(BookRideActivity.this, "Successfully Deleted", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(BookRideActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bookRide() {
        // Retrieve data from input fields
        String pickUpLocation = etPickupLocation.getText().toString().trim();
        String dropLocation = etDropLocation.getText().toString().trim();

        // Validate inputs
        if (pickUpLocation.isEmpty() || dropLocation.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pickedPickTimeDate == null) {
            Toast.makeText(this, "Please select time", Toast.LENGTH_SHORT).show();
            return;
        }

        String ownerContact = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String ownerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference ref = FirebaseFirestore.getInstance().collection("bookedRides").document();
        Map<String, Object> data = new HashMap<>();
        data.put("id", ref.getId());
        data.put("pickupLocation", pickUpLocation);
        data.put("dropLocation", dropLocation);
        data.put("pickupTime", pickedPickTimeDate);
        data.put("dropTime", new Date());
        data.put("noOfPassengers", 1);
        data.put("ownerId", ownerId);
        data.put("ownerContact", ownerContact);
        data.put("type", "bookedRides");
        data.put("lastModified", new Date());

        progressDialog.show();

        ref.set(data).addOnCompleteListener(BookRideActivity.this, task -> {
            progressDialog.hide();
            if (task.isSuccessful()) {
                Toast.makeText(BookRideActivity.this, "Successfully Booked", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(BookRideActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRide() {
        // Retrieve data from input fields
        String pickUpLocation = etPickupLocation.getText().toString().trim();
        String dropLocation = etDropLocation.getText().toString().trim();

        // Validate inputs
        if (pickUpLocation.isEmpty() || dropLocation.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pickedPickTimeDate == null) {
            Toast.makeText(this, "Please select time", Toast.LENGTH_SHORT).show();
            return;
        }

        String ownerContact = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String ownerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference ref = FirebaseFirestore.getInstance().collection("bookedRides").document(ride.getId());
        Map<String, Object> data = new HashMap<>();
        data.put("pickupLocation", pickUpLocation);
        data.put("dropLocation", dropLocation);
        data.put("pickupTime", pickedPickTimeDate);
        data.put("dropTime", new Date());
        data.put("noOfPassengers", 1);

        progressDialog.show();

        ref.update(data).addOnCompleteListener(BookRideActivity.this, task -> {
            progressDialog.hide();
            if (task.isSuccessful()) {
                Toast.makeText(BookRideActivity.this, "Successfully updated", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(BookRideActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pickDateAndTime() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create a date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(BookRideActivity.this,
                (view, year1, month1, dayOfMonth) -> {
                    // Set the selected date to the calendar object
                    calendar.set(Calendar.YEAR, year1);
                    calendar.set(Calendar.MONTH, month1);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    // Create a time picker dialog
                    TimePickerDialog timePickerDialog = new TimePickerDialog(BookRideActivity.this,
                            (view1, hourOfDay1, minute1) -> {
                                // Set the selected time to the calendar object
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay1);
                                calendar.set(Calendar.MINUTE, minute1);
                                Date selectedDateTime = calendar.getTime();

                                if (selectedDateTime.before(Calendar.getInstance().getTime())) {
                                    Toast.makeText(BookRideActivity.this, "Selected time has already passed", Toast.LENGTH_SHORT).show();
                                } else {
                                    pickedPickTimeDate = selectedDateTime;
                                    btPickTime.setText(Utils.dateFormat(pickedPickTimeDate));
                                }
                            }, hourOfDay, minute, true);
                    timePickerDialog.show();

                }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();

    }
}
