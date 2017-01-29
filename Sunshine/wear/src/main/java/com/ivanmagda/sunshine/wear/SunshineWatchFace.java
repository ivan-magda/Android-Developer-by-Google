package com.ivanmagda.sunshine.wear;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;

import com.ivanmagda.sunshine.shared.SunshineColorUtils;
import com.ivanmagda.sunshine.shared.SunshineDateFormatUtils;
import com.ivanmagda.sunshine.shared.SunshineTemperatureUtils;

import java.util.Calendar;
import java.util.Date;
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
    private final Paint mHighTempPaint;
    private final Paint mLowTempPaint;
    private final Paint mIconPaint = new Paint();

    private Calendar mCalendar;
    private boolean mShouldShowSeconds = true;
    private boolean mIsInAmbientMode = false;

    public static SunshineWatchFace newInstance(@NonNull final Context context) {
        Resources resources = context.getResources();

        Paint timePaint = createTextPaint(TEXT_PRIMARY_COLOR,
                resources.getDimension(R.dimen.time_size));
        Paint datePaint = createTextPaint(SunshineColorUtils.getPrimaryLightColor(context),
                resources.getDimension(R.dimen.date_size));

        Paint highTempPaint = createTextPaint(TEXT_PRIMARY_COLOR,
                resources.getDimension(R.dimen.temp_text_size));
        Paint lowTempPaint = createTextPaint(SunshineColorUtils.getPrimaryLightColor(context),
                resources.getDimension(R.dimen.temp_text_size));

        Paint linePaint = new Paint();
        linePaint.setColor(SunshineColorUtils.getPrimaryLightColor(context));
        linePaint.setStrokeWidth(resources.getDimension(R.dimen.divider_height));
        linePaint.setAntiAlias(true);

        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(SunshineColorUtils.getPrimaryColor(context));

        return new SunshineWatchFace(context, timePaint, datePaint, linePaint, backgroundPaint,
                highTempPaint, lowTempPaint, Calendar.getInstance());
    }

    private static Paint createTextPaint(int colorId, float textSize) {
        Paint paint = new Paint();
        paint.setColor(colorId);
        paint.setTypeface(NORMAL_TYPEFACE);
        paint.setTextSize(textSize);
        paint.setAntiAlias(true);

        return paint;
    }

    private SunshineWatchFace(@NonNull final Context context, Paint timePaint, Paint datePaint,
                              Paint centerDividerPaint, Paint backgroundPaint, Paint highTempPaint,
                              Paint lowTempPaint, Calendar calendar) {
        this.mContext = context;
        this.mTimePaint = timePaint;
        this.mDatePaint = datePaint;
        this.mBackgroundPaint = backgroundPaint;
        this.mCenterDividerPaint = centerDividerPaint;
        this.mHighTempPaint = highTempPaint;
        this.mLowTempPaint = lowTempPaint;
        this.mCalendar = calendar;
        this.mIconPaint.setAntiAlias(true);
    }

    public void draw(Canvas canvas, Rect bounds) {
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        Date now = mCalendar.getTime();

        canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);
        drawCenterDivider(canvas, bounds);

        final Resources resources = mContext.getResources();

        final float DIVIDER_MARGIN = resources.getDimension(R.dimen.margin_divider);
        final float TEXT_MARGIN = resources.getDimension(R.dimen.margin_text);
        final float CENTER_Y = bounds.exactCenterY();

        String dateText = SunshineDateFormatUtils.dateStringFrom(now).toUpperCase();
        float dateXOffset = computeXOffset(dateText, mDatePaint, bounds);
        float dateYOffset = CENTER_Y - DIVIDER_MARGIN;
        canvas.drawText(dateText, dateXOffset, dateYOffset, mDatePaint);

        String timeText = SunshineDateFormatUtils.timeStringFrom(now, mShouldShowSeconds);
        float timeHeight = computeTextBounds(timeText, mTimePaint).height();
        float timeXOffset = computeXOffset(timeText, mTimePaint, bounds);
        float timeYOffset = dateYOffset - (timeHeight / 2.0f)
                - resources.getDimension(R.dimen.margin_date_and_time);
        canvas.drawText(timeText, timeXOffset, timeYOffset, mTimePaint);

        String highTempText = SunshineTemperatureUtils.formatTemperature(mContext, 25.0, true);
        Rect highTempBounds = computeTextBounds(highTempText, mHighTempPaint);

        float tempYOffset = CENTER_Y + highTempBounds.height() + DIVIDER_MARGIN;

        float highTempXOffset = computeXOffset(highTempText, mHighTempPaint, bounds);
        canvas.drawText(highTempText, highTempXOffset, tempYOffset, mHighTempPaint);

        String lowTempText = SunshineTemperatureUtils.formatTemperature(mContext, 16.0, true);
        float lowTempXOffset = computeXOffset(lowTempText, mLowTempPaint, bounds) +
                highTempBounds.width() + TEXT_MARGIN;
        canvas.drawText(lowTempText, lowTempXOffset, tempYOffset, mLowTempPaint);

        if (!mIsInAmbientMode) {
            final float iconWidth = resources.getDimension(R.dimen.icon_width);
            Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_clear);
            int left = Math.round(highTempXOffset - (TEXT_MARGIN * 2) - (iconWidth / 2.0f));
            int top = Math.round(tempYOffset - (iconWidth / 2.0f));
            canvas.drawBitmap(bitmap, left, top, new Paint());
        }
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
        float measureText = paint.measureText(text);
        return centerX - (measureText / 2.0f);
    }

    private Rect computeTextBounds(String text, Paint paint) {
        Rect textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        return textBounds;
    }

    public void setTimeZone(TimeZone timeZone) {
        mCalendar.setTimeZone(timeZone);
    }

    public void setAntiAlias(boolean antiAlias) {
        mTimePaint.setAntiAlias(antiAlias);
        mDatePaint.setAntiAlias(antiAlias);
        mCenterDividerPaint.setAntiAlias(antiAlias);
        mHighTempPaint.setAntiAlias(antiAlias);
        mLowTempPaint.setAntiAlias(antiAlias);
        mIconPaint.setAntiAlias(antiAlias);
    }

    public void setShowSeconds(boolean showSeconds) {
        mShouldShowSeconds = showSeconds;
    }

    public void setInAmbientMode(boolean inAmbientMode) {
        mIsInAmbientMode = inAmbientMode;
        if (mIsInAmbientMode) {
            mBackgroundPaint.setColor(Color.BLACK);
            mTimePaint.setColor(Color.WHITE);
            mDatePaint.setColor(Color.WHITE);
            mCenterDividerPaint.setColor(Color.WHITE);
            mHighTempPaint.setColor(Color.WHITE);
            mLowTempPaint.setColor(Color.WHITE);
        } else {
            final int textSecondaryColor = SunshineColorUtils.getPrimaryLightColor(mContext);
            mBackgroundPaint.setColor(SunshineColorUtils.getPrimaryColor(mContext));
            mTimePaint.setColor(TEXT_PRIMARY_COLOR);
            mDatePaint.setColor(textSecondaryColor);
            mCenterDividerPaint.setColor(textSecondaryColor);
            mHighTempPaint.setColor(TEXT_PRIMARY_COLOR);
            mLowTempPaint.setColor(textSecondaryColor);
        }
    }

}
