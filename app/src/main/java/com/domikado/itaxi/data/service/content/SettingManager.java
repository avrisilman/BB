package com.domikado.itaxi.data.service.content;

import android.content.Context;

import com.domikado.itaxi.Constant;
import com.domikado.itaxi.TaxiApplication;
import com.domikado.itaxi.data.entity.ads.Placement;
import com.domikado.itaxi.data.RequestHistory;
import com.domikado.itaxi.data.api.json.ApplicationSettingJson;
import com.domikado.itaxi.data.api.json.PlacementJson;
import com.domikado.itaxi.data.api.json.ResolutionJson;
import com.domikado.itaxi.data.api.DomikadoService;
import com.orhanobut.hawk.Hawk;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SettingManager {

    private final Context mContext;
    private final DomikadoService service;

    private boolean isRunning = false;

    public SettingManager(Context context, DomikadoService service) {
        this.mContext = context;
        this.service = service;
    }

    public Observable<ApplicationSettingJson> setDefault() {
        String deviceId = TaxiApplication.getComponent().device().getIMEI();

        return service.getDefaultSettings(deviceId)
            .flatMap(this::setPlacement)
            .doOnNext(settings -> {
                if (settings != null) {
                    Hawk.put(Constant.DataStore.APPLICATION_SETTINGS, settings);
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ApplicationSettingJson> update() {
        setRunning(true);
        String url = TaxiApplication.getSettings().getApplicationSettingsUrl();

        return service.getSettings(url)
            .flatMap(this::setPlacement)
            .doOnNext(settings -> {
                if (settings != null) {
                    Hawk.put(Constant.DataStore.APPLICATION_SETTINGS, settings);
                    Hawk.put(Constant.DataStore.LAST_UPDATE_SETTINGS, new RequestHistory.Builder()
                        .setUrl(url)
                        .setTimestamp(System.currentTimeMillis())
                        .createRequestHistory());
                }
            })
            .doOnTerminate(() -> this.setRunning(false))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<ApplicationSettingJson> setPlacement(ApplicationSettingJson settings) {
        PlacementJson resolutions = settings.getPlacementResolutions();
        setPlacementResolution(resolutions.getMain(), Placement.ZONE_MAIN);
        setPlacementResolution(resolutions.getPremium(), Placement.ZONE_PREMIUM);
        setPlacementResolution(resolutions.getTop(), Placement.ZONE_TOP);
        setPlacementResolution(resolutions.getBannerLeft(), Placement.ZONE_BANNER_LEFT);
        setPlacementResolution(resolutions.getBannerRight(), Placement.ZONE_BANNER_RIGHT);
        setPlacementResolution(resolutions.getSplash(), Placement.ZONE_SPLASH);

        return Observable.just(settings);
    }

    private void setPlacementResolution(ResolutionJson resolution, String placement) {
        if (resolution != null) {
            new Placement(resolution, placement).save();
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean status) {
        this.isRunning = status;
    }
}
