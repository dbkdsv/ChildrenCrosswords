package com.dbkudryavtsev.childrencrosswords.utilities;

import android.content.Context;
import android.support.annotation.NonNull;

import com.dbkudryavtsev.childrencrosswords.R;
import com.dbkudryavtsev.childrencrosswords.models.Crossword;
import com.dbkudryavtsev.childrencrosswords.models.CrosswordWord;

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

// TODO: это тоже не билдер
public final class ResourcesBuilder {

    //TODO: вынести в отдельный класс, написать на него unit-теста
    @NonNull
    static Crossword parseCrosswordFromJson(String jsonString) {
        JSONObject jsonObject;
        JSONArray array = new JSONArray();
        CrosswordWord[] cwords = new CrosswordWord[]{};
        int horCount = 0;
        if (jsonString!=null && jsonString.length()>0) {
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

    static String loadJSONFromFile(String fileName, Context context) {
        String json = "";
        File inputFile = new File(context.getFilesDir(), fileName + context.getString(R.string.json_extension));
        if (inputFile.exists()) {
            try {
                InputStream inputStream = new FileInputStream(inputFile);
                int size = inputStream.available();
                byte[] buffer = new byte[size];
                if(inputStream.read(buffer)<1) throw new IOException();
                inputStream.close();
                json = new String(buffer, "UTF-8");
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return json;
    }

    static boolean unpackFile(Context context, File resources) {
        try {
            ZipFile zipFile = new ZipFile(resources.getPath());
            zipFile.extractAll(context.getFilesDir().getPath());
            if(!zipFile.getFile().delete()){
                throw new ZipException();
            }
        } catch (ZipException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
