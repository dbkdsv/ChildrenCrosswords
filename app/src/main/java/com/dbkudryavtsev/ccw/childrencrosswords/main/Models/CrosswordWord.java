package com.dbkudryavtsev.ccw.childrencrosswords.main.Models;

public class CrosswordWord {
    private String question;
    private String word;
    private int posX;
    private int posY;

    public CrosswordWord(String question, String word, int posX, int posY) {
        this.question = question;
        this.word = word;
        this.posX=posX;
        this.posY=posY;
    }

    public String getQuestion() {
        return question;
    }

    public String getWord(){
        return word;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() { return posY; }
}