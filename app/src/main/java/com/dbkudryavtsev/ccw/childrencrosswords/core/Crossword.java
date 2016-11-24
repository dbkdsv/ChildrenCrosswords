package com.dbkudryavtsev.ccw.childrencrosswords.core;

import android.util.Pair;

import java.util.HashMap;

/**
 * Created by User on 24.11.2016.
 */

public class Crossword {

    private HashMap<CrosswordWord, String> crossword;

    public Crossword() {
        crossword = new HashMap<CrosswordWord, String>();
        for (int i = 0; i < 4; i++) {
            switch (i){
                case 0: crossword.put(new CrosswordWord("aaaaa", new Pair<Integer, Integer>(0,0), Directions.Horizontal), "1");
                case 1: crossword.put(new CrosswordWord("bbbb", new Pair<Integer, Integer>(0,3), Directions.Horizontal), "2");
                case 2: crossword.put(new CrosswordWord("acccccc", new Pair<Integer, Integer>(0,0), Directions.Vertical), "3");
                case 3: crossword.put(new CrosswordWord("ddddddddd", new Pair<Integer, Integer>(3,0), Directions.Vertical), "4");
            }
        }
    }
}
