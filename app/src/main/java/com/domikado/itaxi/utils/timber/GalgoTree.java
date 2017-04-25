package com.domikado.itaxi.utils.timber;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.inaka.galgo.Galgo;
import com.inaka.galgo.GalgoOptions;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import timber.log.Timber;

public class GalgoTree extends Timber.DebugTree {

    private int minPriority = Log.DEBUG;

    public GalgoTree(Context context, int minPriority) {
        this.minPriority = minPriority;
        GalgoOptions options = new GalgoOptions.Builder()
            .numberOfLines(30)
            .backgroundColor(Color.parseColor("#80d6d6d6"))
            .textColor(Color.BLACK)
            .textSize(12)
            .build();
        Galgo.enable(context, options);
    }

    @Override
    protected String createStackElementTag(StackTraceElement element) {
        return String.format("%s %s",
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME),
            super.createStackElementTag(element)
        );
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if (priority < minPriority)
            return;
        String logMessage = tag + ": " + message;
        Galgo.log(logMessage);
    }
}
