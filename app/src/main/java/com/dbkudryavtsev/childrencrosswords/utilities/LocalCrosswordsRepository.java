package com.dbkudryavtsev.childrencrosswords.utilities;

import android.content.Context;
import android.support.annotation.NonNull;

import com.dbkudryavtsev.childrencrosswords.R;
import com.dbkudryavtsev.childrencrosswords.models.Crossword;
import com.dbkudryavtsev.childrencrosswords.models.CrosswordWord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import static com.dbkudryavtsev.childrencrosswords.utilities.ResourcesBuilder.loadJSONFromFile;
import static com.dbkudryavtsev.childrencrosswords.utilities.ResourcesBuilder.unpackFile;

public final class LocalCrosswordsRepository {

    private int crosswordsCount;
    private int[] answers;

    public LocalCrosswordsRepository(Context context) {
        updateCrosswordsCount(context);
    }

    public Crossword getCrossword(int chosenCrosswordId, Context context) {
        String filename = context.getString(R.string.resource_file_name_template,
                context.getString(R.string.crossword_file_name), chosenCrosswordId);
        String jsonString = loadJSONFromFile(filename , context);
        return parseCrosswordFromJson(jsonString);
    }

    //TODO: вынести в отдельный класс, написать на него unit-теста
    @NonNull
    private static Crossword parseCrosswordFromJson(String jsonString) {
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

    public String[] getAnswers(int chosenCrosswordId, Context context) {
        String filename = context.getString(R.string.resource_file_name_template,
                context.getString(R.string.answers_file_name), chosenCrosswordId);
        String jsonString = loadJSONFromFile(filename, context);
        String[] answers = new String[]{};
        JSONObject jsonObject;
        JSONArray array = new JSONArray();
        if (jsonString!=null && jsonString.length()>0) {
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
        }
        else {
             filename = context.getString(R.string.resource_file_name_template,
                    context.getString(R.string.crossword_file_name), chosenCrosswordId);
            jsonString = loadJSONFromFile(filename, context);
            if (jsonString != null && jsonString.length() > 0) {
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
        }
        return answers;
    }

    public boolean deleteAnswers(Context context){
        File contentsDirectory = new File(context.getFilesDir().getAbsolutePath());
        for (File file: contentsDirectory.listFiles()){
            if(file.getName().contains("answers"))
                if(!file.delete()) {
                    return true;
                }
        }
        return false;
    }

    public int getCrosswordsCount() {
        return crosswordsCount;
    }

    private void updateCrosswordsCount(Context context){
        crosswordsCount = 0;
        File filesDirectory = context.getFilesDir();
        if(filesDirectory.length()!=0) {
            for (File file : filesDirectory.listFiles())
                if (file.getName().contains("crossword")) crosswordsCount++;
        }
    }

    public void downloadCrosswords(Context context) {
        File resources = new File(context.getFilesDir(), context.getString(R.string.zip_file_name));
        FileDownload downloader = new FileDownload();
        downloader.download(context.getString(R.string.zip_download_link), resources.getPath());
        unpackFile(context, resources);
        updateCrosswordsCount(context);
    }
}