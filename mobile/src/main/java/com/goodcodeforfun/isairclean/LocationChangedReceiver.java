package com.goodcodeforfun.isairclean;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.goodcodeforfun.isairclean.data.AirContract;
import com.goodcodeforfun.isairclean.sync.AirSyncAdapter;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;

public class LocationChangedReceiver extends BroadcastReceiver {

    private static final int INDEX_CITY_NAME = 0;

    public LocationChangedReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final LocationInfo locationInfo = (LocationInfo) intent.getSerializableExtra(LocationLibraryConstants.LOCATION_BROADCAST_EXTRA_LOCATIONINFO);
        final String[] CITY_PROJECTION = new String[]{
                AirContract.CityEntry.COLUMN_CITY_NAME
        };
        String location = Util.getPreferredLocation(context);
        String geocodedCityName = Util.getCityName(context, (double) locationInfo.lastLat, (double) locationInfo.lastLong);

        String latKey = context.getString(R.string.pref_lat_key);
        String lonKey = context.getString(R.string.pref_lon_key);

        SharedPreferences.Editor editor = prefs.edit();
        if (!geocodedCityName.equals("null") && !geocodedCityName.equals(location)) {
            Uri cityUri = AirContract.CityEntry.buildCityUri(geocodedCityName);
            Cursor cursor = context.getContentResolver().query(cityUri, CITY_PROJECTION, null, null, null);

            if (cursor.moveToFirst() && !location.equals(geocodedCityName)) {
                editor.putString(context.getString(R.string.pref_prev_location_key), location);
                editor.putString(context.getString(R.string.pref_location_key), geocodedCityName);
                editor.commit();
                editor.putFloat(latKey, locationInfo.lastLat);
                editor.putFloat(lonKey, locationInfo.lastLong);
                editor.apply();
                AirSyncAdapter.syncImmediately(context);
            }
            cursor.close();
        }
    }
}