package com.flajeu.quizviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.flajeu.quizapi.Quiz;
import com.flajeu.quizapi.QuizAPI;

/**
 * Created by Fred on 3/20/16.
 */
public class QuizView extends LinearLayout {

    //private WebView _webView;
    //private ListView _itemListView;
    private Button _btnNext;
    private Quiz _quiz;
    private QuizViewDelegate _delegate;
    private QuizPageFragment _quizPageFragment;
    private QuizResultFragment _quizResultFragment;
    private int _state = 0;

    public interface QuizViewDelegate {
        public void onQuizDone();
    }

    public QuizView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.quiz_view_layout, this, true);

        _quizPageFragment = new QuizPageFragment();
        addFragment(_quizPageFragment);

        final QuizView qv = this;
        _btnNext = (Button)findViewById(R.id.btnNext);
        _btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                _quiz.currentPageIndex ++;
                if (getCurrentPage() == null) {
                    if (_state == 0) {
                        _state = 1;
                    }
                    else if (_state == 1 && _delegate != null) {
                        _delegate.onQuizDone();
                    }
                }
                refreshView();
            }
        });

        refreshView();
    }

    private void addFragment(Fragment fragment) {

        FragmentManager fragmentManager = null;
        try {
            fragmentManager = ((FragmentActivity)getContext()).getSupportFragmentManager();
        } catch (ClassCastException e) {
            Log.e("QuizView", "Can't get fragment manager");
        }

        if (fragmentManager != null)
        {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        }

    }

    private void removeFragment(Fragment fragment) {
        FragmentManager fragmentManager = null;
        try {
            fragmentManager = ((FragmentActivity)getContext()).getSupportFragmentManager();
        } catch (ClassCastException e) {
            Log.e("QuizView", "Can't get fragment manager");
        }
        if (fragmentManager != null)
        {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
        }
    }

    private void refreshView() {
        if (_state == 0) {
            if (_quizPageFragment != null) {
                _quizPageFragment.refreshView(getCurrentPage());
            }
            if (this._btnNext != null) {
                this._btnNext.setText("NEXT");
            }
        }
        else if (_state == 1) {
            if (_quizPageFragment != null) {
                removeFragment(_quizPageFragment);
                _quizPageFragment = null;
            }
            if (_quizResultFragment == null) {
                _quizResultFragment = new QuizResultFragment();
                addFragment(_quizResultFragment);
                _quizResultFragment.loadResult(_quiz);
            }
            if (this._btnNext != null) {
                this._btnNext.setText("DONE");
            }
        }
    }

    public void setDelegate(QuizViewDelegate delegate) {
        _delegate = delegate;
    }

    public void setQuiz(Quiz quiz) {
        _quiz = quiz;
        refreshView();
    }

    public Quiz.Page getCurrentPage() {
        Quiz.Page page = null;
        if (_quiz != null) {
            page = _quiz.getPageAtIndex(_quiz.currentPageIndex);
        }
        return page;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putInt("state", _state);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) // implicit null check
        {
            Bundle bundle = (Bundle) state;
            _state = bundle.getInt("state");
            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
        refreshView();
    }
}
