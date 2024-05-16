package com.example.diplomeng;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    private TextView questionTextView;
    private Button trueButton;
    private Button falseButton;

    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;
    private int totalQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        questionTextView = findViewById(R.id.question_text_view);
        trueButton = findViewById(R.id.true_button);
        falseButton = findViewById(R.id.false_button);

        // Загружаем вопросы из JSON файла
        loadQuestionsFromJSON();

        // Показываем первый вопрос
        showQuestion();

        trueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        falseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });
    }

    private void loadQuestionsFromJSON() {
        questionList = new ArrayList<>();

        try {
            // Получаем AssetManager для доступа к файлу JSON
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open("questions.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String json = new String(buffer, "UTF-8");

            // Парсим JSON и добавляем вопросы в список
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("questions");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject questionObject = jsonArray.getJSONObject(i);
                String question = questionObject.getString("question");
                boolean answer = questionObject.getBoolean("answer");
                questionList.add(new Question(question, answer));
            }
            totalQuestions = questionList.size();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void showQuestion() {
        if (currentQuestionIndex < totalQuestions) {
            Question currentQuestion = questionList.get(currentQuestionIndex);
            questionTextView.setText(currentQuestion.getQuestion());
        } else {
            // Если все вопросы отвечены, показываем результаты
            showResults();
        }
    }

    private void checkAnswer(boolean userAnswer) {
        Question currentQuestion = questionList.get(currentQuestionIndex);
        boolean correctAnswer = currentQuestion.isAnswer();
        if (userAnswer == correctAnswer) {
            correctAnswers++;
            Toast.makeText(this, "Верно!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Неверно!", Toast.LENGTH_SHORT).show();
        }
        // Переходим к следующему вопросу
        currentQuestionIndex++;
        showQuestion();
    }

    private void showResults() {
        // Показываем результаты в виде Toast
        double score = (double) correctAnswers / totalQuestions * 100;
        String resultMessage = "Правильных ответов: " + correctAnswers + " из " + totalQuestions + "\n";
        resultMessage += "Процент правильных ответов: " + String.format("%.2f", score) + "%";
        Toast.makeText(this, resultMessage, Toast.LENGTH_LONG).show();
        // Завершаем активити после показа результатов
        finish();
    }

    // Модель для вопроса
    private static class Question {
        private String question;
        private boolean answer;

        public Question(String question, boolean answer) {
            this.question = question;
            this.answer = answer;
        }

        public String getQuestion() {
            return question;
        }

        public boolean isAnswer() {
            return answer;
        }
    }
}


