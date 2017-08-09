package com.intrasoft.csp.conf.server.utils;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class JodaConverter {

    public static Date getPostgresTimestamptzFromJodaString(String jodaString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date d = sdf.parse(jodaString);

        return d;
    }

    public static String getJodaStringFromPostgresTimestamptz(String pgTimestamptz) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ssZ");
        DateTime dt = DateTime.parse(pgTimestamptz, formatter);

        Date d = dt.toDate();
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);

        return output.format(d);
    }

    public static String getCurrentJodaString() {
        Date d = new Date();
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        output.setTimeZone(TimeZone.getTimeZone("GMT"));
        return output.format(d);
    }
}
