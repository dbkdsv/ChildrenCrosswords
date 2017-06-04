package com.dbkudryavtsev.childrencrosswords.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.dbkudryavtsev.childrencrosswords.models.Crossword;
import com.dbkudryavtsev.childrencrosswords.utilities.LocalCrosswordsRepository;
import com.dbkudryavtsev.childrencrosswords.R;

import java.util.ArrayList;
import java.util.Locale;

import static com.dbkudryavtsev.childrencrosswords.utilities.LocalCrosswordsRepository.getCrossword;
import static com.dbkudryavtsev.childrencrosswords.utilities.ResourcesBuilder.writeToAnswerFile;
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

    private int globalChosenCrosswordId;

    private ScaleGestureDetector detector;

    public CrosswordView(Context context){
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

    public void setValues (int chosenCrosswordId){
        globalChosenCrosswordId = chosenCrosswordId;
        // TODO: переписать
        crossword = new Crossword(getCrossword(globalChosenCrosswordId, getContext()));
        answers = new String[crossword.getCwordsLength()];
        questionsRemaining = new ArrayList<>(crossword.getCwordsLength());
        questionsOrder = new ArrayList<>(crossword.getCwordsLength());
        for (int i = 0; i < crossword.getHorCount(); i++) {
            if (crossword.getCword(i).getAnswer().length() +
                    crossword.getCword(i).getPosX() > maxWordLength) {
                maxWordLength = crossword.getCword(i).getAnswer().length() +
                        crossword.getCword(i).getPosX();
            }
        }
        answers = LocalCrosswordsRepository.getAnswers(globalChosenCrosswordId, getContext());
        for (int i = 0; i < crossword.getCwordsLength(); i++) {
            if(answers[i].isEmpty()) {
                questionsRemaining.add(i);
                questionsOrder.add(i);
            }
            else  questionsOrder.add(0,i);
        }
        invalidate();
    }

    private void init(){
        setFocusable(true);
        setFocusableInTouchMode(true);
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
        detector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    public int onTextChange(String inputString){
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
            else if (currentAnswer.length() == crossword.getCword(currentRect).getAnswer().length()- activeSize) {
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
                    }
                    textInputIsActive = false;
                    ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(this.getWindowToken(), 0);
                    invalidate();
                    return 0;
                }
            }
            else {
                invalidate();
                return 1;
            }
            invalidate();
        }
        return 2;
    }

    private float scaleFactor = 1.f;

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(android.view.ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(1.f, Math.min(scaleFactor, 3.f));
            invalidate();
            return true;
        }
    }

    private int wordHeight;
    private Rect[] rects;

    private float stepX = 0.f;
    private float stepY = 0.f;

    private void allocateRects(){
        rects = new Rect[crossword.getCwordsLength()];
        for(int i = 0; i<crossword.getCwordsLength(); i++)
            rects[i] = new Rect();
    }

    private void rectsSet() {
        fontPaint.setTextSize(wordHeight);
        smallFontPaint.setTextSize(wordHeight / 4);
        questionFontPaint.setTextSize(wordHeight / 3);
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
        int inputBoundsWidth = (int) (getWidth() * .9), inputBoundsHeight = (int) (getHeight() * .3),
                marginTop = getHeight() * BAR_PERCENTAGE / 100, innerMargin = 10;
        canvasBounds.set((getWidth() - inputBoundsWidth) / 2, marginTop,
                (getWidth() + inputBoundsWidth) / 2, marginTop + inputBoundsHeight);
        textBounds.set(canvasBounds.left + innerMargin, canvasBounds.top + innerMargin,
                canvasBounds.right - innerMargin, canvasBounds.bottom - canvasBounds.height() / 2);
    }

    Bitmap inputBitmap;
    Canvas inputCanvas;
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        final int MAX_FILL = 15;//maximum slots count
        wordHeight = getHeight() / MAX_FILL - 10;
        stepX = (getWidth() - maxWordLength * wordHeight) / 2;
        stepY = getHeight() * BAR_PERCENTAGE / 100;
        allocateRects();
        rectsSet();
        inputBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        inputCanvas = new Canvas(inputBitmap);
    }

    public void checkAnswers() {
        Toast toast;
        boolean allright = true;
        for (int i = 0; i < crossword.getCwordsLength(); i++) {
            if (!answers[i].equals(crossword.getCword(i).getAnswer()))
                allright = false;
        }
        if (allright)
            toast = Toast.makeText(getContext(), "Всё правильно!", Toast.LENGTH_LONG);
        else
            toast = Toast.makeText(getContext(), "Ищи ошибку!", Toast.LENGTH_LONG);
        toast.show();
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

    private float previousX, previousY;
    private boolean isMoving;
    private Rect clipBounds = new Rect();

    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                previousX = event.getX();
                previousY = event.getY();
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (!isMoving) {
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
                            if (rects[i].contains((int)(event.getX()/scaleFactor+clipBounds.left),
                                    (int)(event.getY()/scaleFactor+clipBounds.top))) {
                                checked_rects.add(i);
                            }
                        }
                        if (checked_rects.size() == 1) {
                            currentRect = checked_rects.get(0);
                            if (questionsRemaining.contains(currentRect)) {
                                textInputIsActive = true;
                                ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).
                                        toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                                .requestFocus();
                                invalidate();
                            }
                        }
                    }
                } else isMoving = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                isMoving = true;
                stepX += event.getX() - previousX;
                stepY += event.getY() - previousY;
                previousX = event.getX();
                previousY = event.getY();
                Log.e(Float.toString(stepX),Float.toString(stepY));
                rectsSet();
                invalidate();
                break;
            }
        }
        return true;
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
        int rectPosX = crossword.getCword(currentRect).getPosX();
        int rectPosY = crossword.getCword(currentRect).getPosY();
        int horCount = crossword.getHorCount();
        int currentWordLength = crossword.getCword(currentRect).getAnswer().length();
        int[][] currentRectDots = {{rectPosX, rectPosY},
                {currentRect < horCount ? rectPosX +
                        currentWordLength : rectPosX,
                        currentRect < horCount ? rectPosY :
                                (rectPosY + currentWordLength-1)}};
        int i = currentRect < horCount ? horCount : 0,
                maxI = currentRect < horCount ? crossword.getCwordsLength() : horCount;
        for (; i < maxI; i++) {
            int comparableRectPosX = crossword.getCword(i).getPosX();
            int comparableRectPosY = crossword.getCword(i).getPosY();
            int comparableWordLength = crossword.getCword(i).getAnswer().length();
            int[][] comparableRectDots = {{comparableRectPosX, comparableRectPosY},
                    {i < horCount ? comparableRectPosX +
                            comparableWordLength : comparableRectPosX,
                            i < horCount ? comparableRectPosY :
                                    (comparableRectPosY + comparableWordLength-1)}};
            if (currentRect < horCount) {
                if (min(comparableRectDots[0][1], comparableRectDots[1][1]) <= currentRectDots[0][1] &&
                        currentRectDots[0][1] <= max(comparableRectDots[0][1], comparableRectDots[1][1]) &&
                        min(currentRectDots[0][0], currentRectDots[1][0]) <= comparableRectDots[0][0] &&
                        comparableRectDots[0][0] <= max(currentRectDots[0][0], currentRectDots[1][0])) {
                    intersectDot = new Integer[]{i, currentRectDots[0][1]-rectPosY,
                            comparableRectDots[0][0]-rectPosX};
                    intersects.add(intersectDot);
                }
            }
            else {
                if (min(currentRectDots[0][1], currentRectDots[1][1]) <= comparableRectDots[0][1] &&
                        comparableRectDots[0][1] <= max(currentRectDots[0][1], currentRectDots[1][1]) &&
                        min(comparableRectDots[0][0], comparableRectDots[1][0]) <= currentRectDots[0][0] &&
                        currentRectDots[0][0] <= max(comparableRectDots[0][0], comparableRectDots[1][0])) {
                    intersectDot = new Integer[]{i, currentRectDots[0][0]-rectPosX,
                            comparableRectDots[0][1]-rectPosY};
                    intersects.add(intersectDot);
                }
            }
        }
        return intersects;
    }

    public boolean keyIsDown(KeyEvent event){
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (textInputIsActive) {
                textInputIsActive = false;
                invalidate();
                ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(this.getWindowToken(), 0);
                return true;
            } else {
                writeToAnswerFile(answers, globalChosenCrosswordId, getContext());
                ((Activity) getContext()).finish();
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private Rect canvasBounds = new Rect(), textBounds = new Rect(), currentWordRect = new Rect();
    private String currentAnswer = "";
    private StaticLayout sl = new StaticLayout("", questionFontPaint, 0,
            Layout.Alignment.ALIGN_CENTER, 1, 1, true);

    ArrayList<Integer> intersectPositions=new ArrayList<>();

    //TODO: разрезать на несколько
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.scale(scaleFactor, scaleFactor);
        canvas.getClipBounds(clipBounds);
        /*<--------------------BACKGROUND-------------------->*/
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), backgroundPaint);
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
            // TODO: выделить переменные!
            String answer = answers[i];
            if (i < crossword.getHorCount()) {
                //draw lines
                for (int j = 1; j < crossword.getCword(i).getAnswer().length(); j++) {
                    canvas.drawLine(constX + j * wordHeight, constY,
                            constX + j * wordHeight, constY + wordHeight, linePaint);
                }
                //draw answers
                for (int j = 0; j < answer.length(); j++) {
                    fontPaint.getTextBounds(answer, j, j + 1, letterBounds);
                    horizontal_step = (int) (wordHeight -
                            fontPaint.measureText(Character.toString(answer.charAt(j)))) / 2;
                    vertical_step = wordHeight - (wordHeight - letterBounds.height()) / 2;
                    canvas.drawText(Character.toString(answer.charAt(j)),
                            constX + j * wordHeight + horizontal_step, constY + vertical_step, fontPaint);
                }
            }
            //vertical words
            else {
                //draw lines
                for (int j = 1; j < crossword.getCword(i).getAnswer().length(); j++) {
                    canvas.drawLine(constX, constY + j * wordHeight,
                            constX + wordHeight, constY + j * wordHeight, linePaint);
                }
                //clean up and draw answers
                for (int j = 0; j < answer.length(); j++) {
                    fontPaint.getTextBounds(answer, j, j + 1, letterBounds);
                    horizontal_step = (int) (wordHeight -
                            fontPaint.measureText(Character.toString(answer.charAt(j)))) / 2;
                    vertical_step = wordHeight - (wordHeight - letterBounds.height()) / 2;
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
        canvas.restore();
        if (textInputIsActive) {
            drawInput(canvas);
        }

    }

    private void drawInput(Canvas canvas) {
        final int wordLength = crossword.getCword(currentRect).getAnswer().length();
        String textOnCanvas = String.format(getResources().getString(R.string.answer_title), currentRect + 1,
                crossword.getCword(currentRect).getAnswer().length(), crossword.getCword(currentRect).getQuestion());
        canvas.drawRect(0,0,getWidth(), getHeight(), alphaPaint);
        inputCanvas.drawRect(canvasBounds, rectPaint);
        inputCanvas.drawRect(canvasBounds, backgroundPaint);
        inputCanvas.drawRect(textBounds, rectPaint);
        int constX = (canvasBounds.width() - wordLength * wordHeight) / 2,
                constY = textBounds.bottom + (canvasBounds.height() - textBounds.height() - wordHeight) / 2;
        currentWordRect.set(canvasBounds.left + constX, constY, canvasBounds.right - constX,
                constY + wordHeight);
        inputCanvas.drawRect(currentWordRect, rectPaint);
        for (int j = 1; j < crossword.getCword(currentRect).getAnswer().length(); j++) {
            inputCanvas.drawLine(canvasBounds.left + constX + j * wordHeight, constY,
                    canvasBounds.left + constX + j * wordHeight, constY + wordHeight, linePaint);
        }
        canvas.drawBitmap(inputBitmap, 0.0f, 0.0f, null);
        if(Build.VERSION.SDK_INT>=23) {
            StaticLayout.Builder layoutBuilder = StaticLayout.Builder.obtain(textOnCanvas,
                    0, textOnCanvas.length(), questionFontPaint, textBounds.width())
                    .setAlignment(Layout.Alignment.ALIGN_CENTER)
                    .setLineSpacing(1,1)
                    .setIncludePad(true);
            sl = layoutBuilder.build();
        }
        else {
            //Static Layout до API 23 не создать иначе.
            sl = new StaticLayout(textOnCanvas, questionFontPaint, textBounds.width(),
                    Layout.Alignment.ALIGN_CENTER, 1, 1, true);
        }
        canvas.save();
        float textHeight = getTextHeight(textOnCanvas, questionFontPaint);
        int numberOfTextLines = sl.getLineCount();
        float textYCoordinate = textBounds.exactCenterY() - ((numberOfTextLines * textHeight) / 2);
        float textXCoordinate = textBounds.left;
        int horizontal_step, vertical_step;
        ArrayList<Integer[]> intersects = findIntersect();
        intersectPositions.clear();
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
        canvas.translate(textXCoordinate, textYCoordinate);
        sl.draw(canvas);
        canvas.restore();
    }
}