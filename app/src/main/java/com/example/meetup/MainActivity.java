package com.example.meetup;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private TextView tvUser;
    private Button btnCreateNewEvent;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCreateNewEvent = findViewById(R.id.btnCreateEvent);
        tvUser = findViewById(R.id.textView);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Проверка, авторизован ли пользователь
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            loadUserData(user.getUid());
        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        // Обработка нажатия на кнопку "Create Event"
        btnCreateNewEvent.setOnClickListener(v -> {
            // Переход на экран создания мероприятия
            startActivity(new Intent(MainActivity.this, CreateEventActivity.class));
        });

        // Настройка BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.navigation_home) {
                    Toast.makeText(MainActivity.this, "Home Selected", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.navigation_groups) {
                    Toast.makeText(MainActivity.this, "Groups Selected", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.navigation_watch) {
                    Toast.makeText(MainActivity.this, "Watch Selected", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.navigation_profile) {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.navigation_notifications) {
                    Toast.makeText(MainActivity.this, "Notifications Selected", Toast.LENGTH_SHORT).show();
                    return true;
                }

                return false;
            }
        });
    }

    // Загрузка данных пользователя
    private void loadUserData(String userId) {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String email = documentSnapshot.getString("email");
                        tvUser.setText("Вы вошли как: " + email);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Ошибка загрузки данных!", Toast.LENGTH_SHORT).show()
                );
    }
}