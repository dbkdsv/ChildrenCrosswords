package com.dbkudryavtsev.childrencrosswords.utilities;

import android.os.AsyncTask;

import org.apache.commons.io.IOUtils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

final class FileDownload {

    static final class DownloadParams{
        final String url;
        final String name;

        DownloadParams(String url, String name) {
            this.url = url;
            this.name = name;
        }
    }

    static class FileDownloadTask extends AsyncTask<DownloadParams, Void, Boolean>{

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