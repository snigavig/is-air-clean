package com.goodcodeforfun.isairclean.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.goodcodeforfun.isairclean.data.AirContract.CityEntry;
import com.goodcodeforfun.isairclean.data.AirContract.LocationEntry;
import com.goodcodeforfun.isairclean.data.AirContract.ObjectEntry;

public class AirDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "air.db";

    public AirDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + LocationEntry.TABLE_NAME + " (" +
                LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                LocationEntry.COLUMN_LOCATION_SETTING + " TEXT UNIQUE NOT NULL, " +
                LocationEntry.COLUMN_CITY_NAME + " TEXT NOT NULL, " +
                LocationEntry.COLUMN_CARMA_LOCATION_ID + " INTEGER NOT NULL, " +
                LocationEntry.COLUMN_CARBON_CURRENT + " REAL NOT NULL, " +
                LocationEntry.COLUMN_CARBON_FUTURE + " REAL NOT NULL, " +
                LocationEntry.COLUMN_ENERGY_CURRENT + " REAL NOT NULL, " +
                LocationEntry.COLUMN_ENERGY_FUTURE + " REAL NOT NULL, " +
                LocationEntry.COLUMN_INTENSITY_CURRENT + " REAL NOT NULL, " +
                LocationEntry.COLUMN_INTENSITY_FUTURE + " REAL NOT NULL, " +
                LocationEntry.COLUMN_FOSSIL_CURRENT + " REAL NOT NULL, " +
                LocationEntry.COLUMN_FOSSIL_FUTURE + " REAL NOT NULL, " +
                LocationEntry.COLUMN_NUCLEAR_CURRENT + " REAL NOT NULL, " +
                LocationEntry.COLUMN_NUCLEAR_FUTURE + " REAL NOT NULL, " +
                LocationEntry.COLUMN_HYDRO_CURRENT + " REAL NOT NULL, " +
                LocationEntry.COLUMN_HYDRO_FUTURE + " REAL NOT NULL, " +
                LocationEntry.COLUMN_RENEWABLE_CURRENT + " REAL NOT NULL, " +
                LocationEntry.COLUMN_RENEWABLE_FUTURE + " REAL NOT NULL " +
                " );";

        final String SQL_CREATE_OBJECT_TABLE = "CREATE TABLE " + ObjectEntry.TABLE_NAME + " (" +
                ObjectEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                ObjectEntry.COLUMN_LOC_KEY + " INTEGER NOT NULL, " +
                ObjectEntry.COLUMN_COORD_LAT + " REAL NOT NULL, " +
                ObjectEntry.COLUMN_COORD_LONG + " REAL NOT NULL, " +
                ObjectEntry.COLUMN_NAME + " TEXT NOT NULL, " +

                ObjectEntry.COLUMN_CARBON_CURRENT + " REAL NOT NULL, " +
                ObjectEntry.COLUMN_CARBON_FUTURE + " REAL NOT NULL, " +

                ObjectEntry.COLUMN_ENERGY_CURRENT + " REAL NOT NULL, " +
                ObjectEntry.COLUMN_ENERGY_FUTURE + " REAL NOT NULL, " +
                ObjectEntry.COLUMN_INTENSITY_CURRENT + " REAL NOT NULL, " +
                ObjectEntry.COLUMN_INTENSITY_FUTURE + " REAL NOT NULL, " +
                " FOREIGN KEY (" + ObjectEntry.COLUMN_LOC_KEY + ") REFERENCES " +
                LocationEntry.TABLE_NAME + " (" + LocationEntry._ID + "), " +

                " UNIQUE (" + ObjectEntry.COLUMN_NAME + ", " +
                ObjectEntry.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_CITY_TABLE = "CREATE TABLE " + CityEntry.TABLE_NAME + " (" +
                CityEntry._ID + " INTEGER PRIMARY KEY," +
                CityEntry.COLUMN_CITY_NAME + " TEXT NOT NULL " +
                " );";

//        FileReader fr = null;
//        try {
//            fr = new FileReader("file:///android_asset/CARMA-org-city-locations.csv");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        BufferedReader br = null;
//        if (fr != null) {
//            br = new BufferedReader(fr);
//        }
//        String data;
//        String InsertString1 = "INSERT INTO " + CityEntry.TABLE_NAME + " (" + CityEntry.COLUMN_CITY_NAME + ") values(";
//        String InsertString2 = ");";
//
//        sqLiteDatabase.beginTransaction();
//        try {
//            if (br != null) {
//                while ((data = br.readLine()) != null) {
//                    sqLiteDatabase.execSQL(InsertString1 + "'" + data + "'" + InsertString2);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        sqLiteDatabase.setTransactionSuccessful();
//        sqLiteDatabase.endTransaction();

        sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_OBJECT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CITY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ObjectEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CityEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}