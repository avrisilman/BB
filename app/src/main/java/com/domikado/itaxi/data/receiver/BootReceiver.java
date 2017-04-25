package com.domikado.itaxi.data.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.domikado.itaxi.ui.ads.LaunchActivity;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent myIntent = new Intent(context, LaunchActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(myIntent);
    }
}