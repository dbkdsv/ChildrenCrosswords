package com.dbkudryavtsev.ccw.childrencrosswords.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import static com.dbkudryavtsev.ccw.childrencrosswords.main.R.*;


public class AnswerActivity extends Activity {

    String question, output;
    Integer word_length;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent create_intent = getIntent();
        question = create_intent.getStringExtra("question");
        word_length = create_intent.getIntExtra("length", 0);
        setContentView(new AnswerView(this));
    }

    public class AnswerView extends View {
        Paint background = new Paint();
        Paint dark = new Paint();
        Paint fontPaint = new Paint();
        Paint black = new Paint();

        Integer word_height;

        public AnswerView(Context context) {
            super(context);
        }

        protected void onDraw(Canvas canvas) {
            word_height = getWidth() / (word_length + 2);

            background.setColor(ContextCompat.getColor(getContext(), color.puzzle_background));
            canvas.drawRect(0, 0, getWidth(), getHeight(), background);

            dark.setColor(ContextCompat.getColor(getContext(), color.puzzle_dark));
            dark.setStyle(Paint.Style.STROKE);
            dark.setStrokeWidth(5);

            fontPaint.setColor(ContextCompat.getColor(getContext(), color.puzzle_dark));
            fontPaint.setTextSize(100);
            fontPaint.setStyle(Paint.Style.STROKE);

            black.setColor(ContextCompat.getColor(getContext(), color.puzzle_dark));
            black.setStrokeWidth(5);

            canvas.drawText("Вопрос:", word_height, 3 * word_height, fontPaint);

            canvas.drawText(question, word_height, (float) 4 * word_height, fontPaint);

            canvas.drawRect(word_height, word_height, word_height * (word_length + 1), 2 * word_height, dark);
            for (int j = 2; j <= word_length; j++) {
                canvas.drawLine(j * word_height, word_height, j * word_height, 2 * word_height, black);
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
                    if (output.length() != word_length) {
                        Toast toast = Toast.makeText(getContext(), "Неправильная длина ответа", Toast.LENGTH_LONG);
                        toast.show();
                    } else {
                        Intent intent = new Intent(AnswerActivity.this, MainActivity.class);
                        intent.putExtra("RESULT_STRING", output);
                        setResult(1, intent);
                        finish();
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
            return super.onTouchEvent(event);
        }
    }
}
