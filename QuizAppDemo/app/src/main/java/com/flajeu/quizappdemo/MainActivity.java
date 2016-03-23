package com.flajeu.quizappdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import com.flajeu.quizapi.Quiz;
import com.flajeu.quizapi.QuizAPI;
import com.flajeu.quizviews.QuizArrayAdapter;

public class MainActivity extends AppCompatActivity implements QuizAPI.QuizAPIDelegate{

    private static final int SETTINGS_RESULT = 1;
    private ListView _quizzesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _quizzesListView = (ListView)findViewById(R.id.quizzesListView);

        _quizzesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Quiz quiz = (Quiz) _quizzesListView.getItemAtPosition(position);
                quiz.resetQuiz();

                Intent intent = new Intent(getApplicationContext(), QuizActivity.class);
                intent.putExtra("com.flajeu.quizrestapi.QuizId", quiz.getQuizId());
                startActivityForResult(intent, SETTINGS_RESULT);
            }
        });

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String url = sharedPrefs.getString("quizURL", "http://quizdemo.site/api/index.php/quizzes");

        if (!QuizAPI.getInstance().getIsLoaded())
        {
            QuizAPI.getInstance().addDelegate(this);
            QuizAPI.getInstance().loadQuizzed(url);
        }
        else
        {
            refreshListView();
        }
    }

    public void refreshListView()
    {
        if (_quizzesListView != null) {
            QuizArrayAdapter adapter = new QuizArrayAdapter(this, android.R.layout.simple_list_item_1, QuizAPI.getInstance().getQuizzes() );
            _quizzesListView.setAdapter(adapter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==SETTINGS_RESULT)
        {

        }

    }

    //***********************************
    // QuizAPIDelegate implementation
    //***********************************
    public void onDataLoded(Boolean success, Exception error) {

        QuizAPI.getInstance().removeDelegate(this);
        if (success)
        {
            refreshListView();
        }
        else {
            error.printStackTrace();
        }
    }

    //***********************************
    // Menu Options
    //***********************************

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
