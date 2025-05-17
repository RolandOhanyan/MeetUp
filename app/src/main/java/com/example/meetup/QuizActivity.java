package com.example.meetup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizActivity extends AppCompatActivity {

    private TextView questionTextView;
    private TextView option1TextView, option2TextView, option3TextView, option4TextView;
    private TextView progressNumberTextView;
    private LinearLayout quizLayout;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private List<Question> questions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        questionTextView = findViewById(R.id.questionTextView);
        option1TextView = findViewById(R.id.option1TextView);
        option2TextView = findViewById(R.id.option2TextView);
        option3TextView = findViewById(R.id.option3TextView);
        option4TextView = findViewById(R.id.option4TextView);
        progressNumberTextView = findViewById(R.id.progressNumberTextView);
        quizLayout = findViewById(R.id.quizLayout);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        fetchQuestions();
    }

    private void fetchQuestions() {
        String categoryId = getIntent().getStringExtra("CATEGORY_ID");
        String url = "https://opentdb.com/api.php?amount=10&category=" + categoryId + "&type=multiple";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONArray results = response.getJSONArray("results");
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject questionJson = results.getJSONObject(i);
                            String questionText = questionJson.getString("question");
                            String correctAnswer = questionJson.getString("correct_answer");
                            JSONArray incorrectAnswers = questionJson.getJSONArray("incorrect_answers");

                            List<String> options = new ArrayList<>();
                            options.add(correctAnswer);
                            for (int j = 0; j < incorrectAnswers.length(); j++) {
                                options.add(incorrectAnswers.getString(j));
                            }

                            Collections.shuffle(options);
                            questions.add(new Question(questionText, correctAnswer, options));
                        }
                        displayQuestion();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void displayQuestion() {
        if (currentQuestionIndex < questions.size()) {
            updateProgress();
            Question currentQuestion = questions.get(currentQuestionIndex);
            questionTextView.setText(currentQuestion.getQuestionText());

            List<String> options = currentQuestion.getOptions();
            option1TextView.setText(options.get(0));
            option2TextView.setText(options.get(1));
            option3TextView.setText(options.get(2));
            option4TextView.setText(options.get(3));

            resetTextViewsColors();

            option1TextView.setOnClickListener(v -> checkAnswer(option1TextView.getText().toString()));
            option2TextView.setOnClickListener(v -> checkAnswer(option2TextView.getText().toString()));
            option3TextView.setOnClickListener(v -> checkAnswer(option3TextView.getText().toString()));
            option4TextView.setOnClickListener(v -> checkAnswer(option4TextView.getText().toString()));
        } else {
            finishQuiz();
        }
    }

    private void updateProgress() {
        progressNumberTextView.setText(String.format("%d", currentQuestionIndex + 1));
    }

    private void checkAnswer(String selectedAnswer) {
        Question currentQuestion = questions.get(currentQuestionIndex);
        TextView selectedTextView = findSelectedTextView(selectedAnswer);

        setTextViewsEnabled(false);

        if (selectedAnswer.equals(currentQuestion.getCorrectAnswer())) {
            score++;
            selectedTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.correct_answer_bg));
        } else {
            selectedTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.wrong_answer_bg));
            TextView correctTextView = findSelectedTextView(currentQuestion.getCorrectAnswer());
            if (correctTextView != null) {
                correctTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.correct_answer_bg));
            }
        }

        new Handler().postDelayed(() -> {
            currentQuestionIndex++;
            if (currentQuestionIndex < questions.size()) {
                displayQuestion();
                setTextViewsEnabled(true);
            } else {
                finishQuiz();
            }
        }, 1500);
    }

    private TextView findSelectedTextView(String selectedAnswer) {
        if (option1TextView.getText().toString().equals(selectedAnswer)) return option1TextView;
        if (option2TextView.getText().toString().equals(selectedAnswer)) return option2TextView;
        if (option3TextView.getText().toString().equals(selectedAnswer)) return option3TextView;
        if (option4TextView.getText().toString().equals(selectedAnswer)) return option4TextView;
        return null;
    }

    private void resetTextViewsColors() {
        option1TextView.setBackground(ContextCompat.getDrawable(this, R.drawable.sub_item_bg));
        option2TextView.setBackground(ContextCompat.getDrawable(this, R.drawable.sub_item_bg));
        option3TextView.setBackground(ContextCompat.getDrawable(this, R.drawable.sub_item_bg));
        option4TextView.setBackground(ContextCompat.getDrawable(this, R.drawable.sub_item_bg));
    }

    private void setTextViewsEnabled(boolean enabled) {
        option1TextView.setEnabled(enabled);
        option2TextView.setEnabled(enabled);
        option3TextView.setEnabled(enabled);
        option4TextView.setEnabled(enabled);
    }

    private void finishQuiz() {
        Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
        intent.putExtra("CORRECT_COUNT", score);
        intent.putExtra("WRONG_COUNT", questions.size() - score);
        startActivity(intent);
        finish(); // Закрываем QuizActivity
    }
}
