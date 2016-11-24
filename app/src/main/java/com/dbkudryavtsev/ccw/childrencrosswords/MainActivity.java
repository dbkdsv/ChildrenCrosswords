package com.dbkudryavtsev.ccw.childrencrosswords;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final int word_count = 4;
    private static final int hword_count = 2;
    private static int word_height;
    private static final String TAG = "Crossword";
    private static final Rect selRect = new Rect();
    private float width;
    private float height;

    public class cwords{
        String _question;
        String _word;
        int _posX;
        int _posY;
        cwords(){
            _question="";
            _word="";
        }
    }

    public class crossword{
        cwords[] _cwords;
        int _hor_count;
        crossword(){
            _cwords=new cwords[word_count];
            for(int i=0; i<word_count; i++)
                _cwords[i]=new cwords();
            _hor_count=hword_count;
            System.out.println(_cwords[0]);
            for (int i=0; i<word_count; i++){
                System.out.print("Kyky na");System.out.println(i);
                if (i==0) {
                    _cwords[i]._word="aaaaa";
                    _cwords[i]._question= "1111111111111111";

                    _cwords[i]._posX=0;
                    _cwords[i]._posY=0;
                }
                if (i==1) {
                    _cwords[i]._word="aaaa";
                    _cwords[i]._question = "22222222222222222";

                    _cwords[i]._posX=0;
                    _cwords[i]._posY=3;
                }
                if (i==2) {
                    _cwords[i]._word="aaaaaaa";
                    _cwords[i]._question = "33333333333333333333";

                    _cwords[i]._posX=0;
                    _cwords[i]._posY=0;
                }
                if (i==3) {
                    _cwords[i]._word="aaaaaaaaa";
                    _cwords[i]._question = "33333333333333333333";

                    _cwords[i]._posX=3;
                    _cwords[i]._posY=0;
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
            width=canvas.getWidth();
            height=canvas.getHeight();
            Paint background = new Paint();
            background.setColor(getResources().getColor(
                    R.color.puzzle_background));
            canvas.drawRect(0, 0, getWidth(), getHeight(), background);


            // Draw the board...

            // Define colors for the grid lines
            Paint dark = new Paint();
            dark.setColor(getResources().getColor(R.color.puzzle_dark));

            Paint hilite = new Paint();
            hilite.setColor(getResources().getColor(R.color.puzzle_hilite));

            Paint light = new Paint();
            light.setColor(getResources().getColor(R.color.puzzle_light));

            // Draw the minor grid lines
            crossword myc=new crossword();
            word_height=(int)height/10;
            for (int i = 0; i < word_count; i++) {
                if(i<hword_count)
                    canvas.drawRect(myc._cwords[i]._posX*word_height, myc._cwords[i]._posY*word_height, word_height*(myc._cwords[i]._posX+myc._cwords[i]._word.length()), word_height*myc._cwords[i]._posY+word_height, dark);
                else
                    canvas.drawRect(word_height*myc._cwords[i]._posX, word_height*myc._cwords[i]._posY, word_height*myc._cwords[i]._posX+word_height, word_height*myc._cwords[i]._posY+word_height*myc._cwords[i]._word.length(), dark);
//                canvas.drawLine(0, i * height/8, getWidth(), i * height/8,
//                        light);
//                canvas.drawLine(0, i * height + 1, getWidth(), i * height
//                        + 1, hilite);
//                canvas.drawLine(i * width, 0, i * width, getHeight(),
//                        light);
//                canvas.drawLine(i * width + 1, 0, i * width + 1,
//                        getHeight(), hilite);
            }

            // Draw the major grid lines
//            for (int i = 0; i < 9; i++) {
//                if (i % 3 != 0)
//                    continue;
//                canvas.drawLine(0, i * height/8, getWidth(), i * height,
//                        dark);
//                canvas.drawLine(0, i * height + 1, getWidth(), i * height
//                        + 1, hilite);
//                canvas.drawLine(i * width, 0, i * width, getHeight(), dark);
//                canvas.drawLine(i * width + 1, 0, i * width + 1,
//                        getHeight(), hilite);
//            }
//
//            // Draw the numbers...
//            // Define color and style for numbers
//            Paint foreground = new Paint(Paint.ANTI_ALIAS_FLAG);
//            foreground.setColor(getResources().getColor(
//                    R.color.puzzle_foreground));
//            foreground.setStyle(Style.FILL);
//            foreground.setTextSize(height * 0.75f);
//            foreground.setTextScaleX(width / height);
//            foreground.setTextAlign(Paint.Align.CENTER);
//
//            // Draw the number in the center of the tile
//            FontMetrics fm = foreground.getFontMetrics();
//            // Centering in X: use alignment (and X at midpoint)
//            float x = width / 2;
//            // Centering in Y: measure ascent/descent first
//            float y = height / 2 - (fm.ascent + fm.descent) / 2;
////            for (int i = 0; i < 9; i++) {
////                for (int j = 0; j < 9; j++) {
////                    canvas.drawText(this.game.getTileString(i, j), i
////                            * width + x, j * height + y, foreground);
////                }
////            }
//
//            // Draw the selection...
//            Log.d(TAG, "selRect=" + selRect);
//            Paint selected = new Paint();
//            selected.setColor(getResources().getColor(
//                    R.color.puzzle_selected));
//            canvas.drawRect(selRect, selected);
        }
    }
}
