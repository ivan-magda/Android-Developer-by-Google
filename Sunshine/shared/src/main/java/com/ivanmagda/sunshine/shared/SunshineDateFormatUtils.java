package com.ivanmagda.sunshine.shared;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public final class SunshineDateFormatUtils {

    private SunshineDateFormatUtils() {
    }

    private static final SimpleDateFormat DATE_FORMATTER =
            new SimpleDateFormat("EEE, MMM d yyyy", Locale.getDefault());

    public static String dateStringFrom(long time) {
        return dateStringFrom(new Date(time));
    }

    public static String dateStringFrom(Date date) {
        return DATE_FORMATTER.format(date);
    }

}
