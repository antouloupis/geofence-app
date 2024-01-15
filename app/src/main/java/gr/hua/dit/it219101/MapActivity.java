package gr.hua.dit.it219101;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;


public class MapActivity extends AppCompatActivity {

    public static final int CHECK_FINE_LOCATION_CODE = 26;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Check if permissions have been granted to start tracking
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // If permissions are not granted, request them
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, CHECK_FINE_LOCATION_CODE);
        } else {
            // If permissions are already granted, start the service
            startService(new Intent(this, MyService.class));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop the service
        stopService(new Intent(this, MyService.class));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CHECK_FINE_LOCATION_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    // If location permission is granted, start the service
                    startService(new Intent(this, MyService.class));
                }

            }
        }
    }
}
