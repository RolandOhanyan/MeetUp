package com.example.meetup;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvProfileName, tvProfileUsername, tvProfileEmail;
    private Button btnEditProfile, btnSignOut;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileUsername = findViewById(R.id.tvProfileUsername);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnSignOut = findViewById(R.id.btnSignOut);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Получение текущего пользователя
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Загрузка данных пользователя из Firestore
            loadUserData(user.getUid());
        } else {
            // Если пользователь не авторизован, перенаправляем на экран входа
            Toast.makeText(this, "Пользователь не авторизован", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Обработка нажатия на кнопку "Edit Profile"
        btnEditProfile.setOnClickListener(v -> {
            // Переход на экран редактирования профиля (если нужно)
            Toast.makeText(this, "Edit Profile Clicked", Toast.LENGTH_SHORT).show();
        });

        // Обработка нажатия на кнопку "Sign out"
        btnSignOut.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });
    }

    // Загрузка данных пользователя из Firestore
    private void loadUserData(String userId) {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Получение данных из Firestore
                        String firstName = documentSnapshot.getString("firstName");
                        String lastName = documentSnapshot.getString("lastName");
                        String username = documentSnapshot.getString("username");
                        String email = documentSnapshot.getString("email");

                        // Отображение данных в TextView
                        tvProfileName.setText("Имя: " + firstName + " " + lastName);
                        tvProfileUsername.setText("Никнейм: @" + username);
                        tvProfileEmail.setText("Email: " + email);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Ошибка загрузки данных!", Toast.LENGTH_SHORT).show()
                );
    }
}