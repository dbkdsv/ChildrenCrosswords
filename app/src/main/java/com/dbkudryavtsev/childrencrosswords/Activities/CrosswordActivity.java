package com.dbkudryavtsev.childrencrosswords.Activities;

import android.app.Activity;
import android.os.Bundle;

import com.dbkudryavtsev.childrencrosswords.R;
import com.dbkudryavtsev.childrencrosswords.Views.CrosswordView;

import static com.dbkudryavtsev.childrencrosswords.Views.ChoiceView.chosenRectString;

public class CrosswordActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        setContentView(R.layout.crossword_view_layout);
        CrosswordView crosswordView = (CrosswordView) findViewById(R.id.crossword_view);
        crosswordView.setValues(extras.getInt(chosenRectString));
    }
}