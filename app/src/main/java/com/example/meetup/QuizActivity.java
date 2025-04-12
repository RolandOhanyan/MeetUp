package com.example.meetup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private TextView questionTextView;
    private Button option1Button, option2Button, option3Button, option4Button;
    private TextView scoreTextView;
    private RequestQueue requestQueue;

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

        requestQueue = Volley.newRequestQueue(this);
        fetchQuestions();
    }

    private void fetchQuestions() {
        String url = "https://opentdb.com/api.php?amount=10&type=multiple";

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
            Question currentQuestion = questions.get(currentQuestionIndex);
            questionTextView.setText(currentQuestion.getQuestionText());

            List<String> options = currentQuestion.getOptions();
            option1Button.setText(options.get(0));
            option2Button.setText(options.get(1));
            option3Button.setText(options.get(2));
            option4Button.setText(options.get(3));

            // Reset button colors
            option1Button.setBackgroundColor(getResources().getColor(android.R.color.white));
            option2Button.setBackgroundColor(getResources().getColor(android.R.color.white));
            option3Button.setBackgroundColor(getResources().getColor(android.R.color.white));
            option4Button.setBackgroundColor(getResources().getColor(android.R.color.white));

            // Set click listeners
            option1Button.setOnClickListener(v -> checkAnswer(option1Button.getText().toString()));
            option2Button.setOnClickListener(v -> checkAnswer(option2Button.getText().toString()));
            option3Button.setOnClickListener(v -> checkAnswer(option3Button.getText().toString()));
            option4Button.setOnClickListener(v -> checkAnswer(option4Button.getText().toString()));
        } else {
            // Quiz finished
            questionTextView.setText("Quiz finished! Your score: " + score + "/" + questions.size());
            option1Button.setVisibility(View.GONE);
            option2Button.setVisibility(View.GONE);
            option3Button.setVisibility(View.GONE);
            option4Button.setVisibility(View.GONE);
        }
    }

    private void checkAnswer(String selectedAnswer) {
        Question currentQuestion = questions.get(currentQuestionIndex);
        Button selectedButton = findSelectedButton(selectedAnswer);

        // Отключаем все кнопки, чтобы предотвратить множественные нажатия
        setButtonsEnabled(false);

        if (selectedAnswer.equals(currentQuestion.getCorrectAnswer())) {
            score++;
            scoreTextView.setText("Score: " + score);
            animateButton(selectedButton, true); // Анимация для правильного ответа
        } else {
            animateButton(selectedButton, false); // Анимация для неправильного ответа
            highlightCorrectAnswer(currentQuestion.getCorrectAnswer());
        }

        // Переход к следующему вопросу после задержки
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

    private Button findSelectedButton(String selectedAnswer) {
        if (option1Button.getText().toString().equals(selectedAnswer)) return option1Button;
        if (option2Button.getText().toString().equals(selectedAnswer)) return option2Button;
        if (option3Button.getText().toString().equals(selectedAnswer)) return option3Button;
        if (option4Button.getText().toString().equals(selectedAnswer)) return option4Button;
        return null;
    }

    private void highlightCorrectAnswer(String correctAnswer) {
        Button correctButton = findSelectedButton(correctAnswer);
        if (correctButton != null) {
            correctButton.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
        }
    }

    private void animateButton(Button button, boolean isCorrect) {
        // Изменение цвета кнопки
        int colorRes = isCorrect ? R.color.green : R.color.red;
        button.setBackgroundColor(ContextCompat.getColor(this, colorRes));

        // Анимация масштабирования
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
    }
}