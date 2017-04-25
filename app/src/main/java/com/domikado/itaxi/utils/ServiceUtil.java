package com.domikado.itaxi.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.domikado.itaxi.Constant;

import java.util.List;

public class ServiceUtil {

    public static void callSystemService(Context context, String operation, String appUri) {
        Intent intent = new Intent(Constant.SYSTEM_SERVICE_START_ACTION);
        Intent explicitIntent = createExplicitFromImplicitIntent(context, intent);
        if (explicitIntent != null) {
            intent = explicitIntent;
        }
        intent.putExtra("code", operation);
        intent.setPackage(Constant.PACKAGE_NAME);
        if (appUri != null) {
            intent.putExtra("appUri", appUri);
        }
        context.startService(intent);
    }

    public static void startUsbSerialService(Context context) {
        Intent intent = new Intent(Constant.USB_SERIAL_SERVICE);
        Intent explicitIntent = createExplicitFromImplicitIntent(context, intent);
        if (explicitIntent != null) {
            intent = explicitIntent;
        }
        context.startService(intent);
    }

    private static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        Intent explicitIntent = new Intent(implicitIntent);
        explicitIntent.setComponent(component);

        return explicitIntent;
    }
}
