package com.dbkudryavtsev.childrencrosswords.utilities;

import android.os.AsyncTask;

import org.apache.commons.io.IOUtils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
//TODO VK плохое название класса
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
            DownloadParams param = params[0];
            URL url;
            try {
                url = new URL(param.url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                throw new RuntimeException("URL assignment unsuccessful.");
            }
            try(FileOutputStream fos = new FileOutputStream (new File(param.name), true)) {
                DataInputStream stream = new DataInputStream(url.openStream()); //TODO VK пльлк не закрываеся
                byte[] buffer = IOUtils.toByteArray(stream);
                fos.write(buffer);
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