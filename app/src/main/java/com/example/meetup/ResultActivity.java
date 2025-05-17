package com.example.meetup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    private TextView correctTextView, wrongTextView, scoreTextView;
    private Button leaderboardButton;
    private TextView replayTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result); // ваш XML layout

        correctTextView = findViewById(R.id.correctTextView);
        wrongTextView = findViewById(R.id.wrongTextView);
        scoreTextView = findViewById(R.id.scoreTextView);
        leaderboardButton = findViewById(R.id.exploreButton);
        replayTextView = findViewById(R.id.replayTextView);

        int correct = getIntent().getIntExtra("CORRECT_COUNT", 0);
        int wrong = getIntent().getIntExtra("WRONG_COUNT", 0);

        correctTextView.setText(correct + " Correct");
        wrongTextView.setText(wrong + " Wrong");
        scoreTextView.setText("You got the " + correct + " out of " + (correct + wrong));

        leaderboardButton.setOnClickListener(v -> {
            startActivity(new Intent(ResultActivity.this, LeaderBoardActivity.class));
        });

        replayTextView.setOnClickListener(v -> {
            startActivity(new Intent(ResultActivity.this, SelectCategoryActivity.class)); // Вернёт в выбор категории
            finish();
        });
    }
}
