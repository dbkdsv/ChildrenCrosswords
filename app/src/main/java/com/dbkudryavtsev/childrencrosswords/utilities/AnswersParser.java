package com.dbkudryavtsev.childrencrosswords.utilities;

import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

class AnswersParser {

    static String[] parseAnswersFromJson(String jsonString) {
        final String answersArrayName = "answers";
        String[] answers;
        if (jsonString != null && jsonString.length() > 0) {
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

    static String[] parseAnswersFromSQL(Cursor cursor){
        final String answerColumnString = "answer";
        ArrayList<String> answersList = new ArrayList<>();
        if (cursor.moveToFirst()){
            int answer = cursor.getColumnIndex(answerColumnString);
            do{
                answersList.add(cursor.getString(answer));
            } while(cursor.moveToNext());
        }
        else return null;
        return answersList.toArray(new String[0]);
    }
}
