package com.example.meetup;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_PERMISSION_CODE = 2;
    private EditText etEventName;
    private Button btnChooseDate, btnChooseTime, btnSaveEvent, btnAttachPhoto;
    private FusedLocationProviderClient fusedLocationClient;
    private String selectedDate = "", selectedTime = "", imageUrl = "";
    private GoogleMap googleMap;
    private LatLng selectedLocation = null; // Store the selected location
    private FirebaseFirestore db;  // Firestore instance
    private FirebaseStorage storage;  // Firebase Storage instance
    private StorageReference storageRef;  // Firebase Storage reference
    private ImageView ivEventPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // Initialize Firestore and Firebase Storage
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        etEventName = findViewById(R.id.etEventName);
        btnChooseDate = findViewById(R.id.btnChooseDate);
        btnChooseTime = findViewById(R.id.btnChooseTime);
        btnSaveEvent = findViewById(R.id.btnSaveEvent);
        btnAttachPhoto = findViewById(R.id.btnAttachPhoto);
        ivEventPhoto = findViewById(R.id.ivEventPhoto);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnChooseDate.setOnClickListener(v -> showDatePicker());
        btnChooseTime.setOnClickListener(v -> showTimePicker());
        btnSaveEvent.setOnClickListener(v -> saveEvent());
        btnAttachPhoto.setOnClickListener(v -> openGallery());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }

        googleMap.setOnMapClickListener(latLng -> {
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(latLng).title("Event Location"));
            selectedLocation = latLng; // Save the selected location
        });

        getCurrentLocation();
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
            btnChooseDate.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, selectedHour, selectedMinute) -> {
            selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
            btnChooseTime.setText(selectedTime);
        }, hour, minute, true);

        timePickerDialog.show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ivEventPhoto.setImageBitmap(bitmap);
                ivEventPhoto.setVisibility(View.VISIBLE);  // Show the selected image
                uploadImageToFirebase(imageUri);  // Upload image to Firebase Storage
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        if (imageUri == null) {
            Toast.makeText(this, "Ошибка: не удалось получить изображение", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference fileReference = storageRef.child("event_photos/" + System.currentTimeMillis() + ".jpg");

        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            imageUrl = uri.toString();
                            Toast.makeText(CreateEventActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(CreateEventActivity.this, "Ошибка при получении URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        })
                )
                .addOnFailureListener(e -> {
                    Toast.makeText(CreateEventActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void saveEvent() {
        String eventName = etEventName.getText().toString().trim();

        // Check if all fields are filled and location is selected
        if (eventName.isEmpty() || selectedDate.isEmpty() || selectedTime.isEmpty() || selectedLocation == null) {
            Toast.makeText(this, "Please fill all fields and select a location", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create Event object
        Event event = new Event(eventName, selectedDate, selectedTime, selectedLocation.latitude, selectedLocation.longitude, imageUrl);

        // Get reference to Firestore collection
        CollectionReference eventsRef = db.collection("events");

        // Add the event to Firestore
        eventsRef.add(event)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(CreateEventActivity.this, "Event saved successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CreateEventActivity.this, "Error saving event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }
    }
}
