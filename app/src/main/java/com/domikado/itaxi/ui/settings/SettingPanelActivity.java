package com.domikado.itaxi.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.domikado.itaxi.BuildConfig;
import com.domikado.itaxi.R;
import com.domikado.itaxi.TaxiApplication;
import com.domikado.itaxi.data.api.json.ApplicationSettingJson;
import com.domikado.itaxi.data.service.content.SettingManager;
import com.domikado.itaxi.ui.ads.MainActivity;
import com.domikado.itaxi.ui.base.BaseSettingActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingPanelActivity extends BaseSettingActivity {

    @BindView(R.id.txt_device_id)
    TextView txtDeviceId;

    @BindView(R.id.txt_ping_interval)
    TextView txtPingInterval;

    @BindView(R.id.txt_visibility_off)
    TextView txtVisibilityOff;

    @BindView(R.id.txt_day_brightness)
    TextView txtDayBrightness;

    @BindView(R.id.txt_night_brightness)
    TextView txtNightBrightness;

    @BindView(R.id.txt_initial_volume)
    TextView txtInitialVolume;

    @BindView(R.id.txt_location_update_interval)
    TextView txtLocationUpdateInterval;

    @BindView(R.id.txt_splash_screen_duration)
    TextView txtSplashScreenDuration;

    @BindView(R.id.txt_call_to_action_show)
    TextView txtCallToActionShow;

    @BindView(R.id.txt_call_to_action_dismiss)
    TextView txtCallToActionDismiss;

    @BindView(R.id.txt_playlist_url)
    TextView txtPlaylistUrl;

    @BindView(R.id.txt_settings_url)
    TextView txtSettingsUrl;

//    @BindView(R.id.txt_news_url)
//    TextView txtNewsUrl;

    @BindView(R.id.txt_menu_url)
    TextView txtMenuUrl;

    @BindView(R.id.txt_upload_analytics_url)
    TextView txtUploadAnalyticsUrl;

    @BindView(R.id.txt_upload_log_url)
    TextView txtUploadLogUrl;

    @BindView(R.id.txt_banner_slide_interval)
    TextView txtBannerSlideInterval;

    @BindView(R.id.txt_news_auto_update_interval)
    TextView txtNewsAutoUpdateInterval;

    @BindView(R.id.txt_ads_auto_update_interval)
    TextView txtAdsAutoUpdateAutoUpdate;

    @BindView(R.id.txt_settings_auto_update_interval)
    TextView txtSettingsAutoUpdateInterval;

    @BindView(R.id.txt_analytics_auto_upload_interval)
    TextView txtStatisticsAutoUploadInterval;

    @BindView(R.id.txt_log_auto_upload_interval)
    TextView txtLogAutoUploadInterval;

    @BindView(R.id.txt_idle_time_before_shutdown)
    TextView txtIdleTimeBeforeShutdown;

    @BindView(R.id.txt_taximeter)
    TextView txtTaximeter;

    @BindView(R.id.txt_version)
    TextView txtVersion;

    @Inject
    SettingManager settingManager;

    public static void start(Context context) {
        context.startActivity(new Intent(context, SettingPanelActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent().inject(this);

        setContentView(R.layout.activity_setting_panel);
        ButterKnife.bind(this);

        setupTitle(getString(R.string.setting_panel));

        ApplicationSettingJson setting = TaxiApplication.getSettings();
        txtDeviceId.setText(TaxiApplication.getComponent().device().getIMEI());
        txtPingInterval.setText(String.valueOf(setting.getPingInterval()));
        txtVisibilityOff.setText(String.valueOf(setting.getVisibilityOff()));
        txtDayBrightness.setText(String.valueOf(setting.getDayBrightness()));
        txtNightBrightness.setText(String.valueOf(setting.getNightBrightness()));
        txtInitialVolume.setText(String.valueOf(setting.getInitialVolume()));
        txtLocationUpdateInterval.setText(String.valueOf(setting.getLocationUpdateInterval()));
        txtSplashScreenDuration.setText(String.valueOf(setting.getSplashScreenDuration()));
        txtBannerSlideInterval.setText(String.valueOf(setting.getBannerSlideInterval()));
        txtCallToActionShow.setText(String.valueOf(setting.getCallToActionShow()));
        txtCallToActionDismiss.setText(String.valueOf(setting.getCallToActionDismiss()));
        txtPlaylistUrl.setText(setting.getPlaylistsUrl());
        txtSettingsUrl.setText(setting.getApplicationSettingsUrl());
        //txtNewsUrl.setText(setting.getNewsUrl());
        txtMenuUrl.setText(setting.getMenuUrl());
        txtUploadAnalyticsUrl.setText(setting.getUploadAnalyticsUrl());
        txtUploadLogUrl.setText(setting.getUploadLogUrl());
        txtAdsAutoUpdateAutoUpdate.setText(String.valueOf(setting.getAdsAutoUpdateInterval()));
        txtNewsAutoUpdateInterval.setText(String.valueOf(setting.getNewsAutoUpdateInterval()));
        txtSettingsAutoUpdateInterval.setText(String.valueOf(setting.getSettingAutoUpdateInterval()));
        txtStatisticsAutoUploadInterval.setText(String.valueOf(setting.getAnalyticsAutoUploadInterval()));
        txtLogAutoUploadInterval.setText(String.valueOf(setting.getLogAutoUploadInterval()));
        txtIdleTimeBeforeShutdown.setText(String.valueOf(setting.getIdleTimeBeforeShutdown()));
        txtTaximeter.setText(setting.getTaximeter());
        txtVersion.setText(BuildConfig.VERSION_NAME);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void finish() {
        MainActivity.start(this);
        super.finish();
    }
}
