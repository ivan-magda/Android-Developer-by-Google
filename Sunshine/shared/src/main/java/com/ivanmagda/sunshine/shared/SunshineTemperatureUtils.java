package com.ivanmagda.sunshine.shared;

import android.content.Context;

public final class SunshineTemperatureUtils {

    private SunshineTemperatureUtils() {
    }

    /**
     * Temperature data is stored in Celsius by our app. Depending on the user's preference,
     * the app may need to display the temperature in Fahrenheit. This method will perform that
     * temperature conversion if necessary. It will also format the temperature so that no
     * decimal points show. Temperatures will be formatted to the following form: "21°"
     *
     * @param context     Android Context to access preferences and resources
     * @param temperature Temperature in degrees Celsius (°C)
     * @return Formatted temperature String in the following form:
     * "21°"
     */
    public static String formatTemperature(Context context, double temperature, boolean inCelsius) {
        if (!inCelsius) {
            temperature = celsiusToFahrenheit(temperature);
        }

        int temperatureFormatResourceId = R.string.format_temperature;

        /* For presentation, assume the user doesn't care about tenths of a degree. */
        return String.format(context.getString(temperatureFormatResourceId), temperature);
    }

    /**
     * This method will convert a temperature from Celsius to Fahrenheit.
     *
     * @param temperatureInCelsius Temperature in degrees Celsius(°C)
     * @return Temperature in degrees Fahrenheit (°F)
     */
    private static double celsiusToFahrenheit(double temperatureInCelsius) {
        return (temperatureInCelsius * 1.8) + 32;
    }

}
