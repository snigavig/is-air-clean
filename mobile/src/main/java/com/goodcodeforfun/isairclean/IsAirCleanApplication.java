package com.goodcodeforfun.isairclean;

import android.app.Application;
import android.util.Log;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;

/**
 * Created by snigavig on 03.04.15.
 */
public class IsAirCleanApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // output debug to LogCat, with tag LittleFluffyLocationLibrary
        LocationLibrary.showDebugOutput(true);

        try {
            // LocationLibrary.initialiseLibrary(getBaseContext(), "com.goodcodeforfun.isairclean");
            LocationLibrary.initialiseLibrary(getBaseContext(), 60 * 1000, 2 * 60 * 1000, "com.goodcodeforfun.isairclean");
        }
        catch (UnsupportedOperationException ex) {
            Log.d("IsAirClean", "UnsupportedOperationException thrown - the device doesn't have any location providers");
        }
    }
}