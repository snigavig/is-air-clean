package com.goodcodeforfun.isairclean.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.goodcodeforfun.isairclean.MainActivity;
import com.goodcodeforfun.isairclean.R;
import com.goodcodeforfun.isairclean.Util;
import com.goodcodeforfun.isairclean.data.AirContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class AirSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = AirSyncAdapter.class.getSimpleName();
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 4 * 60 * 180; //12 hours
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private ContentResolver mContentResolver;

    public AirSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        String locationQuery = Util.getPreferredLocation(getContext());
        HttpURLConnection locationUrlConnection = null;
        HttpURLConnection objectsUrlConnection = null;
        BufferedReader locationReader = null;
        BufferedReader objectsReader = null;
        String locationJsonStr;
        String objectsJsonStr;

        //http://carma.org/api/1.1/searchLocations?name=Kiev
        try {
            final String CARMA_BASE_LOCATION_URL =
                    "http://carma.org/api/1.1/searchLocations?";
            final String CARMA_BASE_OBJECTS_URL =
                    "http://carma.org/api/1.1/searchPlants?";
            final String QUERY_PARAM = "name";
            final String LOCATION_QUERY_PARAM = "location";
            final String REGION_TYPE_PARAM = "region_type";
            final String REGION_TYPE_CITY = "7";
            final String QUERY_LIMIT = "limit";
            final String QUERY_LIMIT_VALUE = "1";

            Uri builtLocationUri = Uri.parse(CARMA_BASE_LOCATION_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, locationQuery)
                    .appendQueryParameter(REGION_TYPE_PARAM, REGION_TYPE_CITY)
                    .appendQueryParameter(QUERY_LIMIT, QUERY_LIMIT_VALUE)
                    .build();

            URL locationUrl = new URL(builtLocationUri.toString());

            locationUrlConnection = (HttpURLConnection) locationUrl.openConnection();
            locationUrlConnection.setRequestMethod("GET");
            locationUrlConnection.connect();

            InputStream inputStream = locationUrlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            locationReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = locationReader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            locationJsonStr = buffer.toString();

            int[] locationIds = getLocationDataFromJson(locationJsonStr, locationQuery);
            if (locationIds[1] != 0) {
                Uri builtObjectsUrl = Uri.parse(CARMA_BASE_OBJECTS_URL).buildUpon()
                        .appendQueryParameter(LOCATION_QUERY_PARAM, String.valueOf(locationIds[0]))
                        .build();

                URL objectsUrl = new URL(builtObjectsUrl.toString());


                objectsUrlConnection = (HttpURLConnection) objectsUrl.openConnection();
                objectsUrlConnection.setRequestMethod("GET");
                objectsUrlConnection.connect();

                inputStream = objectsUrlConnection.getInputStream();
                buffer = new StringBuilder();
                objectsReader = new BufferedReader(new InputStreamReader(inputStream));

                while ((line = objectsReader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                objectsJsonStr = buffer.toString();

                getObjectsDataFromJson(objectsJsonStr, locationIds[1]);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (locationUrlConnection != null) {
                locationUrlConnection.disconnect();
            }
            if (locationReader != null) {
                try {
                    locationReader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
            if (objectsUrlConnection != null) {
                objectsUrlConnection.disconnect();
            }
            if (objectsReader != null) {
                try {
                    objectsReader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

    private void getObjectsDataFromJson(String objectsJsonStr, int locationId){
        // Location information
        final String CARMA_OBJECT_NAME = "name";

        final String CARMA_CARBON = "carbon";

        final String CARMA_CARBON_PRESENT = "present";
        final String CARMA_CARBON_FUTURE = "future";

        final String CARMA_ENERGY = "energy";

        final String CARMA_ENERGY_PRESENT = "present";
        final String CARMA_ENERGY_FUTURE = "future";

        final String CARMA_INTENSITY = "intensity";

        final String CARMA_INTENSITY_PRESENT = "present";
        final String CARMA_INTENSITY_FUTURE = "future";

        final String CARMA_LOCATION = "location";

        final String CARMA_LOCATION_LATITUDE = "latitude";
        final String CARMA_LOCATION_LONGTITUDE = "longitude";

        JSONArray objectsArray = null;
        try {
            objectsArray = new JSONArray(objectsJsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Vector<ContentValues> cVVector = null;
        if (objectsArray != null) {
            cVVector = new Vector<>(objectsArray.length());
        }

        if (objectsArray != null) {
            for(int i = 0; i < objectsArray.length(); i++) {
                JSONObject objectJson;
                try {
                    objectJson = objectsArray.getJSONObject(i);
                    String objectName = objectJson.getString(CARMA_OBJECT_NAME);

                    JSONObject carbon = objectJson.getJSONObject(CARMA_CARBON);
                    double carbonPresent = carbon.getDouble(CARMA_CARBON_PRESENT);
                    double carbonFuture = carbon.getDouble(CARMA_CARBON_FUTURE);

                    JSONObject energy = objectJson.getJSONObject(CARMA_ENERGY);
                    double energyPresent = energy.getDouble(CARMA_ENERGY_PRESENT);
                    double energyFuture = energy.getDouble(CARMA_ENERGY_FUTURE);

                    JSONObject intensity = objectJson.getJSONObject(CARMA_INTENSITY);
                    double intensityPresent = intensity.getDouble(CARMA_INTENSITY_PRESENT);
                    double intensityFuture = intensity.getDouble(CARMA_INTENSITY_FUTURE);

                    JSONObject location = objectJson.getJSONObject(CARMA_LOCATION);
                    double locationLatitude = location.getDouble(CARMA_LOCATION_LATITUDE);
                    double locationLongtitude = location.getDouble(CARMA_LOCATION_LONGTITUDE);

                    ContentValues objectValues = new ContentValues();

                    objectValues.put(AirContract.ObjectEntry.COLUMN_LOC_KEY, locationId);
                    objectValues.put(AirContract.ObjectEntry.COLUMN_NAME, objectName);
                    objectValues.put(AirContract.ObjectEntry.COLUMN_CARBON_CURRENT, carbonPresent);
                    objectValues.put(AirContract.ObjectEntry.COLUMN_CARBON_FUTURE, carbonFuture);
                    objectValues.put(AirContract.ObjectEntry.COLUMN_ENERGY_CURRENT, energyPresent);
                    objectValues.put(AirContract.ObjectEntry.COLUMN_ENERGY_FUTURE, energyFuture);
                    objectValues.put(AirContract.ObjectEntry.COLUMN_INTENSITY_CURRENT, intensityPresent);
                    objectValues.put(AirContract.ObjectEntry.COLUMN_INTENSITY_FUTURE, intensityFuture);
                    objectValues.put(AirContract.ObjectEntry.COLUMN_COORD_LAT, locationLatitude);
                    objectValues.put(AirContract.ObjectEntry.COLUMN_COORD_LONG, locationLongtitude);

                    cVVector.add(objectValues);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //int inserted;
        if ( cVVector != null && cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            mContentResolver.bulkInsert(AirContract.ObjectEntry.CONTENT_URI, cvArray);
            //inserted = mContentResolver.bulkInsert(AirContract.ObjectEntry.CONTENT_URI, cvArray);
            //getContext().getContentResolver().delete(AirContract.ObjectEntry.CONTENT_URI,
            //AirContract.ObjectEntry.COLUMN_DATE + " <= ?",
            //new String[] {Long.toString(dayTime.setJulianDay(julianStartDay-1))});
        }

    }

    private int[] getLocationDataFromJson(String locationJsonStr,
                                         String locationSetting)
            throws JSONException {


        int locationId = 0;
        int locationDbId = 0;
        // Location information
        final String CARMA_CITY_NAME = "name";
        final String CARMA_AREA = "province";
        final String CARMA_AREA_ID = "id";

        final String CARMA_CARBON = "carbon";

        final String CARMA_CARBON_PRESENT = "present";
        final String CARMA_CARBON_FUTURE = "future";

        final String CARMA_ENERGY = "energy";

        final String CARMA_ENERGY_PRESENT = "present";
        final String CARMA_ENERGY_FUTURE = "future";

        final String CARMA_INTENSITY = "intensity";

        final String CARMA_INTENSITY_PRESENT = "present";
        final String CARMA_INTENSITY_FUTURE = "future";

        final String CARMA_FOSSIL = "fossil";

        final String CARMA_FOSSIL_PRESENT = "present";
        final String CARMA_FOSSIL_FUTURE = "future";

        final String CARMA_NUCLEAR = "nuclear";

        final String CARMA_NUCLEAR_PRESENT = "present";
        final String CARMA_NUCLEAR_FUTURE = "future";

        final String CARMA_HYDRO = "hydro";

        final String CARMA_HYDRO_PRESENT = "present";
        final String CARMA_HYDRO_FUTURE = "future";

        final String CARMA_RENEWABLE = "renewable";

        final String CARMA_RENEWABLE_PRESENT = "present";
        final String CARMA_RENEWABLE_FUTURE = "future";

        try {
            JSONArray locationArray = new JSONArray(locationJsonStr);
            JSONObject locationJson = locationArray.getJSONObject(0);//limited to one result, so getting the first one


            String cityName = locationJson.getString(CARMA_CITY_NAME);
            
            JSONObject area = locationJson.getJSONObject(CARMA_AREA);
            int areaId = area.getInt(CARMA_AREA_ID);

            JSONObject carbon = locationJson.getJSONObject(CARMA_CARBON);
            double carbonPresent = carbon.getDouble(CARMA_CARBON_PRESENT);
            double carbonFuture = carbon.getDouble(CARMA_CARBON_FUTURE);

            JSONObject energy = locationJson.getJSONObject(CARMA_ENERGY);
            double energyPresent = energy.getDouble(CARMA_ENERGY_PRESENT);
            double energyFuture = energy.getDouble(CARMA_ENERGY_FUTURE);

            JSONObject intensity = locationJson.getJSONObject(CARMA_INTENSITY);
            double intensityPresent = intensity.getDouble(CARMA_INTENSITY_PRESENT);
            double intensityFuture = intensity.getDouble(CARMA_INTENSITY_FUTURE);

            JSONObject fossil = locationJson.getJSONObject(CARMA_FOSSIL);
            double fossilPresent = fossil.getDouble(CARMA_FOSSIL_PRESENT);
            double fossilFuture = fossil.getDouble(CARMA_FOSSIL_FUTURE);

            JSONObject nuclear = locationJson.getJSONObject(CARMA_NUCLEAR);
            double nuclearPresent = nuclear.getDouble(CARMA_NUCLEAR_PRESENT);
            double nuclearFuture = nuclear.getDouble(CARMA_NUCLEAR_FUTURE);

            JSONObject hydro = locationJson.getJSONObject(CARMA_HYDRO);
            double hydroPresent = hydro.getDouble(CARMA_HYDRO_PRESENT);
            double hydroFuture = hydro.getDouble(CARMA_HYDRO_FUTURE);

            JSONObject renewable = locationJson.getJSONObject(CARMA_RENEWABLE);
            double renewablePresent = renewable.getDouble(CARMA_RENEWABLE_PRESENT);
            double renewableFuture = renewable.getDouble(CARMA_RENEWABLE_FUTURE);


            Cursor locationCursor = mContentResolver.query(
                    AirContract.LocationEntry.CONTENT_URI,
                    new String[]{AirContract.LocationEntry._ID},
                    AirContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
                    new String[]{locationSetting},
                    null);

            if (locationCursor.moveToFirst()) {
                int locationIdIndex = locationCursor.getColumnIndex(AirContract.LocationEntry.COLUMN_CARMA_LOCATION_ID);
                if (locationIdIndex != -1) {
                    locationId = locationCursor.getInt(locationIdIndex);
                }

            } else {
                ContentValues locationValues = new ContentValues();

                locationValues.put(AirContract.LocationEntry.COLUMN_CITY_NAME, cityName);
                locationValues.put(AirContract.LocationEntry.COLUMN_LOCATION_SETTING, locationSetting);
                locationValues.put(AirContract.LocationEntry.COLUMN_CARMA_LOCATION_ID, areaId);
                locationValues.put(AirContract.LocationEntry.COLUMN_CARBON_CURRENT, carbonPresent);
                locationValues.put(AirContract.LocationEntry.COLUMN_CARBON_FUTURE, carbonFuture);
                locationValues.put(AirContract.LocationEntry.COLUMN_ENERGY_CURRENT, energyPresent);
                locationValues.put(AirContract.LocationEntry.COLUMN_ENERGY_FUTURE, energyFuture);
                locationValues.put(AirContract.LocationEntry.COLUMN_INTENSITY_CURRENT, intensityPresent);
                locationValues.put(AirContract.LocationEntry.COLUMN_INTENSITY_FUTURE, intensityFuture);
                locationValues.put(AirContract.LocationEntry.COLUMN_FOSSIL_CURRENT, fossilPresent);
                locationValues.put(AirContract.LocationEntry.COLUMN_FOSSIL_FUTURE, fossilFuture);
                locationValues.put(AirContract.LocationEntry.COLUMN_NUCLEAR_CURRENT, nuclearPresent);
                locationValues.put(AirContract.LocationEntry.COLUMN_NUCLEAR_FUTURE, nuclearFuture);
                locationValues.put(AirContract.LocationEntry.COLUMN_HYDRO_CURRENT, hydroPresent);
                locationValues.put(AirContract.LocationEntry.COLUMN_HYDRO_FUTURE, hydroFuture);
                locationValues.put(AirContract.LocationEntry.COLUMN_RENEWABLE_CURRENT, renewablePresent);
                locationValues.put(AirContract.LocationEntry.COLUMN_RENEWABLE_FUTURE, renewableFuture);

                Uri insertedUri = mContentResolver.insert(
                        AirContract.LocationEntry.CONTENT_URI,
                        locationValues
                );

                showNotification(getContext(), cityName, intensityPresent);
                locationId = areaId;
                locationDbId = (int) ContentUris.parseId(insertedUri);
            }

            locationCursor.close();

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return new int[]{locationId, locationDbId};
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        if ( null == accountManager.getPassword(newAccount) ) {

            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        AirSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    public static void showNotification (Context context, String cityName, double intensity) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);
        boolean displayNotifications = prefs.getBoolean(displayNotificationsKey,
                Boolean.parseBoolean(context.getString(R.string.pref_enable_notifications_default)));
        if (displayNotifications) {
            String lastNotificationKey = context.getString(R.string.pref_last_notification);
            long lastSync = prefs.getLong(lastNotificationKey, 0);

            if (System.currentTimeMillis() - lastSync >= TimeUnit.HOURS.toMillis(6)) {
            //if (System.currentTimeMillis() - lastSync >= TimeUnit.MINUTES.toMillis(2)) {
                String title = context.getString(R.string.app_name);

                String contentText = String.format(context.getString(R.string.format_notification),
                        cityName, intensity);

                int notifyID = 1;
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_notification_icon)
                                .setContentTitle(title)
                                .setAutoCancel(true)
                                .setContentText(contentText);

                Intent resultIntent = new Intent(context, MainActivity.class);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(notifyID, mBuilder.build());

                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong(lastNotificationKey, System.currentTimeMillis());
                editor.apply();
            }
        }
    }
}