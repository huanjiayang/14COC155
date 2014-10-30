package com.example.huanjiayang.week05extras;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Week05BroadcastReceiver extends BroadcastReceiver {
    public Week05BroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");

        // Create an explicit Intent to open the application itself when
        // the receiver receives an Intent over a power connected action.
        // Because this is not an Activity you cannot get context directly,
        // Therefore the application context required was passed in to
        // onReceive callback as the first parameter, we use that to create
        // the Intent as well as start the Activity.
        // Also see the Receiver registration added in AndroidManifest.xml

        Intent autoStartIntent = new Intent(context, Week04Activity.class);
        // This flag is required to start an Activity from the receiver
        autoStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(autoStartIntent);

        // Test this receive on an AVD by connecting to your AVD in command line
        // through telnet, e.g.: telnet localhost 5554
        // the 4 digit number is the port your AVD is using,
        // you should be able to see this on top of your AVD window.
        // then use:
        // power ac off
        // to simulate power disconnection, and use:
        // power ac on
        // to simulate power connection

    }
}
