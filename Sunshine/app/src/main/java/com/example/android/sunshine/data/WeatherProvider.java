package com.example.android.sunshine.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.sunshine.data.WeatherContract.WeatherEntry;

public final class WeatherProvider extends ContentProvider {

    /*
     * These constant will be used to match URIs with the data they are looking for. We will take
     * advantage of the UriMatcher class to make that matching MUCH easier than doing something
     * ourselves, such as using regular expressions.
     */
    public static final int WEATHER = 100;
    public static final int WEATHER_WITH_DATE = 101;

    /**
     * The URI Matcher used by this content provider. The leading "s" in this variable name
     * signifies that this UriMatcher is a static member variable of WeatherProvider and is a
     * common convention in Android programming.
     */
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    /**
     * Creates the UriMatcher that will match each URI to the CODE_WEATHER and
     * CODE_WEATHER_WITH_DATE constants defined above.
     * <p>
     * It's possible you might be thinking, "Why create a UriMatcher when you can use regular
     * expressions instead? After all, we really just need to match some patterns, and we can
     * use regular expressions to do that right?" Because you're not crazy, that's why.
     * <p>
     * UriMatcher does all the hard work for you. You just have to tell it which code to match
     * with which URI, and it does the rest automatically. Remember, the best programmers try
     * to never reinvent the wheel. If there is a solution for a problem that exists and has
     * been tested and proven, you should almost always use it unless there is a compelling
     * reason not to.
     *
     * @return A UriMatcher that correctly matches the constants for CODE_WEATHER and CODE_WEATHER_WITH_DATE
     */
    private static UriMatcher buildUriMatcher() {
        /**
         * All paths added to the UriMatcher have a corresponding code to return when a match is
         * found. The code passed into the constructor of UriMatcher here represents the code to
         * return for the root URI. It's common to use NO_MATCH as the code for this case.
         */
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WeatherContract.AUTHORITY;

        /**
         * For each type of URI you want to add, create a corresponding code. Preferably, these are
         * constant fields in your class so that you can use them throughout the class and you no
         * they aren't going to change. In Sunshine, we use CODE_WEATHER or CODE_WEATHER_WITH_DATE.
         */
        /* This URI is content://com.example.android.sunshine/weather/ */
        uriMatcher.addURI(authority, WeatherContract.PATH_WEATHER, WEATHER);

        /**
         * This URI would look something like content://com.example.android.sunshine/weather/1472214172
         * The "/#" signifies to the UriMatcher that if PATH_WEATHER is followed by ANY number,
         * that it should return the CODE_WEATHER_WITH_DATE code
         */
        uriMatcher.addURI(authority, WeatherContract.PATH_WEATHER + "/#", WEATHER_WITH_DATE);

        return uriMatcher;
    }

    private WeatherDbHelper mDatabaseHelper;

    @Override
    public boolean onCreate() {
        this.mDatabaseHelper = new WeatherDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDatabaseHelper.getReadableDatabase();
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {

            /**
             * When sUriMatcher's match method is called with a URI that looks EXACTLY like this
             *
             *      content://com.example.android.sunshine/weather/
             *
             * sUriMatcher's match method will return the code that indicates to us that we need
             * to return all of the weather in our weather table.
             *
             * In this case, we want to return a cursor that contains every row of weather data
             * in our weather table.
             */
            case WEATHER:
                cursor = database.query(WeatherEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            /**
             * When sUriMatcher's match method is called with a URI that looks something like this
             *
             *      content://com.example.android.sunshine/weather/1472214172
             *
             * sUriMatcher's match method will return the code that indicates to us that we need
             * to return the weather for a particular date. The date in this code is encoded in
             * milliseconds and is at the very end of the URI (1472214172) and can be accessed
             * programmatically using Uri's getLastPathSegment method.
             *
             * In this case, we want to return a cursor that contains one row of weather data for
             * a particular date.
             */
            case WEATHER_WITH_DATE:
                selection = WeatherEntry.COLUMN_DATE + "=?";

                /**
                 * In order to determine the date associated with this URI, we look at the last
                 * path segment. In the comment above, the last path segment is 1472214172 and
                 * represents the number of seconds since the epoch, or UTC time.
                 */
                String normalizedUtcDateString = uri.getLastPathSegment();
                selectionArgs = new String[]{normalizedUtcDateString};

                cursor = database.query(WeatherEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        Context context = getContext();
        if (context != null) {
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case WEATHER:
                long rowId = database.insertOrThrow(WeatherEntry.TABLE_NAME, null, null);
                notifyChangeForUri(uri);
                return ContentUris.withAppendedId(uri, rowId);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    private void notifyChangeForUri(Uri uri) {
        Context context = getContext();
        if (context != null) context.getContentResolver().notifyChange(uri, null);
    }

    private String idStringFromUri(Uri uri) {
        return String.valueOf(ContentUris.parseId(uri));
    }

}
