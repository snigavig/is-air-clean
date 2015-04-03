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

import com.goodcodeforfun.isairclean.data.AirContract;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;

import java.util.concurrent.TimeUnit;

public class LocationChangedReceiver extends BroadcastReceiver {

    private static final String[] NOTIFY_LOCATION_PROJECTION = new String[] {
            AirContract.LocationEntry.COLUMN_CITY_NAME,
            AirContract.LocationEntry.COLUMN_INTENSITY_CURRENT,
    };
    private static final int INDEX_CITY_NAME = 0;
    private static final int INDEX_INTENSITY_CURRENT = 1;

    public LocationChangedReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final LocationInfo locationInfo = (LocationInfo) intent.getSerializableExtra(LocationLibraryConstants.LOCATION_BROADCAST_EXTRA_LOCATIONINFO);

        String locationQuery = Util.getPreferredLocation(context);
        String geocodedCityName = Util.getCityName(context, (double) locationInfo.lastLat, (double) locationInfo.lastLong);

        if (geocodedCityName.equals("null") && !geocodedCityName.equals(locationQuery)) {
            String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);
            boolean displayNotifications = prefs.getBoolean(displayNotificationsKey,
                    Boolean.parseBoolean(context.getString(R.string.pref_enable_notifications_default)));
            if (displayNotifications) {
                String lastNotificationKey = context.getString(R.string.pref_last_notification);
                long lastSync = prefs.getLong(lastNotificationKey, 0);

                if (System.currentTimeMillis() - lastSync >= TimeUnit.MINUTES.toMillis(2)) {

                    Uri locationUri = AirContract.LocationEntry.buildLocationBySetting(locationQuery);
                    Cursor cursor = context.getContentResolver().query(locationUri, NOTIFY_LOCATION_PROJECTION, null, null, null);

                    if (cursor.moveToFirst()) {
                        double intensity = cursor.getDouble(INDEX_INTENSITY_CURRENT);
                        String desc = cursor.getString(INDEX_CITY_NAME);
                        String title = context.getString(R.string.app_name);

                        String contentText = String.format(context.getString(R.string.format_notification),
                                desc, intensity);

                        int notifyID = 1;
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(context)
                                        .setSmallIcon(R.drawable.ic_logo)
                                        .setContentTitle(title)
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
                    cursor.close();
                }
            }
        }
    }
}