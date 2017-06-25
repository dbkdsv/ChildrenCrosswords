package com.dbkudryavtsev.childrencrosswords.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dbkudryavtsev.childrencrosswords.models.Crossword;
import com.dbkudryavtsev.childrencrosswords.R;
import com.dbkudryavtsev.childrencrosswords.models.CrosswordWord;
import com.plattysoft.leonids.ParticleSystem;

import java.util.ArrayList;
import java.util.Locale;

import static android.graphics.Rect.intersects;
import static java.lang.Math.max;
import static java.lang.Math.min;

public final class CrosswordView extends View {

    private final int BAR_PERCENTAGE = 5;

    private int maxWordLength = -1;

    private final Paint backgroundPaint = new Paint();
    private final Paint rectPaint = new Paint();
    private final Paint linePaint = new Paint();
    private final TextPaint fontPaint = new TextPaint();
    private final TextPaint smallFontPaint = new TextPaint();
    private final TextPaint questionFontPaint = new TextPaint();
    private final Paint whitePaint = new Paint();
    private final Paint alphaPaint = new Paint();

    private Crossword crossword;
    private String[] answers;

    private ArrayList<Integer> questionsRemaining;
    private ArrayList<Integer> questionsOrder;

    public CrosswordView(Context context) {
        super(context);
        init();
    }

    public CrosswordView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CrosswordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private SharedPreferences preferences;
    private static final String SETTINGS_STRING = "settings";
    private static final String HINTS_STRING = "hints";
    private int hintsCounter;

    private void init() {
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

        alphaPaint.setStyle(Paint.Style.FILL);
        alphaPaint.setColor(ContextCompat.getColor(getContext(), R.color.puzzle_dark));
        alphaPaint.setAlpha(150);

        preferences = getContext().getSharedPreferences(SETTINGS_STRING, 0);
        if(preferences.contains(HINTS_STRING))
            hintsCounter = preferences.getInt(HINTS_STRING, 0);
        else {
            hintsCounter = 20;
            Toast.makeText(getContext(), "За первый запуск начисляю тебе "+
                    Integer.toString(hintsCounter)+" баллов!", Toast.LENGTH_LONG).show();
        }
    }

    private int chosenLevel;

    public void setValues(Crossword crossword, String[] answers, int chosenLevel) {
        this.crossword = crossword;
        this.answers = new String[answers.length];
        this.answers = answers;
        this.chosenLevel = chosenLevel;
        questionsRemaining = new ArrayList<>(this.crossword.getCwordsLength());
        questionsOrder = new ArrayList<>(this.crossword.getCwordsLength());
        for (int i = 0; i < crossword.getHorCount(); i++) {
            final CrosswordWord cword = this.crossword.getCword(i);
            if (cword.getAnswer().length() + cword.getPosX() > maxWordLength) {
                maxWordLength = cword.getAnswer().length() +
                        crossword.getCword(i).getPosX();
            }
        }
        for (int i = 0; i < this.crossword.getCwordsLength(); i++) {
            if (answers[i].isEmpty()) {
                questionsRemaining.add(i);
                questionsOrder.add(i);
            } else questionsOrder.add(0, i);
        }
        invalidate();
    }

    public int onTextChange(String inputString) {
        ArrayList<Integer[]> intersects = findIntersect();
        int activeSize = 0;
        for (int i = 0; i < intersects.size(); i++) {
            if (answers[intersects.get(i)[0]].length() > 0) activeSize++;
        }
        if (textInputIsActive) {
            inputString = inputString.toUpperCase(Locale.getDefault());
            currentAnswer = inputString;
            if (currentAnswer.length() < crossword.getCword(currentRect).getAnswer().length() - activeSize)
                currentAnswer = inputString;
            else if (currentAnswer.length() == crossword.getCword(currentRect).getAnswer().length() - activeSize) {
                String answerString = currentAnswer;
                for (int i = 0; i < intersects.size(); i++) {
                    if (answers[intersects.get(i)[0]].length() > 0) {
                        answerString = answerString.substring(0, intersects.get(i)[2]) +
                                answers[intersects.get(i)[0]].charAt(intersects.get(i)[1]) +
                                answerString.substring(intersects.get(i)[2]);
                    }
                }
                if (answerString.equals(crossword.getCword(currentRect).getAnswer())) {
                    answers[currentRect] = answerString;
                    currentAnswer = "";
                    questionsOrder.add(0, questionsOrder.remove(questionsOrder.indexOf(currentRect)));
                    if (questionsRemaining.contains(currentRect)) {
                        questionsRemaining.remove(questionsRemaining.indexOf(currentRect));
                        if (questionsRemaining.size() == 0) {
                            hintsCounter += 5;
                            Toast.makeText(getContext(), "Ты получил баллы для подсказок! Теперь их " +
                                    Integer.toString(hintsCounter) + "!", Toast.LENGTH_LONG).show();
                        }
                    }
                    textInputIsActive = false;
                    ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(this.getWindowToken(), 0);
                    invalidate();
                    return 0;
                }
            } else {
                invalidate();
                return 1;
            }
            invalidate();
        }
        return 2;
    }

    private int wordHeight;
    private Rect[] rects;

    private float stepX = 0.f;
    private float stepY = 0.f;

    private void allocateRects() {
        rects = new Rect[crossword.getCwordsLength()];
        for (int i = 0; i < crossword.getCwordsLength(); i++)
            rects[i] = new Rect();
    }

    private void rectsSet() {
        fontPaint.setTextSize((int)(wordHeight*.9));
        fontPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        smallFontPaint.setTextSize(wordHeight / 4);
        questionFontPaint.setTextSize(wordHeight / 2);
        for (int i = 0; i < crossword.getCwordsLength(); i++) {
            //set constant margin
            int constX = crossword.getCword(i).getPosX() * wordHeight + (int) stepX;
            int constY = crossword.getCword(i).getPosY() * wordHeight + (int) stepY;
            //horisontal words
            if (i < crossword.getHorCount()) {
                //set border
                rects[i].set(constX, constY,
                        constX + crossword.getCword(i).getAnswer().length() * wordHeight,
                        constY + wordHeight);
            }
            //vertical words
            else {
                //set border
                rects[i].set(constX, constY,
                        constX + wordHeight,
                        constY + wordHeight * crossword.getCword(i).getAnswer().length());
            }
        }
        int inputBoundsWidth = (int) (getWidth() * .9);
        int inputBoundsHeight = (int) (getHeight() * .4),
                marginTop = getHeight() * BAR_PERCENTAGE / 100, innerMargin = 10;
        inputWindowBounds.set((getWidth() - inputBoundsWidth) / 2, marginTop,
                (getWidth() + inputBoundsWidth) / 2, marginTop + inputBoundsHeight);
        textBounds.set(inputWindowBounds.left + innerMargin, inputWindowBounds.top + innerMargin,
                inputWindowBounds.right - innerMargin, inputWindowBounds.bottom - inputWindowBounds.height() / 2);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        getDimensions();
        wordHeight = Math.min(getWidth() / (crosswordBotX - crosswordTopX + 1),
                getHeight() / (crosswordBotY - crosswordTopY + 1));
        stepX = (getWidth() - maxWordLength * wordHeight) / 2;
        stepY = (getHeight() - (crosswordBotY - crosswordTopY) * wordHeight) / 2
                - getHeight() * BAR_PERCENTAGE / 100;
        allocateRects();
        rectsSet();
    }

    public void checkAnswers() {
        Toast toast;
        boolean allright = true;
        for (int i = 0; i < crossword.getCwordsLength(); i++) {
            if (!answers[i].equals(crossword.getCword(i).getAnswer()))
                allright = false;
        }
        if (allright) {
            showCongratulations();
        }
        else {
            toast = Toast.makeText(getContext(), "Ищи ошибку!", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void showCongratulations() {
        ViewGroup row = (ViewGroup) this.getParent().getParent();
        RelativeLayout layout = (RelativeLayout) row.findViewById(R.id.congrats_layout);
        layout.setVisibility(VISIBLE);
        int numParticles = 100;
        long timeToLive = 3000;
        int drawableResId;
        switch(chosenLevel%4){
            case 0:{
                drawableResId =R.drawable.star_pink;
                break;
            }
            case 1:{
                drawableResId =R.drawable.star_yellow;
                break;
            }
            case 2:{
                drawableResId =R.drawable.star_blue;
                break;
            }
            case 3:{
                drawableResId =R.drawable.star_green;
                break;
            }
            default:{
                drawableResId =R.drawable.star_pink;
                break;
            }
        }
        new ParticleSystem((Activity) getContext(), numParticles, drawableResId, timeToLive)
                .setSpeedRange(0.1f, 0.3f)
                .emit(getWidth()*3/4,getHeight()*3/10, 3000);

        new ParticleSystem((Activity) getContext(), numParticles, drawableResId, timeToLive)
                .setSpeedRange(0.1f, 0.3f)
                .emit(getWidth()/4,getHeight()*3/10, 3000);

        new ParticleSystem((Activity) getContext(), 30, R.drawable.balloon1, 10000)
                .setSpeedModuleAndAngleRange(0f, 0.3f, 180, 180)
                .setRotationSpeed(0)
                .setAcceleration(0.00005f, 90)
                .emit(getWidth(), 0, 8);


        new ParticleSystem((Activity) getContext(), 30, R.drawable.balloon2, 10000)
                .setSpeedModuleAndAngleRange(0f, 0.3f, 0, 0)
                .setRotationSpeed(0)
                .setAcceleration(0.00005f, 90)
                .emit(0, 0, 8);
    }

    int crosswordTopX;
    int crosswordTopY;
    int crosswordBotX;
    int crosswordBotY;

    private void getDimensions() {
        if (crossword.getCwordsLength() > 0) {
            crosswordTopX = crossword.getCword(0).getPosX();
            crosswordTopY = crossword.getCword(0).getPosY();
            crosswordBotX = crosswordTopX;
            crosswordBotY = crosswordTopY;
            for (int i = 0; i < crossword.getCwordsLength(); i++) {
                final CrosswordWord cword = crossword.getCword(i);
                final int posX = cword.getPosX();
                if (posX < crosswordTopX) crosswordTopX = posX;
                final int posY = cword.getPosY();
                if (posY < crosswordTopY) crosswordTopY = posY;
                final int length = cword.getAnswer().length();
                if (i < crossword.getHorCount()) {
                    if (posX + length > crosswordBotX)
                        crosswordBotX = posX + length;
                    if (posY + 1 > crosswordBotY)
                        crosswordBotY = posY + 1;
                } else {
                    if (posX + 1 > crosswordBotX)
                        crosswordBotX = posX + 1;
                    if (posY + length > crosswordBotY)
                        crosswordBotY = posY + length;
                }
            }
        }
    }

    public void listQuestions() {
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

    int location[] = new int[2];

    public boolean viewTouched(MotionEvent event) {
        this.getLocationOnScreen(location);
        event.setLocation(event.getX() - location[0], event.getY() - location[1]);
        boolean result = false;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_UP: {
                if (textInputIsActive) {
                    if (!inputWindowBounds.contains((int) event.getX(), (int) event.getY())) {
                        textInputIsActive = false;
                        currentAnswer = "";
                        invalidate();
                        ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                                .hideSoftInputFromWindow(this.getWindowToken(), 0);
                    } else {
                        ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).
                                toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        if (nextQuestionBounds.contains((int) event.getX(), (int) event.getY())) {
                            if (questionsRemaining.indexOf(currentRect) < questionsRemaining.size() - 1)
                                currentRect = questionsRemaining.get(questionsRemaining.indexOf(currentRect) + 1);
                            else currentRect = questionsRemaining.get(0);
                            result = true;
                            invalidate();
                        } else if (hintsBounds.contains((int) event.getX(), (int) event.getY())) {
                            getHint();
                        }
                    }
                } else {
                    ArrayList<Integer> checked_rects = new ArrayList<>();
                    for (int i = 0; i < crossword.getCwordsLength(); i++) {
                        if (rects[i].contains((int) (event.getX()),
                                (int) (event.getY()))) {
                            checked_rects.add(i);
                        }
                    }
                    if (checked_rects.size() == 1) {
                        currentRect = checked_rects.get(0);
                        if (questionsRemaining.contains(currentRect)) {
                            textInputIsActive = true;
                            ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).
                                    toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                            invalidate();
                            result = true;
                        }
                    } else if (checked_rects.size() == 2) Toast
                            .makeText(getContext(), "Тыкни не на пересечении", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            }
        }
        return result;
    }

    private void getHint() {
        ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(this.getWindowToken(), 0);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Чем помочь? У тебя " + Integer.toString(hintsCounter) + " баллов.")
                .setItems(new String[]{/*
                                "Открыть одну букву - 1 балл",*/
                                "Открыть всё слово - 5 баллов"},
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                boolean hasOpened =true;
                                switch (which) {
                                    case 1: {
                                        if(hintsCounter>=1)
                                            hintsCounter -= 1;
                                        else hasOpened = false;
                                        break;
                                    }
                                    case 0: {
                                        if(hintsCounter>=5) {
                                            hintsCounter -= 5;
                                            answers[currentRect] = crossword.getCword(currentRect).getAnswer();
                                            questionsRemaining.remove(questionsRemaining.indexOf(currentRect));
                                            textInputIsActive = false;
                                        }
                                        else hasOpened = false;
                                        break;
                                    }
                                }
                                if(hasOpened)
                                Toast.makeText(getContext(), "Открыл! Теперь у тебя " +
                                        Integer.toString(hintsCounter) + " баллов!", Toast.LENGTH_LONG).show();
                                else Toast.makeText(getContext(), "Не могу открыть... У тебя мало баллов.",
                                        Toast.LENGTH_LONG).show();
                                invalidate();
                            }
                        });
        builder.setNeutralButton("Как получить?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Как получить баллы?");
                builder.setMessage("Всё просто! Заполняй болностью кроссворды и за каждый получишь 5 баллов!");
                builder.show();
            }
        });
        builder.show();
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
        final CrosswordWord currentCword = crossword.getCword(currentRect);
        int[][] currentRectDots = {{currentCword.getPosX(), currentCword.getPosY()},
                {currentRect < crossword.getHorCount() ? currentCword.getPosX() +
                        currentCword.getAnswer().length() : currentCword.getPosX(),
                        currentRect < crossword.getHorCount() ? currentCword.getPosY() :
                                (currentCword.getPosY() + currentCword.getAnswer().length() - 1)}};
        int i = currentRect < crossword.getHorCount() ? crossword.getHorCount() : 0,
                maxI = currentRect < crossword.getHorCount() ? crossword.getCwordsLength() : crossword.getHorCount();
        for (; i < maxI; i++) {
            final CrosswordWord cwordI = crossword.getCword(i);
            int[][] comparableRectDots = {{cwordI.getPosX(), cwordI.getPosY()},
                    {i < crossword.getHorCount() ? cwordI.getPosX() +
                            cwordI.getAnswer().length() : cwordI.getPosX(),
                            i < crossword.getHorCount() ? cwordI.getPosY() :
                                    (cwordI.getPosY() + cwordI.getAnswer().length() - 1)}};
            if (currentRect < crossword.getHorCount()) {
                if (min(comparableRectDots[0][1], comparableRectDots[1][1]) <= currentRectDots[0][1] &&
                        currentRectDots[0][1] <= max(comparableRectDots[0][1], comparableRectDots[1][1]) &&
                        min(currentRectDots[0][0], currentRectDots[1][0]) <= comparableRectDots[0][0] &&
                        comparableRectDots[0][0] <= max(currentRectDots[0][0], currentRectDots[1][0])) {
                    intersectDot = new Integer[]{i, currentRectDots[0][1] - cwordI.getPosY(),
                            comparableRectDots[0][0] - currentCword.getPosX()};
                    intersects.add(intersectDot);
                }
            } else {
                if (min(currentRectDots[0][1], currentRectDots[1][1]) <= comparableRectDots[0][1] &&
                        comparableRectDots[0][1] <= max(currentRectDots[0][1], currentRectDots[1][1]) &&
                        min(comparableRectDots[0][0], comparableRectDots[1][0]) <= currentRectDots[0][0] &&
                        currentRectDots[0][0] <= max(comparableRectDots[0][0], comparableRectDots[1][0])) {
                    intersectDot = new Integer[]{i, currentRectDots[0][0] - cwordI.getPosX(),
                            comparableRectDots[0][1] - currentCword.getPosY()};
                    intersects.add(intersectDot);
                }
            }
        }
        return intersects;
    }

    public boolean keyIsDown(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (textInputIsActive) {
                textInputIsActive = false;
                invalidate();
                ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(this.getWindowToken(), 0);
                currentAnswer = "";
            } else {
                ((Activity) getContext()).finish();
            }
            return true;
        }
        return false;
    }

    public String[] getCurrentAnswers() {
        return answers;
    }

    public void onDestroy() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(HINTS_STRING, hintsCounter);
        editor.apply();
    }

    private Rect inputWindowBounds = new Rect();
    private Rect textBounds = new Rect();
    private Rect currentWordRect = new Rect();
    private String currentAnswer = "";

    ArrayList<Integer> intersectPositions = new ArrayList<>();

    protected void onDraw(Canvas canvas) {
        /*<--------------------BACKGROUND-------------------->*/
        Drawable d = getResources().getDrawable(R.drawable.house);
        d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        d.draw(canvas);
        /*<--------------------CROSSWORD-------------------->*/
        //draw crossword
        for (int number = questionsOrder.size() - 1; number >= 0; number--) {
            int i = questionsOrder.get(number);
            //draw white background
            canvas.drawRect(rects[i], whitePaint);
            //constant margin values for words
            int constX = crossword.getCword(i).getPosX() * wordHeight + (int) stepX;
            int constY = crossword.getCword(i).getPosY() * wordHeight + (int) stepY;
            int horizontal_step, vertical_step;
            //horizontal words
            String answer = answers[i];
            final int iCwordAnswerLength = crossword.getCword(i).getAnswer().length();
            if (i < crossword.getHorCount()) {
                //draw lines
                for (int j = 1; j < iCwordAnswerLength; j++) {
                    canvas.drawLine(constX + j * wordHeight, constY,
                            constX + j * wordHeight, constY + wordHeight, linePaint);
                }
                //draw answers
                for (int j = 0; j < answer.length(); j++) {
                    fontPaint.getTextBounds(answer, j, j + 1, letterBounds);
                    horizontal_step = (int) (wordHeight -
                            fontPaint.measureText(Character.toString(answer.charAt(j)))) / 2;
                    vertical_step = wordHeight - (wordHeight - letterBounds.height()+letterBounds.bottom) / 2;
                    canvas.drawText(Character.toString(answer.charAt(j)),
                            constX + j * wordHeight + horizontal_step, constY + vertical_step, fontPaint);
                }
            }
            //vertical words
            else {
                //draw lines
                for (int j = 1; j < iCwordAnswerLength; j++) {
                    canvas.drawLine(constX, constY + j * wordHeight,
                            constX + wordHeight, constY + j * wordHeight, linePaint);
                }
                //clean up and draw answers
                for (int j = 0; j < answer.length(); j++) {
                    fontPaint.getTextBounds(answer, j, j + 1, letterBounds);
                    horizontal_step = (int) (wordHeight -
                            fontPaint.measureText(Character.toString(answer.charAt(j)))) / 2;
                    vertical_step = wordHeight - (wordHeight - letterBounds.height()+letterBounds.bottom) / 2;
                    canvas.drawText(Character.toString(answer.charAt(j)),
                            constX + horizontal_step, constY + j * wordHeight + vertical_step, fontPaint);
                }
            }
            //draw border
            canvas.drawRect(rects[i], rectPaint);
        }
        //draw word numbers
        for (int i = 0; i < crossword.getCwordsLength(); i++) {
            if (i < crossword.getHorCount()) {
                if (answers[i].length() == 0) {
                    String label = Integer.toString(i + 1);
                    fontPaint.getTextBounds(label, 0, label.length(), letterBounds);
                    canvas.drawText(label,
                            rects[i].left + smallFontPaint.measureText(Integer.toString(i + 1)) / 2,
                            rects[i].top + letterBounds.height() / 3, smallFontPaint);
                }
            } else {
                if (answers[i].length() == 0) {
                    String label = Integer.toString(i + 1);
                    fontPaint.getTextBounds(label, 0, label.length(), letterBounds);
                    canvas.drawText(label,
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
            drawInput(canvas);
        }
    }

    private Rect nextQuestionBounds = new Rect();
    private Rect hintsBounds = new Rect();
    private Rect doneBounds  = new Rect();
    private Rect tempRect = new Rect(inputWindowBounds);

    private void drawInput(Canvas canvas) {
        int margin = 30;
        final CrosswordWord cword = crossword.getCword(currentRect);
        final int wordLength = cword.getAnswer().length();
        String textOnCanvas = String.format(getResources().getString(R.string.answer_title), currentRect + 1,
                wordLength, cword.getQuestion());
        canvas.drawRect(0, 0, getWidth(), getHeight(), alphaPaint);
        final boolean isTooBig = wordLength > getWidth() / wordHeight;
        int lineCounter=1;
        if(tempRect.width()!=0)
            inputWindowBounds.set(tempRect);
        tempRect.set(inputWindowBounds);
        if(isTooBig) {
            lineCounter = wordLength / (getWidth() / wordHeight);
            inputWindowBounds.set(inputWindowBounds.left,inputWindowBounds.top,
                    inputWindowBounds.right,inputWindowBounds.bottom + (margin + wordHeight)*lineCounter);
        }
        canvas.drawRect(inputWindowBounds, rectPaint);
        canvas.drawRect(inputWindowBounds, backgroundPaint);
        canvas.drawRect(textBounds, rectPaint);
        int constX = (inputWindowBounds.width() - wordLength * wordHeight) / 2;
        int constY = textBounds.bottom + margin;
        int startLetter = 0;
        int endLetter = wordLength-1;
        if(isTooBig){
            int toDraw = getWidth() / wordHeight-2;
            endLetter = toDraw-1;
            for(int i=0; i<=lineCounter;i++){
                constX = (inputWindowBounds.width() - toDraw * wordHeight) / 2;
                constY = textBounds.bottom + margin +i*(margin+wordHeight);
                currentWordRect.set(inputWindowBounds.left + constX, constY, inputWindowBounds.right - constX,
                        constY + wordHeight);
                canvas.drawRect(currentWordRect, rectPaint);
                for (int j = 1; j < toDraw; j++) {
                    canvas.drawLine(inputWindowBounds.left + constX + j * wordHeight, constY,
                            inputWindowBounds.left + constX + j * wordHeight, constY + wordHeight, linePaint);
                }
                //<-----БУКВЫ----->
                drawLetters(canvas, startLetter, endLetter);

                toDraw = wordLength - (i+1)*toDraw;
                if (toDraw>getWidth() / wordHeight) toDraw-=2;
                startLetter = endLetter+1;
                endLetter = startLetter + toDraw-1;
            }
        }
        else {
            currentWordRect.set(inputWindowBounds.left + constX, constY,
                    inputWindowBounds.right - constX, constY + wordHeight);
            canvas.drawRect(currentWordRect, rectPaint);
            for (int j = 1; j < wordLength; j++) {
                canvas.drawLine(inputWindowBounds.left + constX + j * wordHeight, constY,
                        inputWindowBounds.left + constX + j * wordHeight, constY + wordHeight, linePaint);
            }
            //<-----БУКВЫ----->
            drawLetters(canvas, startLetter, endLetter);
        }
        //<-----ВОПРОС---->
        StaticLayout sl;
        if (Build.VERSION.SDK_INT >= 23) {
            StaticLayout.Builder layoutBuilder = StaticLayout.Builder.obtain(textOnCanvas,
                    0, textOnCanvas.length(), questionFontPaint, textBounds.width())
                    .setAlignment(Layout.Alignment.ALIGN_CENTER)
                    .setLineSpacing(1, 1)
                    .setIncludePad(true);
            sl = layoutBuilder.build();
        } else {
            sl = new StaticLayout(textOnCanvas, questionFontPaint, textBounds.width(),
                    Layout.Alignment.ALIGN_CENTER, 1, 1, true);
        }
        int numberOfTextLines = sl.getLineCount();
        float textHeight = getTextHeight(textOnCanvas, questionFontPaint);
        while (textHeight>=textBounds.height()) {
            questionFontPaint.setTextSize(questionFontPaint.getTextSize()*.9f);
            textHeight = getTextHeight(textOnCanvas, questionFontPaint);
        }
        float textYCoordinate = textBounds.exactCenterY() - numberOfTextLines * textHeight*1.2f / 2;
        float textXCoordinate = textBounds.left;
        canvas.save();
        canvas.translate(textXCoordinate, textYCoordinate);
        sl.draw(canvas);
        canvas.restore();
        double scale = 1.05;
        hintsBounds.set(inputWindowBounds.left + margin, inputWindowBounds.bottom - margin - wordHeight,
                inputWindowBounds.left + wordHeight + margin, inputWindowBounds.bottom - margin);
        Drawable hint = getResources().getDrawable(R.drawable.ic_help);
        while(intersects(currentWordRect, hintsBounds)) hintsBounds.set(hintsBounds.left,
                (int) (hintsBounds.top*scale),(int) (hintsBounds.right*(2-scale)),hintsBounds.bottom);
        hint.setBounds(hintsBounds);
        nextQuestionBounds.set(inputWindowBounds.right - 2*(wordHeight + margin), inputWindowBounds.bottom - margin - wordHeight,
                inputWindowBounds.right - wordHeight-2*margin, inputWindowBounds.bottom - margin);
        Drawable forward = getResources().getDrawable(R.drawable.ic_forward);
        while(intersects(currentWordRect, nextQuestionBounds)) nextQuestionBounds.set((int) (nextQuestionBounds.left*scale),
                (int) (nextQuestionBounds.top*scale),nextQuestionBounds.right,nextQuestionBounds.bottom);
        forward.setBounds(nextQuestionBounds);

        doneBounds.set(inputWindowBounds.right - wordHeight - margin, inputWindowBounds.bottom - margin - wordHeight,
                inputWindowBounds.right - margin, inputWindowBounds.bottom - margin);
        Drawable done = getResources().getDrawable(R.drawable.ic_done_black);
        while(intersects(currentWordRect, doneBounds)) doneBounds.set((int) (doneBounds.left*scale),
                (int) (doneBounds.top*scale),doneBounds.right,doneBounds.bottom);
        done.setBounds(doneBounds);

        hint.draw(canvas);
        forward.draw(canvas);
        done.draw(canvas);
    }

    private void drawLetters(Canvas canvas, int startLetter, int endLetter) {
        int horizontal_step, vertical_step;
        ArrayList<Integer[]> intersects = findIntersect();
        intersectPositions.clear();
        int step = 0;
        for (int k = 0; k < intersects.size(); k++) {
            final Integer[] intersectK = intersects.get(k);
            final Integer intersectedWord = intersectK[0];
            final Integer positionIntersected = intersectK[1];
            final Integer positionCurrent = intersectK[2];
            if (answers[intersectedWord].length() != 0) {
                intersectPositions.add(positionCurrent);
                fontPaint.getTextBounds(answers[intersectedWord], positionIntersected, positionIntersected + 1, letterBounds);
                horizontal_step = (int) (wordHeight -
                        fontPaint.measureText(Character.toString(answers[intersectedWord].charAt(positionIntersected)))) / 2;
                vertical_step = wordHeight - (wordHeight - letterBounds.height()+letterBounds.bottom) / 2;
                if(startLetter<=positionCurrent && positionCurrent<=endLetter)
                canvas.drawText(Character.toString(answers[intersectedWord].charAt(positionIntersected)),
                        currentWordRect.left + (positionCurrent-startLetter) * wordHeight + horizontal_step,
                        currentWordRect.top + vertical_step,
                        fontPaint);
            }
        }
        for (int j = 0; j < currentAnswer.length(); j++) {
            while (intersectPositions.contains(j + step)) step++;
            fontPaint.getTextBounds(currentAnswer, j, j + 1, letterBounds);
            horizontal_step = (int) (wordHeight -
                    fontPaint.measureText(Character.toString(currentAnswer.charAt(j)))) / 2;
            vertical_step = wordHeight - (wordHeight - letterBounds.height()+letterBounds.bottom) / 2;
            if (startLetter-step  <= j && j <= endLetter-step) {
                canvas.drawText(Character.toString(currentAnswer.charAt(j)),
                        currentWordRect.left + (j - startLetter + step) * wordHeight + horizontal_step,
                        currentWordRect.top + vertical_step,
                        fontPaint);
            }
        }
    }
}