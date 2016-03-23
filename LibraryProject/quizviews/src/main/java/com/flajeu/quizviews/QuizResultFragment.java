package com.flajeu.quizviews;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.flajeu.quizapi.Quiz;
import com.flajeu.quizapi.QuizAPI;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by Fred on 3/21/16.
 */
public class QuizResultFragment extends Fragment implements QuizAPI.QuizAPIDelegate {

    ListView _resultlistView;
    Quiz _quiz;

    public void loadResult(Quiz quiz) {
        _quiz = quiz;
        HashMap<String, Boolean> answers = new HashMap<String, Boolean>();
        for (Quiz.Page page : _quiz.getPages()) {
            for (Quiz.Page.Item item : page.getItems()) {
                answers.put(item.getItemId(), item.getSelected());
            }
        }
        
        QuizAPI.getInstance().addDelegate(this);
        QuizAPI.getInstance().validateQuiz(_quiz.getQuizId(), answers);
    }

    public void refreshView(Quiz quiz) {
        _quiz = quiz;
        if (_resultlistView != null && _quiz != null) {
            ArrayList<String> txtList = new ArrayList<String>();
            int qIndex = 0;
            ArrayList<Quiz.Page> pages = _quiz.getPages();
            if (pages != null) {
                for(Quiz.Page page : pages) {
                    if (!page.getPageType().equals(Quiz.PageType.CHAPTER))
                    {
                        String txtResult = "Fail";
                        if (page.getPass()) {txtResult = "Pass";}
                        txtList.add(" - Question " + qIndex + " : " + txtResult );
                        qIndex ++;
                    }
                }
            }
            _resultlistView.setAdapter(new ArrayAdapter<String> (getContext(), android.R.layout.simple_list_item_1, txtList));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_result, container, false);
        _resultlistView = (ListView)view.findViewById(R.id.resultListView);
        refreshView(_quiz);
        return view;
    }

    public void onDataLoded(Boolean success, Exception error) {
        QuizAPI.getInstance().removeDelegate(this);
        int qIndex = 1;
        refreshView(_quiz);
    }
}
