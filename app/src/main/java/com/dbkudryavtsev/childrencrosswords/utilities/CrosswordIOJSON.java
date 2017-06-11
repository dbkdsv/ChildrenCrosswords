package com.dbkudryavtsev.childrencrosswords.utilities;

import android.support.annotation.NonNull;

import com.dbkudryavtsev.childrencrosswords.models.Crossword;
import com.dbkudryavtsev.childrencrosswords.models.CrosswordWord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

final class CrosswordIOJSON {

    private static final String answersArrayName = "answers"; // TODO VK: стандарт кодирования для имен констант!
    //TODO VK: нет тестов на разбор и запись ответов

    @NonNull
    static Crossword parseCrosswordFromJson(String jsonString) {
        ArrayList<CrosswordWord> cwords;
        int horCount;
        if (jsonString!=null && jsonString.length()>0) {
            JSONObject jsonObject;
            JSONArray crosswordWordsJSONArray;
            try {
                jsonObject = new JSONObject(jsonString);
                crosswordWordsJSONArray = jsonObject.getJSONArray("crosswordWord");
                cwords = new ArrayList<>(crosswordWordsJSONArray.length());
                horCount = (Integer) jsonObject.get("horCount");
            } catch (JSONException ex) {
                ex.printStackTrace();
                throw new RuntimeException("Crossword JSON to string unsuccessful");
            }
            for (int i = 0; i < cwords.size(); i++)
                try {
                    jsonObject = crosswordWordsJSONArray.getJSONObject(i);
                    cwords.set(i, new CrosswordWord(jsonObject.getString("question"),
                            jsonObject.getString("word"),
                            jsonObject.getInt("posX"),
                            jsonObject.getInt("posY")));

                } catch (JSONException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException("Crossword JSON search unsuccessful");
                }
        }
        else throw new IllegalArgumentException("Null JSON string.");
        return new Crossword(cwords, horCount);
    }

    static String[] parseAnswersFromJson(String jsonString) {
        String[] answers;
        if (jsonString!=null && jsonString.length()>0) {
            JSONObject jsonObject;
            JSONArray answersJSONArray;
            try {
                jsonObject = new JSONObject(jsonString);
                answersJSONArray = jsonObject.getJSONArray(answersArrayName);
                answers = new String[answersJSONArray.length()];
            } catch (JSONException ex) {
                ex.printStackTrace();
                throw new RuntimeException("Answers JSON to string unsuccessful");
            }
            for (int i = 0; i < answers.length; i++)
                try {
                    answers[i] = answersJSONArray.getJSONObject(i).getString("answer");
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException("JSON answer search unsuccessful");
                }
            return answers;
        }
        else {
            return null;
        }
    }

    static String convertAnswersToJSON(String[] answers){
        String answersString = "{ \"answers\": [";
        for (int i = 0; i < answers.length; i++) {
            answersString += "{\n\"answer\": \"" + answers[i] + "\"" + "}";
            if (i != answers.length - 1) answersString += ",\n";
        }
        answersString += "] }";
        return answersString;
    }
}
