package com.example.meetup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class SelectCategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);

        MaterialCardView cardGeneral = findViewById(R.id.cardGeneral);
        MaterialCardView cardScience = findViewById(R.id.cardScience);
        MaterialCardView cardHistory = findViewById(R.id.cardHistory);

        cardGeneral.setOnClickListener(v -> launchQuiz("9"));     // General Knowledge
        cardScience.setOnClickListener(v -> launchQuiz("17"));    // Science & Nature
        cardHistory.setOnClickListener(v -> launchQuiz("23"));    // History
    }

    private void launchQuiz(String categoryId) {
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra("CATEGORY_ID", categoryId);
        startActivity(intent);
        finish(); // Если хочешь закрыть это активити
    }
}
