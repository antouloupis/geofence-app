package gr.hua.dit.it219101;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.List;


public class MapActivity extends AppCompatActivity {

    public static final int CHECK_FINE_LOCATION_CODE = 26;
    private LocationManager locationManager;
    private myLocationListener locationListener;

    private class myLocationListener implements LocationListener{
        @Override
        public void onLocationChanged(@NonNull Location location) {
            Log.d("Location listener", location.toString());
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

//        BroadcastReceiver receiver = new MyReceiver();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
//        registerReceiver(receiver, filter);

        //Check if permissions have been granted to start tracking
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new myLocationListener();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, CHECK_FINE_LOCATION_CODE);
        } else {
            startLocationListener();
        }

    }

    public void startLocationListener() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 50, locationListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CHECK_FINE_LOCATION_CODE){
        for (int i=0;i<permissions.length;i++) {
            if (permissions[i] == android.Manifest.permission.ACCESS_FINE_LOCATION) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    startLocationListener();
                    }
                }
            }
        }
        }
    }
