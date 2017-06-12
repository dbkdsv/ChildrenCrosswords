package com.dbkudryavtsev.childrencrosswords.activities;

import android.app.Activity;
import android.os.Bundle;

import com.dbkudryavtsev.childrencrosswords.R;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle(getString(R.string.about));
    }
}