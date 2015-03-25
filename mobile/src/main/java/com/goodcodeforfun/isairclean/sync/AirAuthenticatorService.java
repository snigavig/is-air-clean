package com.goodcodeforfun.isairclean.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AirAuthenticatorService extends Service {
    private AirAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new AirAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
