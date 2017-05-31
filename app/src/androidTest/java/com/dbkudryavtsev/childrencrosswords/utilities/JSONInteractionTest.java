package com.dbkudryavtsev.childrencrosswords.utilities;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.MediumTest;

import com.dbkudryavtsev.childrencrosswords.models.Crossword;
import com.dbkudryavtsev.childrencrosswords.models.CrosswordWord;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.dbkudryavtsev.childrencrosswords.utilities.CrosswordBuilder.getCrossword;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class JSONInteractionTest{

    private Context instrumentationCtx;

    @Before
    public void setup() {
        instrumentationCtx = InstrumentationRegistry.getTargetContext();
    }

    private boolean equalCrosswords(Crossword crossword1, Crossword crossword2) {
        for (int i = 0; i < crossword1.getCwordsLength(); i++) {
            if (crossword1.getCword(i).getPosX() != crossword2.getCword(i).getPosX() ||
                    crossword1.getCword(i).getPosY() != crossword2.getCword(i).getPosY() ||
                    !crossword1.getCword(i).getQuestion().equals(crossword2.getCword(i).getQuestion()) ||
                    !crossword1.getCword(i).getWord().equals(crossword2.getCword(i).getWord()))
                return false;
        }
        return crossword1.getHorCount() == crossword2.getHorCount();
    }


    @Test
    public void loadJSONFromFileTest() throws Exception {

    }

    @Test
    public void getCrosswordTest() throws Exception {
        assertTrue(
                equalCrosswords(
                        getCrossword(0, instrumentationCtx),
                        new Crossword(new CrosswordWord[]{
                                new CrosswordWord("Дерево с красными плодами.", "РЯБИНА", 0, 0),
                                new CrosswordWord("Сосновый лес.", "БОР", 1, 1),
                                new CrosswordWord("Сочная ягода в сосновом лесу.", "ЧЕРНИКА", 0, 2),
                                new CrosswordWord("Серый зубастый хищник.", "ВОЛК", 1, 3),
                                new CrosswordWord("Ночная птица.", "СОВА", 0, 4),
                                new CrosswordWord("Лиственное дерево.", "ОСИНА", 0, 5),
                                new CrosswordWord("Подземный обитатель.", "КРОТ", 2, 6),
                        }, 7))
        );
    }
}