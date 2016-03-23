package com.flajeu.quizviews;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ListView;

import com.flajeu.quizapi.Quiz;
/**
 * Created by Fred on 3/21/16.
 */
public class QuizPageFragment extends Fragment{

    private WebView _webView;
    private ListView _itemListView;
    private Quiz.Page _page;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_quiz_page, container, false);

        _webView = (WebView)view.findViewById(R.id.webView);
        _itemListView = (ListView)view.findViewById(R.id.listView);

        refreshView(_page);

        return view;
    }

    public void refreshView(Quiz.Page page) {
        if (page != null) {
            _page = page;
            if (_webView != null){
                _webView.loadDataWithBaseURL("", page.getDescription(), "text/html", "UTF-8", "");
            }

            if (_itemListView != null) {
                ItemArrayAdapter adapter = new ItemArrayAdapter( getContext(), android.R.layout.simple_list_item_1, _page.getItems(), _page );
                _itemListView.setAdapter(adapter);
            }
        }
    }
}
