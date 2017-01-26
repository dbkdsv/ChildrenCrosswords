package com.dbkudryavtsev.ccw.childrencrosswords.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.graphics.Canvas;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Paint.Style;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.dbkudryavtsev.ccw.childrencrosswords.main.R.*;

public class MainActivity extends Activity {


    private final int MAX_FILL = 15;
    private final int BAR_PERCENTAGE =15;
    private int maxwordlength=-1;

    Crossword myc = new Crossword();
    Keyboard keyboard=new Keyboard();

    private Rect[] rects = new Rect[myc._cwords.length];
    private Rect[] KeyRects =new Rect[keyboard.russiankeys.length()];
    private String[] answers = new String[myc._cwords.length];

    Paint backgroundPaint = new Paint();
    Paint rectPaint = new Paint();
    Paint linePaint = new Paint();
    Paint fontPaint = new Paint();
    Paint smallFontPaint = new Paint();
    Paint whitePaint = new Paint();

    private ArrayList<Integer> questionsRemaining =new ArrayList<>(myc._cwords.length);
    private ArrayList<Integer> questionsOrder =new ArrayList<>(myc._cwords.length);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new PuzzleView(this));
        for (int i = 0; i < myc._cwords.length; i++) {
            answers[i] = "";
        }
    }

    public class PuzzleView extends View {

        private int word_height;

        private Drawable checkButton, listButton;
        private Rect checkBounds, listButtonBounds;
        Rect letterBounds=new Rect();
        String output;


        private void rectsSet(){
            int const_x, const_y;
            for (int i = 0; i < myc._cwords.length; i++) {
                //set constant margin
                const_x = myc._cwords[i]._posX * word_height + (getWidth() - maxwordlength * word_height) / 2;
                const_y = myc._cwords[i]._posY * word_height + getHeight() * BAR_PERCENTAGE / 100;
                //horisontal words
                if (i < myc._hor_count) {
                    //set border
                    rects[i] = new Rect(const_x, const_y,
                            const_x + myc._cwords[i]._word.length() * word_height, const_y + word_height);
                }
                //vertical words
                else {
                    //set border
                    rects[i] = new Rect(const_x, const_y,
                            const_x + word_height, const_y + word_height * myc._cwords[i]._word.length());
                }
            }
            checkBounds = new Rect(5,5, (int) (getWidth()*.2),(int) ( getWidth()*.2));
            listButtonBounds = new Rect((int) (getWidth()*.8),5, getWidth(),(int) ( getWidth()*.2));
        }

        void keyboardSet(){
//            int column_length=getWidth()/(keyboard.columns);
//            int current_row, current_column, x_coordinate, y_coordinate;
//            for(int i=0; i<keyboard.russiankeys.length(); i++){
//                current_row=i/keyboard.columns;
//                current_column=i%(keyboard.columns+1);
//                x_coordinate=current_column*column_length;
//                y_coordinate=getHeight()-(4-current_row)*column_length;
//                KeyRects[i]=new Rect(x_coordinate,y_coordinate,x_coordinate+column_length, y_coordinate+column_length);
//            }
        }

        public PuzzleView(Context context) {
            super(context);

            whitePaint.setColor(ContextCompat.getColor(getContext(), color.white));
            whitePaint.setStyle(Style.FILL);
            backgroundPaint.setColor(ContextCompat.getColor(getContext(), color.puzzle_background));
            backgroundPaint.setStyle(Style.FILL);
            rectPaint.setColor(ContextCompat.getColor(getContext(), color.puzzle_dark));
            rectPaint.setStyle(Style.STROKE);
            rectPaint.setStrokeWidth(5);
            linePaint.setColor(ContextCompat.getColor(getContext(), color.puzzle_dark));
            linePaint.setStrokeWidth(5);
            fontPaint.setColor(ContextCompat.getColor(getContext(), color.puzzle_dark));
            fontPaint.setStyle(Style.STROKE);
            smallFontPaint.setColor(ContextCompat.getColor(getContext(), color.puzzle_dark));
            smallFontPaint.setStyle(Style.STROKE);

            checkButton = ContextCompat.getDrawable(getContext(), drawable.ic_done);
            listButton = ContextCompat.getDrawable(getContext(), drawable.ic_list);

            for(int i=0; i<myc._hor_count; i++){
                if(myc._cwords[i]._word.length()+myc._cwords[i]._posX>maxwordlength)
                    maxwordlength=myc._cwords[i]._word.length()+myc._cwords[i]._posX;
            }

            for (int i=0; i<myc._cwords.length; i++){
                questionsRemaining.add(i);
                questionsOrder.add(i);
            }
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            word_height=  getHeight() / MAX_FILL -10;
            rectsSet(); //height is ready
            keyboardSet();
            fontPaint.setTextSize(word_height);
            smallFontPaint.setTextSize(word_height/4);

        }
        void check_answers(){
            Toast toast;
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

        void list_questions(){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Список всех вопросов:")
                    .setItems(myc.getAllQuestions(), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            inputAnswer(which);
                        }
                    });
            builder.show();
        }

        void inputAnswer(final int currentRect){
            final int word_length=myc._cwords[currentRect]._word.length();
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            TextView textView = new TextView(getContext());
            textView.setText("Вопрос:\n"+ myc._cwords[currentRect]._question);
            builder.setCustomTitle(textView);
            final EditText input = new EditText(getContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    output = input.getText().toString();
                    if (output.length() != word_length) {
                        Toast toast = Toast.makeText(getContext(), "Неправильная длина ответа", Toast.LENGTH_LONG);
                        toast.show();
                    } else {
                        questionsOrder.add(0, questionsOrder.remove(questionsOrder.indexOf(currentRect)));
                        answers[currentRect] = output.toUpperCase();
                        if(questionsRemaining.contains((currentRect))){
                            questionsRemaining.remove(questionsRemaining.indexOf(currentRect));
                        }
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                ArrayList<Integer> checked_rects = new ArrayList<>();
                for (int i = 0; i < myc._cwords.length; i++) {
                    if (rects[i].contains((int) event.getX(), (int) event.getY())) {
                        checked_rects.add(i);
                    }
                }
                if (checked_rects.size() == 1) {
                    inputAnswer(checked_rects.get(0));
                }
                else if (checkBounds.contains((int) event.getX(), (int) event.getY())){
                    /*Check all answers*/
                    check_answers();
                }
                else if (listButtonBounds.contains((int) event.getX(), (int) event.getY())){
                    /*List all questions*/
                    list_questions();
                }
            }
            return super.onTouchEvent(event);
        }

        protected void onDraw(Canvas mycanvas) {
            if(questionsRemaining.isEmpty()){
                check_answers();
            }
            /*<--------------------BACKGROUND-------------------->*/
            mycanvas.drawRect(0, 0, mycanvas.getWidth(), mycanvas.getHeight(), backgroundPaint);
            int horizontal_step,vertical_step;

            /*<--------------------BUTTONS-------------------->*/
            checkButton.setBounds(checkBounds);
            checkButton.draw(mycanvas);
            listButton.setBounds(listButtonBounds);
            listButton.draw(mycanvas);

            /*<--------------------CROSSWORD-------------------->*/
            //constant margin values for words
            int const_x, const_y;
            //draw crossword
            for (int orderNumber = questionsOrder.size()-1; orderNumber >= 0; orderNumber--) {
                int i=questionsOrder.get(orderNumber);
                mycanvas.drawRect(rects[i], whitePaint);
                //set constant margin

                const_x=myc._cwords[i]._posX* word_height+(getWidth()-maxwordlength*word_height)/2;
                const_y=myc._cwords[i]._posY * word_height + getHeight()* BAR_PERCENTAGE /100;
                //horisontal words
                if (i < myc._hor_count) {
                    //draw lines
                    for (int j = 1; j < myc._cwords[i]._word.length(); j++) {
                        mycanvas.drawLine(const_x + j * word_height, const_y,
                                const_x + j * word_height, const_y + word_height, linePaint);
                    }
                    //draw answers
                    for (int j = 0; j < answers[i].length(); j++) {
                        fontPaint.getTextBounds(answers[i],j,j+1,letterBounds);
                        horizontal_step=(int)(word_height-fontPaint.measureText(Character.toString(answers[i].charAt(j))))/2;
                        vertical_step=word_height-(word_height-letterBounds.height())/2;
                        mycanvas.drawText(Character.toString(answers[i].charAt(j)),
                                const_x + j * word_height + horizontal_step,const_y + vertical_step, fontPaint);
                    }

                }
                //vertical words
                else {
                    //draw lines
                    for (int j = 1; j < myc._cwords[i]._word.length(); j++) {
                        mycanvas.drawLine(const_x, const_y + j * word_height,
                                const_x+ word_height, const_y + j * word_height, linePaint);
                    }
                    //clean up and draw answers
                    for (int j = 0; j < answers[i].length(); j++) {
                        fontPaint.getTextBounds(answers[i],j,j+1,letterBounds);
                        horizontal_step=(int)(word_height-fontPaint.measureText(Character.toString(answers[i].charAt(j))))/2;
                        vertical_step=word_height-(word_height-letterBounds.height())/2;
                        mycanvas.drawText(Character.toString(answers[i].charAt(j)),
                                const_x + horizontal_step, const_y + j * word_height + vertical_step, fontPaint);
                    }

                }
                //draw border
                mycanvas.drawRect(rects[i], rectPaint);
                //draw keyboard
                //            int column_length=getWidth()/(keyboard.columns);
//            int current_row, current_column, x_coordinate, y_coordinate;
//            fontPaint.getTextBounds(keyboard.russiankeys,0,keyboard.russiankeys.length(),letterBounds);
//            for(int i=0; i<KeyRects.length; i++){
//                current_row=i/keyboard.columns;
//                current_column=i%(keyboard.columns+1);
//                x_coordinate=current_column*column_length;
//                y_coordinate=getHeight()-(4-current_row)*column_length;
//                horizontal_step=(int)(word_height-fontPaint.measureText(keyboard.russiankeys.substring(i,i)))/2;
//                vertical_step=word_height-(word_height-letterBounds.height())/2;
//                mycanvas.drawText(keyboard.russiankeys.substring(i,i+1),
//                        x_coordinate+horizontal_step,y_coordinate+vertical_step, fontPaint);
//            }
            }
            for (int i=0; i<myc._cwords.length; i++) {
                if (i < myc._hor_count) {
                    if(answers[i].length()==0){
                        String label = Integer.toString(i+1);
                        fontPaint.getTextBounds(label,0,label.length(),letterBounds);
                        mycanvas.drawText(label,
                                rects[i].left + smallFontPaint.measureText(Integer.toString(i+1))/2,rects[i].top + letterBounds.height()/3, smallFontPaint);
                    }
                }
                else {
                    if(answers[i].length()==0){
                        String label = Integer.toString(i+1);
                        fontPaint.getTextBounds(label,0,label.length(),letterBounds);
                        mycanvas.drawText(label,
                                rects[i].right - smallFontPaint.measureText(Integer.toString(i+1))*1.5f,rects[i].top + letterBounds.height()/3, smallFontPaint);
                    }
                }
            }
        }
    }
}
