package com.ivanmagda.sunshine.wear;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;

import com.ivanmagda.sunshine.shared.SunshineColorUtils;
import com.ivanmagda.sunshine.shared.SunshineDateFormatUtils;

import java.util.Calendar;
import java.util.TimeZone;

public final class SunshineWatchFace {

    private static final Typeface NORMAL_TYPEFACE =
            Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);

    private static final int TEXT_PRIMARY_COLOR = Color.WHITE;

    private final Context mContext;
    private final Paint mTimePaint;
    private final Paint mDatePaint;
    private final Paint mCenterDividerPaint;
    private final Paint mBackgroundPaint;

    private Calendar mCalendar;
    private boolean mShouldShowSeconds = true;

    public static SunshineWatchFace newInstance(@NonNull final Context context) {
        Resources resources = context.getResources();

        Paint timePaint = new Paint();
        timePaint.setColor(TEXT_PRIMARY_COLOR);
        timePaint.setTypeface(NORMAL_TYPEFACE);
        timePaint.setTextSize(resources.getDimension(R.dimen.time_size));
        timePaint.setAntiAlias(true);

        Paint datePaint = new Paint();
        datePaint.setColor(SunshineColorUtils.getPrimaryLightColor(context));
        datePaint.setTypeface(NORMAL_TYPEFACE);
        datePaint.setTextSize(context.getResources().getDimension(R.dimen.date_size));
        datePaint.setAntiAlias(true);

        Paint linePaint = new Paint();
        linePaint.setColor(SunshineColorUtils.getPrimaryLightColor(context));
        linePaint.setStrokeWidth(resources.getDimension(R.dimen.divider_height));
        linePaint.setAntiAlias(true);

        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(SunshineColorUtils.getPrimaryColor(context));

        return new SunshineWatchFace(context, timePaint, datePaint, linePaint, backgroundPaint,
                Calendar.getInstance());
    }

    private SunshineWatchFace(Context context, Paint timePaint, Paint datePaint,
                              Paint centerDividerPaint, Paint backgroundPaint, Calendar calendar) {
        this.mContext = context;
        this.mTimePaint = timePaint;
        this.mDatePaint = datePaint;
        this.mBackgroundPaint = backgroundPaint;
        this.mCenterDividerPaint = centerDividerPaint;
        this.mCalendar = calendar;
    }

    public void draw(Canvas canvas, Rect bounds) {
        long now = System.currentTimeMillis();
        mCalendar.setTimeInMillis(now);

        canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);
        drawCenterDivider(canvas, bounds);

        String dateText = SunshineDateFormatUtils.dateStringFrom(now).toUpperCase();
        float dateXOffset = computeXOffset(dateText, mDatePaint, bounds);
        float dateYOffset = computeDateYOffset(dateText, mDatePaint, bounds);
        canvas.drawText(dateText, dateXOffset, dateYOffset, mDatePaint);

        String timeText = SunshineDateFormatUtils.timeStringFrom(now, mShouldShowSeconds);
        float timeBounds = computeTimeBounds(timeText, mTimePaint);
        float timeXOffset = computeXOffset(timeText, mTimePaint, bounds);
        float timeYOffset = dateYOffset - (timeBounds / 2.0f) - 8.0f;
        canvas.drawText(timeText, timeXOffset, timeYOffset, mTimePaint);
    }

    private void drawCenterDivider(Canvas canvas, Rect watchBounds) {
        Resources resources = mContext.getResources();

        final float halfOfWidth = resources.getDimension(R.dimen.divider_width) / 2.0f;
        final float halfOfHeight = resources.getDimension(R.dimen.divider_height) / 2.0f;

        float centerX = watchBounds.exactCenterX();
        float centerY = watchBounds.exactCenterY();

        float startX = centerX - halfOfWidth;
        float endX = centerX + halfOfWidth;
        float startY = centerY - halfOfHeight;

        canvas.drawLine(startX, startY, endX, startY, mCenterDividerPaint);
    }

    private float computeXOffset(String text, Paint paint, Rect watchBounds) {
        float centerX = watchBounds.exactCenterX();
        float timeLength = paint.measureText(text);
        return centerX - (timeLength / 2.0f);
    }

    private float computeTimeBounds(String timeText, Paint timePaint) {
        Rect textBounds = new Rect();
        timePaint.getTextBounds(timeText, 0, timeText.length(), textBounds);
        return textBounds.height();
    }

    private float computeDateYOffset(String dateText, Paint datePaint, Rect watchBounds) {
        float centerY = watchBounds.exactCenterY();
        Rect textBounds = new Rect();
        datePaint.getTextBounds(dateText, 0, dateText.length(), textBounds);
        int textHeight = textBounds.height();
        return centerY - (textHeight / 2.0f) - 10.0f;
    }

    public void setTimeZone(TimeZone timeZone) {
        mCalendar.setTimeZone(timeZone);
    }

    public void setAntiAlias(boolean antiAlias) {
        mTimePaint.setAntiAlias(antiAlias);
        mDatePaint.setAntiAlias(antiAlias);
    }

    public void setShowSeconds(boolean showSeconds) {
        mShouldShowSeconds = showSeconds;
    }

    public void setInAmbientMode(boolean inAmbientMode) {
        if (inAmbientMode) {
            mBackgroundPaint.setColor(Color.BLACK);
            mTimePaint.setColor(Color.WHITE);
            mDatePaint.setColor(Color.WHITE);
            mCenterDividerPaint.setColor(Color.WHITE);
        } else {
            final int textSecondaryColor = SunshineColorUtils.getPrimaryLightColor(mContext);
            mBackgroundPaint.setColor(SunshineColorUtils.getPrimaryColor(mContext));
            mTimePaint.setColor(TEXT_PRIMARY_COLOR);
            mDatePaint.setColor(textSecondaryColor);
            mCenterDividerPaint.setColor(textSecondaryColor);
        }
    }

}
