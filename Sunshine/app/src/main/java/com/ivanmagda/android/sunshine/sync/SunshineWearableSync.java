package com.ivanmagda.android.sunshine.sync;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.ivanmagda.android.sunshine.DataLayerSyncExtras;
import com.ivanmagda.android.sunshine.data.WeatherContract.WeatherEntry;
import com.ivanmagda.android.sunshine.utilities.SunshineWeatherUtils;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;

public final class SunshineWearableSync {

    private static final String TAG = SunshineWearableSync.class.getSimpleName();

    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private ContentValues mContentValues;

    public synchronized static void syncDataLayer(Context context, ContentValues contentValues) {
        SunshineWearableSync wearableSync = new SunshineWearableSync(context, contentValues);
        wearableSync.doJob();
    }

    private SunshineWearableSync(Context context, ContentValues contentValues) {
        this.mContext = context;
        this.mContentValues = contentValues;
        this.mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
    }

    private void doJob() {
        ConnectionResult connectionResult = mGoogleApiClient.blockingConnect(30, TimeUnit.SECONDS);

        if (!connectionResult.isSuccess()) {
            Log.e(TAG, "Failed to connect to GoogleApiClient.");
            doJob();
        }

        processOnValues();
    }

    private void processOnValues() {
        double high = mContentValues.getAsDouble(WeatherEntry.COLUMN_MAX_TEMP);
        double low = mContentValues.getAsDouble(WeatherEntry.COLUMN_MIN_TEMP);

        int weatherId = mContentValues.getAsInteger(WeatherEntry.COLUMN_WEATHER_ID);
        int weatherImageId = SunshineWeatherUtils.getSmallArtResourceIdForWeatherCondition(weatherId);
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), weatherImageId);
        Asset asset = createAssetFromBitmap(bitmap);

        PutDataMapRequest putDataMapReq = PutDataMapRequest.create(DataLayerSyncExtras.FORECAST_SYNC_PATH);
        DataMap dataMap = putDataMapReq.getDataMap();

        dataMap.putDouble(DataLayerSyncExtras.HIGH_TEMP_SYNC_KEY, high);
        dataMap.putDouble(DataLayerSyncExtras.LOW_TEMP_SYNC_KEY, low);
        dataMap.putAsset(DataLayerSyncExtras.ICON_ASSET_SYNC_KEY, asset);

        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq).await();
    }

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }

}
