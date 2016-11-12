package com.dbkudryavtsev.ccw.childrencrosswords;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final int MILLIS_PER_SECOND = 1000;
    private static final int SECONDS_TO_COUNTDOWN = 30;
    private TextView countdownDisplay;
    private CountDownTimer timer;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countdownDisplay = (TextView) findViewById(R.id.time_display_box);
        Button startButton = (Button) findViewById(R.id.startbutton);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    showTimer(SECONDS_TO_COUNTDOWN * MILLIS_PER_SECOND);
                } catch (NumberFormatException e) {
                    // method ignores invalid (non-integer) input and waits
                    // for something it can use
                }
            }

            private void showTimer(int countdownMillis) {
                if(timer != null) { timer.cancel(); }
                timer = new CountDownTimer(countdownMillis, MILLIS_PER_SECOND) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        countdownDisplay.setText("counting down: " + millisUntilFinished / MILLIS_PER_SECOND);
                    }
                    @Override
                    public void onFinish() {
                        countdownDisplay.setText("KABOOM!");
                    }
                }.start();
            }
        });
    }
}
