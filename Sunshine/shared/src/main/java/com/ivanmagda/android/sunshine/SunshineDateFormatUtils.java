package com.ivanmagda.android.sunshine;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class SunshineDateFormatUtils {

    private SunshineDateFormatUtils() {
    }

    private static final SimpleDateFormat DATE_FORMATTER =
            new SimpleDateFormat("EEE, MMM d yyyy", Locale.getDefault());

    private static final SimpleDateFormat TIME_FORMATTER_WITH_SECONDS =
            new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat TIME_FORMATTER_WITHOUT_SECONDS =
            new SimpleDateFormat("HH:mm", Locale.getDefault());

    public static String dateStringFrom(long time) {
        return dateStringFrom(new Date(time));
    }

    public static String dateStringFrom(Date date) {
        return DATE_FORMATTER.format(date);
    }

    public static String timeStringFrom(long time, boolean showSeconds) {
        return timeStringFrom(new Date(time), showSeconds);
    }

    public static String timeStringFrom(Date date, boolean showSeconds) {
        return (showSeconds ? TIME_FORMATTER_WITH_SECONDS.format(date) :
                TIME_FORMATTER_WITHOUT_SECONDS.format(date)
        );
    }

}
