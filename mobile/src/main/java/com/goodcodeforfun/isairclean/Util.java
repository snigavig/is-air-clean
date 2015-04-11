package com.goodcodeforfun.isairclean;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by snigavig on 24.03.15.
 */
public class Util {

    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_location_key),
                context.getString(R.string.pref_location_default));
    }

    public static String getPrevPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_prev_location_key),
                context.getString(R.string.pref_prev_location_default));
    }

    public static float getLat(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getFloat(context.getString(R.string.pref_lat_key),
                Float.parseFloat(context.getString(R.string.pref_lat_default)));
    }

    public static float getLon(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getFloat(context.getString(R.string.pref_lon_key),
                Float.parseFloat(context.getString(R.string.pref_lon_default)));
    }

    public static int getPanelHeight(Context context, View view) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        int panelHeight;
        int height = (int) (view.getHeight() / displayMetrics.density);
        panelHeight = (int) (dpHeight - height - (250 / displayMetrics.density));//maaaagic...(
        return panelHeight;
    }

    public static int getOrientation(Context context) {
        return context.getResources().getConfiguration().orientation;
    }

    //from stackoverflow
    public static String getCityName(Context context, double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                result.append(address.getLocality());
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }
        if (result.toString().equals("Kyiv")) {
            return "Kiev";
        }//carma city database problem...need this at least for testing.
        return result.toString();
    }
}