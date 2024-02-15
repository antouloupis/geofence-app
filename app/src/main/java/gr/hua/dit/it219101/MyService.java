package gr.hua.dit.it219101;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
    private ArrayList<LatLng> center;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        // Define dbHelper as a member variable
        DbHelper dbHelper = new DbHelper(MyService.this); // Initialize dbHelper
        center = dbHelper.getCenterTable();
        db = dbHelper.getWritableDatabase();

        Log.d("Service","Service running");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        startLocationListener(); //doesnt need permission check since its done in MapActivity

        }

    public void startLocationListener() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 25, locationListener);
            Log.d("Service","Made it in startLocation");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //delete last session info and copy the new session over

        db.execSQL("DELETE FROM "+DbHelper.LAST_TOUCH);
        db.execSQL("DELETE FROM "+DbHelper.LAST_CENTER);
        db.execSQL("INSERT INTO "+DbHelper.LAST_TOUCH+" SELECT * FROM "+DbHelper.TOUCH_TABLE);//insert current data to last session tables
        db.execSQL("INSERT INTO "+DbHelper.LAST_CENTER+" SELECT * FROM "+DbHelper.CENTER_TABLE);
        locationManager.removeUpdates(locationListener);
    }

    // Tracking part of service
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            compareAgainstCenter(location);
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {

            stopSelf();
            Log.d("MyService","GPS Signal turned off");
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
            //le
        }

        // Add other LocationListener methods if needed
    }

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000;
        double f1 = Math.toRadians(lat1);
        double f2 = Math.toRadians(lat2);
        double Df = Math.toRadians(lat2 - lat1);
        double Dl = Math.toRadians(lon2 - lon1);

        double a = Math.sin(Df / 2) * Math.sin(Df / 2) +
                Math.cos(f1) * Math.cos(f2) *
                        Math.sin(Dl / 2) * Math.sin(Dl / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // Distance in meters
    }

    private void compareAgainstCenter(Location location){
        double d;
        ContentValues values = new ContentValues();
        for (LatLng latlng:center) {
            d = calculateDistance(latlng.latitude, latlng.longitude, location.getLatitude(),location.getLongitude());
            if (d>=90.0 && d<=101.00){
                values.put(DbHelper.FIELD_LAT, location.getLatitude()); //put lat to values
                values.put(DbHelper.FIELD_LON,location.getLongitude()); //put lng to values
                db.insert(DbHelper.TOUCH_TABLE,null,values);
                Log.d("hello world",location.toString());
            }


        }

    }

}
