package com.dbkudryavtsev.childrencrosswords.utilities;

import android.content.Context;

import com.dbkudryavtsev.childrencrosswords.R;
import com.dbkudryavtsev.childrencrosswords.models.Crossword;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

import static com.dbkudryavtsev.childrencrosswords.utilities.ResourcesBuilder.loadJSONFromFile;
import static com.dbkudryavtsev.childrencrosswords.utilities.ResourcesBuilder.parseCrosswordFromJson;
import static com.dbkudryavtsev.childrencrosswords.utilities.ResourcesBuilder.unpackFile;

public final class LocalCrosswordsRepository {

    private int crosswordsCount;
    private int[] completenesses;

    public LocalCrosswordsRepository(Context context) {
        updateCrosswordsCount(context);
        updateCompletenesses(context);
    }

    public Crossword getCrossword(int chosenCrosswordId, Context context) {
        String filename = context.getString(R.string.resource_file_name_template,
                context.getString(R.string.crossword_file_name), chosenCrosswordId);
        String jsonString = loadJSONFromFile(filename , context);
        return parseCrosswordFromJson(jsonString);
    }

    private void updateCompletenesses(Context context) {
        completenesses = new int[crosswordsCount];
        for(int i=0; i<crosswordsCount; i++) {
            String[] answers = getAnswers(i, context);
            for (String answer : answers)
                completenesses[i] += (answer.equals("")) ? 0 : 1 / ((float) answers.length) * 100.;
        }
    }

    private void updateCompleteness(int fileId, Context context) {
        completenesses[fileId] = 0;
        String[] answers = getAnswers(fileId, context);
        for (String answer : answers)
            completenesses[fileId] += (answer.equals("")) ? 0 : 1 / ((float) answers.length) * 100.;
    }

    public int getCompleteness(int crosswordId){
        return completenesses[crosswordId];
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
                array = jsonObject.getJSONArray(context.getString(R.string.answers_file_name));
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

    public boolean putAnswers(String[] answers, int chosenCrosswordId, Context context) {
        String answersString = context.getString(R.string.answers_json_start_string);
        for (int i = 0; i < answers.length; i++) {
            answersString += "{\n\"answer\": \"" + answers[i] + "\"" + "}";
            if (i != answers.length - 1) answersString += ",\n";
        }
        answersString += context.getString(R.string.answers_json_finish_string);
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(
                    context.getString(R.string.resource_file_name_template,
                            context.getString(R.string.answers_file_name),
                            chosenCrosswordId) + context.getString(R.string.json_extension),
                    Context.MODE_PRIVATE);
            outputStream.write(answersString.getBytes());
            outputStream.close();
            // TODO try with resources
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
        updateCompleteness(chosenCrosswordId, context);
        return false;
    }

    public boolean deleteAnswers(Context context){
        File contentsDirectory = new File(context.getFilesDir().getAbsolutePath());
        for (File file: contentsDirectory.listFiles()){
            if(file.getName().contains(context.getString(R.string.answers_file_name)))
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
                if (file.getName()
                        .contains(context.getString(R.string.crossword_file_name)))
                    crosswordsCount++;
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