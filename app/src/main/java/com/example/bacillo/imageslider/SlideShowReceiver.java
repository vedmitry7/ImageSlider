package com.example.bacillo.imageslider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SlideShowReceiver extends BroadcastReceiver {
    public SlideShowReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        switch (intent.getAction()){
            case "android.intent.action.ACTION_POWER_CONNECTED":
                Intent i1 = new Intent(context,MainActivity.class);
                i1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i1.setAction("Power Connected");
                context.startActivity(i1);
                break;
            case "android.intent.action.BOOT_COMPLETED":
                Intent i2 = new Intent(context,MainActivity.class);
                i2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i2.setAction("Boot Completed");
                context.startActivity(i2);
                break;
            case MainActivity.ACTION_ALARM:
                Intent i3 = new Intent(context,MainActivity.class);
                int hour = intent.getIntExtra(MainActivity.HOURS, 0);
                int minutes = intent.getIntExtra(MainActivity.MINUTES, 0);
                i3.putExtra(MainActivity.HOURS, hour);
                i3.putExtra(MainActivity.MINUTES, minutes);
                i3.setAction(MainActivity.SET_FINISH);
                i3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i3);
                break;
        }
    }
}
