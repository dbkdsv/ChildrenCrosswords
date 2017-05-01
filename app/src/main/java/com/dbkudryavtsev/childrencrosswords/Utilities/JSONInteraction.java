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

    public static void writeToJson(String[] answers, int chosenRectId, Context context) {
        FileOutputStream outputStream;
        String answersString = "{\n\"answers\": [";
        for (int i = 0; i < answers.length; i++) {
            answersString += "{\n\"answer\": \"" + answers[i] + "\"" + "}";
            if (i != answers.length - 1) answersString += ",\n";
        }
        answersString += "]\n}";
        try {
            outputStream = context.openFileOutput(answersFileName.substring(0, answersFileName.length() - 5) +
                    Integer.toString(chosenRectId) + answersFileName.substring(answersFileName.length() - 5,
                    answersFileName.length()), Context.MODE_PRIVATE);
            outputStream.write(answersString.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Toast toast = Toast.makeText(context, "Ошибка записи ответов в JSON.",
                    Toast.LENGTH_LONG);
            toast.show();
            e.printStackTrace();
        }
    }

    private static void downloadFile(String _url, String _name, Context context) {
        Boolean result = true;
        try {
            result = new AsyncTasks.DownloadCrosswords().execute(new AsyncTasks.DownloadParams(_url, _name, context)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        Toast toast;
        if(result) {
            toast = Toast.makeText(context, "Ошибка скачивания кроссвордов.",
                    Toast.LENGTH_SHORT);
        }
        else{
            toast = Toast.makeText(context, "Скачивание завершено успешно.",
                    Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    public static void createResourceFiles(Context context) {
        Toast toast = Toast.makeText(context, "Дождитесь завершения скачивания.", Toast.LENGTH_LONG);
        toast.show();
        File resources = new File(context.getFilesDir(), "raw.zip");
        downloadFile("https://drive.google.com/uc?export=download&id=0B8tU6dTdHawETXk0NHVtUkp0VDg",
                resources.getPath(), context);
        try {
            ZipFile zipFile = new ZipFile(resources.getPath());
            zipFile.extractAll(context.getFilesDir().getPath());
            zipFile.getFile().delete();
        } catch (ZipException e) {
            toast = Toast.makeText(context, "Ошибка распаковки архива.",
                    Toast.LENGTH_LONG);
            toast.show();
            e.printStackTrace();
        }
    }
}
