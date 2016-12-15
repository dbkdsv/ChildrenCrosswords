package com.dbkudryavtsev.ccw.childrencrosswords;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

public class AnswerActivity extends Activity {

    String question, output;
    Integer word_length;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent create_intent=getIntent();
        question = create_intent.getStringExtra("question");
        word_length = create_intent.getIntExtra("length", 0);
        setContentView(new AnswerView(this));
    }

    public class AnswerView extends View{

        Integer word_height;

        public AnswerView(Context context){ super(context); }

        protected void onDraw(Canvas canvas){
            word_height = getWidth() / (word_length+2);
            Paint background = new Paint();
            background.setColor(getResources().getColor(R.color.puzzle_background));
            canvas.drawRect(0,0, getWidth(), getHeight(), background);
            Paint dark = new Paint();
            dark.setColor(getResources().getColor(R.color.puzzle_dark));
            dark.setStyle(Paint.Style.STROKE);
            dark.setStrokeWidth(5);

            Paint fontPaint = new Paint();
            fontPaint.setColor(getResources().getColor(R.color.puzzle_dark));
            fontPaint.setTextSize(100);
            fontPaint.setStyle(Paint.Style.STROKE);

            Paint black =new Paint();
            black.setColor(getResources().getColor(R.color.puzzle_dark));
            black.setStrokeWidth(5);

            canvas.drawText("Вопрос:", word_height, 3 * word_height, fontPaint);

            canvas.drawText(question, word_height, (float) 4*word_height, fontPaint);

            canvas.drawRect(word_height,word_height, word_height*(word_length+1), 2*word_height, dark);
            for (int j = 2; j <= word_length; j++) {
                canvas.drawLine(j * word_height , word_height, j * word_height, 2*word_height, black);
            }
        }

        public boolean onTouchEvent(MotionEvent event) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Введите ответ");
            final EditText input = new EditText(getContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    output = input.getText().toString();
                    Intent intent = new Intent(AnswerActivity.this, MainActivity.class);
                    intent.putExtra("RESULT_STRING", output);
                    setResult(1, intent);
                    finish();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
            return super.onTouchEvent(event);
        }
        }

}
