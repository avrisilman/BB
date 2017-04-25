package com.domikado.itaxi.data.entity.ads;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.domikado.itaxi.data.api.json.ResolutionJson;

import java.util.Locale;

@Table(name = "placements")
public class Placement extends Model {

    public static final String ZONE_SPLASH = "splash_screen";
    public static final String ZONE_PREMIUM = "premium";

    public static final String ZONE_TOP = "top_banner";
    public static final String ZONE_MAIN = "middleboard";
    public static final String ZONE_BANNER_LEFT = "bottom_banner_left";
    public static final String ZONE_BANNER_RIGHT = "bottom_banner_right";
    public static final String ZONE_POPUP = "popup";

    @Column(name = "height")
    private int height;

    @Column(name = "width")
    private int width;

    @Column(unique = true, name = "name", onUniqueConflict = Column.ConflictAction.REPLACE)
    private String name;

    public Placement() {
        super();
    }

    public Placement(ResolutionJson resolution, String name) {
        super();
        this.height = resolution.getHeight();
        this.width = resolution.getWidth();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "Placement {name=%s, height=%d, width=%d}", name, height, width);
    }
}
