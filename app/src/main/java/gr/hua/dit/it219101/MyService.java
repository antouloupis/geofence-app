package gr.hua.dit.it219101;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class MyService extends Service {
    private LocationManager locationManager;
    private MyLocationListener locationListener;
    private SQLiteDatabase db;
    private ArrayList<LatLng> center; //create arraylist to use in other methods
    public static boolean status;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        status = true;

        // Define dbHelper as a member variable
        DbHelper dbHelper = new DbHelper(MyService.this); // Initialize dbHelper
        center = dbHelper.getCenterTable(); //arraylist contains the center latlng of each user created circle from db
        db = dbHelper.getWritableDatabase();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        startLocationListener(); //doesnt need permission check since its done in MapActivity



        }

    public void startLocationListener() { //check every 5 min if distance is more than 50m
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 50, locationListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //delete last session info and copy the new session over
        status = false;
        db.execSQL("DELETE FROM "+DbHelper.LAST_TOUCH);
        db.execSQL("DELETE FROM "+DbHelper.LAST_CENTER);
        db.execSQL("INSERT INTO "+DbHelper.LAST_TOUCH+" SELECT * FROM "+DbHelper.TOUCH_TABLE);//insert current data to last session tables
        db.execSQL("INSERT INTO "+DbHelper.LAST_CENTER+" SELECT * FROM "+DbHelper.CENTER_TABLE);
        locationManager.removeUpdates(locationListener); //stop receiving updates from lm
    }

    // Tracking part of service
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(@NonNull Location location) { //default method of locationlistener
            compareAgainstCenter(location); //when location is changed according to parameters provided in startLocationListener(), check location against every circle center in db
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {


            stopSelf();
            Log.d("MyService","GPS Signal turned off");
        }

    }

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) { //same as in map activity
        double R = 6371000;
        double f1 = Math.toRadians(lat1);
        double f2 = Math.toRadians(lat2);
        double Df = Math.toRadians(lat2 - lat1);
        double Dl = Math.toRadians(lon2 - lon1);

        double a = Math.sin(Df / 2) * Math.sin(Df / 2) +
                Math.cos(f1) * Math.cos(f2) *
                        Math.sin(Dl / 2) * Math.sin(Dl / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    private void compareAgainstCenter(Location location){
        double d;
        ContentValues values = new ContentValues();
        for (LatLng latlng:center) {
            d = calculateDistance(latlng.latitude, latlng.longitude, location.getLatitude(),location.getLongitude());
            if (d>=90.0 && d<=101.00){ //if user location is near the edge of a marked area, register to db
                values.put(DbHelper.FIELD_LAT, location.getLatitude()); //put lat to values
                values.put(DbHelper.FIELD_LON,location.getLongitude()); //put lng to values
                db.insert(DbHelper.TOUCH_TABLE,null,values);
            }


        }

    }

    public static boolean isRunning(){ //check if service is running
        return status;
    }

}
