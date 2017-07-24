package com.dbkudryavtsev.childrencrosswords.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dbkudryavtsev.childrencrosswords.R;
import com.dbkudryavtsev.childrencrosswords.models.Crossword;
import com.dbkudryavtsev.childrencrosswords.utilities.LocalCrosswordsRepository;
import com.dbkudryavtsev.childrencrosswords.utilities.LocalCrosswordsRepositoryProvider;
import com.dbkudryavtsev.childrencrosswords.views.CrosswordView;

import java.util.Locale;

public final class CrosswordActivity extends AppCompatActivity {

    private CrosswordView crosswordView;
    private int chosenCrosswordId;
    private LocalCrosswordsRepository repository;
    private EditText input;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        LocalCrosswordsRepositoryProvider provider = (LocalCrosswordsRepositoryProvider) getApplication();
        repository = provider.getLocalCrosswordsRepository();

        Bundle extras = getIntent().getExtras();
        chosenCrosswordId = extras.getInt(CrosswordActivity.this.getString(R.string.chosen_crossword_string));
        Crossword chosenCrossword = repository.getCrossword(chosenCrosswordId, CrosswordActivity.this);
        String[] answers = repository.getAnswers(chosenCrosswordId, CrosswordActivity.this);
        crosswordView = (CrosswordView) findViewById(R.id.crossword_view);
        crosswordView.setValues(chosenCrossword, answers, chosenCrosswordId);
        setTitle("Уровень "+ Integer.toString(chosenCrosswordId +1));
        FloatingActionButton checkButton = (FloatingActionButton) findViewById(R.id.check_button);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crosswordView.checkAnswers();
            }
        });
        Button returnButton = (Button) findViewById(R.id.return_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrosswordActivity.this.finish();
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
    protected void onPause() {
        super.onPause();
        String[] finalAnswers = crosswordView.getCurrentAnswers();
        crosswordView.onDestroy();
        repository.putAnswers(finalAnswers, chosenCrosswordId, CrosswordActivity.this);
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (crosswordView.viewTouched(event)) {
            input.setText("");
            input.requestFocus();
            crosswordView.invalidate();
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(crosswordView.keyIsDown(event)) {
            return true;
        }
        else if(keyCode==KeyEvent.KEYCODE_DEL && event.getAction()==KeyEvent.ACTION_DOWN){
            String text = input.getText().toString();
            input.setText(text.substring(0, text.length() - 1));
            input.setSelection(input.length());
            crosswordView.invalidate();
            return true;
        }
        else return super.onKeyDown(keyCode, event);
    }
}