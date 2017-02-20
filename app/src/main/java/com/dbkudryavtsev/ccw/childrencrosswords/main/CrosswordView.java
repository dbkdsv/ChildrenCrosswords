package com.dbkudryavtsev.ccw.childrencrosswords.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class CrosswordView extends View {

    private final int BAR_PERCENTAGE = 15;

    private int maxWordLength = -1;

    private Paint backgroundPaint = new Paint();
    private Paint rectPaint = new Paint();
    private Paint linePaint = new Paint();
    private Paint fontPaint = new Paint();
    private Paint smallFontPaint = new Paint();
    private Paint whitePaint = new Paint();

    public String loadJSONFromAsset(int chosenRectId) {
        String json;
        try {
            InputStream inputStream = getResources().openRawResource(chosenRectId);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private Crossword crossword;
    private String[] answers;

    private ArrayList<Integer> questionsRemaining;
    private ArrayList<Integer> questionsOrder;

    public CrosswordView(Context context) {
        super(context);
    }

    private Drawable checkButton, listButton;

    public CrosswordView(Context context, int chosenRectId) {
        super(context);
        crossword = new Crossword(loadJSONFromAsset(chosenRectId));
        answers = new String[crossword.getCwordsLength()];
        questionsRemaining = new ArrayList<>(crossword.getCwordsLength());
        questionsOrder = new ArrayList<>(crossword.getCwordsLength());

        whitePaint.setColor(ContextCompat.getColor(getContext(), R.color.white));
        whitePaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(ContextCompat.getColor(getContext(), R.color.puzzle_background));
        backgroundPaint.setStyle(Paint.Style.FILL);
        rectPaint.setColor(ContextCompat.getColor(getContext(), R.color.puzzle_dark));
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(5);
        linePaint.setColor(ContextCompat.getColor(getContext(), R.color.puzzle_dark));
        linePaint.setStrokeWidth(5);
        fontPaint.setColor(ContextCompat.getColor(getContext(), R.color.puzzle_dark));
        fontPaint.setStyle(Paint.Style.STROKE);
        smallFontPaint.setColor(ContextCompat.getColor(getContext(), R.color.puzzle_dark));
        smallFontPaint.setStyle(Paint.Style.STROKE);

        checkButton = ContextCompat.getDrawable(getContext(), R.drawable.ic_done);
        listButton = ContextCompat.getDrawable(getContext(), R.drawable.ic_list);

        for (int i = 0; i < crossword.getHorCount(); i++) {
            if (crossword.getCword(i).getWord().length() +
                    crossword.getCword(i).getPosX() > maxWordLength) {
                maxWordLength = crossword.getCword(i).getWord().length() +
                        crossword.getCword(i).getPosX();
            }
        }

        for (int i = 0; i < crossword.getCwordsLength(); i++) {
            answers[i] = "";
            questionsRemaining.add(i);
            questionsOrder.add(i);
        }
    }

    private int wordHeight;
    private Rect checkBounds, listButtonBounds;
    private Rect[] rects;

    private void rectsSet() {
        rects = new Rect[crossword.getCwordsLength()];
        for (int i = 0; i < crossword.getCwordsLength(); i++) {
            //set constant margin
            int constX = crossword.getCword(i).getPosX() * wordHeight +
                    (getWidth() - maxWordLength * wordHeight) / 2;
            int constY = crossword.getCword(i).getPosY() * wordHeight +
                    getHeight() * BAR_PERCENTAGE / 100;
            //horisontal words
            if (i < crossword.getHorCount()) {
                //set border
                rects[i] = new Rect(constX, constY,
                        constX + crossword.getCword(i).getWord().length() * wordHeight,
                        constY + wordHeight);
            }
            //vertical words
            else {
                //set border
                rects[i] = new Rect(constX, constY,
                        constX + wordHeight,
                        constY + wordHeight * crossword.getCword(i).getWord().length());
            }
        }
        checkBounds = new Rect(5, 5, (int) (getWidth() * .2), (int) (getWidth() * .2));
        listButtonBounds = new Rect((int) (getWidth() * .8), 5, getWidth(), (int) (getWidth() * .2));
        checkButton.setBounds(checkBounds);
        listButton.setBounds(listButtonBounds);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        final int MAX_FILL = 15;//maximum slots count
        wordHeight = getHeight() / MAX_FILL - 10;
        rectsSet();
        fontPaint.setTextSize(wordHeight);
        smallFontPaint.setTextSize(wordHeight / 4);
    }

    private void checkAnswers() {
        Toast toast;
        boolean allright = true;
        for (int i = 0; i < crossword.getCwordsLength(); i++) {
            if (!answers[i].equals(crossword.getCword(i).getWord()))
                allright = false;
        }
        if (allright)
            toast = Toast.makeText(getContext(), "Всё правильно!", Toast.LENGTH_LONG);
        else
            toast = Toast.makeText(getContext(), "Ищи ошибку!", Toast.LENGTH_LONG);
        toast.show();
    }

    //Форма вывода вопросов
    //Реализованные требования:
    //1. Выводить список всех вопросов
    //Пожелания:
    //1. ?

    private void listQuestions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Список всех вопросов:")
                .setItems(crossword.getAllQuestions(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        inputAnswer(which);
                    }
                });
        builder.show();
    }

    //Форма ввода ответов
    //Реализлванные требования:
    //1. На форме отображается вопрос
    //2.  При вводе неверного количества букв отображается предупреждение и
    // ответ нельзя зафиксировать
    //3. Показывать номер вопроса
    //Пожелания:
    //TODO: В поле ввода отображать уже введённые буквы (на перекрестьях с другими словами)


    private void inputAnswer(final int currentRect) {
        final int wordLength = crossword.getCword(currentRect).getWord().length();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        TextView textView = new TextView(getContext());
        textView.setText(String.format(getResources().getString(R.string.answer_title), currentRect+1,
                crossword.getCword(currentRect).getWord().length(), crossword.getCword(currentRect).getQuestion()));
        builder.setCustomTitle(textView);
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String output = input.getText().toString();
                if (output.length() != wordLength) {
                    Toast toast = Toast.makeText(getContext(), "Неправильная длина ответа", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    questionsOrder.add(0, questionsOrder.remove(questionsOrder.indexOf(currentRect)));
                    answers[currentRect] = output.toUpperCase();
                    if (questionsRemaining.contains((currentRect))) {
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
            for (int i = 0; i < crossword.getCwordsLength(); i++) {
                if (rects[i].contains((int) event.getX(), (int) event.getY())) {
                    checked_rects.add(i);
                }
            }
            if (checked_rects.size() == 1) {
                inputAnswer(checked_rects.get(0));
            } else if (checkBounds.contains((int) event.getX(), (int) event.getY())) {
                    /*Check all answers*/
                checkAnswers();
            } else if (listButtonBounds.contains((int) event.getX(), (int) event.getY())) {
                    /*List all questions*/
                listQuestions();
            }
        }
        return super.onTouchEvent(event);
    }

    private Rect letterBounds = new Rect();

    protected void onDraw(Canvas myCanvas) {
        /*<--------------------BACKGROUND-------------------->*/
        myCanvas.drawRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight(), backgroundPaint);
        /*<--------------------BUTTONS-------------------->*/
        checkButton.draw(myCanvas);
        listButton.draw(myCanvas);
        /*<--------------------CROSSWORD-------------------->*/
        //draw crossword
        for (int number = questionsOrder.size() - 1; number >= 0; number--) {
            int i = questionsOrder.get(number);
            //draw white background
            myCanvas.drawRect(rects[i], whitePaint);
            //constant margin values for words
            int constX = crossword.getCword(i).getPosX() * wordHeight + (getWidth() -
                    maxWordLength * wordHeight) / 2;
            int constY = crossword.getCword(i).getPosY() * wordHeight +
                    getHeight() * BAR_PERCENTAGE / 100;
            int horizontal_step, vertical_step;
            //horisontal words
            if (i < crossword.getHorCount()) {
                //draw lines
                for (int j = 1; j < crossword.getCword(i).getWord().length(); j++) {
                    myCanvas.drawLine(constX + j * wordHeight, constY,
                            constX + j * wordHeight, constY + wordHeight, linePaint);
                }
                //draw answers
                for (int j = 0; j < answers[i].length(); j++) {
                    fontPaint.getTextBounds(answers[i], j, j + 1, letterBounds);
                    horizontal_step = (int) (wordHeight -
                            fontPaint.measureText(Character.toString(answers[i].charAt(j)))) / 2;
                    vertical_step = wordHeight - (wordHeight - letterBounds.height()) / 2;
                    myCanvas.drawText(Character.toString(answers[i].charAt(j)),
                            constX + j * wordHeight + horizontal_step, constY + vertical_step, fontPaint);
                }

            }
            //vertical words
            else {
                //draw lines
                for (int j = 1; j < crossword.getCword(i).getWord().length(); j++) {
                    myCanvas.drawLine(constX, constY + j * wordHeight,
                            constX + wordHeight, constY + j * wordHeight, linePaint);
                }
                //clean up and draw answers
                for (int j = 0; j < answers[i].length(); j++) {
                    fontPaint.getTextBounds(answers[i], j, j + 1, letterBounds);
                    horizontal_step = (int) (wordHeight -
                            fontPaint.measureText(Character.toString(answers[i].charAt(j)))) / 2;
                    vertical_step = wordHeight - (wordHeight - letterBounds.height()) / 2;
                    myCanvas.drawText(Character.toString(answers[i].charAt(j)),
                            constX + horizontal_step, constY + j * wordHeight + vertical_step, fontPaint);
                }

            }
            //draw border
            myCanvas.drawRect(rects[i], rectPaint);
        }
        //draw word numbers
        for (int i = 0; i < crossword.getCwordsLength(); i++) {
            if (i < crossword.getHorCount()) {
                if (answers[i].length() == 0) {
                    String label = Integer.toString(i + 1);
                    fontPaint.getTextBounds(label, 0, label.length(), letterBounds);
                    myCanvas.drawText(label,
                            rects[i].left + smallFontPaint.measureText(Integer.toString(i + 1)) / 2,
                            rects[i].top + letterBounds.height() / 3, smallFontPaint);
                }
            } else {
                if (answers[i].length() == 0) {
                    String label = Integer.toString(i + 1);
                    fontPaint.getTextBounds(label, 0, label.length(), letterBounds);
                    myCanvas.drawText(label,
                            rects[i].right - smallFontPaint.measureText(Integer.toString(i + 1)) * 1.5f,
                            rects[i].top + letterBounds.height() / 3, smallFontPaint);
                }
            }
        }
        //check if all questions are answered
        if (questionsRemaining.isEmpty()) {
            checkAnswers();
        }
    }
}