package com.flajeu.quizviews;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.flajeu.quizapi.Quiz;

import java.util.ArrayList;

/**
 * Created by Fred on 3/20/16.
 */
public class ItemArrayAdapter extends ArrayAdapter<Quiz.Page.Item> {

    private Quiz.Page _page = null;

    private int selectedPosition = 0;

    public ItemArrayAdapter(Context context, int layoutResourceId, ArrayList<Quiz.Page.Item> data, Quiz.Page page) {
        super(context, layoutResourceId, data);
        _page = page;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        final Quiz.Page.Item item = getItem(position);


        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.checkbox_row_layout, parent, false);

            final CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.checkBox);
            checkBox.setText(item.getText());

            checkBox.setChecked(item.getSelected());
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.setSelected(checkBox.isChecked());
                }
            });
        }

        // Return the completed view to render on screen
        return convertView;
    }


}