package com.dbkudryavtsev.childrencrosswords.utilities;

import android.support.annotation.NonNull;

import com.dbkudryavtsev.childrencrosswords.models.Crossword;
import com.dbkudryavtsev.childrencrosswords.models.CrosswordWord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

final class CrosswordsParser {

    //TODO VK: нет тестов на разбор и запись ответов

    @NonNull
    static Crossword parseCrosswordFromJson(String jsonString) {
        ArrayList<CrosswordWord> cwords = new ArrayList<>();
        int horCount;
        if (jsonString!=null && jsonString.length()>0) {
            JSONObject jsonObject;
            JSONArray crosswordWordsJSONArray;
            try {
                jsonObject = new JSONObject(jsonString);
                crosswordWordsJSONArray = jsonObject.getJSONArray("crosswordWord");
                horCount = (Integer) jsonObject.get("horCount");
            } catch (JSONException ex) {
                ex.printStackTrace();
                throw new RuntimeException("Crossword JSON to string unsuccessful");
            }
            for (int i = 0; i < crosswordWordsJSONArray.length(); i++)
                try {
                    jsonObject = crosswordWordsJSONArray.getJSONObject(i);
                    cwords.add(i, new CrosswordWord(jsonObject.getString("question"),
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

}
