package com.goodcodeforfun.isairclean;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by snigavig on 24.03.15.
 */
public class Util {

    private static float TOOLBAR_HEIGHT = 56f;
    private static float INDICATOR_HEIGHT = 35f;

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

    public static void setPanelHeight(final Context context, final View view, final SlidingUpPanelLayout panel) {
        final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);


        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                int panelHeight = displayMetrics.heightPixels - view.getHeight() - (int) toPx(context, TOOLBAR_HEIGHT) - (int) toPx(context, INDICATOR_HEIGHT) - getStatusBarHeight(context);
                panel.setPanelHeight(panelHeight);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    public static float toPx(Context context, float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public static int toDp(Context context, int pixels) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = pixels / (metrics.densityDpi / 160f);
        return (int) dp;
    }

    public static int getOrientation(Context context) {
        return context.getResources().getConfiguration().orientation;
    }

    //from stackoverflow

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

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