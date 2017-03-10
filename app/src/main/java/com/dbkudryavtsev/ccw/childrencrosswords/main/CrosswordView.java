package com.dbkudryavtsev.ccw.childrencrosswords.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class CrosswordView extends View {

    private final int BAR_PERCENTAGE = 15;

    private int maxWordLength = -1;

    private Paint backgroundPaint = new Paint();
    private Paint rectPaint = new Paint();
    private Paint linePaint = new Paint();
    private TextPaint fontPaint = new TextPaint();
    private TextPaint smallFontPaint = new TextPaint();
    private TextPaint questionFontPaint = new TextPaint();
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
        } catch (IOException ex) {
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
        setFocusable(true);
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
        smallFontPaint.setColor(ContextCompat.getColor(getContext(), R.color.puzzle_dark));
        smallFontPaint.setStyle(Paint.Style.STROKE);
        questionFontPaint.setColor(ContextCompat.getColor(getContext(), R.color.puzzle_dark));
        questionFontPaint.setStyle(Paint.Style.STROKE);

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
    private Rect checkBounds = new Rect(), listButtonBounds = new Rect();
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
        checkBounds.set(5, 5, (int) (getWidth() * .2), (int) (getWidth() * .2));
        listButtonBounds.set((int) (getWidth() * .8), 5, getWidth(), (int) (getWidth() * .2));
        checkButton.setBounds(checkBounds);
        listButton.setBounds(listButtonBounds);
        int inputBoundsWidth = (int) (getWidth() * .9), inputBoundsHeight = 600,
                marginTop = getHeight() * BAR_PERCENTAGE / 100, innerMargin = 10;
        canvasBounds.set((getWidth() - inputBoundsWidth) / 2, marginTop,
                (getWidth() + inputBoundsWidth) / 2, marginTop + inputBoundsHeight);
        textBounds.set(canvasBounds.left + innerMargin, canvasBounds.top + innerMargin,
                canvasBounds.right - innerMargin, canvasBounds.bottom - canvasBounds.height() / 2);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        final int MAX_FILL = 15;//maximum slots count
        wordHeight = getHeight() / MAX_FILL - 10;
        rectsSet();
        fontPaint.setTextSize(wordHeight);
        smallFontPaint.setTextSize(wordHeight / 4);
        questionFontPaint.setTextSize(wordHeight / 3);
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

    private void listQuestions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Список всех вопросов:")
                .setItems(crossword.getAllQuestions(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        currentRect = which;
                        if (questionsRemaining.contains(currentRect)) {
                            textInputIsActive = true;
                            invalidate();
                        }
                    }
                });
        builder.show();
    }

    private int currentRect;

//    private void inputAnswer() {
//        final int wordLength = crossword.getCword(currentRect).getWord().length();
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        TextView textView = new TextView(getContext());
//        textView.setText(String.format(getResources().getString(R.string.answer_title), currentRect+1,
//                crossword.getCword(currentRect).getWord().length(), crossword.getCword(currentRect).getQuestion()));
//        builder.setCustomTitle(textView);
//        final EditText input = new EditText(getContext());
//        input.setInputType(InputType.TYPE_CLASS_TEXT);
//        builder.setView(input);
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                final String output = input.getText().toString();
//                if (output.length() != wordLength) {
//                    Toast toast = Toast.makeText(getContext(), "Неправильная длина ответа", Toast.LENGTH_LONG);
//                    toast.show();
//                } else {
//                    questionsOrder.add(0, questionsOrder.remove(questionsOrder.indexOf(currentRect)));
//                    answers[currentRect] = output.toUpperCase();
//                    if (questionsRemaining.contains((currentRect))) {
//                        questionsRemaining.remove(questionsRemaining.indexOf(currentRect));
//                    }
//                }
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//        builder.show();
//    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (textInputIsActive) {
                if (!canvasBounds.contains((int) event.getX(), (int) event.getY())) {
                    textInputIsActive = false;
                    currentAnswer = "";
                    invalidate();
                    ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(this.getWindowToken(), 0);
                }
            } else {
                ArrayList<Integer> checked_rects = new ArrayList<>();
                for (int i = 0; i < crossword.getCwordsLength(); i++) {
                    if (rects[i].contains((int) event.getX(), (int) event.getY())) {
                        checked_rects.add(i);
                    }
                }
                if (checked_rects.size() == 1) {
                    currentRect = checked_rects.get(0);
                    if (questionsRemaining.contains(currentRect)) {
                        textInputIsActive = true;
                        findIntersect();
                        invalidate();
                    }
                } else if (checkBounds.contains((int) event.getX(), (int) event.getY())) {
                    /*Check all answers*/
                    checkAnswers();
                } else if (listButtonBounds.contains((int) event.getX(), (int) event.getY())) {
                    /*List all questions*/
                    listQuestions();
                }
            }

        }
        return super.onTouchEvent(event);
    }

    private Rect letterBounds = new Rect();
    private boolean textInputIsActive = false;

    private float getTextHeight(String text, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }

    private ArrayList<Integer[]> findIntersect() {
        ArrayList<Integer[]> intersects = new ArrayList<>();
        Integer[] intersectDot;
        int[][] currentRectDots = {{crossword.getCword(currentRect).getPosX(), crossword.getCword(currentRect).getPosY()},
                {currentRect < crossword.getHorCount() ? crossword.getCword(currentRect).getPosX() +
                        crossword.getCword(currentRect).getWord().length() : crossword.getCword(currentRect).getPosX(),
                        currentRect < crossword.getHorCount() ? crossword.getCword(currentRect).getPosY() :
                                (crossword.getCword(currentRect).getPosY() + crossword.getCword(currentRect).getWord().length()-1)}};
        int i = currentRect < crossword.getHorCount() ? crossword.getHorCount() : 0,
                maxI = currentRect < crossword.getHorCount() ? crossword.getCwordsLength() : crossword.getHorCount();
        for (; i < maxI; i++) {
            int[][] comparableRectDots = {{crossword.getCword(i).getPosX(), crossword.getCword(i).getPosY()},
                    {i < crossword.getHorCount() ? crossword.getCword(i).getPosX() +
                            crossword.getCword(i).getWord().length() : crossword.getCword(i).getPosX(),
                            i < crossword.getHorCount() ? crossword.getCword(i).getPosY() :
                                    (crossword.getCword(i).getPosY() + crossword.getCword(i).getWord().length()-1)}};
            if (currentRect < crossword.getHorCount()) {
                if (min(comparableRectDots[0][1], comparableRectDots[1][1]) <= currentRectDots[0][1] &&
                        currentRectDots[0][1] <= max(comparableRectDots[0][1], comparableRectDots[1][1]) &&
                        min(currentRectDots[0][0], currentRectDots[1][0]) <= comparableRectDots[0][0] &&
                        comparableRectDots[0][0] <= max(currentRectDots[0][0], currentRectDots[1][0])) {
                    intersectDot = new Integer[]{i, currentRectDots[0][1]-crossword.getCword(i).getPosY(),
                            comparableRectDots[0][0]-crossword.getCword(currentRect).getPosX()};
                    intersects.add(intersectDot);
                }
            } else {
                if (min(currentRectDots[0][1], currentRectDots[1][1]) <= comparableRectDots[0][1] &&
                        comparableRectDots[0][1] <= max(currentRectDots[0][1], currentRectDots[1][1]) &&
                        min(comparableRectDots[0][0], comparableRectDots[1][0]) <= currentRectDots[0][0] &&
                        currentRectDots[0][0] <= max(comparableRectDots[0][0], comparableRectDots[1][0])) {
                    intersectDot = new Integer[]{i, currentRectDots[0][0]-crossword.getCword(i).getPosX(),
                            comparableRectDots[0][1]-crossword.getCword(currentRect).getPosY()};
                    intersects.add(intersectDot);
                }
            }
        }
        return intersects;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        ArrayList<Integer[]> intersects = findIntersect();
        int activeSize=0;
        for(int i=0; i<intersects.size();i++){
            if(answers[intersects.get(i)[0]].length()>0) activeSize++;
        }
        if(textInputIsActive) {
            if (event.getAction() == 2) {
                if(currentAnswer.length()<crossword.getCword(currentRect).getWord().length()-activeSize)
                    currentAnswer += event.getCharacters();
            }
            if (event.getAction() == KeyEvent.ACTION_DOWN)
                if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && currentAnswer.length()>0)
                    currentAnswer = currentAnswer.substring(0, currentAnswer.length() - 1);
            currentAnswer = currentAnswer.toUpperCase();
            if(currentAnswer.length()+activeSize==crossword.getCword(currentRect).getWord().length()){
                String answerString=currentAnswer;
                for(int i=0; i<intersects.size();i++){
                    if(answers[intersects.get(i)[0]].length()>0) {
                        answerString = answerString.substring(0, intersects.get(i)[2]) +
                                answers[intersects.get(i)[0]].charAt(intersects.get(i)[1]) +
                                answerString.substring(intersects.get(i)[2]);
                    }
                }
                if(answerString.equals(crossword.getCword(currentRect).getWord())){
                    answers[currentRect]=answerString;
                    currentAnswer="";
                    questionsOrder.add(0, questionsOrder.remove(questionsOrder.indexOf(currentRect)));
                    if (questionsRemaining.contains(currentRect)) {
                        questionsRemaining.remove(questionsRemaining.indexOf(currentRect));
                    }
                    textInputIsActive=false;
                }
            }
            invalidate();
        }
        return true;
    }

    boolean previousTextInputIsActiveState =false;
    Rect canvasBounds = new Rect(), textBounds = new Rect(), currentWordRect = new Rect();
    String currentAnswer = "";

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
        if (textInputIsActive) {
            ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).
                    toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            final int wordLength = crossword.getCword(currentRect).getWord().length();
            String textOnCanvas = String.format(getResources().getString(R.string.answer_title), currentRect + 1,
                    crossword.getCword(currentRect).getWord().length(), crossword.getCword(currentRect).getQuestion());
            Bitmap inputBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas inputCanvas = new Canvas(inputBitmap);
            inputCanvas.drawRect(canvasBounds, rectPaint);
            inputCanvas.drawRect(canvasBounds, backgroundPaint);
            inputCanvas.drawRect(textBounds, rectPaint);
            int constX = (canvasBounds.width() - wordLength * wordHeight) / 2,
                    constY = textBounds.bottom + (canvasBounds.height() - textBounds.height() - wordHeight) / 2;
            currentWordRect.set(canvasBounds.left + constX, constY, canvasBounds.right - constX,
                    constY + wordHeight);
            inputCanvas.drawRect(currentWordRect, rectPaint);
            for (int j = 1; j < crossword.getCword(currentRect).getWord().length(); j++) {
                inputCanvas.drawLine(canvasBounds.left + constX + j * wordHeight, constY,
                        canvasBounds.left + constX + j * wordHeight, constY + wordHeight, linePaint);
            }
            myCanvas.drawBitmap(inputBitmap, 0.0f, 0.0f, null);
            StaticLayout sl = new StaticLayout(textOnCanvas, questionFontPaint, textBounds.width(),
                    Layout.Alignment.ALIGN_CENTER, 1, 1, true);
            myCanvas.save();
            float textHeight = getTextHeight(textOnCanvas, questionFontPaint);
            int numberOfTextLines = sl.getLineCount();
            float textYCoordinate = textBounds.exactCenterY() - ((numberOfTextLines * textHeight) / 2);
            float textXCoordinate = textBounds.left;
            int horizontal_step, vertical_step;
            ArrayList<Integer[]> intersects = findIntersect();
            ArrayList<Integer> intersectPositions=new ArrayList<>();
            int step=0;
            for(int k=0; k<intersects.size();k++){
                if (answers[intersects.get(k)[0]].length()!=0){
                    int j=intersects.get(k)[1];
                    intersectPositions.add(intersects.get(k)[2]);
                    fontPaint.getTextBounds(answers[intersects.get(k)[0]], j, j + 1, letterBounds);
                    horizontal_step = (int) (wordHeight -
                            fontPaint.measureText(Character.toString(answers[intersects.get(k)[0]].charAt(intersects.get(k)[1])))) / 2;
                    vertical_step = wordHeight - (wordHeight - letterBounds.height()) / 2;
                    inputCanvas.drawText(Character.toString(answers[intersects.get(k)[0]].charAt(intersects.get(k)[1])),
                            canvasBounds.left + constX + (intersects.get(k)[2]) * wordHeight + horizontal_step, constY + vertical_step, fontPaint);
                }
            }
            for (int j = 0; j < currentAnswer.length(); j++) {
                if(intersectPositions.contains(j+step)) step++;
                fontPaint.getTextBounds(currentAnswer, j, j + 1, letterBounds);
                horizontal_step = (int) (wordHeight -
                        fontPaint.measureText(Character.toString(currentAnswer.charAt(j)))) / 2;
                vertical_step = wordHeight - (wordHeight - letterBounds.height()) / 2;
                inputCanvas.drawText(Character.toString(currentAnswer.charAt(j)),
                        canvasBounds.left + constX + (j+step) * wordHeight + horizontal_step, constY + vertical_step, fontPaint);
            }
            myCanvas.translate(textXCoordinate, textYCoordinate);
            sl.draw(myCanvas);
            myCanvas.restore();
            if(textInputIsActive!= previousTextInputIsActiveState) {
                String keySpace = "input keyevent " + KeyEvent.KEYCODE_SPACE;
                Runtime runtime = Runtime.getRuntime();
                try {
                    Process proc = runtime.exec(keySpace);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}