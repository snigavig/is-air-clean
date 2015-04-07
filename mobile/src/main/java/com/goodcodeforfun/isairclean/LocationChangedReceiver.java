package com.goodcodeforfun.isairclean;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.goodcodeforfun.isairclean.data.AirContract;
import com.goodcodeforfun.isairclean.sync.AirSyncAdapter;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;

import java.util.concurrent.TimeUnit;

public class LocationChangedReceiver extends BroadcastReceiver {

    public LocationChangedReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final LocationInfo locationInfo = (LocationInfo) intent.getSerializableExtra(LocationLibraryConstants.LOCATION_BROADCAST_EXTRA_LOCATIONINFO);

        String locationQuery = Util.getPreferredLocation(context);
        String geocodedCityName = Util.getCityName(context, (double) locationInfo.lastLat, (double) locationInfo.lastLong);

        String latKey = context.getString(R.string.pref_lat_key);
        String lonKey = context.getString(R.string.pref_lon_key);

        SharedPreferences.Editor editor = prefs.edit();

        Log.d("LittleFluffy", geocodedCityName);
        if (!geocodedCityName.equals("null") && !geocodedCityName.equals(locationQuery)) {
            editor.putString(context.getString(R.string.pref_location_key), geocodedCityName);
            editor.commit();
            editor.putFloat(latKey, locationInfo.lastLat);
            editor.putFloat(lonKey, locationInfo.lastLong);
            editor.apply();
            AirSyncAdapter.syncImmediately(context);
        }
    }
}