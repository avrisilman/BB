package com.domikado.itaxi.data.service.analytics;

import android.content.Context;
import android.util.Pair;

import com.domikado.itaxi.Constant;
import com.domikado.itaxi.TaxiApplication;
import com.domikado.itaxi.data.RequestHistory;
import com.domikado.itaxi.data.api.DomikadoService;
import com.domikado.itaxi.data.repository.AnalyticsRepository;
import com.domikado.itaxi.utils.FileUtils;
import com.orhanobut.hawk.Hawk;

import java.io.File;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Emitter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class AnalyticsUploader {

    @Inject
    Context mContext;

    @Inject
    DomikadoService service;

    private boolean isRunning = false;

    public boolean isRunning() {
        return isRunning;
    }

    private void setRunning(boolean running) {
        isRunning = running;
    }

    public AnalyticsUploader() {
        TaxiApplication.getComponent().inject(this);
    }

    public Observable<ResponseBody> call() {
        setRunning(true);
        String url = TaxiApplication.getSettings().getUploadAnalyticsUrl();

        return listFiles()
            .flatMap(this::createRequest)
            .flatMap(result ->
                service.post(url, result.second)
                    .doOnNext(response -> {
                        File file = result.first;
                        AnalyticsRepository.saveUpload(file.getAbsolutePath());
                        cleanup(file);
                    }))
            .doOnNext(response ->
                Hawk.put(Constant.DataStore.ANALYTICS_LAST_UPLOAD, new RequestHistory.Builder()
                    .setUrl(url)
                    .setTimestamp(System.currentTimeMillis())
                    .createRequestHistory())
            )
            .doOnTerminate(() -> this.setRunning(false))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<File[]> listFiles() {
        return Observable.fromEmitter(emitter -> {
            try {
                File statsDir = FileUtils.getDataDir(Constant.DataStore.STATISTIC_DATA_DIR);
                File[] files = statsDir.listFiles(File::isFile);
                if (files != null) {
                    emitter.onNext(files);
                }
                emitter.onCompleted();
            } catch (Exception ex) {
                emitter.onError(ex);
            }
        }, Emitter.BackpressureMode.BUFFER);
    }

    private Observable<Pair<File, RequestBody>> createRequest(File[] files) {
        String deviceId = TaxiApplication.getComponent().device().getIMEI();

        return Observable.fromEmitter(emitter -> {
            try {
                for(File file: files) {
                    Timber.i(file.getAbsolutePath());
                    RequestBody body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("imei", deviceId)
                        .addFormDataPart("file", deviceId + ".zip",
                            RequestBody.create(MediaType.parse("application/zip"), file))
                        .build();
                    emitter.onNext(new Pair<>(file, body));
                }
                emitter.onCompleted();
            } catch (Exception ex) {
                emitter.onError(ex);
            }
        }, Emitter.BackpressureMode.BUFFER);
    }

    private void cleanup(File file) {
        file.delete();
    }
}
