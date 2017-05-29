package com.dbkudryavtsev.childrencrosswords.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.dbkudryavtsev.childrencrosswords.R;
import com.dbkudryavtsev.childrencrosswords.Views.CrosswordView;

import static com.dbkudryavtsev.childrencrosswords.Views.LevelFragment.chosenRectString;

public class CrosswordActivity extends AppCompatActivity {

    private CrosswordView crosswordView;
    private int chosenRectId;
    private FloatingActionButton checkButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        chosenRectId = extras.getInt(chosenRectString);
        setContentView(R.layout.activity_view);
        crosswordView = (CrosswordView) findViewById(R.id.crossword_view);
        crosswordView.setValues(chosenRectId);
        setTitle("Уровень "+ Integer.toString(chosenRectId+1));
        checkButton = (FloatingActionButton) findViewById(R.id.check_button);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crosswordView.checkAnswers();
            }
        });
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
        }
        return super.onOptionsItemSelected(item);
    }
}