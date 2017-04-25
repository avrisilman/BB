package com.domikado.itaxi.ui.activities.kiosk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;

import com.domikado.itaxi.R;
import com.domikado.itaxi.TaxiApp;
import com.domikado.itaxi.data.service.AutoApkInstaller;
import com.domikado.itaxi.data.service.AutoShutdown;
import com.domikado.itaxi.data.service.AutoUpdater;
import com.domikado.itaxi.data.service.AutoUploader;
import com.domikado.itaxi.events.EventHire;
import com.domikado.itaxi.utils.EBus;
import com.domikado.itaxi.utils.SPSession;

import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class VacantActivity extends KioskActivity {

    @Inject
    AutoShutdown autoShutdown;

    @Inject
    AutoUpdater autoUpdater;

    @Inject
    AutoUploader autoUploader;

    @Inject
    AutoApkInstaller autoApkInstaller;

    private final BroadcastReceiver powerReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            switch (plugged) {
                case BatteryManager.BATTERY_PLUGGED_AC:
                case BatteryManager.BATTERY_PLUGGED_USB:
                    autoShutdown.stop();
                    break;
                case 0:
                    autoShutdown.start();
                    break;
            }
        }
    };

    public static void start(Context context) {
        context.startActivity(new Intent(context, VacantActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TaxiApp.getComponent().inject(this);

        setContentView(R.layout.activity_vacant);
        ButterKnife.bind(this);

        SPSession.clearSession(this);
        logScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EBus.register(this);
        registerPowerReceiver(powerReceiver);
        autoUpdater.start();
        autoUploader.start();
        autoApkInstaller.start();
    }

    @Override
    protected void onPause() {
        autoApkInstaller.stop();
        autoUploader.stop();
        autoUpdater.stop();
        autoShutdown.stop();
        unregisterReceiver(powerReceiver);

        EBus.unregister(this);
        super.onPause();
    }

    @Subscribe
    public void onEvent(EventHire e) {
        navigateToSplash();
    }

    @OnClick(R.id.txt_taximeter_status)
    public void onTvModeClick() {
        navigateToSplash();
    }

    private void registerPowerReceiver(BroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, filter);
    }
}
