package com.domikado.itaxi.data.service.content;

import android.net.Uri;

import com.domikado.itaxi.Constant;
import com.domikado.itaxi.TaxiApplication;
import com.domikado.itaxi.data.api.DomikadoService;
import com.domikado.itaxi.data.api.json.NewsJson;
import com.domikado.itaxi.data.api.json.NewsResponseJson;
import com.domikado.itaxi.data.entity.content.News;
import com.domikado.itaxi.data.repository.NewsRepository;
import com.domikado.itaxi.data.RequestHistory;
import com.domikado.itaxi.utils.Downloader;
import com.domikado.itaxi.utils.FileUtils;
import com.domikado.itaxi.utils.IoUtils;
import com.orhanobut.hawk.Hawk;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Request;
import rx.Emitter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class NewsManager {

    private final DomikadoService service;
    private final Downloader mDownloader;

    private boolean isRunning = false;

    @Inject
    public NewsManager(DomikadoService service, Downloader downloader) {
        this.service = service;
        this.mDownloader = downloader;
    }

    public Observable<String> sync() {
        setRunning(true);
        Hawk.put(Constant.DataStore.NEWS_LAST_CHECK, System.currentTimeMillis());

        String url = TaxiApplication.getSettings().getNewsUrl();
        return service.getNews(url)
            .filter(response -> hasUpdate(response.getVersion()))
            .flatMap(response -> {
                NewsRepository.deleteThisVersion(response.getVersion());
                return downloadMedia(response);
            })
            .doOnNext(version -> {
                Hawk.put(Constant.DataStore.NEWS_LAST_UPDATE, new RequestHistory.Builder()
                    .setUrl(url)
                    .setTimestamp(System.currentTimeMillis())
                    .createRequestHistory());
                invalidateVersion(version);
                cleanup();
            })
            .doOnTerminate(() -> this.setRunning(false))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<String> downloadMedia(NewsResponseJson response) {

        return Observable.fromEmitter(emitter -> {
            try {
                File dir = FileUtils.getDataDir(Constant.DataStore.NEWS_DATA_DIR + response.getVersion());
                for (NewsJson news : response.getNews()) {
                    String fingerprint = news.getFingerprint();
                    File file = new File(dir, Uri.parse(news.getImageUrl()).getLastPathSegment());

                    /**
                     * Find media (just copy if already existed)
                     */
                    News existingNews = NewsRepository.findByFingerprint(fingerprint);
                    if (existingNews != null && !existingNews.getVersion().equals(response.getVersion())) {
                        File existingFile = new File(existingNews.getFilepath());
                        if (existingFile.exists() && FileUtils.validChecksum(existingFile, fingerprint)) {
                            Timber.i("Copy file %s to %s", existingFile.getAbsolutePath(), file.getAbsolutePath());
                            IoUtils.copyFile(existingFile, file);
                        } else {
                            file = downloadFile(news.getImageUrl(), file);
                            if (!FileUtils.validChecksum(file, fingerprint))
                                throw new IOException(String.format("File %s is corrupted.", file.getAbsolutePath()));
                        }
                    } else {
                        file = downloadFile(news.getImageUrl(), file);
                        if (!FileUtils.validChecksum(file, fingerprint))
                            throw new IOException(String.format("File %s is corrupted.", file.getAbsolutePath()));
                    }

                    News newsModel = new News(news);
                    newsModel.setFilepath(file.getAbsolutePath());
                    newsModel.setVersion(response.getVersion());
                    newsModel.save();
                }

                Timber.i("Media downloaded successfully.");
                emitter.onNext(response.getVersion());
                emitter.onCompleted();
            } catch (Exception e) {
                emitter.onError(e);
            }
        }, Emitter.BackpressureMode.BUFFER);
    }

    private void cleanup() {
        NewsRepository.deleteObsoleteNews();

        File dir = FileUtils.getDataDir(Constant.DataStore.NEWS_DATA_DIR);
        if (dir.exists()) {
            File[] children = dir.listFiles();
            for (File file: children) {
                if ((!file.getName().equals(Hawk.get(Constant.DataStore.NEWS_BEFORE_VERSION))) &&
                    (!file.getName().equals(getCurrentVersion()))) {
                        Timber.d("Deleting %s", file.getName());
                        FileUtils.deleteContentsAndDir(file);
                }
            }
        }
    }

    public boolean hasUpdate(String version) {
        return !version.equals(getCurrentVersion());
    }

    public static String getCurrentVersion() {
        return Hawk.get(Constant.DataStore.NEWS_CURRENT_VERSION);
    }

    private void invalidateVersion(String version) {
        Hawk.put(Constant.DataStore.NEWS_BEFORE_VERSION, getCurrentVersion());
        Hawk.put(Constant.DataStore.NEWS_CURRENT_VERSION, version);
    }

    private File downloadFile(String url, File file) throws IOException {
        Timber.i("Downloading %s to %s", url, file.getAbsolutePath());
        Request request = new Request.Builder().url(url).build();
        return mDownloader.download(request, file);
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean status) {
        this.isRunning = status;
    }
}
