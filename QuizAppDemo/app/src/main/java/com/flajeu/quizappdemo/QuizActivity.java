package com.flajeu.quizappdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.flajeu.quizapi.Quiz;
import com.flajeu.quizapi.QuizAPI;
import com.flajeu.quizviews.QuizView;

import java.util.HashMap;
import java.util.Objects;

public class QuizActivity extends AppCompatActivity implements QuizView.QuizViewDelegate{

    private String _quizId;
    private Quiz _quiz;
    private QuizView _quizView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Bundle bundle = getIntent().getExtras();
        if(bundle.getString("com.flajeu.quizrestapi.QuizId") != null)
        {
            _quizId= bundle.getString("com.flajeu.quizrestapi.QuizId");
            _quiz = QuizAPI.getInstance().getQuizById(_quizId);
        }

        if (_quiz != null) {
            setTitle(_quiz.getName());
        }
        _quizView = (QuizView)findViewById(R.id.quiz_view);
        _quizView.setDelegate(this);
        _quizView.setQuiz(_quiz);
    }

    public void onQuizDone()
    {
        finish();
    }

    // Disable back button
    @Override
    public void onBackPressed(){}
}
