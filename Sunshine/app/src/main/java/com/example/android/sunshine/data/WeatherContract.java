package com.example.android.sunshine.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the weather database. This class is not necessary, but keeps
 * the code organized.
 */
public final class WeatherContract {

    /*
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website. A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * Play Store.
     */
    public static final String AUTHORITY = "com.example.android.sunshine";

    /*
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider for Sunshine.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    /**
     * Possible paths that can be appended to BASE_CONTENT_URI to form valid URI's that Sunshine
     * can handle. For instance,
     * <p>
     * content://com.example.android.sunshine/weather/
     * [           BASE_CONTENT_URI         ][ PATH_WEATHER ]
     * <p>
     * is a valid path for looking at weather data.
     * <p>
     * content://com.example.android.sunshine/givemeroot/
     * <p>
     * will fail, as the ContentProvider hasn't been given any information on what to do with
     * "givemeroot". At least, let's hope not. Don't be that dev, reader. Don't be that dev.
     */
    public static final String PATH_WEATHER = "weather";

    public static final class WeatherEntry implements BaseColumns {

        // TaskEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();

        /* Used internally as the name of our weather table. */
        public static final String TABLE_NAME = "weather";

        /*
         * The date column will store the UTC date that correlates to the local date for which
         * each particular weather row represents. For example, if you live in the Eastern
         * Standard Time (EST) time zone and you load weather data at 9:00 PM on September 23, 2016,
         * the UTC time stamp for that particular time would be 1474678800000 in milliseconds.
         * However, due to time zone offsets, it would already be September 24th, 2016 in the GMT
         * time zone when it is 9:00 PM on the 23rd in the EST time zone. In this example, the date
         * column would hold the date representing September 23rd at midnight in GMT time.
         * (1474588800000)
         *
         * The reason we store GMT time and not local time is because it is best practice to have a
         * "normalized", or standard when storing the date and adjust as necessary when
         * displaying the date. Normalizing the date also allows us an easy way to convert to
         * local time at midnight, as all we have to do is add a particular time zone's GMT
         * offset to this date to get local time at midnight on the appropriate date.
         */
        public static final String COLUMN_DATE = "date";

        /* Weather ID as returned by API, used to identify the icon to be used */
        public static final String COLUMN_WEATHER_ID = "weather_id";

        /* Min and max temperatures in °C for the day (stored as floats in the database) */
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";

        /* Humidity is stored as a float representing percentage */
        public static final String COLUMN_HUMIDITY = "humidity";

        /* Pressure is stored as a float representing percentage */
        public static final String COLUMN_PRESSURE = "pressure";

        /* Wind speed is stored as a float representing wind speed in mph */
        public static final String COLUMN_WIND_SPEED = "wind";

        /*
         * Degrees are meteorological degrees (e.g, 0 is north, 180 is south).
         * Stored as floats in the database.
         *
         * Note: These degrees are not to be confused with temperature degrees of the weather.
         */
        public static final String COLUMN_DEGREES = "degrees";
    }

}
