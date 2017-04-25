package com.domikado.itaxi.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import timber.log.Timber;

public class ZipUtils {

    private static String getPath(File zipFile, String name) {
        return String.format("%s/%s/", zipFile.getParent(), name);
    }

    public static boolean unzip(File file, String name) {
        Timber.d("Unzipping for: " + file.getPath());

        String path = getPath(file, name);

        InputStream is;
        ZipInputStream zis;
        try {
            String filename;
            is = new FileInputStream(file);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null) {
                filename = ze.getName();

                if (ze.isDirectory()) {
                    File fmd = new File(path + filename);
                    fmd.mkdirs();
                    continue;
                }

                FileOutputStream fout = new FileOutputStream(path + filename);
                while ((count = zis.read(buffer)) != -1) {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();
            }

            zis.close();
        } catch (IOException e) {
            Timber.e(e);
            return false;
        }
        return true;
    }
}
