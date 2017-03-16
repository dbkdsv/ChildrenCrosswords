package com.dbkudryavtsev.ccw.childrencrosswords.main.Activities;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

import com.dbkudryavtsev.ccw.childrencrosswords.main.Views.CrosswordView;

import java.io.UnsupportedEncodingException;

import static com.dbkudryavtsev.ccw.childrencrosswords.main.Views.ChoiceView.chosenRectString;

public class CrosswordActivity extends Activity {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        try {
            setContentView(new CrosswordView(this, extras.getInt(chosenRectString)));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}