package com.gabriel.projectmonster;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver(){ super(); }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED) || intent.getAction().equalsIgnoreCase(Intent.ACTION_PACKAGE_REPLACED)
                || intent.getAction().equalsIgnoreCase(Intent.ACTION_PACKAGE_ADDED))
            context.startService(new Intent(context, DSync.class));
    }
}