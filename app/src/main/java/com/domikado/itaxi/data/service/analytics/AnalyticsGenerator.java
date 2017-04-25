package com.domikado.itaxi.data.service.analytics;

import android.content.Context;

import com.domikado.itaxi.Constant;
import com.domikado.itaxi.TaxiApplication;
import com.domikado.itaxi.data.repository.AnalyticsRepository;
import com.domikado.itaxi.data.repository.CTARepository;
import com.domikado.itaxi.data.repository.SessionRepository;
import com.domikado.itaxi.utils.Compress;
import com.domikado.itaxi.utils.DateUtils;
import com.domikado.itaxi.utils.FileUtils;

import java.io.File;

import javax.inject.Inject;

import rx.Emitter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class AnalyticsGenerator {

    private Context mContext;
    private boolean isRunning = false;

    @Inject
    public AnalyticsGenerator(Context context) {
        this.mContext = context;
    }

    public Observable<String> generate() {
        setRunning(true);
        return zipDb()
            .doOnNext(path -> cleanup())
            .doOnTerminate(() -> setRunning(false))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<String> zipDb() {
        return Observable.fromEmitter(emitter -> {
            try {
                if (SessionRepository.isAnySession()) {
                    String deviceId = TaxiApplication.getComponent().device().getIMEI();

                    String dbPath = mContext.getDatabasePath(Constant.DataStore.DB_NAME).getAbsolutePath();
                    Timber.d("DB Path: %s", dbPath);

                    File reportDir = FileUtils.getDataDir(Constant.DataStore.STATISTIC_DATA_DIR);
                    String zipDbPath = new File(reportDir,
                        deviceId + "-" + DateUtils.getTimeInString(System.currentTimeMillis(), "yyyyMMddHHmmss") + ".zip")
                        .getAbsolutePath();
                    Timber.d("Zip DB Path: %s", zipDbPath);

                    Compress compress = new Compress(new String[] { dbPath }, zipDbPath);
                    compress.zip();

                    emitter.onNext(zipDbPath);
                }
                emitter.onCompleted();
            } catch (Exception e) {
                emitter.onError(e);
            }
        }, Emitter.BackpressureMode.BUFFER);
    }

    private void cleanup() {
        AnalyticsRepository.deleteAll();
        CTARepository.deleteAllCTAResult();
    }

    public boolean isRunning() {
        return isRunning;
    }

    private void setRunning(boolean running) {
        isRunning = running;
    }
}
