package com.dbkudryavtsev.childrencrosswords.utilities;

import android.content.Context;

import com.dbkudryavtsev.childrencrosswords.R;
import com.dbkudryavtsev.childrencrosswords.models.Crossword;

import java.io.File;

import static com.dbkudryavtsev.childrencrosswords.utilities.AnswersParser.parseAnswersFromJson;
import static com.dbkudryavtsev.childrencrosswords.utilities.AnswersWriter.convertAnswersToJSON;
import static com.dbkudryavtsev.childrencrosswords.utilities.CrosswordsParser.parseCrosswordFromJson;

import static com.dbkudryavtsev.childrencrosswords.utilities.CrosswordsDownloader.downloadCrosswords;
import static com.dbkudryavtsev.childrencrosswords.utilities.LocalRepository.loadJSONFromFile;
import static com.dbkudryavtsev.childrencrosswords.utilities.LocalRepository.writeStringToJSONFile;

public final class LocalCrosswordsRepository {

    private int crosswordsCount;
    private int[] completenesses;

    public LocalCrosswordsRepository(Context context) {
        updateCrosswordsCount(context);
    }

    public void deleteAnswers(Context context){
        File contentsDirectory = new File(context.getFilesDir().getAbsolutePath());
        for (File file: contentsDirectory.listFiles()){
            if(file.getName().contains(context.getString(R.string.answers_file_name)))
                if(!file.delete()) {
                    throw new RuntimeException();
                }
        }
        updateCompletenesses(context);
    }

    public String[] getAnswers(int chosenCrosswordId, Context context) {
        String filename = context.getString(R.string.resource_file_name_template,
                context.getString(R.string.answers_file_name), chosenCrosswordId);
        String jsonString = loadJSONFromFile(filename, context);
        String[] answers = parseAnswersFromJson(jsonString);
        if(answers == null){
            answers = new String[getCrossword(chosenCrosswordId, context).getCwordsLength()];
            for(int i = 0; i<answers.length; i++)
                answers[i] = "";
        }
        return answers;
    }

    public Crossword getCrossword(int chosenCrosswordId, Context context) {
        String filename = context.getString(R.string.resource_file_name_template,
                context.getString(R.string.crossword_file_name), chosenCrosswordId);
        String jsonString = loadJSONFromFile(filename , context);
        return parseCrosswordFromJson(jsonString);
    }

    public int getCrosswordsCount() {
        return crosswordsCount;
    }

    public int getCompleteness(int crosswordId){
        return completenesses[crosswordId];
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
        updateCompletenesses(context);
    }

    public void putAnswers(String[] answers, int chosenCrosswordId, Context context) {
        writeStringToJSONFile(convertAnswersToJSON(answers),
                context.getString(R.string.resource_file_name_template,
                        context.getString(R.string.answers_file_name),
                        chosenCrosswordId), context);
        updateCompleteness(chosenCrosswordId, context);
    }

    private void updateCompleteness(int fileId, Context context) {
        completenesses[fileId] = 0;
        String[] answers = getAnswers(fileId, context);
        for (String answer : answers)
            completenesses[fileId] += (answer.equals("")) ? 0 : 1 / ((float) answers.length) * 100.;
    }

    private void updateCompletenesses(Context context) {
        completenesses = new int[crosswordsCount];
        for(int i=0; i<crosswordsCount; i++) {
            String[] answers = getAnswers(i, context);
            for (String answer : answers)
                completenesses[i] += (answer.equals("")) ? 0 : 1 / ((float) answers.length) * 100.;
        }
    }

    public void updateCrosswords(Context context){
        downloadCrosswords(context);
        updateCompletenesses(context);
    }
}