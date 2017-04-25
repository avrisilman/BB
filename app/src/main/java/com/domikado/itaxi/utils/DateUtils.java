package com.domikado.itaxi.utils;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Locale;

public class DateUtils {

    public static String getTimeInString(String dateInString, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");
        LocalDateTime dateTime = LocalDateTime.parse(dateInString, formatter);

        return getTimeInString(dateTime.toInstant(ZoneOffset.ofHours(7)).toEpochMilli(), format);
    }

    public static String getTimeInString(long milis, String format) {
        return LocalDateTime
            .ofInstant(Instant.ofEpochMilli(milis), ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern(format));
    }

    public static String getTimeInString(long milis, String format, Locale locale) {
        return LocalDateTime
                .ofInstant(Instant.ofEpochMilli(milis), ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern(format, locale));
    }
}
