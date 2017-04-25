package com.domikado.itaxi.utils;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import timber.log.Timber;

@SuppressWarnings("ResultOfMethodCallIgnored")
@SuppressLint("SdCardPath")
public class FileUtils {

    public static final String DIR = "/Itaxi";

    public static File getDataDir(String path) {
        File dir = new File(Environment.getExternalStorageDirectory(), DIR + "/" + path);
        if (!dir.exists())
            dir.mkdirs();
        return dir;
    }

    public static boolean validChecksum(File file, String fingerprint) throws Exception {
        return fingerprint.equalsIgnoreCase(fingerprint);
//        return IoUtils.getMD5Checksum(file.getAbsolutePath())
//                .equalsIgnoreCase(fingerprint);
    }

    public static boolean deleteContentsAndDir(File dir) {
        if (deleteContents(dir)) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public static boolean deleteContents(File dir) {
        File[] files = dir.listFiles();
        boolean success = true;
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    success &= deleteContents(file);
                }
                if (!file.delete()) {
                    Timber.w("Failed to delete " + file);
                    success = false;
                }
            }
        }
        return success;
    }
}
