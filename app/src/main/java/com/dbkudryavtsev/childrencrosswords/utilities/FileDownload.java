package com.dbkudryavtsev.childrencrosswords.utilities;

import android.os.AsyncTask;

import org.apache.commons.io.IOUtils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public final class FileDownload {

    private static final class DownloadParams{
        final String url;
        final String name;

        DownloadParams(String url, String name) {
            this.url = url;
            this.name = name;
        }
    }

    private class FileDownloadTask extends AsyncTask<DownloadParams, Void, Boolean>{

        @Override
        protected Boolean doInBackground(DownloadParams... params) {
            try {
                DownloadParams param = params[0];
                URL url = new URL(param.url);
                // TODO: использвать try with resources!!!
                DataInputStream stream = new DataInputStream(url.openStream());
                byte[] buffer = IOUtils.toByteArray(stream);
                FileOutputStream fos = new FileOutputStream (new File(param.name), true);
                fos.write(buffer);
                //fos.flush();
                fos.close();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
                return true;
            }
            return false;
        }
    }

    boolean download(String url, String name) {
        boolean result;
        try {
            result = new FileDownload.FileDownloadTask()
                    .execute(new FileDownload.DownloadParams(url, name))
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return true;
        }
        return result;
    }
}