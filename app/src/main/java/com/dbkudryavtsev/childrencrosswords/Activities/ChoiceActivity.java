package com.dbkudryavtsev.childrencrosswords.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.app.FragmentManager;

import com.dbkudryavtsev.childrencrosswords.R;
import com.dbkudryavtsev.childrencrosswords.Views.LevelFragment;

import java.io.File;

import static com.dbkudryavtsev.childrencrosswords.Utilities.CrosswordBuilder.getAnswers;

public class ChoiceActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
    }

    @Override
    protected void onResume() {
        super.onResume();
        drawLevels();
    }

    private void drawLevels(){
        File filesDirectory = ChoiceActivity.this.getFilesDir();
        int crosswordsCount=0;
        if(filesDirectory.length()!=0) {
            for (File file : filesDirectory.listFiles())
                if (file.getName().contains("crossword")) crosswordsCount++;
        }
        FragmentManager fm = getFragmentManager();
        for(int i=0; i<crosswordsCount; i++) {
            LevelFragment fragment = (LevelFragment) fm.findFragmentByTag(Integer.toString(i));
            if (fragment != null)
                fm.beginTransaction().remove(fragment).commit();
        }
        for(int i=0; i<crosswordsCount; i++) {
            fm.beginTransaction().add(R.id.choice_layout, LevelFragment.newInstance(i, checkCompleteness(i)),
                    Integer.toString(i)).commit();
        }
    }

    private int checkCompleteness(int fileId) {
        float completeness = 0;
        String[] answers = getAnswers(fileId, ChoiceActivity.this);
        for (String answer : answers)
            completeness += (answer.equals("")) ? 0 : 1 / ((float) answers.length) * 100.;
        return (int) completeness;
    }
}