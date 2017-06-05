package com.dbkudryavtsev.childrencrosswords;

import android.app.Application;

import com.dbkudryavtsev.childrencrosswords.utilities.LocalCrosswordsRepository;
import com.dbkudryavtsev.childrencrosswords.utilities.LocalCrosswordsRepositoryProvider;

public class CrosswordApplication extends Application implements LocalCrosswordsRepositoryProvider{

    LocalCrosswordsRepository crosswordsRepository;

    public CrosswordApplication() {
        crosswordsRepository = new LocalCrosswordsRepository();
    }

    @Override
    public LocalCrosswordsRepository getLocalCrosswordsRepository() {
        return crosswordsRepository;
    }
}
