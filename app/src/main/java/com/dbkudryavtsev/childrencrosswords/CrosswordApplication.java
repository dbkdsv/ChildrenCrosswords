package com.dbkudryavtsev.childrencrosswords;

import android.app.Application;

import com.dbkudryavtsev.childrencrosswords.utilities.LocalCrosswordsRepository;
import com.dbkudryavtsev.childrencrosswords.utilities.LocalCrosswordsRepositoryProvider;

public class CrosswordApplication extends Application implements LocalCrosswordsRepositoryProvider{

    LocalCrosswordsRepository crosswordsRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        crosswordsRepository = new LocalCrosswordsRepository(CrosswordApplication.this);
    }

    @Override
    public LocalCrosswordsRepository getLocalCrosswordsRepository() {
        return crosswordsRepository;
    }
}
