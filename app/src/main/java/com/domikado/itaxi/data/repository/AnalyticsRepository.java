package com.domikado.itaxi.data.repository;

import android.content.Context;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.domikado.itaxi.TaxiApplication;
import com.domikado.itaxi.data.SPSession;
import com.domikado.itaxi.data.entity.Session;
import com.domikado.itaxi.data.entity.analytics.AdsActionMetric;
import com.domikado.itaxi.data.entity.analytics.AdsMetric;
import com.domikado.itaxi.data.entity.analytics.NewsMetric;
import com.domikado.itaxi.data.entity.analytics.Rating;
import com.domikado.itaxi.data.entity.analytics.ScreenMetric;
import com.domikado.itaxi.data.entity.analytics.SystemMetric;
import com.domikado.itaxi.data.entity.analytics.Upload;
import com.domikado.itaxi.data.entity.analytics.Volume;

import org.threeten.bp.Instant;

import java.text.SimpleDateFormat;
import java.util.Date;

import rx.Emitter;
import rx.Observable;

public class AnalyticsRepository {

    public static Observable<Long> recordAdsMetric(Context context, long adsId, String  placement, String adsType) {
        return Observable.fromEmitter(subscriber -> {
            AdsMetric metric = new AdsMetric();
            metric.setAdsId(adsId);
            metric.setAdsType(adsType);
            metric.setPlacement(placement);
            metric.setStartedAt(Instant.now().getEpochSecond());
            metric.setSessionDevice(SPSession.getSessionId(context));
            subscriber.onNext(metric.save());
            subscriber.onCompleted();
        }, Emitter.BackpressureMode.BUFFER);
    }

    public static void completeAdsMetric(long id) {
        TaxiApplication.getComponent().executor().execute(() -> {
            AdsMetric metric = new Select().from(AdsMetric.class).where("id = ?", id).executeSingle();
            if (metric != null) {
                metric.setFinishedAt(Instant.now().getEpochSecond());
                metric.save();
            }
        });
    }

    public static void recordCTAMetric(Context context, String action, long adsId) {
        TaxiApplication.getComponent().executor().execute(() -> {
            AdsActionMetric metric = new AdsActionMetric();
            metric.setSessionId(SPSession.getSessionId(context));
            metric.setTimestamp(Instant.now().getEpochSecond());
            metric.setAdsId(adsId);
            metric.setAction(action);
            metric.save();
        });
    }

    public static void recordRating(Context context, String rate){
        Date datetime = new Date();
        SimpleDateFormat dtformat = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");
        TaxiApplication.getComponent().executor().execute(() -> {
            Rating r = new Rating();
            r.setSessionId(SPSession.getSessionId(context));
            r.setDatetime(dtformat.format(datetime));
            r.setRate(String.valueOf(rate));
            r.save();
        });
    }

    public static void recordVolumeBrightness(Context context, String action){
        Date datetime = new Date();
        SimpleDateFormat dtformat = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");
        TaxiApplication.getComponent().executor().execute(() -> {
            Volume vol = new Volume();
            vol.setSessionId(SPSession.getSessionId(context));
            vol.setDatetime(dtformat.format(datetime));
            vol.setAction(action);
            vol.save();
        });
    }

    public static void recordNewsMetric(Context context, long newsId) {
        TaxiApplication.getComponent().executor().execute(() -> {
            NewsMetric metric = new NewsMetric();
            metric.setSessionId(SPSession.getSessionId(context));
            metric.setNewsId(newsId);
            metric.setTimestamp(Instant.now().getEpochSecond());
            metric.save();
        });
    }

    public static void recordScreenMetric(Context context, String screen, boolean visible) {
        TaxiApplication.getComponent().executor().execute(() -> {
            ScreenMetric metric = new ScreenMetric();
            metric.setSessionId(SPSession.getSessionId(context));
            metric.setTimestamp(Instant.now().getEpochSecond());
            metric.setVisible(visible);
            metric.setScreen(screen);
            metric.save();
        });
    }

    public static void recordSystemMetric(Context context, String signalStrength, String batteryLevel, String networkType) {
        TaxiApplication.getComponent().executor().execute(() -> {
            SystemMetric metric = new SystemMetric();
            metric.setBatteryLevel(batteryLevel);
            metric.setNetworkType(networkType);
            metric.setSignalStrength(signalStrength);
            metric.setTimestamp(Instant.now().getEpochSecond());
            metric.setSessionId(SPSession.getSessionId(context));
            metric.save();
        });
    }

    public static void saveUpload(String filePath) {
        TaxiApplication.getComponent().executor().execute(() -> {
            Upload upload = new Upload();
            upload.setPath(filePath);
            upload.setTimestamp(Instant.now().getEpochSecond());
            upload.save();
        });
    }

    public static void deleteAll() {
        new Delete().from(AdsMetric.class).execute();
        new Delete().from(AdsActionMetric.class).execute();
        new Delete().from(NewsMetric.class).execute();
        new Delete().from(ScreenMetric.class).execute();
        new Delete().from(SystemMetric.class).execute();
        new Delete().from(Session.class).execute();
    }
}
