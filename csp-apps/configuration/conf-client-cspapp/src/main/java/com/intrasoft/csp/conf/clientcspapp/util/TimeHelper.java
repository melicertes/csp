package com.intrasoft.csp.conf.clientcspapp.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Created by tangelatos on 10/09/2017.
 */
public final class TimeHelper {
    private static final DateTimeFormatter localIsoLike = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static final DateTimeFormatter iso =
            ISODateTimeFormat.dateTime().withZone(DateTimeZone.getDefault());
    private static final DateTimeFormatter compact = DateTimeFormat.forPattern("yyyyMMdd-HH:mm:ss");

    public static DateTimeFormatter getISOlikeDateTimeFormatter() {
        return localIsoLike;
    }

    public static final String localNow() {
        return compact.print(new LocalDateTime());
    }

    public static final void sleepFor(long milliseconds) {

        try {
            Thread.currentThread().sleep(milliseconds);
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
    }

    public static String isoNow() {
        return iso.print(new DateTime());
    }

    public static String isoFormat(LocalDateTime dateTime) {
        return iso.print(dateTime);
    }
}
