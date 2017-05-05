package com.dbkudryavtsev.childrencrosswords.Utilities;

import android.content.Context;
import android.widget.Toast;

import com.dbkudryavtsev.childrencrosswords.Models.Crossword;
import com.dbkudryavtsev.childrencrosswords.Models.CrosswordWord;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

public final class JSONInteraction {

    private static final String answersFileName = "answers.json";

    public static String loadJSONFromFile(int chosenRectId, String fileName, Context context) {
        String json = "";
        File inputFile = new File(context.getFilesDir(), fileName + chosenRectId + ".json");
        if (inputFile.exists()) {
            try {
                InputStream inputStream = new FileInputStream(inputFile);
                int size = inputStream.available();
                byte[] buffer = new byte[size];
                inputStream.read(buffer);
                inputStream.close();
                json = new String(buffer, "UTF-8");
            } catch (IOException ex) {
                Toast toast = Toast.makeText(context, "Не могу загрузить кроссворды из файла.", Toast.LENGTH_LONG);
                toast.show();
                ex.printStackTrace();
                return null;
            }
        }
        return json;
    }

    public static Crossword getCrossword(int chosenRectId, Context context) {
        String jsonString = loadJSONFromFile(chosenRectId,"crossword", context);
        JSONObject jsonObject;
        JSONArray array = new JSONArray();
        CrosswordWord[] cwords = new CrosswordWord[]{};
        int horCount = 0;
        if (jsonString.length()>0) {
            try {
                jsonObject = new JSONObject(jsonString);
                array = jsonObject.getJSONArray("crosswordWord");
                cwords = new CrosswordWord[array.length()];
                horCount = (Integer) jsonObject.get("horCount");
            } catch (JSONException ex) {
                Toast toast = Toast.makeText(context, "Не могу создать JSON-объект для получения кроссворда.",
                        Toast.LENGTH_LONG);
                toast.show();
                ex.printStackTrace();
            }
            for (int i = 0; i < cwords.length; i++)
                try {
                    cwords[i] = new CrosswordWord(array.getJSONObject(i).getString("question"),
                            array.getJSONObject(i).getString("word"),
                            array.getJSONObject(i).getInt("posX"),
                            array.getJSONObject(i).getInt("posY"));

                } catch (JSONException ex) {
                    Toast toast = Toast.makeText(context, "Ошибка обращения к JSON-объекту с кроссвордами.",
                            Toast.LENGTH_LONG);
                    toast.show();
                    ex.printStackTrace();
                }
        }
        return new Crossword(cwords, horCount);
    }

    public static String[] getAnswers(int chosenRectId, Context context) {
        String jsonString = loadJSONFromFile(chosenRectId, "answers", context);
        String[] answers = new String[]{};
        JSONObject jsonObject;
        JSONArray array = new JSONArray();
        if (jsonString.length()>0) {
            try {
                jsonObject = new JSONObject(jsonString);
                array = jsonObject.getJSONArray("answers");
                answers = new String[array.length()];
            } catch (JSONException ex) {
                Toast toast = Toast.makeText(context, "Не могу создать JSON-объект для получения ответов.",
                        Toast.LENGTH_LONG);
                toast.show();
                ex.printStackTrace();
            }
            for (int i = 0; i < answers.length; i++)
                try {
                    answers[i] = array.getJSONObject(i).getString("answer");
                } catch (JSONException ex) {
                    Toast toast = Toast.makeText(context, "Ошибка обращения к JSON-объекту с ответами.",
                            Toast.LENGTH_LONG);
                    toast.show();
                    ex.printStackTrace();
                }
        } else {
            jsonString = loadJSONFromFile(chosenRectId,"crossword", context);
            try {
                jsonObject = new JSONObject(jsonString);
                array = jsonObject.getJSONArray("crosswordWord");
                answers = new String[array.length()];
            } catch (JSONException ex) {
                Toast toast = Toast.makeText(context, "Ошибка чтения длины кроссвордов.",
                        Toast.LENGTH_LONG);
                toast.show();
                ex.printStackTrace();
            }
            for (int i = 0; i < answers.length; i++)
                answers[i] = "";
        }
        return answers;
    }
}
