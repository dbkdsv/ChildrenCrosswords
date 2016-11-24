package com.dbkudryavtsev.ccw.childrencrosswords.core;

import android.util.Pair;

public class CrosswordWord {

    private String value;
    private Pair<Integer,Integer> coordinates;
    private Directions direction;

    public CrosswordWord(String value, Pair<Integer, Integer> coordinates, Directions direction) {
        this.value = value;
        this.coordinates = coordinates;
        this.direction = direction;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Pair<Integer, Integer> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Pair<Integer, Integer> coordinates) {
        this.coordinates = coordinates;
    }

    public Directions getDirection() {
        return direction;
    }

    public void setDirection(Directions direction) {
        this.direction = direction;
    }
}
