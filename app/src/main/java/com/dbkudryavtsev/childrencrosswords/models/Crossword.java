package com.dbkudryavtsev.childrencrosswords.models;

import java.util.Arrays;
import java.util.Objects;

public final class Crossword {
    private CrosswordWord[] cwords;
    private int horCount;

    public Crossword(CrosswordWord[] cwords, int horCount) {
        Objects.requireNonNull(cwords);
        if(horCount<=0)
            throw new NullPointerException();
        this.cwords = cwords;
        this.horCount = horCount;
    }

    public Crossword(Crossword crossword){
        this(crossword.getAllCwords(), crossword.getHorCount());
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