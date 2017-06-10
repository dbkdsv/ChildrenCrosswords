package com.dbkudryavtsev.childrencrosswords.utilities;

import android.support.annotation.NonNull;

import com.dbkudryavtsev.childrencrosswords.R;
import com.dbkudryavtsev.childrencrosswords.models.Crossword;
import com.dbkudryavtsev.childrencrosswords.models.CrosswordWord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
// TODO VK: что означает название этого класса??
final class CrosswordIOJSON {

    private static final String answersArrayName = "answers";


    @NonNull
    static Crossword parseCrosswordFromJson(String jsonString) {
        JSONObject jsonObject;
        JSONArray array;
        CrosswordWord[] cwords = new CrosswordWord[]{};
        int horCount = 0;
        if (jsonString!=null && jsonString.length()>0) {  // TODO VK: а что в else-ветви?
            try {
                jsonObject = new JSONObject(jsonString);
                array = jsonObject.getJSONArray("crosswordWord"); //TODO VK: array -плохле название переменной
                cwords = new CrosswordWord[array.length()];
                horCount = (Integer) jsonObject.get("horCount");
            } catch (JSONException ex) {
                ex.printStackTrace();
                throw new RuntimeException("Crossword JSON to string unsuccessful");
            }
            for (int i = 0; i < cwords.length; i++)
                try { // TODO VK array.getJSONObject(i) стоит выделить в переменную. И понятнее, и работает быстрее
                    cwords[i] = new CrosswordWord(array.getJSONObject(i).getString("question"),
                            array.getJSONObject(i).getString("word"),
                            array.getJSONObject(i).getInt("posX"),
                            array.getJSONObject(i).getInt("posY"));

                } catch (JSONException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException("Crossword JSON search unsuccessful");
                }
        }
        return new Crossword(cwords, horCount);
    }

    static String[] parseAnswersFromJson(String jsonString) {
        String[] answers;
        if (jsonString!=null && jsonString.length()>0) {
            JSONObject jsonObject;
            JSONArray array;
            try {
                jsonObject = new JSONObject(jsonString);
                array = jsonObject.getJSONArray(answersArrayName);
                answers = new String[array.length()];
            } catch (JSONException ex) {
                ex.printStackTrace();
                throw new RuntimeException("Answers JSON to string unsuccessful");
            }
            for (int i = 0; i < answers.length; i++)
                try {
                    answers[i] = array.getJSONObject(i).getString("answer");
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
