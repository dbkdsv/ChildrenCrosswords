package com.dbkudryavtsev.ccw.childrencrosswords;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Paint.Style;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private static final int word_count = 4;
    private static final int hword_count = 2;
    private static int word_height;
    private Rect[] rects;
    Paint background = new Paint();
    Paint dark = new Paint();
    Paint mblack =new Paint();
    private Canvas mycanvas=new Canvas();

    private int currentRect;

    public class cwords {
        String _question;
        String _word;
        int _posX;
        int _posY;

        cwords() {
            _question = "";
            _word = "";
        }
    }

    public class crossword {
        cwords[] _cwords;
        int _hor_count;

        crossword() {
            _cwords = new cwords[word_count];
            for (int i = 0; i < word_count; i++)
                _cwords[i] = new cwords();
            _hor_count = hword_count;
            for (int i = 0; i < word_count; i++) {
                if (i == 0) {
                    _cwords[i]._word = "aaaaa";
                    _cwords[i]._question = "1111111111111111";

                    _cwords[i]._posX = 0;
                    _cwords[i]._posY = 0;
                }
                if (i == 1) {
                    _cwords[i]._word = "aaaa";
                    _cwords[i]._question = "22222222222222222";

                    _cwords[i]._posX = 0;
                    _cwords[i]._posY = 3;
                }
                if (i == 2) {
                    _cwords[i]._word = "aaaaaaa";
                    _cwords[i]._question = "33333333333333333333";

                    _cwords[i]._posX = 0;
                    _cwords[i]._posY = 0;
                }
                if (i == 3) {
                    _cwords[i]._word = "aaaaaaaaa";
                    _cwords[i]._question = "33333333333333333333";

                    _cwords[i]._posX = 3;
                    _cwords[i]._posY = 0;
                }
            }
        }
    }

    crossword myc = new crossword();

    private String[] answers=new String[myc._cwords.length];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new PuzzleView(this));

        for(int i=0; i<myc._cwords.length; i++){
            answers[i]=new String();
        }
    }

    public class PuzzleView extends View {

        public PuzzleView(Context context) { super(context); }

        public boolean onTouchEvent(MotionEvent event) {
            Intent answer;
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                ArrayList<Integer> checked_rects = new ArrayList<>();
                for (int i = 0; i < word_count; i++) {
                    if (rects[i].contains((int) event.getX(), (int) event.getY())) {
                        checked_rects.add(i);
                    }
                }
                if (checked_rects.size()==1) {
                    currentRect=checked_rects.get(0);
                    answer = new Intent(MainActivity.this, AnswerActivity.class);
                    answer.putExtra("question", myc._cwords[currentRect]._question);
                    answer.putExtra("length", myc._cwords[currentRect]._word.length());
                    startActivityForResult(answer, 1);
                }
            }
            return super.onTouchEvent(event);
        }



        protected void onDraw(Canvas mycanvas) {

            background.setColor(getResources().getColor(R.color.puzzle_background));
            background.setStyle(Style.FILL);
            dark.setColor(getResources().getColor(R.color.puzzle_dark));
            dark.setStyle(Style.STROKE);
            dark.setStrokeWidth(5);
            mblack.setColor(getResources().getColor(R.color.puzzle_dark));
            mblack.setStrokeWidth(5);
            mycanvas.drawRect(0, 0, getWidth(), getHeight(), background);
            Paint fontPaint = new Paint();
            fontPaint.setColor(getResources().getColor(R.color.puzzle_dark));
            fontPaint.setTextSize(100);
            fontPaint.setStyle(Style.STROKE);
            word_height = getHeight() / 10;
            rects = new Rect[word_count];
            for (int i = 0; i < word_count; i++) {
                if (i < hword_count) {
                    rects[i] = new Rect(myc._cwords[i]._posX * word_height, myc._cwords[i]._posY * word_height, word_height * (myc._cwords[i]._posX + myc._cwords[i]._word.length()), word_height * myc._cwords[i]._posY + word_height);
                    for (int j = 1; j < myc._cwords[i]._word.length(); j++) {
                        mycanvas.drawLine((myc._cwords[i]._posX + j) * word_height , myc._cwords[i]._posY * word_height, (myc._cwords[i]._posX + j) * word_height, word_height * myc._cwords[i]._posY + word_height, mblack);
                    }
                    for(int j=0; j<answers[i].length();j++){
                        mycanvas.drawText(Character.toString(answers[i].charAt(j)),(myc._cwords[i]._posX +j)* word_height+word_height/3, myc._cwords[i]._posY * word_height+word_height*2/3,fontPaint);
                    }
                }
                else {
                    rects[i] = new Rect(word_height * myc._cwords[i]._posX, word_height * myc._cwords[i]._posY, word_height * myc._cwords[i]._posX + word_height, word_height * myc._cwords[i]._posY + word_height * myc._cwords[i]._word.length());
                    for (int j = 1; j < myc._cwords[i]._word.length(); j++) {
                        mycanvas.drawLine(myc._cwords[i]._posX * word_height , (myc._cwords[i]._posY + j) * word_height, (myc._cwords[i]._posX+1) * word_height, (myc._cwords[i]._posY + j) * word_height, mblack);
                    }
                    for(int j=0; j<answers[i].length();j++){
                        mycanvas.drawRect(word_height * myc._cwords[i]._posX+5, word_height * (myc._cwords[i]._posY+j)+5, word_height * myc._cwords[i]._posX + word_height-5, word_height * (myc._cwords[i]._posY+j) + word_height-5 ,background);
                        mycanvas.drawText(Character.toString(answers[i].charAt(j)),myc._cwords[i]._posX* word_height+word_height/3, (myc._cwords[i]._posY +j) * word_height+word_height*2/3,fontPaint);
                    }
                }
                mycanvas.drawRect(rects[i], dark);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == 1) {
                answers[currentRect]=data.getStringExtra("RESULT_STRING");
                Log.d("checked", answers[currentRect]);
            }
        }
    }
}
