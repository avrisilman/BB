package com.domikado.itaxi.data.repository;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.domikado.itaxi.Constant;
import com.domikado.itaxi.data.entity.content.News;
import com.orhanobut.hawk.Hawk;

import java.util.List;

public class NewsRepository {

    public static List<News> getAll() {
        String version = Hawk.get(Constant.DataStore.NEWS_CURRENT_VERSION);
        return new Select()
            .from(News.class)
            .where("version = ?", version)
            .execute();
    }

    public static News findByFingerprint(String fingerprint) {
        if (fingerprint == null)
            return null;

        return new Select()
            .from(News.class)
            .where("fingerprint = ?", fingerprint)
            .executeSingle();
    }

    public static void deleteThisVersion(String version) {
        new Delete()
            .from(News.class)
            .where("version = ?", version)
            .execute();
    }

    public static void deleteObsoleteNews() {
        String before = Hawk.get(Constant.DataStore.NEWS_BEFORE_VERSION);
        String current = Hawk.get(Constant.DataStore.NEWS_CURRENT_VERSION);

        if (before == null || current == null) return;
        new Delete()
            .from(News.class)
            .where("version NOT IN (?, ?)", new String[] {before, current})
            .execute();
    }
}
