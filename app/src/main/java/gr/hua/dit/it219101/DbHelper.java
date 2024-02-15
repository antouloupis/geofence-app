package gr.hua.dit.it219101;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class DbHelper  extends SQLiteOpenHelper {

    public static final String DB_NAME = "POINTS_DB";
    public static final int DB_VERSION = 1;
    public static final String CENTER_TABLE = "CENTER";
    public static final String TOUCH_TABLE = "TOUCH";
    public static final String LAST_TOUCH = "LAST_TOUCH";
    public static final String LAST_CENTER = "LAST_CENTER";
    public static final String FIELD_LAT = "LAT";
    public static final String FIELD_LON = "LON";

    public DbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "+CENTER_TABLE+" ("+FIELD_LAT+" double,"+FIELD_LON+" double);");
        sqLiteDatabase.execSQL("CREATE TABLE "+TOUCH_TABLE+" ("+FIELD_LAT+" double,"+FIELD_LON+" double);");
        sqLiteDatabase.execSQL("CREATE TABLE "+LAST_TOUCH+" ("+FIELD_LAT+" double,"+FIELD_LON+" double);");
        sqLiteDatabase.execSQL("CREATE TABLE "+LAST_CENTER+" ("+FIELD_LAT+" double,"+FIELD_LON+" double);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    // Method to retrieve all latitudes and longitudes from the database
    public ArrayList<LatLng> getCenterTable() {
        ArrayList<LatLng> latLngList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + FIELD_LAT + "," + FIELD_LON + " FROM " + CENTER_TABLE;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    double latitude = cursor.getDouble(0);
                    double longitude = cursor.getDouble(1);
                    LatLng latLng = new LatLng(latitude, longitude);
                    latLngList.add(latLng);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return latLngList;
    }

    public ArrayList<LatLng> getLastCenter() {
        ArrayList<LatLng> latLngList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + FIELD_LAT + "," + FIELD_LON + " FROM " + LAST_CENTER;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    double latitude = cursor.getDouble(0);
                    double longitude = cursor.getDouble(1);
                    LatLng latLng = new LatLng(latitude, longitude);
                    latLngList.add(latLng);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return latLngList;
    }

    public ArrayList<LatLng> getLastTouch() {
        ArrayList<LatLng> latLngList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + FIELD_LAT + "," + FIELD_LON + " FROM " + LAST_TOUCH;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    double latitude = cursor.getDouble(0);
                    double longitude = cursor.getDouble(1);
                    LatLng latLng = new LatLng(latitude, longitude);
                    latLngList.add(latLng);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return latLngList;
    }
}
