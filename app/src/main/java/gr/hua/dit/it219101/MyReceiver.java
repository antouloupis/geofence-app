package gr.hua.dit.it219101;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;

public class MyReceiver extends BroadcastReceiver {
    boolean active;

    @Override
    public void onReceive(Context context, Intent intent) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        active = true;
        Log.d("Receiver","Receiver is active");
        Intent serviceIntent = new Intent(context, MyService.class);
        if (isGpsEnabled) {
            context.startService(serviceIntent);
            Log.d("Receiver","Service starting");
        } else {
            context.stopService(serviceIntent);
            Log.d("Receiver","Service stopping");
        }

        }


}


