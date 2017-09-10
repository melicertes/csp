package com.intrasoft.csp.conf.clientcspapp.service;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by tangelatos on 10/09/2017.
 */
public class TimeHelper {
    private static final  DateTimeFormatter isoLike = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static final  DateTimeFormatter compact = DateTimeFormat.forPattern("yyyyMMdd-HH:mm:ss");

    public static DateTimeFormatter getISOlikeDateTimeFormatter() {
        return isoLike;
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
}
