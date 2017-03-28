package com.dbkudryavtsev.childrencrosswords.Activities;

import android.app.Activity;
import android.os.Bundle;

import com.dbkudryavtsev.childrencrosswords.Views.ChoiceView;

public class ChoiceActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new ChoiceView(this));
    }
}