package gr.hua.dit.it219101;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button mapButton;
    private Button trackButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //track button setup
        trackButton = findViewById(R.id.trackButton);
        int duration = Toast.LENGTH_LONG;


        //Switch to map activity
        mapButton = findViewById(R.id.mapButton);
        mapButton.setOnClickListener(v-> openMapActivity());




    }

    public void openMapActivity() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }
}