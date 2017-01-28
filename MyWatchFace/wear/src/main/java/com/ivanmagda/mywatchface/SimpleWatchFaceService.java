package com.ivanmagda.mywatchface;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.Log;
import android.view.SurfaceHolder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.TimeUnit;

public final class SimpleWatchFaceService extends CanvasWatchFaceService {

    private static final String TAG = SimpleWatchFaceService.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;

    @Override
    public Engine onCreateEngine() {
        return new SimpleEngine();
    }

    /**
     * The SimpleEngine is an implementation of CanvasWatchFaceService.Engine which actually draws
     * watch face on the canvas and also contains a series of useful callbacks.
     */
    private class SimpleEngine extends CanvasWatchFaceService.Engine
            implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

        private final long TICK_PERIOD_MILLIS = TimeUnit.SECONDS.toMillis(1);

        private SimpleWatchFace mWatchFace;

        /**
         * Handler that will post a runnable only if the watch is visible and not in ambient mode
         * (see startTimerIfNecessary()) in order to start ticking.
         */
        private Handler mTimeTick;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            // Configure the system UI.
            setWatchFaceStyle(new WatchFaceStyle.Builder(SimpleWatchFaceService.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setAmbientPeekMode(WatchFaceStyle.AMBIENT_PEEK_MODE_HIDDEN)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .build()
            );

            mTimeTick = new Handler(Looper.myLooper());
            startTimerIfNecessary();

            mWatchFace = SimpleWatchFace.newInstance(SimpleWatchFaceService.this);

            // Create a GoogleApiClient object and connect when the watch face becomes visible and
            // release the client when the watch face is not visible anymore.
            mGoogleApiClient = new GoogleApiClient.Builder(SimpleWatchFaceService.this)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        private void startTimerIfNecessary() {
            mTimeTick.removeCallbacks(timeRunnable);
            if (isVisible() && !isInAmbientMode()) {
                mTimeTick.post(timeRunnable);
            }
        }

        /**
         * The actual runnable posted by mTimeTick handler.
         * It invalidates the watch and schedules another run of itself on the handler with a delay
         * of one second (since we want to tick every second) if necessary.
         */
        private final Runnable timeRunnable = new Runnable() {
            @Override
            public void run() {
                onSecondTick();

                if (isVisible() && !isInAmbientMode()) {
                    mTimeTick.postDelayed(this, TICK_PERIOD_MILLIS);
                }
            }
        };

        private void onSecondTick() {
            invalidateIfNecessary();
        }

        private void invalidateIfNecessary() {
            if (isVisible() && !isInAmbientMode()) {
                invalidate();
            }
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            super.onDraw(canvas, bounds);
            mWatchFace.draw(canvas, bounds);
        }

        /**
         * This is called when the watch becomes visible or not. If we decide to override this callback.
         *
         * @param visible The watch visibility.
         */
        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                mGoogleApiClient.connect();
            } else {
                releaseGoogleApiClient();
            }

            startTimerIfNecessary();
        }

        /**
         * Called when the device enters or exits ambient mode.
         * <p>
         * While on ambient mode, one should be considerate to preserve battery consumption by
         * providing a black and white display and not provide any animation such as displaying seconds.
         *
         * @param inAmbientMode The watch ambient mode.
         */
        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);

            mWatchFace.setAntiAlias(!inAmbientMode);
            mWatchFace.setShowSeconds(!isInAmbientMode());

            if (inAmbientMode) {
                mWatchFace.updateBackgroundColourToDefault();
                mWatchFace.updateDateAndTimeColourToDefault();
            } else {
                mWatchFace.restoreBackgroundColour();
                mWatchFace.restoreDateAndTimeColour();
            }

            invalidate();
            startTimerIfNecessary();
        }

        /**
         * This callback is invoked every minute when the watch is in ambient mode.
         * It is very important to consider that this callback is only invoked while on ambient mode,
         * as it's name is rather confusing suggesting that this callbacks every time.
         * <p>
         * This being said, usually, here we will have only to invalidate() the watch in order to
         * trigger onDraw(). In order to keep track of time outside ambient mode, we will have to
         * provide our own mechanism.
         */
        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onDestroy() {
            // Remove all the callbacks to the handler in order stop it ticking.
            mTimeTick.removeCallbacks(timeRunnable);
            releaseGoogleApiClient();
            super.onDestroy();
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Log.d(TAG, "connected GoogleAPI");
            Wearable.DataApi.addListener(mGoogleApiClient, mOnDataChangedListener);
            Wearable.DataApi.getDataItems(mGoogleApiClient).setResultCallback(mOnConnectedResultCallback);
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.e(TAG, "suspended GoogleAPI");
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.e(TAG, "connectionFailed GoogleAPI");
        }

        private void releaseGoogleApiClient() {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }

        // Will get notified every time there is a change in the data layer.
        private final DataApi.DataListener mOnDataChangedListener = new DataApi.DataListener() {
            @Override
            public void onDataChanged(DataEventBuffer dataEvents) {
                for (DataEvent event : dataEvents) {
                    if (event.getType() == DataEvent.TYPE_CHANGED) {
                        DataItem item = event.getDataItem();
                        processConfigurationFor(item);
                    }
                }

                dataEvents.release();
                invalidateIfNecessary();
            }
        };

        private void processConfigurationFor(DataItem item) {
            if ("/simple_watch_face_config".equals(item.getUri().getPath())) {
                DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                if (dataMap.containsKey("KEY_BACKGROUND_COLOUR")) {
                    String backgroundColour = dataMap.getString("KEY_BACKGROUND_COLOUR");
                    mWatchFace.updateBackgroundColourTo(Color.parseColor(backgroundColour));
                }

                if (dataMap.containsKey("KEY_DATE_TIME_COLOUR")) {
                    String timeColour = dataMap.getString("KEY_DATE_TIME_COLOUR");
                    mWatchFace.updateDateAndTimeColourTo(Color.parseColor(timeColour));
                }
            }
        }

        // Only notified when the service is firstly connected.
        private final ResultCallback<DataItemBuffer> mOnConnectedResultCallback = new ResultCallback<DataItemBuffer>() {
            @Override
            public void onResult(@NonNull DataItemBuffer dataItems) {
                for (DataItem item : dataItems) {
                    processConfigurationFor(item);
                }

                dataItems.release();
                invalidateIfNecessary();
            }
        };

    }

}
