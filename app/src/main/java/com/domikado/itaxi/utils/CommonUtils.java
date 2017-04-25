package com.domikado.itaxi.utils;

import android.app.Activity;
import android.content.Context;
import android.os.StrictMode;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class CommonUtils {

    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void hideSoftKeyboard(Activity activity) {
        try {
            hideSoftKeyboard(activity.getCurrentFocus());
        } catch (Exception ignored) {
        }
    }

    public static void hideSoftKeyboard(View view) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception ignored) {
        }
    }

    public static void enableStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
            .detectDiskReads()
            .detectDiskWrites()
            .detectNetwork()   // or .detectAll() for all detectable problems
            .penaltyLog()
            .build());

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//			.detectLeakedSqlLiteObjects()
            .detectAll()
//			.detectLeakedClosableObjects()
            .penaltyLog()
            .penaltyDeath()
            .build());
    }
}
