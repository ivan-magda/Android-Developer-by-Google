package com.ivanmagda.sunshine.wear;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;

import com.ivanmagda.sunshine.shared.SunshineDateFormatUtils;

import java.util.Calendar;
import java.util.TimeZone;

public final class SunshineWatchFace {

    private static final Typeface NORMAL_TYPEFACE =
            Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);

    private static final String TIME_FORMAT_WITHOUT_SECONDS = "%02d:%02d";
    private static final String TIME_FORMAT_WITH_SECONDS = TIME_FORMAT_WITHOUT_SECONDS + ":%02d";

    private static final int BACKGROUND_DEFAULT_COLOR = Color.parseColor("#03A9F4");
    private static final int TEXT_PRIMARY_COLOR = Color.WHITE;
    private static final int TEXT_SECONDARY_COLOR = Color.parseColor("#BBE5FB");

    private final Paint mTimePaint;
    private final Paint mDatePaint;
    private final Paint mBackgroundPaint;

    private Calendar mCalendar;
    private boolean shouldShowSeconds = true;

    public static SunshineWatchFace newInstance(@NonNull final Context context) {
        Paint timePaint = new Paint();
        timePaint.setColor(TEXT_PRIMARY_COLOR);
        timePaint.setTypeface(NORMAL_TYPEFACE);
        timePaint.setTextSize(context.getResources().getDimension(R.dimen.time_size));
        timePaint.setAntiAlias(true);

        Paint datePaint = new Paint();
        datePaint.setColor(TEXT_SECONDARY_COLOR);
        datePaint.setTextSize(context.getResources().getDimension(R.dimen.date_size));
        datePaint.setTypeface(NORMAL_TYPEFACE);
        datePaint.setAntiAlias(true);

        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(BACKGROUND_DEFAULT_COLOR);

        return new SunshineWatchFace(timePaint, datePaint, backgroundPaint, Calendar.getInstance());
    }

    private SunshineWatchFace(Paint timePaint, Paint datePaint, Paint backgroundPaint, Calendar calendar) {
        this.mTimePaint = timePaint;
        this.mDatePaint = datePaint;
        this.mBackgroundPaint = backgroundPaint;
        mCalendar = calendar;
    }

    public void draw(Canvas canvas, Rect bounds) {
        long now = System.currentTimeMillis();
        mCalendar.setTimeInMillis(now);

        canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);

        String timeText = String.format(shouldShowSeconds
                        ? TIME_FORMAT_WITH_SECONDS : TIME_FORMAT_WITHOUT_SECONDS,
                mCalendar.get(Calendar.HOUR),
                mCalendar.get(Calendar.MINUTE),
                mCalendar.get(Calendar.SECOND));
        float timeXOffset = computeXOffset(timeText, mTimePaint, bounds);
        float timeYOffset = computeTimeYOffset(timeText, mTimePaint, bounds);
        canvas.drawText(timeText, timeXOffset, timeYOffset, mTimePaint);

        String dateText = SunshineDateFormatUtils.dateStringFrom(now).toUpperCase();
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

    public void setTimeZone(TimeZone timeZone) {
        mCalendar.setTimeZone(timeZone);
    }

    public void setAntiAlias(boolean antiAlias) {
        mTimePaint.setAntiAlias(antiAlias);
        mDatePaint.setAntiAlias(antiAlias);
    }

    public void setShowSeconds(boolean showSeconds) {
        shouldShowSeconds = showSeconds;
    }

    public void setInAmbientMode(boolean inAmbientMode) {
        if (inAmbientMode) {
            mBackgroundPaint.setColor(Color.BLACK);
            mTimePaint.setColor(Color.WHITE);
            mDatePaint.setColor(Color.WHITE);
        } else {
            mBackgroundPaint.setColor(BACKGROUND_DEFAULT_COLOR);
            mTimePaint.setColor(TEXT_PRIMARY_COLOR);
            mDatePaint.setColor(TEXT_SECONDARY_COLOR);
        }
    }

}
