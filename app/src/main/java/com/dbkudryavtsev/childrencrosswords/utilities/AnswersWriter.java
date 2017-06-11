package com.dbkudryavtsev.childrencrosswords.utilities;

/**
 * Created by User on 12.06.2017.
 */

public class AnswersWriter {
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
