package com.ivanmagda.mywatchface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.text.format.Time;

public final class SimpleWatchFace {

    private static final String TIME_FORMAT_WITHOUT_SECONDS = "%02d.%02d";
    private static final String TIME_FORMAT_WITH_SECONDS = TIME_FORMAT_WITHOUT_SECONDS + ".%02d";
    private static final String DATE_FORMAT = "%02d.%02d.%d";

    private static final int DATE_AND_TIME_DEFAULT_COLOUR = Color.WHITE;
    private static final int BACKGROUND_DEFAULT_COLOUR = Color.BLACK;

    private int mBackgroundColour = BACKGROUND_DEFAULT_COLOUR;
    private int mDateAndTimeColour = DATE_AND_TIME_DEFAULT_COLOUR;

    private final Paint mTimePaint;
    private final Paint mDatePaint;
    private final Paint mBackgroundPaint;
    private final Time mTime;

    private boolean shouldShowSeconds = true;

    public static SimpleWatchFace newInstance(@NonNull final Context context) {
        Paint timePaint = new Paint();
        timePaint.setColor(DATE_AND_TIME_DEFAULT_COLOUR);
        timePaint.setTextSize(context.getResources().getDimension(R.dimen.time_size));
        timePaint.setAntiAlias(true);

        Paint datePaint = new Paint();
        datePaint.setColor(DATE_AND_TIME_DEFAULT_COLOUR);
        datePaint.setTextSize(context.getResources().getDimension(R.dimen.date_size));
        datePaint.setAntiAlias(true);

        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(BACKGROUND_DEFAULT_COLOUR);

        return new SimpleWatchFace(timePaint, datePaint, backgroundPaint, new Time());
    }

    SimpleWatchFace(Paint timePaint, Paint datePaint, Paint backgroundPaint, Time time) {
        this.mTimePaint = timePaint;
        this.mDatePaint = datePaint;
        this.mBackgroundPaint = backgroundPaint;
        this.mTime = time;
    }

    public void draw(Canvas canvas, Rect bounds) {
        mTime.setToNow();
        canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);

        String timeText = String.format(
                shouldShowSeconds ? TIME_FORMAT_WITH_SECONDS : TIME_FORMAT_WITHOUT_SECONDS,
                mTime.hour, mTime.minute, mTime.second);
        float timeXOffset = computeXOffset(timeText, mTimePaint, bounds);
        float timeYOffset = computeTimeYOffset(timeText, mTimePaint, bounds);
        canvas.drawText(timeText, timeXOffset, timeYOffset, mTimePaint);

        String dateText = String.format(DATE_FORMAT, mTime.monthDay, (mTime.month + 1), mTime.year);
        float dateXOffset = computeXOffset(dateText, mDatePaint, bounds);
        float dateYOffset = computeDateYOffset(dateText, mDatePaint);
        canvas.drawText(dateText, dateXOffset, timeYOffset + dateYOffset, mDatePaint);
    }

    private float computeXOffset(String text, Paint paint, Rect watchBounds) {
        float centerX = watchBounds.exactCenterX();
        float timeLength = paint.measureText(text);
        return centerX - (timeLength / 2.0f);
    }

    private float computeTimeYOffset(String timeText, Paint timePaint, Rect watchBounds) {
        float centerY = watchBounds.exactCenterY();
        Rect textBounds = new Rect();
        timePaint.getTextBounds(timeText, 0, timeText.length(), textBounds);
        int textHeight = textBounds.height();
        return centerY + (textHeight / 2.0f);
    }

    private float computeDateYOffset(String dateText, Paint datePaint) {
        Rect textBounds = new Rect();
        datePaint.getTextBounds(dateText, 0, dateText.length(), textBounds);
        return textBounds.height() + 10.0f;
    }

    public void setAntiAlias(boolean antiAlias) {
        mTimePaint.setAntiAlias(antiAlias);
        mDatePaint.setAntiAlias(antiAlias);
    }

    public void setColor(int color) {
        mTimePaint.setColor(color);
        mDatePaint.setColor(color);
    }

    public void setShowSeconds(boolean showSeconds) {
        shouldShowSeconds = showSeconds;
    }

    public void updateDateAndTimeColourTo(int colour) {
        mDateAndTimeColour = colour;
        mTimePaint.setColor(colour);
        mDatePaint.setColor(colour);
    }

    public void updateBackgroundColourTo(int colour) {
        mBackgroundColour = colour;
        mBackgroundPaint.setColor(colour);
    }

    public void updateBackgroundColourToDefault() {
        mBackgroundPaint.setColor(BACKGROUND_DEFAULT_COLOUR);
    }

    public void updateDateAndTimeColourToDefault() {
        mTimePaint.setColor(DATE_AND_TIME_DEFAULT_COLOUR);
        mDatePaint.setColor(DATE_AND_TIME_DEFAULT_COLOUR);
    }

    public void restoreDateAndTimeColour() {
        mTimePaint.setColor(mDateAndTimeColour);
        mDatePaint.setColor(mDateAndTimeColour);
    }

    public void restoreBackgroundColour() {
        mBackgroundPaint.setColor(mBackgroundColour);
    }

}
