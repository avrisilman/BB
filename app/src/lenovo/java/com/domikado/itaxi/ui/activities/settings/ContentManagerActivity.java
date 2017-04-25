package com.domikado.itaxi.ui.settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.domikado.itaxi.Constant;
import com.domikado.itaxi.R;
import com.domikado.itaxi.TaxiApp;
import com.domikado.itaxi.data.RequestHistory;
import com.domikado.itaxi.data.service.AdsManager;
import com.domikado.itaxi.data.service.AnalyticsGenerator;
import com.domikado.itaxi.data.service.AnalyticsUploader;
import com.domikado.itaxi.data.service.ApkDownloader;
import com.domikado.itaxi.data.service.NewsManager;
import com.domikado.itaxi.data.service.PingManager;
import com.domikado.itaxi.data.service.SettingManager;
import com.domikado.itaxi.data.api.ApiService;
import com.domikado.itaxi.utils.CommonUtils;
import com.domikado.itaxi.utils.DateUtils;
import com.orhanobut.hawk.Hawk;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.Lazy;
import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

public class ContentManagerActivity extends BaseSettingActivity {

    @BindView(R.id.txt_ads_last_update)
    TextView mTxtAdsLastUpdate;

    @BindView(R.id.txt_ads_last_checked)
    TextView mTxtAdsLastChecked;

    @BindView(R.id.txt_news_last_update)
    TextView mTxtNewsLastUpdate;

    @BindView(R.id.txt_news_last_checked)
    TextView mTxtNewsLastChecked;

    @BindView(R.id.txt_last_ping)
    TextView mTxtLastPing;

    @BindView(R.id.txt_settings_last_update)
    TextView mTxtSettingsLastUpdate;

    @BindView(R.id.txt_last_setup)
    TextView mTxtLastSetup;

    @BindView(R.id.txt_last_upload)
    TextView mTxtLastUpload;

    @Inject
    SettingManager settingManager;

    @Inject
    AdsManager adsManager;

    @Inject
    NewsManager newsManager;

    @Inject
    Lazy<PingManager> mPingManager;

    @Inject
    ApiService mApiService;

    @Inject
    AnalyticsGenerator analyticsGenerator;

    @Inject
    AnalyticsUploader analyticsUploader;

    @Inject
    ApkDownloader apkDownloader;

    public static void start(Context context) {
        context.startActivity(new Intent(context, ContentManagerActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TaxiApp.getComponent().inject(this);

        setContentView(R.layout.activity_content_manager);
        ButterKnife.bind(this);

        setupTitle("Content Manager");
        invalidateAdsUpdateHistory();
        invalidateNewsUpdateHistory();
        invalidateUploadHistory();
        invalidatePingHistory();
        invalidateSettingsUpdateHistory();
        invalidateSetupHistory();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_content_manager, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @OnClick(R.id.btn_ads_update)
    public void updateAds() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Checking & Updating...");
        dialog.show();

        adsManager.sync()
            .doOnTerminate(dialog::dismiss)
            .subscribe(new Subscriber<String>() {
                @Override
                public void onCompleted() {
                    CommonUtils.toast(ContentManagerActivity.this, "Your content is up to date.");
                    ContentManagerActivity.this.invalidateAdsUpdateHistory();
                }

                @Override
                public void onError(Throwable e) {
                    Timber.e(e);
                    CommonUtils.toast(ContentManagerActivity.this, e.getMessage());
                    ContentManagerActivity.this.invalidateAdsUpdateHistory();
                }

                @Override
                public void onNext(String version) {
                    Timber.i("[Ads UPDATE] Version %s is applied.", version);
                    CommonUtils.toast(ContentManagerActivity.this,
                        String.format("[Ads UPDATE] Version %s is applied.", version));
                    ContentManagerActivity.this.invalidateAdsUpdateHistory();
                }
            });
    }

    @OnClick(R.id.btn_news_update)
    public void updateNews() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Checking & updating...");
        dialog.show();

        newsManager.sync()
            .doOnTerminate(dialog::dismiss)
            .subscribe(new Subscriber<String>() {
                @Override
                public void onCompleted() {
                    CommonUtils.toast(ContentManagerActivity.this, "Your content is up to date.");
                    ContentManagerActivity.this.invalidateNewsUpdateHistory();
                }

                @Override
                public void onError(Throwable e) {
                    Timber.e(e);
                    CommonUtils.toast(ContentManagerActivity.this, e.getMessage());
                    ContentManagerActivity.this.invalidateNewsUpdateHistory();
                }

                @Override
                public void onNext(String version) {
                    Timber.i("[News UPDATE] Version %s is applied.", version);
                    CommonUtils.toast(ContentManagerActivity.this,
                        String.format("[News UPDATE] Version %s is applied.", version));
                    ContentManagerActivity.this.invalidateNewsUpdateHistory();
                }
            });
    }

    @OnClick(R.id.btnGenerateReports)
    public void onGenerateReportsClick() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Generating reports...");
        dialog.show();

        analyticsGenerator.generate()
            .doOnTerminate(dialog::dismiss)
            .subscribe(aBoolean -> {
                CommonUtils.toast(this, "Stats data successfully generated!.");
            }, throwable -> {
                Timber.e(throwable);
                CommonUtils.toast(this, throwable.getMessage());
            }, () -> CommonUtils.toast(this, "There is no more data need to be generated."));
    }

    @OnClick(R.id.btnUploadReports)
    public void onUploadReportsClick() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Uploading...");
        dialog.show();

        analyticsUploader.call()
            .doOnTerminate(dialog::dismiss)
            .subscribe(aBoolean -> {
                CommonUtils.toast(this, "Stats data successfully uploaded.");
                invalidateUploadHistory();
            }, throwable -> {
                Timber.e(throwable);
                CommonUtils.toast(this, throwable.getMessage());
                invalidateUploadHistory();
            }, () -> CommonUtils.toast(this, "There is no more data need to be uploaded."));
    }

    @OnClick(R.id.btnSettingPanelPing)
    public void onPingClick() {
        ProgressDialog dialog = ProgressDialog.show(this, null, "Pinging...", true);

        mPingManager.get().ping()
            .doOnTerminate(dialog::dismiss)
            .subscribe(aBoolean -> {
                CommonUtils.toast(this, "Ping success!");
                invalidatePingHistory();
            }, throwable -> {
                Timber.e(throwable);
                CommonUtils.toast(this, throwable.getMessage());
                invalidatePingHistory();
            });
    }

    @OnClick(R.id.btn_update_settings)
    public void updateSettings() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Updating ...");
        dialog.show();

        settingManager.update()
            .doOnTerminate(dialog::dismiss)
            .subscribe(i -> {
                CommonUtils.toast(this, "Settings updated!");
                this.invalidateSettingsUpdateHistory();
            }, throwable -> {
                Timber.e(throwable);
                CommonUtils.toast(this, throwable.getMessage());
            });
    }

    @OnClick(R.id.btn_update_apk)
    public void updateAPK() {
        apkDownloader
            .call()
            .subscribe(version -> {
                Timber.i("New APK (%s) installed successfully", version);
            }, Timber::e);
    }

    @OnClick(R.id.btn_initial_setup)
    public void onInitialSetup() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Setup...");
        dialog.show();

        settingManager.setDefault()
            .flatMap(s -> settingManager.update())
            .flatMap(s -> Observable.merge(newsManager.sync(), adsManager.sync()))
            .doOnTerminate(dialog::dismiss)
            .subscribe(s -> {
                invalidateSetupHistory();
                invalidateSettingsUpdateHistory();
                invalidateNewsUpdateHistory();
                invalidateAdsUpdateHistory();
            }, throwable -> {
                Timber.e(throwable);
                CommonUtils.toast(this, throwable.getMessage());
                invalidateSetupHistory();
                invalidateSettingsUpdateHistory();
                invalidateNewsUpdateHistory();
                invalidateAdsUpdateHistory();
            }, () -> {
                Hawk.put(Constant.DataStore.LAST_SETUP, new RequestHistory.Builder()
                    .setTimestamp(System.currentTimeMillis())
                    .createRequestHistory());
                CommonUtils.toast(this, "Original setup is done. Your content is up to date.");
                invalidateSetupHistory();
                invalidateSettingsUpdateHistory();
                invalidateNewsUpdateHistory();
                invalidateAdsUpdateHistory();
            });
    }

    private void invalidateAdsUpdateHistory() {
        Long lastChecked = Hawk.get(Constant.DataStore.ADS_LAST_CHECK);
        if (lastChecked != null) {
            mTxtAdsLastChecked.setText(String.format(
                "Last checked: %s", DateUtils.getSimpleTime(lastChecked)
            ));
        }

        RequestHistory history = Hawk.get(Constant.DataStore.ADS_LAST_UPDATE);
        if (history != null) {
            mTxtAdsLastUpdate.setText(String.format(Locale.getDefault(),
                "Last updated:" +
                    "\n URL: %s" +
                    "\n Version: %s" +
                    "\n Time: %s",
                history.getUrl(),
                AdsManager.getCurrentVersion(),
                DateUtils.getSimpleTime(history.getTimestamp())));
        }
    }

    private void invalidateNewsUpdateHistory() {
        Long lastChecked = Hawk.get(Constant.DataStore.NEWS_LAST_CHECK);
        if (lastChecked != null) {
            mTxtNewsLastChecked.setText(String.format(
                "Last checked: %s", DateUtils.getSimpleTime(lastChecked)
            ));
        }

        RequestHistory history = Hawk.get(Constant.DataStore.NEWS_LAST_UPDATE);
        if (history != null) {
            mTxtNewsLastUpdate.setText(String.format(Locale.getDefault(),
                "Last updated:" +
                    "\n URL: %s" +
                    "\n Version: %s" +
                    "\n Time: %s",
                history.getUrl(),
                NewsManager.getCurrentVersion(),
                DateUtils.getSimpleTime(history.getTimestamp())));
        }
    }

    private void invalidatePingHistory() {
        RequestHistory history = Hawk.get(Constant.DataStore.LAST_PING);
        if (history == null)
            return;

        mTxtLastPing.setText(String.format(Locale.getDefault(),
            "Last ping:" +
                "\n Time: %s " +
                "\n Response: %s",
            DateUtils.getSimpleTime(history.getTimestamp()),
            history.getResponse()));
    }

    private void invalidateSettingsUpdateHistory() {
        RequestHistory history = Hawk.get(Constant.DataStore.LAST_UPDATE_SETTINGS);
        if (history != null) {
            mTxtSettingsLastUpdate.setText(String.format(Locale.getDefault(),
                "Last updated:" +
                    "\n URL: %s" +
                    "\n Time: %s",
                history.getUrl(),
                DateUtils.getSimpleTime(history.getTimestamp())));
        }
    }

    private void invalidateSetupHistory() {
        RequestHistory history = Hawk.get(Constant.DataStore.LAST_SETUP);
        if (history != null) {
            mTxtLastSetup.setText(String.format(Locale.getDefault(),
                "Last setup:" +
                    "\n Time: %s",
                DateUtils.getSimpleTime(history.getTimestamp())));
        }
    }

    private void invalidateUploadHistory() {
        RequestHistory history = Hawk.get(Constant.DataStore.ANALYTICS_LAST_UPLOAD);
        if (history != null) {
            mTxtLastUpload.setText(String.format(Locale.getDefault(),
                "Last upload:" +
                    "\n Time: %s",
                DateUtils.getSimpleTime(history.getTimestamp())));
        }
    }
}
