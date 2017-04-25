package com.domikado.itaxi.ui.ads;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.domikado.itaxi.BuildConfig;
import com.domikado.itaxi.Constant;
import com.domikado.itaxi.R;
import com.domikado.itaxi.data.SPSession;
import com.domikado.itaxi.data.service.analytics.DataUploader;
import com.domikado.itaxi.data.service.content.ContentUpdater;
import com.domikado.itaxi.events.EventHire;
import com.domikado.itaxi.kiosk.AppUpdater;
import com.domikado.itaxi.kiosk.AutoShutdown;
import com.domikado.itaxi.ui.base.KioskActivity;
import com.domikado.itaxi.utils.EBus;
import com.domikado.itaxi.utils.KioskUtils;
import com.orhanobut.hawk.Hawk;

import org.greenrobot.eventbus.Subscribe;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VacantActivity extends KioskActivity {

    @Inject
    ContentUpdater contentUpdater;

    @Inject
    DataUploader dataUploader;

    @Inject
    AutoShutdown autoShutdown;

    @Inject
    AppUpdater appUpdater;

    @BindView(R.id.txt_taximeter_status)
    TextView txtStatus;
    private final BroadcastReceiver powerReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            switch (plugged) {
                case 0:
                    enableAutoShutdown();
                    break;
                case BatteryManager.BATTERY_PLUGGED_AC:
                case BatteryManager.BATTERY_PLUGGED_USB:
                    disableAutoShutdown();
                    break;
            }
        }
    };

    public static void start(Context context) {
        context.startActivity(new Intent(context, VacantActivity.class));
    }

    private void updateStatus(long second) {
        txtStatus.setText(String.valueOf(second));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getComponent().inject(this);

        setContentView(R.layout.activity_vacant);
        ButterKnife.bind(this);

        KioskUtils.setBrightness(getWindow(), 0);

        if(BuildConfig.SHOW_METER_CONTROL){
            txtStatus.setVisibility(View.VISIBLE);
        }

        SPSession.clearSession(this);
        logScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EBus.register(this);
        registerPowerReceiver(powerReceiver);
        contentUpdater.start();
        dataUploader.start();
        appUpdater.start();
    }

    @Override
    protected void onPause() {
        appUpdater.stop();
        dataUploader.stop();
        contentUpdater.stop();
        disableAutoShutdown();
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

    private void enableAutoShutdown() {
        if (Hawk.get(Constant.DataStore.DEVICE_ON_BATTERY_TIME) == null) {
            Hawk.put(Constant.DataStore.DEVICE_ON_BATTERY_TIME, System.currentTimeMillis());
        }
        autoShutdown.start();
    }

    private void disableAutoShutdown() {
        if (Hawk.get(Constant.DataStore.DEVICE_ON_BATTERY_TIME) != null) {
            Hawk.put(Constant.DataStore.DEVICE_ON_BATTERY_TIME, null);
        }
        autoShutdown.stop();
    }
}
