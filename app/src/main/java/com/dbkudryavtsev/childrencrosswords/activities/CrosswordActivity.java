package com.dbkudryavtsev.childrencrosswords.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.dbkudryavtsev.childrencrosswords.R;
import com.dbkudryavtsev.childrencrosswords.views.CrosswordView;

import static com.dbkudryavtsev.childrencrosswords.views.LevelFragment.chosenCrosswordString;

public final class CrosswordActivity extends AppCompatActivity {

    private CrosswordView crosswordView;
    public EditText input;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        int chosenCrosswordId = extras.getInt(chosenCrosswordString);
        setContentView(R.layout.activity_view);
        crosswordView = (CrosswordView) findViewById(R.id.crossword_view);
        crosswordView.setValues(chosenCrosswordId);
        setTitle("Уровень "+ Integer.toString(chosenCrosswordId +1));
        FloatingActionButton checkButton = (FloatingActionButton) findViewById(R.id.check_button);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crosswordView.checkAnswers();
            }
        });
        input = (EditText) findViewById(R.id.input);
        input.setFocusableInTouchMode(true);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = crosswordView.onTextChange(input.getText().toString());
                if (i==0) {
                    input.setText("");
                    input.clearFocus();
                } else  if(i==1){
                    String text = input.getText().toString();
                    input.setText(text.substring(0, text.length() - 1));
                    input.setSelection(input.length());
                    crosswordView.invalidate();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.crossword_view_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void some(){}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return crosswordView.keyIsDown(event);
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