package com.domikado.itaxi.utils;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

public class SignalStrengthListener extends PhoneStateListener {

    private int mSignalStrength = -1;

    public SignalStrengthListener(Context context) {
        TelephonyManager telecomManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telecomManager.listen(this, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        mSignalStrength = signalStrength.getGsmSignalStrength();
    }

    public int getGSMSignalStrength() {
        return mSignalStrength;
    }
}
