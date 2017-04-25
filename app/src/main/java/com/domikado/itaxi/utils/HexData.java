package com.domikado.itaxi.utils;

public class HexData {

    public static final String HEXES = "0123456789ABCDEF";
    public static final String HEX_INDICATOR = "0x";
    public static final String SPACE = " ";

    public static String bytesToString(byte[] data) {
        if (data != null) {
            StringBuilder hex = new StringBuilder(2 * data.length);
            for (int i = 0; i <= data.length - 1; i++) {
                byte dataAtIndex = data[i];
                hex.append(HEX_INDICATOR);
                hex.append(HEXES.charAt((dataAtIndex & 0xF0) >> 4))
                    .append(HEXES.charAt((dataAtIndex & 0x0F)));
                hex.append(SPACE);
            }
            return hex.toString();
        } else {
            return null;
        }
    }

    public static byte[] stringToBytes(String hexString) {
        String stringProcessed = hexString.trim().replaceAll("0x", "");
        stringProcessed = stringProcessed.replaceAll("\\s+","");

        byte[] data = new byte[stringProcessed.length()/2];
        int i = 0;
        int j = 0;
        while (i <= stringProcessed.length() - 1) {
            byte character = (byte) Integer.parseInt(stringProcessed.substring(i, i + 2), 16);
            data[j] = character;
            j++;
            i += 2;
        }
        return data;
    }
}
