package com.domikado.itaxi.data;

import android.content.Context;

public class SPSession {

    private static final String PREF_KEY = "bulb_session";
    private static final String KEY_DEVICE_SESSION_TOKEN = "deviceToken";
    private static final String KEY_DEVICE_SESSION_ID = "deviceTokenId";

    private static void setSharedPreference(Context context, String mKey, String mValue) {
        context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE)
            .edit()
            .putString(mKey, mValue)
            .apply();
    }

    private static void setSharedPreference(Context context, String mKey, long mValue) {
        context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE)
            .edit()
            .putLong(mKey, mValue)
            .apply();
    }

    private static String getSharedPreferenceString(Context context, String mKey) {
        return context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE)
            .getString(mKey, null);
    }

    private static long getSharedPreferenceLong(Context context, String mKey) {
        return context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE)
            .getLong(mKey, 0);
    }

  /* --------------------------------------------------- */
  /* > Getter Setter */
  /* --------------------------------------------------- */

    public static String getSessionToken(Context context) {
        return getSharedPreferenceString(context, KEY_DEVICE_SESSION_TOKEN);
    }

    public static void setSessionToken(Context context, String deviceToken) {
        setSharedPreference(context, KEY_DEVICE_SESSION_TOKEN, deviceToken);
    }

    public static long getSessionId(Context context) {
        return getSharedPreferenceLong(context, KEY_DEVICE_SESSION_ID);
    }

    public static void setSessionId(Context context, long value) {
        setSharedPreference(context, KEY_DEVICE_SESSION_ID, value);
    }

    public static void clearSession(Context context) {
        context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE)
            .edit().clear().apply();
    }
}
