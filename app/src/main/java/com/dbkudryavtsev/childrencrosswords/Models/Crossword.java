package com.dbkudryavtsev.childrencrosswords.Models;

import android.content.Context;

import com.dbkudryavtsev.childrencrosswords.Utilities.CrosswordBuilder;

import java.util.Arrays;

public class Crossword {
    private CrosswordWord[] cwords;
    private int horCount;

    public Crossword(CrosswordWord[] anotherCwords, int anotherHorCount) {
        cwords = anotherCwords;
        horCount = anotherHorCount;
    }

    private Crossword(Crossword anotherCrossword){
        this(anotherCrossword.getAllCwords(), anotherCrossword.getHorCount());
    }

    public Crossword(int chosenRectId, Context context) {this(CrosswordBuilder.getCrossword(chosenRectId, context));
    }

    public int getHorCount(){
        return horCount;
    }

    public int getCwordsLength(){
        return cwords.length;
    }

    public CrosswordWord getCword(int position){
        return cwords[position];
    }

    private CrosswordWord[] getAllCwords(){ return Arrays.copyOf(cwords, cwords.length);}

    public String[] getAllQuestions(){
        String[] questions=new String[cwords.length];
        for(int i=0; i<cwords.length;i++){
            questions[i]=Integer.toString(i+1)+". "+cwords[i].getQuestion();
        }
        return questions;
    }
}