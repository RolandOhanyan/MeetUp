package com.example.meetup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private String selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Получаем выбранную категорию из предыдущей Activity
        selectedCategory = getIntent().getStringExtra("CATEGORY");

        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(v -> startQuiz());

        Button signOutButton = findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(v -> signOut());
    }

    // Метод выхода из аккаунта
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    // Метод запуска викторины, передаёт категорию в QuizActivity
    private void startQuiz() {
        Intent intent = new Intent(this, SelectCategoryActivity.class);
        intent.putExtra("CATEGORY", selectedCategory); // передаём категорию
        startActivity(intent);
    }
}
