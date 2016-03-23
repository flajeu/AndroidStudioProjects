package com.flajeu.quizapi;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Fred on 3/20/16.
 */
public class QuizAPI {

    //***********************************
    // Singleton
    //***********************************
    private static QuizAPI ourInstance = new QuizAPI();
    public static QuizAPI getInstance() {
        return ourInstance;
    }
    //***********************************

    private String _url;
    private List<QuizAPIDelegate> _delegates = new ArrayList<QuizAPIDelegate>();
    private ArrayList<Quiz> _quizzes = new ArrayList<Quiz>();
    private Boolean _isLoaded = false;

    private Quiz _quiztoValidate = null;
    private HashMap<String, Boolean> _answers;

    public interface QuizAPIDelegate {
        public void onDataLoded(Boolean success, Exception error);
    }

    private QuizAPI() {

    }

    public void loadQuizzed(String url) {
        this._url = url;
        loadQuizzed();
    }

    public void loadQuizzed() {
        _isLoaded = false;
        new getData().execute(_url, "quizzes");
    }

    public void validateQuiz(String quizId, HashMap<String, Boolean> answers) {
        _quiztoValidate = getQuizById(quizId);
        _answers = answers;

        if (_quiztoValidate != null) {
            new getData().execute(_url + "/" + quizId + "/validate", "validation");
        }
        else {
            dispatchOnDataLoded(false, new Exception("invalide quiz Id"));
        }
    }

    //***********************************
    // Gettwer Setters
    //***********************************
    public String getUrl(){ return _url; }
    public void setUrl(String value){ _url = value; }

    public Boolean getIsLoaded() { return _isLoaded; }

    public ArrayList<Quiz> getQuizzes() { return _quizzes; }

    public Quiz getQuizById(String quizId){
        Quiz result = null;
        for (int i = 0; i < _quizzes.size(); i++) {
            Quiz quiz = _quizzes.get(i);
            if (quiz.getQuizId().equals(quizId)) {
                result = quiz;
                break;
            }
        }
        return result;
    }

    //***********************************
    // Delegates
    //***********************************

    public void addDelegate(QuizAPIDelegate delegate) {
        if (!_delegates.contains(delegate)) {
            _delegates.add(delegate);
        }
    }

    public void removeDelegate(QuizAPIDelegate delegate) {
        if (_delegates.contains(delegate)) {
            _delegates.remove(delegate);
        }
    }
    
    private void dispatchOnDataLoded(Boolean success, Exception error) {
        for (int i = 0; i < _delegates.size(); i++) {
            ((QuizAPIDelegate) _delegates.get(i)).onDataLoded(success, error);
        }
    }

    //***********************************
    // getData
    //***********************************

    public class getData extends AsyncTask<String, String, String> {

        HttpURLConnection _urlConnection;

        @Override
        protected String doInBackground(String... params) {
            String urlString=params[0];
            String type=params[1];

            StringBuilder result = new StringBuilder();

            try {
                if (type.equals("validation")) {
                    String jsting = new JSONObject(_answers).toString();
                    urlString += "?answers="+jsting;
                }
                URL url = new URL(urlString);
                _urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = new BufferedInputStream(_urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

            }catch( Exception e) {
                e.printStackTrace();
            }
            finally {
                _urlConnection.disconnect();
            }

            return result.toString();

        }

        @Override
        protected void onPostExecute(String result) {
            Boolean succes = true;
            Exception error = null;

            try  {
                JSONObject jObject = new JSONObject(result);

                String responseType = jObject.getString("dataType");

                // ** parse quizzes response
                if (responseType.equals("quizzes"))
                {
                    JSONArray jArray = jObject.getJSONArray("quizzes");

                    for (int i=0; i < jArray.length(); i++)
                    {
                        try {
                            JSONObject jQuiz = jArray.getJSONObject(i);
                            Quiz quiz = new Quiz();
                            quiz.parse(jQuiz);
                            _quizzes.add(quiz);

                            succes = true;
                        } catch (JSONException e) {
                            succes = false;
                            error = e;
                            //e.printStackTrace();
                        }
                    }
                }

                else if (responseType.equals("validation")) {
                    JSONObject jPages = jObject.getJSONObject("pages");

                    Iterator<?> keys = jPages.keys();
                    while( keys.hasNext() ){
                        String key = (String)keys.next();
                        Boolean value = jPages.getBoolean(key);
                        _quiztoValidate.getPageById(key).setPass(value);
                    }
                    succes = true;
                }
                else {
                    succes = false;
                    error = new Exception("invalide dataType reponse : " + responseType);
                }

            }catch ( Exception e ){
                succes = false;
                error = e;
                //e.printStackTrace();
            }

            _isLoaded = succes;
            dispatchOnDataLoded(succes, error);
        }
    }

}
