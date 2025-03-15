package com.example.meetup;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText etEmail, etPassword, etFirstName, etLastName, etPhone, etUsername;
    private Button btnRegister;
    private TextView tvLogin;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etPhone = findViewById(R.id.etPhone);
        etUsername = findViewById(R.id.etUsername); // Новое поле для username
        etEmail = findViewById(R.id.etRegEmail);
        etPassword = findViewById(R.id.etRegPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLoginHere);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnRegister.setOnClickListener(view -> registerUser());
        tvLogin.setOnClickListener(view -> startActivity(new Intent(this, LoginActivity.class)));
    }

    private void registerUser() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String username = etUsername.getText().toString().trim(); // Получаем username
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Проверка, что все поля заполнены
        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) ||
                TextUtils.isEmpty(phone) || TextUtils.isEmpty(username) ||
                TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Создание пользователя в Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Сохранение данных пользователя в Firestore
                            saveUserToFirestore(user.getUid(), firstName, lastName, phone, username, email);
                        }
                    } else {
                        // Обработка ошибок регистрации
                        if (task.getException() != null) {
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(this, "Ошибка регистрации: " + errorMessage, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Неизвестная ошибка регистрации!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Сохранение данных пользователя в Firestore
    private void saveUserToFirestore(String userId, String firstName, String lastName, String phone, String username, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("phone", phone);
        user.put("username", username); // Сохраняем username
        user.put("email", email);
        user.put("created_at", System.currentTimeMillis());

        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Ошибка сохранения данных!", Toast.LENGTH_SHORT).show()
                );
    }
}