package com.dbkudryavtsev.childrencrosswords.utilities;

import android.content.Context;

import com.dbkudryavtsev.childrencrosswords.R;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

// TODO: это тоже не билдер
public final class ResourcesBuilder {

    private static final String answersFileName = "answers.json";

    public static boolean writeToAnswerFile(String[] answers, int chosenRectId, Context context) {
        FileOutputStream outputStream;
        String answersString = context.getString(R.string.answers_json_start_string);
        for (int i = 0; i < answers.length; i++) {
            answersString += "{\n\"answer\": \"" + answers[i] + "\"" + "}";
            if (i != answers.length - 1) answersString += ",\n";
        }
        answersString += context.getString(R.string.answers_json_finish_string);
        try {
            // TODO: В репозиторий (этот класс должен принимать экземляр репозитория и использовать его)

            outputStream = context.openFileOutput(answersFileName.substring(0, answersFileName.length() - 5) +
                    Integer.toString(chosenRectId) + answersFileName.substring(answersFileName.length() - 5,
                    answersFileName.length()), Context.MODE_PRIVATE);
            outputStream.write(answersString.getBytes());
            outputStream.close();
            // TODO try with resources
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
        return false;
    }

    //TODO переписать логику формирования имени файла на более явную
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
