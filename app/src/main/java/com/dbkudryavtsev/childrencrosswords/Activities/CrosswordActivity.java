package com.dbkudryavtsev.childrencrosswords.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.dbkudryavtsev.childrencrosswords.R;
import com.dbkudryavtsev.childrencrosswords.Views.CrosswordView;

import static com.dbkudryavtsev.childrencrosswords.Views.ChoiceView.chosenRectString;

public class CrosswordActivity extends AppCompatActivity {

    private CrosswordView crosswordView;
    int chosenRectId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        chosenRectId = extras.getInt(chosenRectString);
        setContentView(R.layout.crossword_view_layout);
        crosswordView = (CrosswordView) findViewById(R.id.crossword_view);
        crosswordView.setValues(chosenRectId);
        setTitle("Уровень "+ Integer.toString(chosenRectId+1));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.crossword_view_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.get_all_questions:
                crosswordView.listQuestions();
                return true;
            case R.id.check:
                crosswordView.checkAnswers();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}