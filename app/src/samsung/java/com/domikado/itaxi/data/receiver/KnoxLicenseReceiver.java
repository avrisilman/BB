package com.domikado.itaxi.data.receiver;

import android.app.enterprise.license.EnterpriseLicenseManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class KnoxLicenseReceiver extends BroadcastReceiver {

    void showToast(Context context, CharSequence msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(EnterpriseLicenseManager.ACTION_LICENSE_STATUS)) {
            String result = intent.getStringExtra(EnterpriseLicenseManager.EXTRA_LICENSE_STATUS);
            showToast(context, "License activation: " + result);
        }
    }
}
