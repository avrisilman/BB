package com.domikado.itaxi.utils;

import android.util.Base64;

import java.security.SecureRandom;

public class TokenGenerator {

    private SecureRandom mSecureRandom = new SecureRandom();

    public String nextString() {
        byte[] output = new byte[64];
        mSecureRandom.nextBytes(output);
        return Base64.encodeToString(output, Base64.DEFAULT)
            .replace(System.getProperty("line.separator"), "")
            .replace("\n", "");
    }
}