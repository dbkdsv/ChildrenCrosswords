package com.dbkudryavtsev.ccw.childrencrosswords.main.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dbkudryavtsev.ccw.childrencrosswords.main.R;

public class LaunchActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        Button startButton = (Button) findViewById(R.id.button);
        startButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), ChoiceActivity.class));
            }
        });
    }
}