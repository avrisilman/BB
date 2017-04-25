package com.domikado.itaxi;

public class Constant {

    public static final String URL_MENU_WEBVIEW = "com.domikado.itaxi.MENU_WEBVIEW";
    public static final String PACKAGE_NAME = "com.domikado.mdm.agent";
    public static final String SYSTEM_SERVICE_START_ACTION = "com.domikado.mdm.system.service.START_SERVICE";
    public static final String USB_SERIAL_SERVICE = "com.domikado.mdm.system.service.USB_SERIAL_SERVICE";

    public static final String ACTION_USB_READY = "com.domikado.mdm.system.service.usb.USB_READY";
    public static final String ACTION_USB_NOT_SUPPORTED = "com.domikado.mdm.system.service.usb.USB_NOT_SUPPORTED";
    public static final String ACTION_NO_USB = "com.domikado.mdm.system.service.usb.NO_USB";
    public static final String ACTION_USB_DISCONNECTED = "com.domikado.mdm.system.service.usb.USB_DISCONNECTED";
    public static final String ACTION_USB_DEVICE_NOT_WORKING = "com.domikado.mdm.system.service.usb.ACTION_USB_DEVICE_NOT_WORKING";
    public static final String ACTION_USB_SERIAL_READ = "com.domikado.mdm.system.service.usb.ACTION_USB_SERIAL_READ";
    public static final String EXTRA_PARAM_USB_SERIAL_READ = "com.domikado.mdm.system.service.usb.EXTRA_PARAM_USB_SERIAL_READ";

    public final class DataStore {
        public static final String DB_NAME = "i-taxi.db";
        public static final String NEWS_DATA_DIR = "news/";
        public static final String CTA_DATA_DIR = "cta/";
        public static final String ADS_DATA_DIR = "ads/";
        public static final String STATISTIC_DATA_DIR = "stats/";
        public static final String LOG_DIR = "logs/";
        public static final String APK_DIR = "apk/";

        public static final String APPLICATION_SETTINGS = "com.domikado.itaxi.APPLICATION_SETTINGS";

        public static final String APK_LAST_CHECK = "com.domikado.itaxi.APK_LAST_CHECK";

        public static final String LAST_SETUP = "com.domikado.itaxi.LAST_SETUP";
        public static final String LAST_UPDATE_SETTINGS = "com.domikado.itaxi.LAST_UPDATE_SETTINGS";
        public static final String LAST_PING = "com.domikado.itaxi.LAST_PING";

        public static final String NEWS_LAST_CHECK = "com.domikado.itaxi.NEWS_LAST_CHECK";
        public static final String NEWS_LAST_UPDATE = "com.domikado.itaxi.NEWS_LAST_UPDATE";
        public static final String NEWS_BEFORE_VERSION = "com.domikado.itaxi.NEWS_BEFORE_VERSION";
        public static final String NEWS_CURRENT_VERSION = "com.domikado.itaxi.NEWS_CURRENT_VERSION";

        public static final String ADS_LAST_CHECK = "com.domikado.itaxi.ADS_LAST_CHECK";
        public static final String ADS_LAST_UPDATE = "com.domikado.itaxi.ADS_LAST_UPDATE";
        public static final String ADS_BEFORE_VERSION = "com.domikado.itaxi.ADS_BEFORE_VERSION";
        public static final String ADS_CURRENT_VERSION = "com.domikado.itaxi.ADS_CURRENT_VERSION";

        public static final String ANALYTICS_LAST_UPLOAD = "com.domikado.itaxi.ANALYTICS_LAST_UPLOAD";
        public static final String LOG_LAST_UPLOAD = "com.domikado.itaxi.LOG_LAST_UPLOAD";

        public static final String DEVICE_ON_BATTERY_TIME = "com.domikado.itaxi.DEVICE_ON_BATTERY_TIME";
    }

    public final class Operation {
        private Operation() {
            throw new AssertionError();
        }

        public static final String SCREEN_PINNING_APPLICATION = "SCREEN_PINNING_APPLICATION";
        public static final String DEVICE_SHUTDOWN = "DEVICE_SHUTDOWN";
        public static final String SILENT_INSTALL_APPLICATION = "SILENT_INSTALL_APPLICATION";
    }
}
