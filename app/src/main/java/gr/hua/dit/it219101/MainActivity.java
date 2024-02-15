package gr.hua.dit.it219101;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DbHelper dbHelper = new DbHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //RESULTS BUTTON SETUP
        Button resultsButton = findViewById(R.id.resultsButton);
        resultsButton.setOnClickListener((v -> openResultsActivity()));

        //Stop service
        Button stopButton = findViewById(R.id.stopButton);
        stopButton.setOnClickListener(v -> stopMyService(db));

        //Switch to map activity
        Button mapButton = findViewById(R.id.mapButton);
        mapButton.setOnClickListener(v-> openMapActivity());

//        BroadcastReceiver receiver = new MyReceiver();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(Intent.ACTION_PROVIDER_CHANGED);
//        registerReceiver(receiver,filter);




    }

    private void openResultsActivity() {
        Intent intent = new Intent(this, ResultsMapActivity.class);
        startActivity(intent);
    }

    private void stopMyService(SQLiteDatabase db) { //try to stop the tracking service
        if (isServiceRunning(MyService.class)) {
            // Stop the service

            db.execSQL("DELETE FROM "+DbHelper.LAST_CENTER);//delete last session info and copy the new session over
            db.execSQL("DELETE FROM "+DbHelper.LAST_TOUCH); //delete last session info and copy the new session over
            db.execSQL("INSERT INTO "+DbHelper.LAST_TOUCH+" SELECT * FROM "+DbHelper.TOUCH_TABLE);//insert current data to last session tables
            db.execSQL("INSERT INTO "+DbHelper.LAST_CENTER+" SELECT * FROM "+DbHelper.CENTER_TABLE);

            stopService(new Intent(this, MyService.class));
            Toast.makeText(this, "MyService stopped", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "MyService is not running", Toast.LENGTH_SHORT).show();
        }
    }

    public void openMapActivity() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}