package com.domikado.itaxi.ui.ads;

import android.os.Bundle;
import android.security.keystore.KeyNotYetValidException;
import android.util.Log;

import com.domikado.itaxi.Constant;
import com.domikado.itaxi.data.entity.analytics.SystemMetric;
import com.domikado.itaxi.data.receiver.KnoxLicenseReceiver;
import com.domikado.itaxi.ui.base.KioskActivity;
import com.domikado.itaxi.ui.settings.ContentManagerActivity;
import com.orhanobut.hawk.Hawk;
import com.sec.enterprise.knox.license.KnoxEnterpriseLicenseManager;

import java.util.List;

public class LaunchActivity extends KioskActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Hawk.get(Constant.DataStore.LAST_SETUP) == null) {
            ContentManagerActivity.start(LaunchActivity.this);
        } else {
            VacantActivity.start(LaunchActivity.this);
        }

    }
}
