package com.dbkudryavtsev.childrencrosswords.models;

import java.util.Objects;

public final class CrosswordWord {
    private final String question;
    private final String answer;
    private final int posX;
    private final int posY;

    public CrosswordWord(String question, String answer, int posX, int posY) {
        Objects.requireNonNull(question);
        Objects.requireNonNull(answer);

        if(posX<0||posY<0)
            throw new NullPointerException();

        this.question = question;
        this.answer = answer;
        this.posX=posX;
        this.posY=posY;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer(){
        return answer;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() { return posY; }
}