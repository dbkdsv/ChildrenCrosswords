package com.dbkudryavtsev.ccw.childrencrosswords;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.graphics.Canvas;
import android.view.View;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;

public class MainActivity extends Activity {

    private static final int word_count = 4;
    private static final int hword_count = 2;
    private static int word_height;
    private static final String TAG = "Crossword";
    private static final Rect selRect = new Rect();
    private float width;
    private float height;

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
            System.out.println(_cwords[0]);
            for (int i = 0; i < word_count; i++) {
                System.out.print("Kyky na");
                System.out.println(i);
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new PuzzleView(this));
    }

    public class PuzzleView extends View {

        public PuzzleView(Context context) {
            super(context);
        }

        protected void onDraw(Canvas canvas) {
            // Draw the background...
            width = canvas.getWidth();
            height = canvas.getHeight();
            Paint background = new Paint();
            background.setColor(getResources().getColor(
                    R.color.puzzle_background));
            canvas.drawRect(0, 0, getWidth(), getHeight(), background);


            // Define colors for the grid lines
            Paint dark = new Paint();
            dark.setColor(getResources().getColor(R.color.puzzle_dark));
            dark.setStyle(Style.STROKE);
            dark.setStrokeWidth(5);

            Paint hilite = new Paint();
            hilite.setColor(getResources().getColor(R.color.puzzle_hilite));

            Paint light = new Paint();
            light.setColor(getResources().getColor(R.color.puzzle_light));

            // Draw the minor grid lines
            crossword myc = new crossword();
            word_height = (int) height / 10;
            Rect[] rects = new Rect[word_count];
            for (int i = 0; i < word_count; i++) {
                if (i < hword_count) {
                    rects[i] = new Rect(myc._cwords[i]._posX * word_height, myc._cwords[i]._posY * word_height, word_height * (myc._cwords[i]._posX + myc._cwords[i]._word.length()), word_height * myc._cwords[i]._posY + word_height);
                    for (int j = 1; j < myc._cwords[i]._word.length(); j++) {
                        canvas.drawLine((myc._cwords[i]._posX + j) * word_height , myc._cwords[i]._posY * word_height, (myc._cwords[i]._posX + j) * word_height, word_height * myc._cwords[i]._posY + word_height, dark);
                    }
                }
                else {
                    rects[i] = new Rect(word_height * myc._cwords[i]._posX, word_height * myc._cwords[i]._posY, word_height * myc._cwords[i]._posX + word_height, word_height * myc._cwords[i]._posY + word_height * myc._cwords[i]._word.length());
                    for (int j = 1; j < myc._cwords[i]._word.length(); j++) {
                        canvas.drawLine(myc._cwords[i]._posX * word_height , (myc._cwords[i]._posY + j) * word_height, (myc._cwords[i]._posX+1) * word_height, (myc._cwords[i]._posY + j) * word_height, dark);
                    }
                }
                canvas.drawRect(rects[i], dark);
            }
        }
    }
}
