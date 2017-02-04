package com.dbkudryavtsev.ccw.childrencrosswords.main;

import android.app.Activity;
import android.os.Bundle;

public class CrosswordActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        setContentView(new CrosswordView(this, extras.getInt("chosenRect")));
    }
}