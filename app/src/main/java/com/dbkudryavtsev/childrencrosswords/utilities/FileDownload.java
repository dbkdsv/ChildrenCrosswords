package com.dbkudryavtsev.childrencrosswords.utilities;

import android.os.AsyncTask;

import org.apache.commons.io.IOUtils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

final class FileDownload {

    private static final class DownloadParams{
        final String url;
        final String name;

        DownloadParams(String url, String name) {
            this.url = url;
            this.name = name;
        }
    }

    private class FileDownloadTask extends AsyncTask<DownloadParams, Void, Void>{

        @Override
        protected Void doInBackground(DownloadParams... params) {
            try {
                DownloadParams param = params[0];
                URL url = new URL(param.url);
                // TODO: использвать try with resources!!!
                DataInputStream stream = new DataInputStream(url.openStream());
                byte[] buffer = IOUtils.toByteArray(stream);
                FileOutputStream fos = new FileOutputStream (new File(param.name), true);
                fos.write(buffer);
                fos.close();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed in background.");
            }
            return null;
        }
    }

    boolean download(String url, String name) {
        try {
            new FileDownload.FileDownloadTask()
                    .execute(new FileDownload.DownloadParams(url, name))
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed while downloading.");
        }
        return true;
    }
}