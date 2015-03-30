package com.goodcodeforfun.isairclean;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by snigavig on 24.03.15.
 */
public class Util {
    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_location_key),
                context.getString(R.string.pref_location_default));
    }
    public static int getPanelHeight(Context context, View view){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        int panelHeight;
        int height = (int) (view.getBottom() / displayMetrics.density);
        panelHeight = (int) (dpHeight - height - 80);//maaaagic...(
        return panelHeight;
    }
    public static int getOrientation(Context context){
        return context.getResources().getConfiguration().orientation;
    }
}