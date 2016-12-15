package com.example.android.sunshine.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshine.R;
import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.utilities.SunshineDateUtils;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * The name of the extra Uri data.
     */
    public static final String EXTRA_WEATHER_URI = "com.example.android.sunshine.activity.weatherUri";

    /**
     * The columns of data that we are interested in displaying within our DetailActivity's list of
     * weather data.
     */
    public static final String[] DETAIL_FORECAST_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES
    };

    /**
     * We store the indices of the values in the array of Strings above to more quickly be able to
     * access the data from our query. If the order of the Strings above changes, these indices
     * must be adjusted to match the order of the Strings.
     */
    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_CONDITION_ID = 1;
    public static final int INDEX_WEATHER_MAX_TEMP = 2;
    public static final int INDEX_WEATHER_MIN_TEMP = 3;
    public static final int INDEX_WEATHER_HUMIDITY = 4;
    public static final int INDEX_WEATHER_PRESSURE = 5;
    public static final int INDEX_WEATHER_WIND_SPEED = 6;
    public static final int INDEX_WEATHER_DEGREES = 7;

    /**
     * In this Activity, you can share the selected day's forecast. No social sharing is complete
     * without using a hashtag. #BeTogetherNotTheSame
     */
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    /**
     * This ID will be used to identify the Loader responsible for loading our weather forecast. In
     * some cases, one Activity can deal with many Loaders. However, in our case, there is only one.
     * We will still use this ID to initialize the loader and create the loader for best practice.
     */
    private static final int ID_FORECAST_LOADER = 1;

    /**
     * The TextView that displays the selected weather date.
     */
    @BindView(R.id.tv_detail_date)
    TextView mDateTextView;

    /**
     * The TextView that displays the selected weather description.
     */
    @BindView(R.id.tv_detail_description)
    TextView mDescriptionTextView;

    /**
     * The TextView that displays the selected weather high temperature.
     */
    @BindView(R.id.tv_detail_high_temperature)
    TextView mHighTempTextView;

    /**
     * The TextView that displays the selected weather low temperature.
     */
    @BindView(R.id.tv_detail_low_temperature)
    TextView mLowTempTextView;

    /**
     * The TextView that displays the selected weather humidity.
     */
    @BindView(R.id.tv_detail_humidity)
    TextView mHumidityTextView;

    /**
     * The TextView that displays the selected weather pressure.
     */
    @BindView(R.id.tv_detail_pressure)
    TextView mPressureTextView;

    /**
     * The TextView that displays the selected weather wind speed.
     */
    @BindView(R.id.tv_detail_wind)
    TextView mWindSpeedTextView;

    /**
     * The URI that is used to access the chosen day's weather details.
     */
    private Uri mForecastUri;

    /**
     * Holds the forecast summary after loader has finished load it.
     */
    private String mForecastSummary;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        configure();
    }

    /**
     * Configures the DetailActivity.
     */
    private void configure() {
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_WEATHER_URI)) {
            mForecastUri = intent.getParcelableExtra(EXTRA_WEATHER_URI);
        }

        if (mForecastUri == null) {
            throw new NullPointerException("URI for DetailActivity cannot be null");
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.activity_detail_title);
        }

        /**
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(ID_FORECAST_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share_weather:
                Intent intent = createShareForecastIntent();
                if (intent != null) startActivity(intent);
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ID_FORECAST_LOADER:
                return new CursorLoader(this, mForecastUri, DETAIL_FORECAST_PROJECTION, null, null, null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            updateUiWithCursor(cursor);
        } else {
            Toast.makeText(this, "Failed to load forecast data", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void updateUiWithCursor(final Cursor cursor) {
        final long dateInMillis = cursor.getLong(DetailActivity.INDEX_WEATHER_DATE);
        String dateString = SunshineDateUtils.getFriendlyDateString(this, dateInMillis, false);

        final int weatherId = cursor.getInt(DetailActivity.INDEX_WEATHER_CONDITION_ID);
        String description = SunshineWeatherUtils.getStringForWeatherCondition(this, weatherId);

        final double highInCelsius = cursor.getDouble(DetailActivity.INDEX_WEATHER_MAX_TEMP);
        String highString = SunshineWeatherUtils.formatTemperature(this, highInCelsius);

        final double lowInCelsius = cursor.getDouble(DetailActivity.INDEX_WEATHER_MIN_TEMP);
        String lowString = SunshineWeatherUtils.formatTemperature(this, lowInCelsius);

        final float humidity = cursor.getFloat(DetailActivity.INDEX_WEATHER_HUMIDITY);
        String humidityString = getString(R.string.format_humidity, humidity);

        final float pressure = cursor.getFloat(DetailActivity.INDEX_WEATHER_PRESSURE);
        String pressureString = getString(R.string.format_pressure, pressure);

        final float windSpeed = cursor.getFloat(DetailActivity.INDEX_WEATHER_WIND_SPEED);
        final float weatherDegrees = cursor.getFloat(DetailActivity.INDEX_WEATHER_DEGREES);
        String windString = SunshineWeatherUtils.getFormattedWind(this, windSpeed, weatherDegrees);

        mDateTextView.setText(dateString);
        mDescriptionTextView.setText(description);
        mHighTempTextView.setText(highString);
        mLowTempTextView.setText(lowString);
        mHumidityTextView.setText(humidityString);
        mPressureTextView.setText(pressureString);
        mWindSpeedTextView.setText(windString);

        mForecastSummary = String.format("%s - %s - %s/%s", dateString, description, highString, lowString);
    }

    /**
     * Uses the ShareCompat Intent builder to create our Forecast intent for sharing. We set the
     * type of content that we are sharing (just regular text), the text itself, and we return the
     * newly created Intent.
     *
     * @return The Intent to use to start our share.
     */
    private Intent createShareForecastIntent() {
        if (mForecastSummary == null) return null;

        String type = "text/plain";
        String title = "Share the Forecast";

        return ShareCompat.IntentBuilder.from(this)
                .setType(type)
                .setText(mForecastSummary + FORECAST_SHARE_HASHTAG)
                .setChooserTitle(title)
                .getIntent();
    }

}
