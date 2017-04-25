package com.domikado.itaxi.data.taximeter;

import com.domikado.itaxi.events.EventHire;
import com.domikado.itaxi.events.EventStop;
import com.domikado.itaxi.events.EventVacant;
import com.domikado.itaxi.utils.HexData;
import com.domikado.itaxi.utils.SerialBuffer;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.nio.ByteBuffer;

import timber.log.Timber;

public class Gigatax2519 extends Gigatax {

    private final static String HEXES = "0123456789ABCDEF";
    private SerialBuffer mSerialBuffer = new SerialBuffer(true);
    private final static int TOTAL_BYTES = 20;
    private final static String START_BYTE = "0x80";
    private final static String VACANT_BYTE = String.format("%s %s", START_BYTE, "0x00");
    private final static String HIRED_BYTE = String.format("%s %s", START_BYTE, "0x01");
    private final static String STOP_BYTE = String.format("%s %s", START_BYTE, "0x02");

    private EventBus bus;

    public Gigatax2519(EventBus bus) {
        this.bus = bus;
    }

    @Override
    public void call(byte[] data) {

        Timber.d("Packet received (%d bytes): %s",
            data.length,
            HexData.bytesToString(data)
        );

        for (byte sByte : data) {
            byte[] sBytes = new byte[] { sByte };
            String sBytesInString = HexData.bytesToString(sBytes).trim();

            if (START_BYTE.equals(sBytesInString)) {
                mSerialBuffer.clearReadBuffer();
                mSerialBuffer.putReadBuffer(ByteBuffer.wrap(sBytes));
            } else if (mSerialBuffer.getReadBuffer().position() > 0) {
                mSerialBuffer.putReadBuffer(ByteBuffer.wrap(sBytes));
            }

            if (mSerialBuffer.getReadBuffer().position() == TOTAL_BYTES) {
                byte[] finalByte = mSerialBuffer.getDataReceived();
                if (valid(finalByte)) {
                    parse(finalByte);
                } else {
                    String command = HexData.bytesToString(finalByte);
                    Timber.i("Final buffer (%d bytes): %s", finalByte.length, command);
                    Timber.i("Checksum not valid");
                }
                mSerialBuffer.clearReadBuffer();
            }
        }
    }

    private void parse(byte[] finalByte) {
        String command = HexData.bytesToString(finalByte);

        Timber.i("Final buffer (%d bytes): %s", finalByte.length, command);

        if (command.startsWith(HIRED_BYTE)) {
            Timber.i("HIRED action triggered");
            bus.post(new EventHire());
        } else if (command.startsWith(STOP_BYTE)) {
            Timber.i("STOP action triggered");
            bus.post(new EventStop(getFareAmount(finalByte)));
        } else if (command.startsWith(VACANT_BYTE)) {
            Timber.i("VACANT action triggered");
            bus.post(new EventVacant());
        } else {
            Timber.i("UNKNOWN action triggered");
        }
    }

    private BigDecimal getFareAmount(byte[] data) {
        StringBuilder fare = new StringBuilder(6);
        fare.append(HEXES.charAt((data[15] & 0xF0) >> 4)).append(HEXES.charAt((data[15] & 0x0F)));
        fare.append(HEXES.charAt((data[14] & 0xF0) >> 4)).append(HEXES.charAt((data[14] & 0x0F)));
        fare.append(HEXES.charAt((data[13] & 0xF0) >> 4)).append(HEXES.charAt((data[13] & 0x0F)));

        return new BigDecimal(fare.toString());
    }
}
