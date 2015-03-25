package com.goodcodeforfun.isairclean.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AirSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static AirSyncAdapter sAirSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("AirSyncService", "onCreate - AirSyncService");
        synchronized (sSyncAdapterLock) {
            if (sAirSyncAdapter == null) {
                sAirSyncAdapter = new AirSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sAirSyncAdapter.getSyncAdapterBinder();
    }
}