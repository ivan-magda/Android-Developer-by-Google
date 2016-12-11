package com.example.android.sunshine.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.android.sunshine.data.WeatherContract.WeatherEntry;

public final class WeatherDbHelper extends SQLiteOpenHelper {

    /*
     * This is the name of our database. Database names should be descriptive and end with the
     * .db extension.
     */
    private static final String DATABASE_NAME = "weather.db";

    /*
     * If you change the database schema, you must increment the database version or the onUpgrade
     * method will not be called.
     *
     * The reason DATABASE_VERSION starts at 3 is because Sunshine has been used in conjunction
     * with the Android course for a while now. Believe it or not, older versions of Sunshine
     * still exist out in the wild. If we started this DATABASE_VERSION off at 1, upgrading older
     * versions of Sunshine could cause everything to break. Although that is certainly a rare
     * use-case, we wanted to watch out for it and warn you what could happen if you mistakenly
     * version your databases.
     */
    private static final int DATABASE_VERSION = 1;

    private static final String INTEGER_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ", ";

    /*
     * This String will contain a simple SQL statement that will create a table that will
     * cache our weather data.
     */
    private static final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " +
            WeatherEntry.TABLE_NAME + " (" +
            /*
             * WeatherEntry did not explicitly declare a column called "_ID". However,
             * WeatherEntry implements the interface, "BaseColumns", which does have a field
             * named "_ID". We use that here to designate our table's primary key.
             */
            WeatherEntry._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
            WeatherEntry.COLUMN_DATE + INTEGER_TYPE + COMMA_SEP +
            WeatherEntry.COLUMN_WEATHER_ID + INTEGER_TYPE + COMMA_SEP +
            WeatherEntry.COLUMN_MIN_TEMP + REAL_TYPE + COMMA_SEP +
            WeatherEntry.COLUMN_MAX_TEMP + REAL_TYPE + COMMA_SEP +
            WeatherEntry.COLUMN_HUMIDITY + REAL_TYPE + COMMA_SEP +
            WeatherEntry.COLUMN_PRESSURE + REAL_TYPE + COMMA_SEP +
            WeatherEntry.COLUMN_WIND_SPEED + REAL_TYPE + COMMA_SEP +
            WeatherEntry.COLUMN_DEGREES + REAL_TYPE + ");";

    /**
     * Constructs a new instance of {@link SQLiteOpenHelper}.
     *
     * @param context of the app
     */
    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the creation of
     * tables and the initial population of the tables should happen.
     *
     * @param sqLiteDatabase The database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    /**
     * This database is only a cache for online data, so its upgrade policy is simply to discard
     * the data and call through to onCreate to recreate the table. Note that this only fires if
     * you change the version number for your database (in our case, DATABASE_VERSION). It does NOT
     * depend on the version number for your application found in your app/build.gradle file. If
     * you want to update the schema without wiping data, commenting out the current body of this
     * method should be your top priority before modifying this method.
     *
     * @param sqLiteDatabase Database that is being upgraded
     * @param oldVersion     The old database version
     * @param newVersion     The new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    }

}
