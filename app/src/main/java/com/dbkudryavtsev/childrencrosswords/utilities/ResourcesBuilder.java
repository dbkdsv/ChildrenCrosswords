package com.dbkudryavtsev.childrencrosswords.utilities;

import android.content.Context;
import android.widget.Toast;

import com.dbkudryavtsev.childrencrosswords.R;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutionException;

// TODO: это тоже не билдер
public final class ResourcesBuilder {

    private static final String answersFileName = "answers.json";

    public static void writeToAnswerFile(String[] answers, int chosenRectId, Context context) {
        FileOutputStream outputStream;
        String answersString = context.getString(R.string.json_start_string);
        for (int i = 0; i < answers.length; i++) {
            answersString += "{\n\"answer\": \"" + answers[i] + "\"" + "}";
            if (i != answers.length - 1) answersString += ",\n";
        }
        answersString += "]\n}";
        try {
            // TODO: В репозиторий (этот класс должен принимать экземляр репозитория и использовать его)

            outputStream = context.openFileOutput(answersFileName.substring(0, answersFileName.length() - 5) +
                    Integer.toString(chosenRectId) + answersFileName.substring(answersFileName.length() - 5,
                    answersFileName.length()), Context.MODE_PRIVATE);
            outputStream.write(answersString.getBytes());
            outputStream.close();
            // TODO try with resources
        } catch (Exception e) {
            // TODO убрать работу с UI
            Toast toast = Toast.makeText(context, "Ошибка записи ответов в файл.",
                    Toast.LENGTH_LONG);
            toast.show();
            e.printStackTrace();
        }
    }

    private static void downloadFile(String url, String name, Context context) {
        Boolean result = true;
        try {
            result = new FileDownload.FileDownloadTask()
                    .execute(new FileDownload.DownloadParams(url, name))
                    .get();
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

    public static void downloadCrosswords(Context context) {
        Toast.makeText(context, "Дождитесь завершения скачивания.", Toast.LENGTH_LONG).show();

        File resources = new File(context.getFilesDir(), context.getString(R.string.zip_file_name));
        downloadFile(context.getString(R.string.zip_download_link),
                resources.getPath(), context);
        unpackFile(context, resources);
    }

    private static void unpackFile(Context context, File resources) {
        try {
            ZipFile zipFile = new ZipFile(resources.getPath());
            zipFile.extractAll(context.getFilesDir().getPath());
            if(!zipFile.getFile().delete()){
                throw new ZipException();
            }
        } catch (ZipException e) {
            Toast.makeText(context, "Ошибка распаковки архива.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
