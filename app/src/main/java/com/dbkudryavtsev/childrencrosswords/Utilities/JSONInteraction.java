package com.dbkudryavtsev.childrencrosswords.Utilities;

import android.content.Context;
import android.os.Environment;

import com.dbkudryavtsev.childrencrosswords.Models.Crossword;
import com.dbkudryavtsev.childrencrosswords.Models.CrosswordWord;
import com.dbkudryavtsev.childrencrosswords.R;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;

public final class JSONInteraction {
    private static final String answersFileName = "answers.json";

    public static String loadJSONFromAsset(int chosenRectId, Context context) {
        String json;
        try {
            InputStream inputStream = context.getResources().openRawResource(chosenRectId);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

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
                ex.printStackTrace();
            }
            for (int i = 0; i < cwords.length; i++)
                try {
                    cwords[i] = new CrosswordWord(array.getJSONObject(i).getString("question"),
                            array.getJSONObject(i).getString("word"),
                            array.getJSONObject(i).getInt("posX"),
                            array.getJSONObject(i).getInt("posY"));

                } catch (JSONException ex) {
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
                ex.printStackTrace();
            }
            for (int i = 0; i < answers.length; i++)
                try {
                    answers[i] = array.getJSONObject(i).getString("answer");
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
        } else {
            jsonString = loadJSONFromFile(chosenRectId,"crossword", context);
            try {
                jsonObject = new JSONObject(jsonString);
                array = jsonObject.getJSONArray("crosswordWord");
                answers = new String[array.length()];
            } catch (JSONException ex) {
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
            e.printStackTrace();
        }
    }

    private static void downloadFile(String _url, String _name, Context context) {
        try {
            URL u = new URL(_url);
            DataInputStream stream = new DataInputStream(u.openStream());
            byte[] buffer = IOUtils.toByteArray(stream);
            FileOutputStream fos = context.openFileOutput(_name, Context.MODE_PRIVATE);
            fos.write(buffer);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createResourceFiles(Context context) {
//        File resources = new File(context.getFilesDir(), "raw.zip");
//        downloadFile("https://drive.google.com/file/d/0B8tU6dTdHawEd2ZBc0p4TW10UU0/view?usp=sharing", resources.getPath(), context);
//        try {
//            ZipFile zipFile = new ZipFile(resources.getPath());
//            zipFile.extractAll(context.getFilesDir().getPath());
//        } catch (ZipException e) {
//            e.printStackTrace();
//        }
        Field[] fields=R.raw.class.getFields();
        FileOutputStream outputStream;
        String jsonString;
        int crosswordCount=0;
        for(int count=0; count < fields.length; count++){
            if(fields[count].getName().contains("crossword")){
                try {
                    outputStream = context.openFileOutput("crossword"+crosswordCount+".json", Context.MODE_PRIVATE);
                    jsonString = loadJSONFromAsset(fields[count].getInt(fields[count]), context);
                    outputStream.write(jsonString.getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                crosswordCount++;
            }
        }
    }
}
