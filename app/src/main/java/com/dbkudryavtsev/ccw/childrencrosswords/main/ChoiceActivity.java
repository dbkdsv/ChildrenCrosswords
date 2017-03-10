package com.dbkudryavtsev.ccw.childrencrosswords.main;

import android.app.Activity;
import android.os.Bundle;

public class ChoiceActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new ChoiceView(this));
    }
}