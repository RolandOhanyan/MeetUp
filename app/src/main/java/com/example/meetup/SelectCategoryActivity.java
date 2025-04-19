package com.example.meetup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class SelectCategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);

        Button buttonGeneral = findViewById(R.id.buttonGeneral);
        Button buttonScience = findViewById(R.id.buttonScience);
        Button buttonHistory = findViewById(R.id.buttonHistory);

        buttonGeneral.setOnClickListener(v -> launchQuiz("9"));     // General Knowledge
        buttonScience.setOnClickListener(v -> launchQuiz("17"));    // Science & Nature
        buttonHistory.setOnClickListener(v -> launchQuiz("23"));    // History
    }

    private void launchQuiz(String categoryId) {
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra("CATEGORY_ID", categoryId);
        startActivity(intent);
        finish(); // опционально
    }
}
