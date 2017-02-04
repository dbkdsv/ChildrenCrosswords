package com.dbkudryavtsev.ccw.childrencrosswords.main;

//TODO: Запоминать состояние заполнения последнего кроссворда и давать возможность продолжить заполнение

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class Crossword {
    private CrosswordWord[] cwords;
    private int horCount;

    Crossword(String jsonString) {
        JSONObject jsonObject;
        JSONArray array = new JSONArray();
        try {
            jsonObject = new JSONObject(jsonString);
            array = jsonObject.getJSONArray("crosswordWord");
            cwords = new CrosswordWord[array.length()];
            horCount = (Integer) jsonObject.get("horCount");
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
        /*-----------2 magic variables HERE-----------------*/

        for (int i = 0; i < cwords.length; i++)
            try {
                cwords[i] = new CrosswordWord(array.getJSONObject(i).getString("question"),
                        array.getJSONObject(i).getString("word"),
                        array.getJSONObject(i).getInt("posX"),
                        array.getJSONObject(i).getInt("posY"));
            }
            catch (JSONException ex) {
                ex.printStackTrace();
            }
    }
    int getHorCount(){
        return horCount;
    }

    int getCwordsLength(){
        return cwords.length;
    }

    CrosswordWord getCword(int position){
        return cwords[position];
    }

    String[] getAllQuestions(){
        String[] questions=new String[cwords.length];
        for(int i=0; i<cwords.length;i++){
            questions[i]=Integer.toString(i+1)+". "+cwords[i].getQuestion();
        }
        return questions;
    }
}