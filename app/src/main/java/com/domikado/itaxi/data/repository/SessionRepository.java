package com.domikado.itaxi.data.repository;

import com.activeandroid.query.Select;
import com.domikado.itaxi.TaxiApplication;
import com.domikado.itaxi.data.entity.Session;

import org.threeten.bp.Instant;

import rx.Emitter;
import rx.Observable;

public class SessionRepository {

    public static Observable<Long> startSession(String token) {
        return Observable.fromEmitter(emitter -> {
            Session session = new Session();
            session.setToken(token);
            session.setStartedAt(Instant.now().getEpochSecond());
            emitter.onNext(session.save());
            emitter.onCompleted();
        }, Emitter.BackpressureMode.BUFFER);
    }

    public static void endSession(long sessionId, int fare) {
        TaxiApplication.getComponent().executor().execute(() -> {
            Session session = new Select().from(Session.class).where("id = ?", sessionId).executeSingle();
            if (session != null) {
                session.setFinishedAt(Instant.now().getEpochSecond());
                session.setFare(fare);
                session.save();
            }
        });
    }

    public static boolean isAnySession() {
        return new Select()
            .from(Session.class)
            .count() > 0;
    }
}
