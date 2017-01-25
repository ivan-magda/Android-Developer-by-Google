package com.ivanmagda.wearcounter;

import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

public class CountService extends WearableListenerService {

    private static final String TAG = CountService.class.getSimpleName();
    private static final String COUNT_KEY = "com.ivanmagda.wearcounter.count";

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        super.onDataChanged(dataEventBuffer);
        for (DataEvent dataEvent : dataEventBuffer) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataMap dataMap = DataMapItem.fromDataItem(dataEvent.getDataItem()).getDataMap();
                String path = dataEvent.getDataItem().getUri().getPath();
                if (path.equals("/count")) {
                    int count = dataMap.getInt(COUNT_KEY);
                    Log.d(TAG, "Successfully received count data item: " + count);
                }
            }
        }
    }

}
