package com.dbkudryavtsev.childrencrosswords.Utilities;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.commons.io.IOUtils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

class AsyncTasks {

    static class DownloadParams{
        String url, name;
        Context context;

        DownloadParams(String url, String name, Context context) {
            this.url = url;
            this.name = name;
            this.context = context;
        }
    }

    static class DownloadCrosswords extends AsyncTask<DownloadParams, Void, Boolean>{

        @Override
        protected Boolean doInBackground(DownloadParams... params) {
            try {
                URL u = new URL(params[0].url);
                DataInputStream stream = new DataInputStream(u.openStream());
                byte[] buffer = IOUtils.toByteArray(stream);
                FileOutputStream fos = new FileOutputStream (new File(params[0].name), true);
                fos.write(buffer);
                fos.flush();
                fos.close();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
                return true;
            }
            return false;
        }
    }

}
