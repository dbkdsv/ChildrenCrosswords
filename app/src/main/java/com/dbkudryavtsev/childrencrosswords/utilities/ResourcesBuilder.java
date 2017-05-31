package com.dbkudryavtsev.childrencrosswords.utilities;

import android.content.Context;
import android.widget.Toast;

import com.dbkudryavtsev.childrencrosswords.R;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutionException;

public class ResourcesBuilder {

    private static final String answersFileName = "answers.json";

    public static void writeToAnswerFile(String[] answers, int chosenRectId, Context context) {
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
            Toast toast = Toast.makeText(context, "Ошибка записи ответов в файл.",
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
        downloadFile(context.getString(R.string.zip_download_link),
                resources.getPath(), context);
        try {
            ZipFile zipFile = new ZipFile(resources.getPath());
            zipFile.extractAll(context.getFilesDir().getPath());
            if(!zipFile.getFile().delete()){
                throw new ZipException();
            }
        } catch (ZipException e) {
            toast = Toast.makeText(context, "Ошибка распаковки архива.",
                    Toast.LENGTH_LONG);
            toast.show();
            e.printStackTrace();
        }
    }
}
