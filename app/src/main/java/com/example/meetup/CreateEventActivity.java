package com.example.meetup;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity {
    private EditText etEventName, etEventLocation;
    private Button btnChooseDate, btnChooseTime, btnSaveEvent;
    private Switch switchRecurring;
    private String selectedDate = "", selectedTime = "";
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        etEventName = findViewById(R.id.etEventName);
        etEventLocation = findViewById(R.id.etEventLocation);
        btnChooseDate = findViewById(R.id.btnChooseDate);
        btnChooseTime = findViewById(R.id.btnChooseTime);
        btnSaveEvent = findViewById(R.id.btnSaveEvent);


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Выбор даты
        btnChooseDate.setOnClickListener(v -> showDatePicker());

        // Выбор времени
        btnChooseTime.setOnClickListener(v -> showTimePicker());

        // Сохранение мероприятия
        btnSaveEvent.setOnClickListener(v -> saveEvent());
    }

    // Показ диалога выбора даты
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth) -> {
                    selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                    btnChooseDate.setText("Date: " + selectedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    // Показ диалога выбора времени
    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute1) -> {
                    selectedTime = hourOfDay + ":" + minute1;
                    btnChooseTime.setText("Time: " + selectedTime);
                },
                hour, minute, true
        );
        timePickerDialog.show();
    }

    // Сохранение мероприятия
    private void saveEvent() {
        String eventName = etEventName.getText().toString().trim();
        String eventLocation = etEventLocation.getText().toString().trim();
        boolean isRecurring = switchRecurring.isChecked();
        String creatorId = mAuth.getCurrentUser().getUid();

        if (eventName.isEmpty() || eventLocation.isEmpty() || selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
            return;
        }

        Event event = new Event(eventName, eventLocation, selectedDate + " " + selectedTime, isRecurring, creatorId);
        db.collection("events")
                .add(event)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Мероприятие создано!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}