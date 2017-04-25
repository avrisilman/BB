package com.domikado.itaxi.data.repository;

import android.content.Context;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.domikado.itaxi.TaxiApplication;
import com.domikado.itaxi.data.entity.ads.CallToAction;
import com.domikado.itaxi.data.entity.ads.AdsResult;
import com.domikado.itaxi.data.SPSession;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CTARepository {

    public static CallToAction findCTAById(long id) {
        return new Select()
            .from(CallToAction.class)
            .where("id = ?", id)
            .executeSingle();
    }

    public static CallToAction findCTAByServerId(long id) {
        return new Select()
            .from(CallToAction.class)
            .where("server_id = ?", id)
            .executeSingle();
    }

    public static void submitCTAResult(final Context context, String result, long adsId) {
        Date datetime = new Date();
        SimpleDateFormat dtformat = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");
        TaxiApplication.getComponent().executor().execute(() -> {
            AdsResult actionResult = new AdsResult();
            actionResult.setDatetime(dtformat.format(datetime));
            actionResult.setData(result);
            actionResult.setSessionId(SPSession.getSessionId(context));
            actionResult.setAdsId(adsId);
            actionResult.save();
        });
    }

    public static boolean isAlreadySubmitCTA(Context context, long adsId) {
        return new Select()
            .from(AdsResult.class)
            .where("ads_id = ?", adsId)
            .and("session_id = ?", SPSession.getSessionId(context))
            .exists();
    }

    public static void deleteAllCTAResult() {
        new Delete()
            .from(AdsResult.class)
            .execute();
    }
}
