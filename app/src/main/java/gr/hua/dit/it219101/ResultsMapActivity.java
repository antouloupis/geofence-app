package gr.hua.dit.it219101;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import java.util.ArrayList;


public class ResultsMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private SQLiteDatabase db;
    private ArrayList<LatLng> last_center;
    private ArrayList<LatLng> last_touch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_map);

        DbHelper dbHelper = new DbHelper(getApplicationContext()); // Initialize dbHelper
        last_center = dbHelper.getLastCenter();
        last_touch = dbHelper.getLastTouch();
        db = dbHelper.getWritableDatabase();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); //MOVE CAMERA TO LAST KNOWN LOCATION
        if (lastKnownLocation != null) {
            LatLng currentLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,16));

        }
        // Enable the My Location layer and the related control on the map.
        updateLocationUI();
        createMarkers();
        createCircles();



    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            // Turn on the My Location layer and the related control on the map.
            mMap.setMyLocationEnabled(true);
            //mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } catch (SecurityException e) {
            // Exception if location permissions are not granted.
            e.printStackTrace();
        }
    }

    private void createCircles() {
        for (LatLng latLng:last_center) {
            CircleOptions circleOptions = new CircleOptions()
                    .center(latLng)
                    .radius(100) // In meters
                    .fillColor(0x70ff0000)
                    .strokeColor(Color.BLUE)
                    .strokeWidth(2);

            mMap.addCircle(circleOptions);


        }
    }

    private void createMarkers() {
        for (LatLng latLng:last_touch) {
            mMap.addMarker(new MarkerOptions().position(latLng));

        }
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(@NonNull Location location) {

        }
    }


}

