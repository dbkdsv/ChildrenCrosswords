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

class LocalRepository {

   static String loadJSONFromFile(String filename, Context context) {
       String json = "";
       File inputFile = new File(context.getFilesDir(), filename + context.getString(R.string.json_extension));
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
               throw new RuntimeException("JSON load unsuccessful");
           }
       }
       return json;
   }

   static void unpackFile(Context context, File resources) {
       try {
           ZipFile zipFile = new ZipFile(resources.getPath());
           zipFile.extractAll(context.getFilesDir().getPath());
           if(!zipFile.getFile().delete()){
               throw new ZipException();
           }
       } catch (ZipException e) {
           e.printStackTrace();
           throw new RuntimeException("ZIP unpack unsuccessful");
       }
   }

    static void writeStringToJSONFile(String JSONString, String filename, Context context) {
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(
                    filename + context.getString(R.string.json_extension),
                    Context.MODE_PRIVATE);
            outputStream.write(JSONString.getBytes());
            outputStream.close();
            // TODO try with resources
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("JSON output unsuccessful");
        }
    }

}
