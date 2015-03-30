package com.goodcodeforfun.isairclean.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class AirContract {

    public static final String CONTENT_AUTHORITY = "com.goodcodeforfun.isairclean";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_OBJECT = "object";
    public static final String PATH_LOCATION = "location";
    public static final String PATH_CITY = "city";

    public static final class CityEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CITY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CITY;

        public static final String TABLE_NAME = "city";

        public static final String COLUMN_CITY_NAME = "city_name";

        public static Uri buildCityUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class LocationEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static final String TABLE_NAME = "location";

        public static final String COLUMN_LOCATION_SETTING = "location_setting";

        public static final String COLUMN_CITY_NAME = "city_name";

        public static final String COLUMN_CARMA_LOCATION_ID = "carma_location_id";

        public static final String COLUMN_CARBON_CURRENT = "carbon_current";

        public static final String COLUMN_CARBON_FUTURE = "carbon_future";

        public static final String COLUMN_ENERGY_CURRENT = "energy_current";

        public static final String COLUMN_ENERGY_FUTURE = "energy_future";

        public static final String COLUMN_INTENSITY_CURRENT = "intensity_current";

        public static final String COLUMN_INTENSITY_FUTURE = "intensity_future";

        public static final String COLUMN_FOSSIL_CURRENT = "fossil_current";

        public static final String COLUMN_FOSSIL_FUTURE = "fossil_future";

        public static final String COLUMN_NUCLEAR_CURRENT = "nuclear_current";

        public static final String COLUMN_NUCLEAR_FUTURE = "nuclear_future";

        public static final String COLUMN_HYDRO_CURRENT = "hydro_current";

        public static final String COLUMN_HYDRO_FUTURE = "hydro_future";

        public static final String COLUMN_RENEWABLE_CURRENT = "renewable_current";

        public static final String COLUMN_RENEWABLE_FUTURE = "renewable_future";

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ObjectEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_OBJECT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_OBJECT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_OBJECT;

        public static final String TABLE_NAME = "object";

        public static final String COLUMN_LOC_KEY = "location_id";

        public static final String COLUMN_NAME = "object_name";

        public static final String COLUMN_COORD_LAT = "coord_lat";

        public static final String COLUMN_COORD_LONG = "coord_long";

        public static final String COLUMN_CARBON_CURRENT = "carbon_current";

        public static final String COLUMN_CARBON_FUTURE  = "carbon_future";

        public static final String COLUMN_ENERGY_CURRENT  = "energy_current";

        public static final String COLUMN_ENERGY_FUTURE  = "energy_future";

        public static final String COLUMN_INTENSITY_CURRENT  = "intensity_current";

        public static final String COLUMN_INTENSITY_FUTURE  = "intensity_future";

        public static Uri buildObjectUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildObjectLocation(String locationSetting) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
        }

        public static Uri buildObjectLocationId(String locationSetting, int id) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).appendPath(String.valueOf(id)).build();
        }

        public static String getLocationSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static int getIdFromUri(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(2));
        }
    }
}