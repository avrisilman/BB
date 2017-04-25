package com.domikado.itaxi.utils;

import com.activeandroid.util.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import timber.log.Timber;

public class Compress {
    private static final int BUFFER = 2048;

    private String[] _files;
    private String _zipFile;

    public Compress(String[] files, String zipFile) {
        _files = files;
        _zipFile = zipFile;
    }

    public void zip() {
        ZipOutputStream out = null;

        try {
            BufferedInputStream origin;
            out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(_zipFile)));

            byte data[] = new byte[BUFFER];

            for (String _file : _files) {
                Timber.d("Adding: " + _file);
                FileInputStream fi = new FileInputStream(_file);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(_file.substring(_file.lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                IOUtils.closeQuietly(origin);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(out);
        }
    }
}
