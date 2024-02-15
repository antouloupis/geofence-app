package gr.hua.dit.it219101;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.widget.Button;

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
        last_center = dbHelper.getLastCenter(); //array list for last session circle centers
        last_touch = dbHelper.getLastTouch(); //array list for last session user locations where enter/exit a circle
        db = dbHelper.getWritableDatabase();

        //Return BUTTON SETUP
        Button returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener((v -> goBack()));

        //pause/start BUTTON SETUP
        Button pauseButton = findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener((v -> StartStop(pauseButton)));

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    private void StartStop(Button button) { //stop service if running, start if not, change text for user to know
        if (MyService.isRunning()){
            button.setText("Restart Service");
            stopService(new Intent(this, MyService.class));
        } else {
            startService(new Intent(this, MyService.class));

            button.setText("Stop Service");
        }
    }

    private void goBack() { //finish and return
        finish();
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;


        //default perm check for location manager
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); //MOVE CAMERA TO LAST KNOWN LOCATION
        if (lastKnownLocation != null) {
            LatLng currentLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,16));

        }
        // enable google overlay, create markers and circles
        updateLocationUI();
        createMarkers();
        createCircles();



    }

    private void updateLocationUI() { //display google overlay (bottom left & top right button)
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

    private void createCircles() { //for each latlng in the array list create a new circle and add on map where the center is
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

    private void createMarkers() { //for each latlng in the last_touch arraylist which is same as db, create a marker and add to map
        for (LatLng latLng:last_touch) {
            mMap.addMarker(new MarkerOptions().position(latLng));

        }
    }



}

