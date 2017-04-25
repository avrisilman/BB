package com.domikado.itaxi.kiosk;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.domikado.itaxi.BuildConfig;
import com.domikado.itaxi.Constant;
import com.domikado.itaxi.TaxiApplication;
import com.domikado.itaxi.data.api.json.ApkResponseJson;
import com.domikado.itaxi.utils.Downloader;
import com.domikado.itaxi.data.api.DomikadoService;
import com.domikado.itaxi.utils.FileUtils;
import com.orhanobut.hawk.Hawk;

import java.io.File;

import javax.inject.Inject;

import okhttp3.Request;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class ApkInstaller {

    private DomikadoService service;
    private Downloader mDownloader;
    private boolean isRunning = false;

    @Inject
    Context context;

    @Inject
    public ApkInstaller(DomikadoService service, Downloader downloader) {
        this.service = service;
        this.mDownloader = downloader;
        TaxiApplication.getComponent().inject(this);
    }

    public Observable<String> call() {
        setRunning(true);
        Hawk.put(Constant.DataStore.APK_LAST_CHECK, System.currentTimeMillis());

        String url = TaxiApplication.getSettings().getApkUrl();

        return service.checkApk(url)
            .filter(response -> hasUpdate(response.getVersion()))
            .flatMap(this::apkPrepare)
            .flatMap(this::apkInstall)
            .doOnTerminate(() -> this.setRunning(false))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<String> apkPrepare(ApkResponseJson response) {
        // Delete all existing APK file
        File dir = FileUtils.getDataDir(Constant.DataStore.APK_DIR);
        File[] files = dir.listFiles(File::isFile);
        for (File file : files) {
            file.delete();
        }

        // Download APK
        return Observable.fromCallable(() -> downloadFile(response));
    }

    private Observable<String> apkInstall(String version) {
        File apk_dir = FileUtils.getDataDir(Constant.DataStore.APK_DIR);
        File file = new File(apk_dir, version + ".apk");

        Uri uri = Uri.fromFile(file);

        Intent intent = new Intent();
        intent.setAction(Constant.Operation.SILENT_INSTALL_APPLICATION);
        intent.putExtra("uri", uri.toString());
        context.sendBroadcast(intent);

//        ServiceUtil.callSystemService(
//            context,
//            Constant.Operation.SILENT_INSTALL_APPLICATION,
//            uri.toString());
        return Observable.just(version);
    }

    private boolean hasUpdate(String version) {
        return !version.equals(getCurrentVersion());
    }

    private static String getCurrentVersion() {
        return BuildConfig.VERSION_NAME;
    }

    private String downloadFile(ApkResponseJson response) throws Exception {
        String url = response.getUrl();

        String fingerprint = response.getFingerprint();
        File apk_dir = FileUtils.getDataDir(Constant.DataStore.APK_DIR);
        File file = new File(apk_dir, fingerprint + ".apk");

        Timber.i("Downloading %s to %s", url, file.getAbsolutePath());
        Request request = new Request.Builder().url(url).build();
        file = mDownloader.download(request, file);
        if (!FileUtils.validChecksum(file, fingerprint))
            throw new Exception(String.format("File %s is corrupted.", file.getAbsolutePath()));

        Timber.i("APK file downloaded successfully.");
        return fingerprint;
    }

    public boolean isRunning() {
        return isRunning;
    }

    private void setRunning(boolean status) {
        this.isRunning = status;
    }
}
