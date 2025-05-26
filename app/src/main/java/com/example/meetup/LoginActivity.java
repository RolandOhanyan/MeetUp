package com.example.meetup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin, btnGuest;
    private TextView tvRegister;
    private CheckBox cbRememberMe;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        // Проверяем сохранённое состояние "Remember me"
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        boolean remember = prefs.getBoolean("rememberMe", false);
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (remember && currentUser != null && currentUser.isEmailVerified()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGuest = findViewById(R.id.btnGuest);
        tvRegister = findViewById(R.id.tvRegisterHere);
        cbRememberMe = findViewById(R.id.cbRememberMe);

        btnLogin.setOnClickListener(view -> loginUser());

        btnGuest.setOnClickListener(view -> loginAsGuest());

        tvRegister.setOnClickListener(view ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            if (user.isEmailVerified()) {
                                // Сохраняем состояние rememberMe
                                SharedPreferences.Editor editor = getSharedPreferences("loginPrefs", MODE_PRIVATE).edit();
                                editor.putBoolean("rememberMe", cbRememberMe.isChecked());
                                editor.apply();

                                Toast.makeText(this, "Вход выполнен!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(this, "Подтвердите почту перед входом!", Toast.LENGTH_LONG).show();
                                mAuth.signOut();
                            }
                        }
                    } else {
                        Toast.makeText(this, "Ошибка входа: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loginAsGuest() {
        String guestEmail = "individualproject2025@gmail.com";
        String guestPassword = "Samsung2025";

        mAuth.signInWithEmailAndPassword(guestEmail, guestPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            // Не сохраняем rememberMe
                            SharedPreferences.Editor editor = getSharedPreferences("loginPrefs", MODE_PRIVATE).edit();
                            editor.putBoolean("rememberMe", false);
                            editor.apply();

                            // Сохраняем флаг в SharedPreferences, что гость вошёл
                            SharedPreferences.Editor userEditor = getSharedPreferences("userPrefs", MODE_PRIVATE).edit();
                            userEditor.putString("guest_username", "user");
                            userEditor.apply();

                            Toast.makeText(this, "Вход как гость выполнен!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Подтвердите почту гостевого аккаунта!", Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                        }
                    } else {
                        Toast.makeText(this, "Ошибка входа гостя: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
