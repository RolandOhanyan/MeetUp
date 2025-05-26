package com.example.meetup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {

    private TextView correctTextView, wrongTextView, scoreTextView;
    private Button leaderboardButton;
    private TextView replayTextView;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        correctTextView = findViewById(R.id.correctTextView);
        wrongTextView = findViewById(R.id.wrongTextView);
        scoreTextView = findViewById(R.id.scoreTextView);
        leaderboardButton = findViewById(R.id.exploreButton);
        replayTextView = findViewById(R.id.replayTextView);

        int correct = getIntent().getIntExtra("CORRECT_COUNT", 0);
        int wrong = getIntent().getIntExtra("WRONG_COUNT", 0);
        String categoryId = getIntent().getStringExtra("CATEGORY_ID"); // получаем категорию

        correctTextView.setText(correct + " Correct");
        wrongTextView.setText(wrong + " Wrong");
        scoreTextView.setText("You got " + correct + " out of " + (correct + wrong));

        // Firebase и сохранение результатов
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() != null && categoryId != null) {
            String username;
            SharedPreferences prefs = getSharedPreferences("userPrefs", MODE_PRIVATE);
            String guestName = prefs.getString("guest_username", null);

            if (guestName != null) {
                username = guestName;
            } else {
                username = mAuth.getCurrentUser().getEmail();
            }


            leaderboardButton.setOnClickListener(v -> {
                Intent intent = new Intent(ResultActivity.this, LeaderBoardActivity.class);
                intent.putExtra("CATEGORY_ID", getIntent().getStringExtra("CATEGORY_ID")); // Передаём ту же категорию
                startActivity(intent);

            });

            replayTextView.setOnClickListener(v -> {
                Intent intent = new Intent(ResultActivity.this, SelectCategoryActivity.class);
                intent.putExtra("CATEGORY_ID", categoryId); // если нужно передать текущую категорию (опционально)
                startActivity(intent);
                finish();
            });
        }
    }
}