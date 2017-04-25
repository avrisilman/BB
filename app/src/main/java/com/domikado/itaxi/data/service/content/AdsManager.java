package com.domikado.itaxi.data.service.content;

import android.net.Uri;
import android.text.TextUtils;
import com.domikado.itaxi.Constant;
import com.domikado.itaxi.TaxiApplication;
import com.domikado.itaxi.data.api.DomikadoService;
import com.domikado.itaxi.data.api.json.AdsJson;
import com.domikado.itaxi.data.api.json.AdsPlaylistJson;
import com.domikado.itaxi.data.api.json.AdsResponseJson;
import com.domikado.itaxi.data.entity.ads.Ads;
import com.domikado.itaxi.data.entity.ads.CallToAction;
import com.domikado.itaxi.data.entity.ads.Placement;
import com.domikado.itaxi.data.repository.AdsRepository;
import com.domikado.itaxi.data.repository.CTARepository;
import com.domikado.itaxi.data.RequestHistory;
import com.domikado.itaxi.utils.Downloader;
import com.domikado.itaxi.utils.FileUtils;
import com.domikado.itaxi.utils.IoUtils;
import com.domikado.itaxi.utils.ZipUtils;
import com.orhanobut.hawk.Hawk;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import okhttp3.Request;
import rx.Emitter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class AdsManager {

    private final DomikadoService service;
    private final Downloader mDownloader;

    private boolean isRunning = false;

    public AdsManager(DomikadoService service, Downloader downloader) {
        this.service = service;
        this.mDownloader = downloader;
    }

    private boolean hasUpdate(String version) {
        return !version.equals(getCurrentVersion());
    }

    private void invalidateVersion(String version) {
        Hawk.put(Constant.DataStore.ADS_BEFORE_VERSION, getCurrentVersion());
        Hawk.put(Constant.DataStore.ADS_CURRENT_VERSION, version);
    }

    public static String getCurrentVersion() {
        return Hawk.get(Constant.DataStore.ADS_CURRENT_VERSION);
    }

    public Observable<String> sync() {
        setRunning(true);
        Hawk.put(Constant.DataStore.ADS_LAST_CHECK, System.currentTimeMillis());

        String url = TaxiApplication.getSettings().getPlaylistsUrl();
        return service.getAdsPlaylist(url)
                .filter(response -> hasUpdate(response.getVersion()))
                .flatMap(response -> {
                    AdsRepository.deleteThisVersion(response.getVersion());
                    return saveAds(response);
                })
                .doOnNext(version -> {
                    Hawk.put(Constant.DataStore.ADS_LAST_UPDATE, new RequestHistory.Builder()
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

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean status) {
        this.isRunning = status;
    }

    private void cleanup() {
        AdsRepository.deleteObsoleteAds();

        File dir = FileUtils.getDataDir(Constant.DataStore.ADS_DATA_DIR);
        if (dir.exists()) {
            File[] children = dir.listFiles();
            for (File file: children) {
                if ((!file.getName().equals(Hawk.get(Constant.DataStore.ADS_BEFORE_VERSION))) &&
                        (!file.getName().equals(getCurrentVersion()))) {
                    Timber.d("Deleting %s", file.getName());
                    FileUtils.deleteContentsAndDir(file);
                }
            }
        }

        File ctaDir = FileUtils.getDataDir(Constant.DataStore.CTA_DATA_DIR);
        if (ctaDir.exists()) {
            File[] children = ctaDir.listFiles();
            for (File file: children) {
                if ((!file.getName().equals(Hawk.get(Constant.DataStore.ADS_BEFORE_VERSION))) &&
                        (!file.getName().equals(getCurrentVersion()))) {
                    Timber.d("Deleting %s", file.getName());
                    FileUtils.deleteContentsAndDir(file);
                }
            }
        }
    }

    public Observable<String> saveAds(AdsResponseJson response) {
        return Observable.fromEmitter(emitter -> {
            try {
                String version = response.getVersion();

                File dir = FileUtils.getDataDir(Constant.DataStore.ADS_DATA_DIR + version);
                AdsPlaylistJson playlist = response.getPlaylists();

                for (AdsJson ads : playlist.getPremium()) {
                    downloadMedia(ads, Placement.ZONE_PREMIUM, Ads.VIDEO, version, dir);
                }
                for (AdsJson ads : playlist.getMain()) {
                    downloadMedia(ads, Placement.ZONE_MAIN, Ads.VIDEO, version, dir);
                }
                for (AdsJson ads : playlist.getSplash()) {
                    downloadMedia(ads, Placement.ZONE_SPLASH, Ads.IMAGE, version, dir);
                }
                for (AdsJson ads : playlist.getTop()) {
                    downloadMedia(ads, Placement.ZONE_TOP, Ads.IMAGE, version, dir);
                }
                for (AdsJson ads : playlist.getBannerLeft()) {
                    downloadMedia(ads, Placement.ZONE_BANNER_LEFT, Ads.IMAGE, version, dir);
                }
                for (AdsJson ads : playlist.getBannerRight()) {
                    downloadMedia(ads, Placement.ZONE_BANNER_RIGHT, Ads.IMAGE, version, dir);
                }
                for (AdsJson ads : playlist.getZonePopup()){
                    downloadMedia(ads, Placement.ZONE_POPUP, Ads.FILES, version, dir);
                }


                Timber.i("Media downloaded successfully.");
                emitter.onNext(version);
                emitter.onCompleted();
            } catch (Exception e) {
                emitter.onError(e);
            }
        }, Emitter.BackpressureMode.BUFFER);
    }

    private void downloadMedia(AdsJson model, String placement, String type, String version, File parent) throws Exception {
        File file = new File(parent, Uri.parse(model.getUrl()).getLastPathSegment());
        File banner_file = null;
        String fingerprint = model.getFingerprint();

        /**
         * Find media (just copy if already existed)
         */
        Ads existingAds = AdsRepository.findByFingerprint(fingerprint);
        if (existingAds != null && !existingAds.getVersion().equals(version)) {
            File existingFile = new File(existingAds.getFilepath());
            if (existingFile.exists() && FileUtils.validChecksum(existingFile, fingerprint)) {
                IoUtils.copyFile(existingFile, file);
            } else {
                file = downloadFile(model.getUrl(), file);
                if (!FileUtils.validChecksum(file, fingerprint))
                    throw new IOException(String.format("File %s checksum is mismatch.", file.getAbsolutePath()));
            }

        } else {
            file = downloadFile(model.getUrl(), file);
            if (!FileUtils.validChecksum(file, fingerprint))
                throw new IOException(String.format("File %s checksum is mismatch.", file.getAbsolutePath()));
        }

        //Download Banner IF exists
        String bannerUrl = model.getBannerUrl();
        boolean withBanner = bannerExists(bannerUrl);
        if(withBanner) {
            banner_file = new File(parent, Uri.parse(bannerUrl).getLastPathSegment());
            banner_file = downloadFile(bannerUrl, banner_file);
        }

        Ads ads = new Ads(model);
        ads.setServerId(model.getId());
        ads.setServerMediaUrl(model.getUrl());
        ads.setFingerprint(model.getFingerprint());
        ArrayList<String> tags = model.getTags();
        ads.setTags(TextUtils.join(",", tags));
        ads.setBannerUrl(bannerUrl);

        if(withBanner){
            ads.setBannerpath(banner_file.getAbsolutePath());
        }

        ads.setVersion(version);
        ads.setType(model.getType().toLowerCase());
        ads.setPlacement(placement);
        ads.setFilepath(file.getAbsolutePath());
        ads.setDuration(model.getDuration());

        if(ads.getPlacement() == "popup"){
            downloadPopup(ads, version);
            ads.setTime(model.getTime());
        }

        if (model.getCallToAction() != null) {
            CallToAction cta = CTARepository.findCTAByServerId(model.getCallToAction().getId());
            if (cta == null) {
                long ctaId = new CallToAction(model.getCallToAction()).save();
                cta = CTARepository.findCTAById(ctaId);
            } else {
                cta.setUrl(model.getCallToAction().getUrl());
                cta.setImage(model.getCallToAction().getButtonImage());
                cta.setMediaFingerprint(model.getCallToAction().getFingerprint());
            }

            downloadCallToAction(cta, version);
            ads.setCallToActionId(cta.getId());
        }
        ads.save();
    }

    private boolean bannerExists(String bannerUrl){
        if(bannerUrl == null){
            return false;
        }else{
            String[] allowedExts = {"jpg", "png", "jpeg", "gif"};
            String ext = bannerUrl.substring(bannerUrl.lastIndexOf(".") + 1).toLowerCase();

            return Arrays.asList(allowedExts).contains(ext);
        }
    }

    private File downloadFile(String url, File file) throws IOException {
        Timber.i("Downloading %s to %s", url, file.getAbsolutePath());
        Request request = new Request.Builder().url(url).build();
        return mDownloader.download(request, file);
    }

    private File downloadCallToAction(CallToAction cta, String version) throws IOException {
        String ctaId = String.valueOf(cta.getServerId());

        File dir = FileUtils.getDataDir(Constant.DataStore.CTA_DATA_DIR + version);
        File zipFile = new File(dir, ctaId + ".zip");
        zipFile = downloadFile(cta.getUrl(), zipFile);

        Timber.d("Unzipping file %s", zipFile.getAbsolutePath());
        boolean isSuccess = ZipUtils.unzip(zipFile, ctaId);
        if (isSuccess) {
            String indexPath = String.format("%s/%s/index.html", zipFile.getParent(), ctaId);
            cta.setFile(indexPath);
        } else {
            Timber.d("Unzip failed.");
        }

        if (!TextUtils.isEmpty(cta.getImage())) {
            File imageFile = new File(dir, ctaId + "-" + Uri.parse(cta.getImage()).getLastPathSegment());
            imageFile = downloadFile(cta.getImage(), imageFile);
            cta.setImageFile(imageFile.getPath());
        }

        cta.save();
        return zipFile;
    }

    private File downloadPopup(Ads pop, String version) throws IOException {
        String popId = String.valueOf(pop.getServerId());

        File dir = FileUtils.getDataDir(Constant.DataStore.ADS_DATA_DIR + version);
        File zipFile = new File(dir, popId + ".zip");
        zipFile = downloadFile(pop.getServerMediaUrl(), zipFile);

        Timber.d("Unzipping file %s", zipFile.getAbsolutePath());
        boolean isSuccess = ZipUtils.unzip(zipFile, popId);
        if (isSuccess) {
            String indexPath = String.format("%s/%s/index.html", zipFile.getParent(), popId);
            pop.setFilepath(indexPath);
        } else {
            Timber.d("Unzip failed.");
        }

        pop.save();
        return zipFile;
    }
}
