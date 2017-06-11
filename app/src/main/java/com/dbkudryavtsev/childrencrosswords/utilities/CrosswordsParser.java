package com.dbkudryavtsev.childrencrosswords.utilities;

import android.support.annotation.NonNull;

import com.dbkudryavtsev.childrencrosswords.models.Crossword;
import com.dbkudryavtsev.childrencrosswords.models.CrosswordWord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

final class CrosswordsParser {


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

}
