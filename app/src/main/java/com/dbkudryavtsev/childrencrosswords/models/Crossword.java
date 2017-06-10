package com.dbkudryavtsev.childrencrosswords.models;

import java.util.ArrayList;
import java.util.Objects;

public final class Crossword {
    private ArrayList<CrosswordWord> cwords;
    private int horCount;

    public Crossword(ArrayList<CrosswordWord> cwords, int horCount) {
        Objects.requireNonNull(cwords);
        if(horCount<=0)
            throw new NumberFormatException("Horizontal count below zero.");
        this.cwords = cwords;
        this.horCount = horCount;
    }

    public int getHorCount(){
        return horCount;
    }

    public int getCwordsLength(){
        return cwords.size();
    }

    public CrosswordWord getCword(int position){
        return cwords.get(position);
    }

    public String[] getAllQuestions(){
        String[] questions=new String[cwords.size()];
        for(int i=0; i<cwords.size();i++){
            questions[i]=Integer.toString(i+1)+". "+cwords.get(i).getQuestion();
        }
        return questions;
    }
}