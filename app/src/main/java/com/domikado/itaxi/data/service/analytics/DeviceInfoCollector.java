package com.domikado.itaxi.data.service.analytics;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.domikado.itaxi.data.entity.analytics.SystemMetric;
import com.domikado.itaxi.data.repository.AnalyticsRepository;
import com.domikado.itaxi.utils.Connectivity;
import com.domikado.itaxi.utils.SignalStrengthListener;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

public class DeviceInfoCollector {

    private final Context mContext;
    private SignalStrengthListener signalStrengthListener;
    private Subscription subscription;

    public DeviceInfoCollector(Context mContext) {
        this.mContext = mContext;
    }

    public void start() {
        if (signalStrengthListener == null)
            signalStrengthListener = new SignalStrengthListener(mContext);

        subscription = Observable.interval(60, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.computation())
            .subscribe(aLong -> {
                AnalyticsRepository.recordSystemMetric(
                    mContext,
                    String.valueOf(signalStrengthListener.getGSMSignalStrength()),
                    String.valueOf(getBatteryLevel()),
                    String.valueOf(Connectivity.getConnectionType(mContext)));
            });
    }

    public void stop() {
        if (subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }
    }

    private float getBatteryLevel() {
        Intent batteryIntent = mContext.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        assert batteryIntent != null;
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if (level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float) level / (float) scale) * 100.0f;
    }
}
