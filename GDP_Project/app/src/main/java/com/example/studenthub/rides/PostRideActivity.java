package com.example.studenthub.rides;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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

public class PostRideActivity extends AppCompatActivity {

    private EditText etPickupLocation, etDropLocation;
    private Button btPostRide, btPickTime, btDropTime, btMinus, btPlus;
    private ImageView ivBack;
    private ProgressDialog progressDialog;
    private Date pickedPickTimeDate, pickedDropTimeDate;
    private TextView tvTitle, tvDelete, tvPassengers;
    private boolean isEditMode = false;
    private Ride ride;
    private int noOfPassengers = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_ride);

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
        btPlus = findViewById(R.id.btPlus);
        btMinus = findViewById(R.id.btMinus);
        tvPassengers = findViewById(R.id.tvPassengers);
        btPostRide = findViewById(R.id.btPost);
        btPickTime = findViewById(R.id.btDatePickupTime);
        btDropTime = findViewById(R.id.btDateDropTime);

        btPostRide.setOnClickListener(v -> {
            if (!isEditMode) {
                postRide();
            } else {
                updateRide();
            }
        });
        btPickTime.setOnClickListener(v -> pickDateAndTime());
        btDropTime.setOnClickListener(v -> pickDateAndTimeDrop());
        ivBack.setOnClickListener(v -> finish());
        btMinus.setOnClickListener(v -> decreasePassengerCount());
        btPlus.setOnClickListener(v -> increasePassengerCount());

        preFillData();
    }

    private void preFillData() {
        if (ride == null) {
            return;
        }
        etDropLocation.setText(ride.getDropLocation());
        etPickupLocation.setText(ride.getPickupLocation());
        pickedPickTimeDate = ride.getPickupTime();
        pickedDropTimeDate = ride.getDropTime();
        btPickTime.setText(Utils.dateFormat(pickedPickTimeDate));
        btDropTime.setText(Utils.dateFormat(pickedDropTimeDate));
        noOfPassengers = ride.getNoOfPassengers();
        btPostRide.setText(R.string.update);
        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.update);
        tvDelete = findViewById(R.id.tvDelete);
        tvDelete.setVisibility(View.VISIBLE);
        tvDelete.setOnClickListener(v -> deleteBooking());
        tvPassengers.setText("Passengers: " + noOfPassengers);
    }

    private void deleteBooking() {
        progressDialog.show();
        DocumentReference ref = FirebaseFirestore.getInstance().collection("postRides").document(ride.getId());
        ref.delete().addOnCompleteListener(PostRideActivity.this, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(PostRideActivity.this, "Successfully Deleted", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(PostRideActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postRide() {
        // Retrieve data from input fields
        String pickUpLocation = etPickupLocation.getText().toString().trim();
        String dropLocation = etDropLocation.getText().toString().trim();

        // Validate inputs
        if (pickUpLocation.isEmpty() || dropLocation.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pickedPickTimeDate == null) {
            Toast.makeText(this, "Please select pickup time", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pickedDropTimeDate == null) {
            Toast.makeText(this, "Please select drop time", Toast.LENGTH_SHORT).show();
            return;
        }

        String ownerContact = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String ownerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference ref = FirebaseFirestore.getInstance().collection("postRides").document();
        Map<String, Object> data = new HashMap<>();
        data.put("id", ref.getId());
        data.put("pickupLocation", pickUpLocation);
        data.put("dropLocation", dropLocation);
        data.put("pickupTime", pickedPickTimeDate);
        data.put("dropTime", pickedDropTimeDate);
        data.put("noOfPassengers", noOfPassengers);
        data.put("ownerId", ownerId);
        data.put("ownerContact", ownerContact);
        data.put("type", "postRides");
        data.put("lastModified", new Date());

        progressDialog.show();

        ref.set(data).addOnCompleteListener(PostRideActivity.this, task -> {
            progressDialog.hide();
            if (task.isSuccessful()) {
                Toast.makeText(PostRideActivity.this, "Successfully Booked", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(PostRideActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
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

        DocumentReference ref = FirebaseFirestore.getInstance().collection("postRides").document(ride.getId());
        Map<String, Object> data = new HashMap<>();
        data.put("pickupLocation", pickUpLocation);
        data.put("dropLocation", dropLocation);
        data.put("pickupTime", pickedPickTimeDate);
        data.put("dropTime", new Date());
        data.put("noOfPassengers", noOfPassengers);
        data.put("type", "postRides");

        progressDialog.show();

        ref.update(data).addOnCompleteListener(PostRideActivity.this, task -> {
            progressDialog.hide();
            if (task.isSuccessful()) {
                Toast.makeText(PostRideActivity.this, "Successfully updated", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(PostRideActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
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
        DatePickerDialog datePickerDialog = new DatePickerDialog(PostRideActivity.this,
                (view, year1, month1, dayOfMonth) -> {
                    // Set the selected date to the calendar object
                    calendar.set(Calendar.YEAR, year1);
                    calendar.set(Calendar.MONTH, month1);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    // Create a time picker dialog
                    TimePickerDialog timePickerDialog = new TimePickerDialog(PostRideActivity.this,
                            (view1, hourOfDay1, minute1) -> {
                                // Set the selected time to the calendar object
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay1);
                                calendar.set(Calendar.MINUTE, minute1);

                                Date selectedDateTime = calendar.getTime();

                                if (selectedDateTime.before(Calendar.getInstance().getTime())) {
                                    Toast.makeText(PostRideActivity.this, "Selected time has already passed", Toast.LENGTH_SHORT).show();
                                } else {
                                    pickedPickTimeDate = calendar.getTime();
                                    btPickTime.setText(Utils.dateFormat(pickedPickTimeDate));
                                }

                            }, hourOfDay, minute, true);
                    timePickerDialog.show();

                }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();

    }

    private void pickDateAndTimeDrop() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create a date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(PostRideActivity.this,
                (view, year1, month1, dayOfMonth) -> {
                    // Set the selected date to the calendar object
                    calendar.set(Calendar.YEAR, year1);
                    calendar.set(Calendar.MONTH, month1);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    // Create a time picker dialog
                    TimePickerDialog timePickerDialog = new TimePickerDialog(PostRideActivity.this,
                            (view1, hourOfDay1, minute1) -> {
                                // Set the selected time to the calendar object
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay1);
                                calendar.set(Calendar.MINUTE, minute1);

                                Date selectedDateTime = calendar.getTime();

                                if (pickedPickTimeDate != null) {
                                    if (selectedDateTime.before(pickedPickTimeDate)) {
                                        Toast.makeText(PostRideActivity.this, "Drop time can not be less than Pickup time", Toast.LENGTH_SHORT).show();
                                    } else {
                                        pickedDropTimeDate = selectedDateTime;
                                        btDropTime.setText(Utils.dateFormat(pickedDropTimeDate));
                                    }
                                } else {
                                    if (selectedDateTime.before(Calendar.getInstance().getTime())) {
                                        Toast.makeText(PostRideActivity.this, "Selected time has already passed", Toast.LENGTH_SHORT).show();
                                    } else {
                                        pickedDropTimeDate = selectedDateTime;
                                        btDropTime.setText(Utils.dateFormat(pickedDropTimeDate));
                                    }
                                }
                            }, hourOfDay, minute, true);
                    timePickerDialog.show();

                }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();

    }

    private void increasePassengerCount() {
        if (noOfPassengers < 10) {
            noOfPassengers++;
            tvPassengers.setText("Passengers: " + noOfPassengers);
        }
    }

    private void decreasePassengerCount() {
        if (noOfPassengers > 1) {
            noOfPassengers--;
            tvPassengers.setText("Passengers: " + noOfPassengers);
        }
    }
}
