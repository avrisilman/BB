package com.domikado.itaxi.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.media.AudioManager;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

import com.domikado.itaxi.TaxiApplication;
import com.domikado.itaxi.data.api.json.ApplicationSettingJson;

import org.threeten.bp.LocalTime;

import timber.log.Timber;


public class KioskUtils {

    public static void blockRecentApps(Activity activity) {
        ActivityManager activityManager = (ActivityManager) activity.getApplicationContext()
            .getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(activity.getTaskId(), 0);
    }

    public static int calculateBrightness(int brightness) {
        return (int) (255 * ((float) brightness / 100));
    }

    public static void setBrightness(Window window, int brightness) {
        brightness = calculateBrightness(brightness);

        Context c = window.getContext();
        Settings.System.putInt(c.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
        Settings.System.putInt(c.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);

        WindowManager.LayoutParams params = window.getAttributes();
        params.screenBrightness = brightness;
        window.setAttributes(params);
    }

    public static int getCurrentBrightnessSetting() {
        int hour = LocalTime.now().getHour();

        ApplicationSettingJson setting = TaxiApplication.getSettings();

        if (hour >= 6 && hour < 18)
            return setting.getDayBrightness();
        return setting.getNightBrightness();
    }

    public static void setVolumeDefault(Context context) {
        int initialVolume = TaxiApplication.getSettings().getInitialVolume();

        Timber.d("Set volume to default: %d", initialVolume );
        setVolumePercentage(context, initialVolume);
    }

    public static void setVolumePercentage(Context context, float percentageVolume) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        float realVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            * percentageVolume / 100;
        setVolume(context, (int) realVolume);
    }

    public static void setVolume(Context context, int volume) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_PLAY_SOUND);
    }
}
