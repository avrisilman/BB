package com.domikado.itaxi.kiosk;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.domikado.itaxi.Constant;
import com.domikado.itaxi.TaxiApplication;
import com.domikado.itaxi.data.RequestHistory;
import com.domikado.itaxi.data.SPSession;
import com.domikado.itaxi.data.api.DomikadoService;
import com.domikado.itaxi.data.api.request.PingRequest;
import com.domikado.itaxi.utils.Connectivity;
import com.domikado.itaxi.utils.LocationUtil;
import com.domikado.itaxi.utils.SignalStrengthListener;
import com.orhanobut.hawk.Hawk;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class PingManager {

    private DomikadoService service;
    private Context context;
    private LocationUtil locationUtil;
    private SignalStrengthListener signalStrengthListener;

    @Inject
    public PingManager(DomikadoService service, Context context) {
        this.service = service;
        this.context = context;
        this.locationUtil = new LocationUtil(context);
        this.signalStrengthListener = new SignalStrengthListener(context);
    }

    public void start() {
        int interval = TaxiApplication.getSettings().getPingInterval();
        Observable.interval(interval, TimeUnit.SECONDS)
            .flatMap(aLong ->  service.ping(buildPingRequest()))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onPingSuccess, Timber::e);
    }

    public Observable<Boolean> ping() {
        return service.ping(buildPingRequest())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(this::onPingSuccess)
            .map(responseBody -> responseBody != null);
    }

    private void onPingSuccess(ResponseBody responseBody) {
        Hawk.put(Constant.DataStore.LAST_PING, new RequestHistory.Builder()
            .setResponse(response(responseBody))
            .setTimestamp(System.currentTimeMillis())
            .createRequestHistory());
        Timber.d("Response " + response(responseBody));
    }

    private String response(ResponseBody responseBody) {
        try {
            return responseBody.string();
        } catch (IOException e) {
            Timber.e(e);
            return e.getMessage();
        }
    }

    private PingRequest buildPingRequest() {
        double latitude = locationUtil.getLatLong()[0];
        double longitude = locationUtil.getLatLong()[1];

        return PingRequest.builder()
            .setLatitude(latitude)
            .setLongitude(longitude)
            .setPlaylistVersion(Hawk.get(Constant.DataStore.ADS_CURRENT_VERSION))
            .setBatteryLevel(getBatteryLevel())
            .setGsmSignalStrength(signalStrengthListener.getGSMSignalStrength())
            .setNetworkType(Connectivity.getConnectionType(context))
            .setSessionToken(SPSession.getSessionToken(context))
            .build();
    }

    private float getBatteryLevel() {
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
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
