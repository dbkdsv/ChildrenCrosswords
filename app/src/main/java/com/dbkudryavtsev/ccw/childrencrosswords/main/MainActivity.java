package com.dbkudryavtsev.ccw.childrencrosswords.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.graphics.Canvas;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Paint.Style;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private static int word_height;
    private static int maxfill = 15;
    private int currentRect;
    private static int barpercentage=15;
    private static int maxwordlength=-1;

    crossword myc = new crossword();

    private Drawable checkButton;
    private Rect checkBounds;
    private Rect[] rects = new Rect[myc._cwords.length];
    private String[] answers = new String[myc._cwords.length];

    Paint backgroundPaint = new Paint();
    Paint rectPaint = new Paint();
    Paint linePaint = new Paint();
    Paint fontPaint = new Paint();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new PuzzleView(this));
        for (int i = 0; i < myc._cwords.length; i++) {
            answers[i] = "";
        }
    }

    public class PuzzleView extends View {

        public PuzzleView(Context context) {
            super(context);

            backgroundPaint.setColor(ContextCompat.getColor(getContext(), R.color.puzzle_background));
            backgroundPaint.setStyle(Style.FILL);
            rectPaint.setColor(ContextCompat.getColor(getContext(), R.color.puzzle_dark));
            rectPaint.setStyle(Style.STROKE);
            rectPaint.setStrokeWidth(5);
            linePaint.setColor(ContextCompat.getColor(getContext(), R.color.puzzle_dark));
            linePaint.setStrokeWidth(5);
            fontPaint.setColor(ContextCompat.getColor(getContext(), R.color.puzzle_dark));
            fontPaint.setTextSize(100);
            fontPaint.setStyle(Style.STROKE);

            checkButton = ContextCompat.getDrawable(getContext(), R.drawable.ic_done);

            for(int i=0; i<myc._hor_count; i++){
                if(myc._cwords[i]._word.length()+myc._cwords[i]._posX>maxwordlength)
                    maxwordlength=myc._cwords[i]._word.length()+myc._cwords[i]._posX;
            }
        }

        public boolean onTouchEvent(MotionEvent event) {
            Toast toast;
            Intent answer;
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                ArrayList<Integer> checked_rects = new ArrayList<>();
                for (int i = 0; i < myc._cwords.length; i++) {
                    if (rects[i].contains((int) event.getX(), (int) event.getY())) {
                        checked_rects.add(i);
                    }
                }
                if (checked_rects.size() == 1) {
                    currentRect = checked_rects.get(0);
                    answer = new Intent(MainActivity.this, AnswerActivity.class);
                    answer.putExtra("question", myc._cwords[currentRect]._question);
                    answer.putExtra("length", myc._cwords[currentRect]._word.length());
                    startActivityForResult(answer, 1);
                }
                else if (checkBounds.contains((int) event.getX(), (int) event.getY())){
                    /*Check all answers*/
                    boolean allright = true;
                    for(int i=0; i<myc._cwords.length;i++){
                        if(!answers[i].equals(myc._cwords[i]._word))
                            allright=false;
                    }
                    if(allright)
                        toast = Toast.makeText(getContext(), "Всё правильно!", Toast.LENGTH_LONG);
                    else
                        toast = Toast.makeText(getContext(), "Ищи ошибку!", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
            return super.onTouchEvent(event);
        }

        protected void onDraw(Canvas mycanvas) {
            /*<--------------------BACKGROUND-------------------->*/
            mycanvas.drawRect(0, 0, mycanvas.getWidth(), mycanvas.getHeight(), backgroundPaint);

            /*<--------------------BUTTONS-------------------->*/
            checkBounds = new Rect(5,5, (int) (getWidth()*.2),(int) ( getWidth()*.2));
            checkButton.setBounds(checkBounds);
            checkButton.draw(mycanvas);

            /*<--------------------CROSSWORD-------------------->*/
            //set box size
            word_height = (int)(mycanvas.getHeight() / maxfill)-10;
            //constant margin values for words
            int const_x, const_y;
            //draw crossword
            for (int i = 0; i < myc._cwords.length; i++) {
                //set constant margin
                const_x=myc._cwords[i]._posX* word_height+(getWidth()-maxwordlength*word_height)/2;
                const_y=myc._cwords[i]._posY * word_height + getHeight()*barpercentage/100;
                //horisontal words
                if (i < myc._hor_count) {
                    //set border
                    rects[i] = new Rect(const_x, const_y, const_x + myc._cwords[i]._word.length()* word_height, const_y + word_height);
                    //draw lines
                    for (int j = 1; j < myc._cwords[i]._word.length(); j++) {
                        mycanvas.drawLine(const_x + j * word_height, const_y, const_x + j * word_height, const_y + word_height, linePaint);
                    }
                    //draw answers
                    for (int j = 0; j < answers[i].length(); j++) {
                        mycanvas.drawText(Character.toString(answers[i].charAt(j)), const_x + j * word_height + word_height / 3,const_y + word_height * 2 / 3, fontPaint);
                    }
                }
                //vertical words
                else {
                    //set border
                    rects[i] = new Rect(const_x, const_y, const_x + word_height, const_y + word_height * myc._cwords[i]._word.length());
                    //draw lines
                    for (int j = 1; j < myc._cwords[i]._word.length(); j++) {
                        mycanvas.drawLine(const_x, const_y + j * word_height, const_x+ word_height, const_y + j * word_height, linePaint);
                    }
                    //clean up and draw answers
                    for (int j = 0; j < answers[i].length(); j++) {
                        mycanvas.drawRect(const_x + 5, word_height *j+ const_y + 5, const_x + word_height - 5, word_height *j+ const_y + word_height - 5, backgroundPaint);
                        mycanvas.drawText(Character.toString(answers[i].charAt(j)), const_x + word_height / 3, const_y + j * word_height + word_height * 2 / 3, fontPaint);
                    }
                }
                //draw border
                mycanvas.drawRect(rects[i], rectPaint);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == 1) {
                answers[currentRect] = data.getStringExtra("RESULT_STRING");
            }
        }
    }
}
