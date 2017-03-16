package com.dbkudryavtsev.ccw.childrencrosswords.main.Utilities;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.dbkudryavtsev.ccw.childrencrosswords.main.Models.Crossword;
import com.dbkudryavtsev.ccw.childrencrosswords.main.Models.CrosswordWord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;

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

    public static Crossword getCrossword(int chosenRectId, Context context) {
        String jsonString=loadJSONFromAsset(chosenRectId, context);
        JSONObject jsonObject;
        JSONArray array = new JSONArray();
        CrosswordWord[] cwords=new CrosswordWord[]{};
        int horCount=0;
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
        return new Crossword(cwords, horCount);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String[] getAnswers(int chosenRectId, Context context){
        StringBuilder stringBuilder = new StringBuilder();
        String jsonString="";
        try {
            File file = new File(context.getFilesDir(), answersFileName.substring(0,
                    answersFileName.length() - 5) + Integer.toString(chosenRectId)+
                    answersFileName.substring(answersFileName.length()-5, answersFileName.length()));
            InputStream inputStream = new FileInputStream(file);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            jsonString= new String(buffer, "UTF-8");
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        JSONObject jsonObject;
        JSONArray array = new JSONArray();
        String[] answers=new String[]{};
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
            }
            catch (JSONException ex) {
                ex.printStackTrace();
            }
        return answers;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void writeToJson(String[] answers, int chosenRectId, Context context) {
        FileOutputStream outputStream;
        String answersString="";
        answersString+="{\n\"answers\": [";
        for(int i=0; i<answers.length; i++){
            answersString+="{\n\"answer\": \""+answers[i]+"\""+"}";
            if(i!=answers.length-1) answersString+=",\n";
        }
        answersString+="]\n}";
        try {
            File file = new File(context.getFilesDir(),answersFileName.substring(0,answersFileName.length()-5)+
                    Integer.toString(chosenRectId)+answersFileName.substring(answersFileName.length()-5,
                    answersFileName.length()));
            outputStream = context.openFileOutput(answersFileName.substring(0, answersFileName.length()-5)+
                    Integer.toString(chosenRectId)+answersFileName.substring(answersFileName.length()-5,
                    answersFileName.length()), Context.MODE_PRIVATE);
            outputStream.write(answersString.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createResourceFiles(Context context){

    }
}
