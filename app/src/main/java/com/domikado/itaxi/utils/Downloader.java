package com.domikado.itaxi.utils;

import com.activeandroid.util.IOUtils;

import java.io.File;
import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class Downloader {

    private OkHttpClient mOkHttpClient = new OkHttpClient();
    
    public File download(Request request, File downloadedFile) throws IOException {
        Response response = mOkHttpClient.newCall(request).execute();
        BufferedSink sink = Okio.buffer(Okio.sink(downloadedFile));
        sink.writeAll(response.body().source());

        IOUtils.closeQuietly(sink);
        IOUtils.closeQuietly(response.body());
//        IOUtils.closeQuietly(response.body().source());

        return downloadedFile;
    }
}
