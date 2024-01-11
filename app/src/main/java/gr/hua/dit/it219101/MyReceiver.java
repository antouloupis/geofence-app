package gr.hua.dit.it219101;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {
    boolean active;
    @Override
    public void onReceive(Context context, Intent intent) {
        active = true;
        Log.d("Broadcast Receiver onReceive", "hello");
        //logs when something happens

    }
}