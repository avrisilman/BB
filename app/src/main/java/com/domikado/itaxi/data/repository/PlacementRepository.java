package com.domikado.itaxi.data.repository;

import com.activeandroid.query.Select;
import com.domikado.itaxi.data.entity.ads.Placement;

import java.util.List;

public class PlacementRepository {

    public static Placement findByName(String name) {
        return new Select()
            .from(Placement.class)
            .where("name = ?", name)
            .executeSingle();
    }

    public static List<Placement> findAll() {
        return new Select()
            .from(Placement.class)
            .execute();
    }
}
