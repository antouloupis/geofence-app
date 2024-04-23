package gr.hua.dit.it219101;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;


import java.util.ArrayList;
import java.util.List;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    public static final int CHECK_FINE_LOCATION_CODE = 26;
    private GoogleMap mMap;
    private List<Circle> circleList = new ArrayList<>();
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        DbHelper dbHelper = new DbHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //delete previous table entries. Before these are deleted, when stopService runs, all previous info goes to the last_Session tables
        db.execSQL("DELETE FROM " + DbHelper.CENTER_TABLE);
        db.execSQL("DELETE FROM " + DbHelper.TOUCH_TABLE);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //begin button initialize
        Button beginButton = findViewById(R.id.beginButton);
        beginButton.setOnClickListener(v -> beginTracking(db));

        //begin button initialize
        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> cancelAndReturn());

        // Check if permissions have been granted to start tracking
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // If permissions are not granted, request them
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, CHECK_FINE_LOCATION_CODE);
        } else {

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    private void cancelAndReturn() {
        circleList = null;

        finish(); //end activity go back to main
    }

    private void beginTracking(SQLiteDatabase db) { //adds location variables to db and returns to mainActivity, starts service
        ContentValues values = new ContentValues();
        for (Circle circle : circleList) { //for every circle the user has created
            values.put(DbHelper.FIELD_LAT, circle.getCenter().latitude); //put lat to values
            values.put(DbHelper.FIELD_LON, circle.getCenter().longitude); //put lng to values
            db.insert(DbHelper.CENTER_TABLE, null, values);
        }
        startMyService();
        finish(); //end activity go back to main
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CHECK_FINE_LOCATION_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    // Permissions not granted, show toast
                    Toast.makeText(this, "Permissions not granted!", Toast.LENGTH_LONG).show();
                } else { //start service
                    startMyService();
                }

            }
        }
    }


    public void onMapClick(@NonNull LatLng point) {
        // When user clicks on map, create a circle 100m radius with center on click point
        CircleOptions circleOptions = new CircleOptions()
                .center(point)
                .radius(100)
                .fillColor(0x30ff0000)
                .strokeColor(Color.RED)
                .strokeWidth(2);

        Circle circle = mMap.addCircle(circleOptions); //add circle to map overlay to display
        circleList.add(circle); //add circle to arraylist

    }

    private void startMyService() { //start MyService
        Intent intent = new Intent(this, MyService.class);
        startService(intent);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // google map overlay (top right button)
        updateLocationUI();

        // Set a listener for map click.
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); //MOVE CAMERA TO LAST KNOWN LOCATION
        if (lastKnownLocation != null) {
            LatLng currentLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,16)); //partially from stackoverflow https://stackoverflow.com/questions/20316698/get-google-maps-to-automatically-zoom-in-on-my-current-location

        }

    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            // my location button top right
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            // Exception if location permissions are not granted.
            e.printStackTrace();
        }
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        Circle circleToRemove = null; //circle to be removed if long pressed

        // for every circle in arraylist, check if the user long pressed inside the radius of a circle. If yes, delete the circle whos center is closest to the click
        for (Circle circle : circleList) {
            LatLng circleCenter = circle.getCenter();
            double d = calculateDistance(latLng.latitude,latLng.longitude,circleCenter.latitude,circleCenter.longitude);
            if (d <= 100.00) {
                circleToRemove = circle;
                break; // break when circle is found
            }
        }

        // Remove the circle if found
        if (circleToRemove != null) {
            circleToRemove.remove();
            circleList.remove(circleToRemove);
        }
    }

    //from https://www.movable-type.co.uk/scripts/latlong.html distance
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

        return R * c;
    }
}
