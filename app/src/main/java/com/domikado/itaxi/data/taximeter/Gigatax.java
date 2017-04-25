package com.domikado.itaxi.data.taximeter;

import com.domikado.itaxi.utils.HexData;

public abstract class Gigatax {

    abstract void call(byte[] data);

    boolean valid(byte[] finalByte) {
        String checksum = HexData.bytesToString(new byte[] { finalByte[finalByte.length - 1] }).trim();
        String byteSum = Integer.toHexString(calculateChecksum(finalByte) & 0xFF).toUpperCase();
        String byteSumHex = HexData.HEX_INDICATOR + byteSum;

        return checksum.equals(byteSumHex);
    }

    private int calculateChecksum(byte[] finalByte) {
        int byteSum = 0;
        for (int i = 0; i < finalByte.length - 1; i++) {
            byteSum += finalByte[i];
        }
        return byteSum;
    }
}
