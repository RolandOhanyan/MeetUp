package com.example.meetup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
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
    private Button option1Button, option2Button, option3Button, option4Button;
    private TextView scoreTextView, progressText;
    private ProgressBar progressBar;
    private Button playAgainButton;
    private RequestQueue requestQueue;
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
        option1Button = findViewById(R.id.option1Button);
        option2Button = findViewById(R.id.option2Button);
        option3Button = findViewById(R.id.option3Button);
        option4Button = findViewById(R.id.option4Button);
        scoreTextView = findViewById(R.id.scoreTextView);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);
        playAgainButton = findViewById(R.id.playAgainButton);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        requestQueue = Volley.newRequestQueue(this);
        fetchQuestions();

        playAgainButton.setOnClickListener(v -> {
            // Закрываем текущую активность и возвращаемся назад
            finish();
        });
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

        requestQueue.add(jsonObjectRequest);
    }

    private void displayQuestion() {
        if (currentQuestionIndex < questions.size()) {
            updateProgress();
            Question currentQuestion = questions.get(currentQuestionIndex);
            questionTextView.setText(currentQuestion.getQuestionText());

            List<String> options = currentQuestion.getOptions();
            option1Button.setText(options.get(0));
            option2Button.setText(options.get(1));
            option3Button.setText(options.get(2));
            option4Button.setText(options.get(3));

            resetButtonColors();

            option1Button.setOnClickListener(v -> checkAnswer(option1Button.getText().toString()));
            option2Button.setOnClickListener(v -> checkAnswer(option2Button.getText().toString()));
            option3Button.setOnClickListener(v -> checkAnswer(option3Button.getText().toString()));
            option4Button.setOnClickListener(v -> checkAnswer(option4Button.getText().toString()));
        } else {
            finishQuiz();
        }
    }

    private void updateProgress() {
        int totalQuestions = questions.size();
        int progress = (int) ((currentQuestionIndex + 1) * 100.0 / totalQuestions);
        progressBar.setProgress(progress);
        progressText.setText(String.format("%d/%d", currentQuestionIndex + 1, totalQuestions));
    }

    private void checkAnswer(String selectedAnswer) {
        Question currentQuestion = questions.get(currentQuestionIndex);
        Button selectedButton = findSelectedButton(selectedAnswer);

        setButtonsEnabled(false);

        if (selectedAnswer.equals(currentQuestion.getCorrectAnswer())) {
            score++;
            scoreTextView.setText("Score: " + score);
            selectedButton.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
        } else {
            selectedButton.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
            Button correctButton = findSelectedButton(currentQuestion.getCorrectAnswer());
            if (correctButton != null) {
                correctButton.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
            }
        }

        resetOtherButtonsColor(selectedButton, currentQuestion.getCorrectAnswer());

        new Handler().postDelayed(() -> {
            currentQuestionIndex++;
            if (currentQuestionIndex < questions.size()) {
                displayQuestion();
                setButtonsEnabled(true);
            } else {
                finishQuiz();
            }
        }, 1500);
    }

    private void resetOtherButtonsColor(Button selectedButton, String correctAnswer) {
        List<Button> allButtons = List.of(option1Button, option2Button, option3Button, option4Button);
        for (Button btn : allButtons) {
            String text = btn.getText().toString();
            if (!text.equals(correctAnswer) && btn != selectedButton) {
                btn.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
            }
        }
    }

    private void setButtonColor(Button button, int colorResId) {
        button.setBackgroundColor(ContextCompat.getColor(this, colorResId));
    }

    private void resetButtonColors() {
        int white = ContextCompat.getColor(this, android.R.color.white);
        option1Button.setBackgroundColor(white);
        option2Button.setBackgroundColor(white);
        option3Button.setBackgroundColor(white);
        option4Button.setBackgroundColor(white);
    }

    private Button findSelectedButton(String selectedAnswer) {
        if (option1Button.getText().toString().equals(selectedAnswer)) return option1Button;
        if (option2Button.getText().toString().equals(selectedAnswer)) return option2Button;
        if (option3Button.getText().toString().equals(selectedAnswer)) return option3Button;
        if (option4Button.getText().toString().equals(selectedAnswer)) return option4Button;
        return null;
    }

    private void animateButton(Button button, boolean isCorrect) {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(button, "scaleX", 0.9f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(button, "scaleY", 0.9f);
        scaleDownX.setDuration(100);
        scaleDownY.setDuration(100);

        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);

        scaleDown.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(button, "scaleX", 1f);
                ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(button, "scaleY", 1f);
                scaleUpX.setDuration(100);
                scaleUpY.setDuration(100);

                AnimatorSet scaleUp = new AnimatorSet();
                scaleUp.play(scaleUpX).with(scaleUpY);
                scaleUp.start();
            }
        });

        scaleDown.start();
    }

    private void setButtonsEnabled(boolean enabled) {
        option1Button.setEnabled(enabled);
        option2Button.setEnabled(enabled);
        option3Button.setEnabled(enabled);
        option4Button.setEnabled(enabled);
    }

    private void finishQuiz() {
        questionTextView.setText("Quiz finished! Your score: " + score + "/" + questions.size());
        option1Button.setVisibility(View.GONE);
        option2Button.setVisibility(View.GONE);
        option3Button.setVisibility(View.GONE);
        option4Button.setVisibility(View.GONE);
        playAgainButton.setVisibility(View.VISIBLE);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            db.collection("users").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");

                            db.collection("leaderboard").document(uid).get()
                                    .addOnSuccessListener(leaderboardSnapshot -> {
                                        Long previousScore = leaderboardSnapshot.getLong("score");
                                        if (previousScore == null || score > previousScore) {
                                            Map<String, Object> scoreEntry = new HashMap<>();
                                            scoreEntry.put("uid", uid);
                                            scoreEntry.put("username", username);
                                            scoreEntry.put("score", score);
                                            scoreEntry.put("timestamp", System.currentTimeMillis());

                                            db.collection("leaderboard").document(uid)
                                                    .set(scoreEntry)
                                                    .addOnSuccessListener(docRef -> {
                                                        // Успешно сохранено/обновлено
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(QuizActivity.this, "Ошибка при сохранении результата!", Toast.LENGTH_SHORT).show();
                                                    });
                                        }
                                    });
                        }
                    });
        }

        Button viewLeaderboardButton = new Button(this);
        viewLeaderboardButton.setText("Посмотреть лидерборд");
        ((LinearLayout) findViewById(R.id.quizLayout)).addView(viewLeaderboardButton);
        viewLeaderboardButton.setOnClickListener(v -> {
            startActivity(new Intent(QuizActivity.this, LeaderBoardActivity.class));
        });
    }

}