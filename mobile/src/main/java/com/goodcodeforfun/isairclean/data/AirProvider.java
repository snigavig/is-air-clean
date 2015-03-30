package com.goodcodeforfun.isairclean.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class AirProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private AirDbHelper mOpenHelper;

    static final int OBJECTS = 100;
    static final int OBJECTS_WITH_LOCATION = 101;
    static final int OBJECTS_WITH_LOCATION_AND_ID = 102;
    static final int LOCATION = 300;
    static final int LOCATION_BY_SETTING = 301;

    private static final SQLiteQueryBuilder sObjectsByLocationSettingQueryBuilder;
    private static final SQLiteQueryBuilder sObjectsByLocationSettingAndIdQueryBuilder;
    private static final SQLiteQueryBuilder sLocationByLocationSettingQueryBuilder;

    static{
        sObjectsByLocationSettingQueryBuilder = new SQLiteQueryBuilder();
        sObjectsByLocationSettingQueryBuilder.setTables(
                AirContract.ObjectEntry.TABLE_NAME + " INNER JOIN " +
                        AirContract.LocationEntry.TABLE_NAME +
                        " ON " + AirContract.ObjectEntry.TABLE_NAME +
                        "." + AirContract.ObjectEntry.COLUMN_LOC_KEY +
                        " = " + AirContract.LocationEntry.TABLE_NAME +
                        "." + AirContract.LocationEntry._ID);

        sObjectsByLocationSettingAndIdQueryBuilder = new SQLiteQueryBuilder();
        sObjectsByLocationSettingAndIdQueryBuilder.setTables(
                AirContract.ObjectEntry.TABLE_NAME);

        sLocationByLocationSettingQueryBuilder = new SQLiteQueryBuilder();
        sLocationByLocationSettingQueryBuilder.setTables(
                AirContract.LocationEntry.TABLE_NAME);
    }

    private static final String sIdSelection =
            AirContract.LocationEntry.TABLE_NAME +
                    "." + AirContract.LocationEntry._ID + " = ? ";

    private static final String sLocationSettingSelection =
            AirContract.LocationEntry.TABLE_NAME+
                    "." + AirContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? ";

    private static final String sLocationSettingAndIdSelection =
                    AirContract.ObjectEntry.TABLE_NAME +
                    "." + AirContract.ObjectEntry._ID + " = ? ";

    private Cursor getObjectsByLocationSetting(Uri uri, String[] projection, String sortOrder) {
        String locationSetting = AirContract.ObjectEntry.getLocationSettingFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = sLocationSettingSelection;
        selectionArgs = new String[]{locationSetting};



        return sObjectsByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getObjectsByLocationSettingAndId(
            Uri uri, String[] projection, String sortOrder) {
        String locationSetting = AirContract.ObjectEntry.getLocationSettingFromUri(uri);
        int id = AirContract.ObjectEntry.getIdFromUri(uri);

        return sObjectsByLocationSettingAndIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sLocationSettingAndIdSelection,
                new String[]{Integer.toString(id)},
                null,
                null,
                null
        );
    }

    private Cursor getLocationByLocationSetting(
            Uri uri, String[] projection, String sortOrder) {
        String locationSetting = AirContract.ObjectEntry.getLocationSettingFromUri(uri);

        return sLocationByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sLocationSettingSelection,
                new String[]{locationSetting},
                null,
                null,
                null
        );
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AirContract.CONTENT_AUTHORITY;


        matcher.addURI(authority, AirContract.PATH_OBJECT, OBJECTS);

        matcher.addURI(authority, AirContract.PATH_OBJECT + "/*", OBJECTS_WITH_LOCATION);

        matcher.addURI(authority, AirContract.PATH_OBJECT + "/*/*", OBJECTS_WITH_LOCATION_AND_ID);

        matcher.addURI(authority, AirContract.PATH_LOCATION, LOCATION);

        matcher.addURI(authority, AirContract.PATH_LOCATION + "/*", LOCATION_BY_SETTING);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new AirDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case OBJECTS_WITH_LOCATION:
                return AirContract.ObjectEntry.CONTENT_TYPE;
            case OBJECTS_WITH_LOCATION_AND_ID:
                return AirContract.ObjectEntry.CONTENT_ITEM_TYPE;
            case OBJECTS:
                return AirContract.ObjectEntry.CONTENT_ITEM_TYPE;
            case LOCATION:
                return AirContract.LocationEntry.CONTENT_TYPE;
            case LOCATION_BY_SETTING:
                return AirContract.LocationEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {
            // "objects/*"
            case OBJECTS_WITH_LOCATION: {
                retCursor = getObjectsByLocationSetting(uri, projection, sortOrder);
                break;
            }
            // "objects/*/*"
            case OBJECTS_WITH_LOCATION_AND_ID: {
                retCursor = getObjectsByLocationSettingAndId(uri, projection, sortOrder);
                break;
            }
            // "objects"
            case OBJECTS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        AirContract.ObjectEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "location/*"
            case LOCATION_BY_SETTING: {
                retCursor = getLocationByLocationSetting(uri, projection, sortOrder);
                break;
            }
            // "location"
            case LOCATION: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        AirContract.LocationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case OBJECTS: {
                long _id = db.insert(AirContract.ObjectEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = AirContract.ObjectEntry.buildObjectUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case LOCATION: {
                long _id = db.insert(AirContract.LocationEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = AirContract.LocationEntry.buildLocationUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case OBJECTS:
                rowsDeleted = db.delete(
                        AirContract.ObjectEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case LOCATION:
                rowsDeleted = db.delete(
                        AirContract.LocationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case OBJECTS:
                rowsUpdated = db.update(AirContract.ObjectEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case LOCATION:
                rowsUpdated = db.update(AirContract.LocationEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case OBJECTS:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(AirContract.ObjectEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}