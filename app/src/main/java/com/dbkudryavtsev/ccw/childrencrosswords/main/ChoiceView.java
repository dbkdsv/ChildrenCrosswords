package com.dbkudryavtsev.ccw.childrencrosswords.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;

public class ChoiceView extends View {
    public ChoiceView(Context context) {
        super(context);
    }

    Rect rects[] = new Rect[R.raw.class.getFields().length-2];

    @Override
    protected void onDraw(Canvas canvas) {
        Paint black = new Paint();
        black.setColor(ContextCompat.getColor(getContext(), R.color.puzzle_dark));
        black.setStyle(Paint.Style.STROKE);
        black.setStrokeWidth(5);
        super.onDraw(canvas);
        int MAX_FILL = 4;
        int margin = (int) (getWidth() * .1);
        int wordHeight = getWidth() / MAX_FILL - (int) 1.25*margin;
        for (int i = 0; i < rects.length; i++) {
            rects[i] = new Rect(margin*i + i * wordHeight, margin, margin*i + i * wordHeight + wordHeight, margin+wordHeight);
            canvas.drawRect(rects[i], black);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        int chosenRect = -1;
        for (int i = 0; i < rects.length; i++) {
            if (rects[i].contains((int) event.getX(), (int) event.getY())) {
                chosenRect = i;
                break;
            }
        }
        chosenRect++;
        Intent intent = new Intent(this.getContext(), CrosswordActivity.class);
        try {
            intent.putExtra("chosenRect", R.raw.class.getFields()[chosenRect].getInt(null));
        } catch (Exception e) {
            e.printStackTrace();
        }
        getContext().startActivity(intent);
        return super.onTouchEvent(event);
    }
}