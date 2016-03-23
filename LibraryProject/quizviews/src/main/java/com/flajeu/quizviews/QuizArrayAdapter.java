package com.flajeu.quizviews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.flajeu.quizapi.Quiz;

import java.util.ArrayList;

/**
 * Created by Fred on 3/20/16.
 */
public class QuizArrayAdapter extends ArrayAdapter<Quiz> {

    public QuizArrayAdapter(Context context, int layoutResourceId, ArrayList<Quiz> data) {
        super(context, layoutResourceId, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Quiz quiz = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        // Lookup view for data population
        TextView quizName = (TextView) convertView.findViewById(android.R.id.text1);

        // Populate the data into the template view using the data object
        quizName.setText(quiz.getName());

        // Return the completed view to render on screen
        return convertView;
    }
}
