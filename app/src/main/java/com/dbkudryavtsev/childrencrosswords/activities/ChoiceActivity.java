package com.dbkudryavtsev.childrencrosswords.activities;

import android.app.Activity;
import android.os.Bundle;
import android.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.dbkudryavtsev.childrencrosswords.R;
import com.dbkudryavtsev.childrencrosswords.utilities.LocalCrosswordsRepository;
import com.dbkudryavtsev.childrencrosswords.utilities.LocalCrosswordsRepositoryProvider;
import com.dbkudryavtsev.childrencrosswords.views.LevelFragment;

public final class ChoiceActivity extends Activity{

    private TextView noCrosswordsView;
    private LocalCrosswordsRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choice);
        noCrosswordsView  = (TextView) findViewById(R.id.no_crosswords);

        LocalCrosswordsRepositoryProvider provider = (LocalCrosswordsRepositoryProvider) getApplication();
        repository = provider.getLocalCrosswordsRepository();
    }

    @Override
    protected void onResume() {
        super.onResume();
        drawLevels();
    }

    private void drawLevels(){
        int crosswordsCount=repository.getCrosswordsCount();
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
                                LevelFragment.newInstance(
                                        crosswordNumber,
                                        repository.getCompleteness(crosswordNumber)),
                                Integer.toString(crosswordNumber))
                        .commit();
            }
        }
        else
            noCrosswordsView.setVisibility(View.VISIBLE);
    }
}