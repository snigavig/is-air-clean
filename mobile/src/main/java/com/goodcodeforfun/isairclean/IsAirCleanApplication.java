package com.goodcodeforfun.isairclean;

import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.goodcodeforfun.isairclean.data.AirContract;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 * Created by snigavig on 03.04.15.
 */
public class IsAirCleanApplication extends Application {

    private static final String PREFERENCE_FIRST_RUN = "first_run";

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String locationUpdatesKey = this.getString(R.string.pref_enable_location_updates_key);

        boolean locationUpdates = prefs.getBoolean(locationUpdatesKey,
                Boolean.parseBoolean(this.getString(R.string.pref_enable_location_updates_default)));
        if (locationUpdates) {
            // output debug to LogCat, with tag LittleFluffyLocationLibrary
            //LocationLibrary.showDebugOutput(true);

            try {
                LocationLibrary.initialiseLibrary(getBaseContext(), "com.goodcodeforfun.isairclean");
                //LocationLibrary.initialiseLibrary(getBaseContext(), 60 * 1000, 2 * 60 * 1000, "com.goodcodeforfun.isairclean");
            }
            catch (UnsupportedOperationException ex) {
                Log.d("IsAirClean", "UnsupportedOperationException thrown - the device doesn't have any location providers");
            }
        }

        boolean firstRun = prefs.getBoolean(PREFERENCE_FIRST_RUN, true);
        if (firstRun) {
            prefs.edit().putBoolean(PREFERENCE_FIRST_RUN, false).commit();
            InitCitiesTask task = new InitCitiesTask();
            task.execute();
        }
    }
    private class InitCitiesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            ContentResolver mContentResolver = getContentResolver();

            String mCSVfile = "CARMA-org-city-locations.csv";
            AssetManager manager = getAssets();
            InputStream inStream = null;
            try {
                inStream = manager.open(mCSVfile);
            } catch (IOException e) {
                e.printStackTrace();
            }


            BufferedReader buffer = null;
            if (inStream != null) {
                buffer = new BufferedReader(new InputStreamReader(inStream));
            }
            Vector<ContentValues> cVVector;
            cVVector = new Vector<>();

            String data;
            try {
                if (buffer != null) {
                    while ((data = buffer.readLine()) != null) {
                        ContentValues cityValues = new ContentValues();
                        cityValues.put(AirContract.CityEntry.COLUMN_CITY_NAME, data);
                        cVVector.add(cityValues);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                mContentResolver.bulkInsert(AirContract.CityEntry.CONTENT_URI, cvArray);
            }
        return null;        }
    }
}