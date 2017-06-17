package com.dbkudryavtsev.childrencrosswords.utilities;

class AnswersWriter {
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
