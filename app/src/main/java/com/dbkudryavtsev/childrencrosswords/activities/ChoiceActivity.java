package com.dbkudryavtsev.childrencrosswords.activities;

import android.app.Activity;
import android.os.Bundle;
import android.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.dbkudryavtsev.childrencrosswords.R;
import com.dbkudryavtsev.childrencrosswords.views.LevelFragment;

import static com.dbkudryavtsev.childrencrosswords.utilities.LocalCrosswordsRepository.getAnswers;
import static com.dbkudryavtsev.childrencrosswords.utilities.LocalCrosswordsRepository.getCrosswordsCount;

public final class ChoiceActivity extends Activity{

    TextView noCrosswordsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        noCrosswordsView  = (TextView) findViewById(R.id.no_crosswords);
    }

    @Override
    protected void onResume() {
        super.onResume();
        drawLevels();
    }

    private void drawLevels(){
        int crosswordsCount=getCrosswordsCount(ChoiceActivity.this);
        if(crosswordsCount>0) {
            noCrosswordsView.setVisibility(View.GONE);
            FragmentManager fm = getFragmentManager();
            for(int i=0; i<crosswordsCount; i++) {
                LevelFragment fragment = (LevelFragment) fm.findFragmentByTag(Integer.toString(i));
                if (fragment != null)
                    fm.beginTransaction()
                            .remove(fragment)
                            .commit();
            }
            for(int crosswordNumber=0; crosswordNumber<crosswordsCount; crosswordNumber++) {
                fm.beginTransaction()
                        .add(R.id.choice_layout,
                                LevelFragment.newInstance(crosswordNumber, checkCompleteness(crosswordNumber)),
                                Integer.toString(crosswordNumber))
                        .commit();
            }
        }
        else
            noCrosswordsView.setVisibility(View.VISIBLE);
    }

    private int checkCompleteness(int fileId) {
        float completeness = 0;
        String[] answers = getAnswers(fileId, ChoiceActivity.this);
        for (String answer : answers)
            completeness += (answer.equals("")) ? 0 : 1 / ((float) answers.length) * 100.;
        return (int) completeness;
    }
}