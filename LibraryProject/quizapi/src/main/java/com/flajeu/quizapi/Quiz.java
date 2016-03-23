package com.flajeu.quizapi;

/**
 * Created by Fred on 3/17/16.
 */

import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class Quiz {

    private String _quizId;
    private String _name;
    private String _description;
    private ArrayList<Page> _pages = new ArrayList<Page>();
    public int currentPageIndex = 0;

    public enum PageType {
        CHAPTER(0), SINGLE(1), MULTIPLE(2);

        private final int id;
        PageType(int id) { this.id = id; }
        public int getValue() { return id; }

        public static PageType fromValue(int id){
            PageType result = null;
            for (PageType pt : values() ) {
                if (pt.getValue() == id ) {
                    result = pt;
                }
            }
            return result;
        }
    }

    public Quiz() {

    }

    public String getQuizId() { return _quizId; }

    public String getName() { return _name; }

    public void resetQuiz() {
        currentPageIndex = 0;
        for (int i = 0; i < _pages.size(); i++) {
            Page page = _pages.get(i);
            page.resetPage();
        }
    }

    public String getDescription()
    {
        return _description;
    }

    public Page getPageAtIndex(int index)
    {
        if(index < _pages.size()   )
        {
            return _pages.get(index);
        }
        return null;
    }

    public Page getPageById(String pageId) {
        Page page = null;

        for (int i = 0; i < _pages.size(); i++) {
            Page p = _pages.get(i);
            if (p.getPageId().equals(pageId)) {
                page = p;
                break;
            }
        }

        return page;
    }

    public ArrayList<Page> getPages() {
        return _pages;
    }

    protected void parse(JSONObject jObject)
    {
        try{
            _quizId = jObject.getString("quizId");
            _name = jObject.getString("name");
            _description = jObject.getString("description");

            JSONArray jPages = jObject.getJSONArray("pages");
            for (int i = 0; i < jPages.length(); i++) {
                Page page = new Page();
                page.parse(jPages.getJSONObject(i));
                _pages.add(page);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public class Page
    {
        private String _pageId;
        private int _pageNumeber = 0;
        private String _title;
        private String _description;
        private ArrayList<Item> _items = new ArrayList<Item>();
        private PageType _pageType = PageType.CHAPTER;
        private Boolean _pass = false;

        public void parse(JSONObject jObject) {
            try
            {
                _pageId = jObject.getString("pageId");
                _pageNumeber = jObject.getInt("pageNumber");
                _title = jObject.getString("title");
                _description = jObject.getString("description");
                _pageType = PageType.fromValue(jObject.getInt("pageType"));

                JSONArray jItems = jObject.getJSONArray("items");
                for (int i = 0; i < jItems.length(); i++) {
                    Item item = new Item();
                    item.parse(jItems.getJSONObject(i));
                    _items.add(item);
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        public String getPageId() { return _pageId; }

        public String getDescription(){ return _description; }

        public ArrayList<Item> getItems(){
            return _items;
        }

        public Item getItemById(String itemId) {
            Item item = null;
            for (int i = 0; i < _items.size(); i++) {
                Item it = _items.get(i);
                if (it.getItemId().equals(itemId)) {
                    item = it;
                    break;
                }
            }
            return item;
        }

        public PageType getPageType() { return _pageType; }

        public Boolean getPass() { return _pass; }
        public void setPass(Boolean value){ _pass = value; }

        public void resetPage() {
            for (int i = 0; i < _items.size(); i++) {
                Item item = _items.get(i);
                item.setSelected(false);
            }
        }

        public class Item
        {
            private String _itemId;
            private int _itemOrder;
            private String _text;
            private Boolean _selected = false;

            public void parse(JSONObject jObject)
            {
                try
                {
                    _itemId = jObject.getString("itemId");
                    _itemOrder = jObject.getInt("itemOrder");
                    _text = jObject.getString("itemText");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            public String getItemId(){ return _itemId; }
            public int getItemOrder(){ return _itemOrder; }
            public String getText(){ return _text; }

            public Boolean getSelected() { return _selected; }
            public void setSelected(Boolean value){ _selected = value; }

        }
    }



}



