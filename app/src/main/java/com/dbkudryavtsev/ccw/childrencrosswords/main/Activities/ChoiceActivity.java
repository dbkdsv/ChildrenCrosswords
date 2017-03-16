package com.dbkudryavtsev.ccw.childrencrosswords.main.Activities;

import android.app.Activity;
import android.os.Bundle;

import com.dbkudryavtsev.ccw.childrencrosswords.main.Views.ChoiceView;

public class ChoiceActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new ChoiceView(this));
    }
}