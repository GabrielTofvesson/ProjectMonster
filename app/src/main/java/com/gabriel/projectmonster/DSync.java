package com.gabriel.projectmonster;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class DSync extends Service {

    public int cacheTimeoutSeconds = 3600;

    public static final String url = "www.luckyprison.com/";
    public static final String store = "store.luckyprison.com";
    public static final String[] pages = {"?page=%p", "forums/", "members/"}; //Simple reference to pages on site
    public static final String[] memberType = {"", "positive_ratings", "points", "staff"};

    public static final String CACHE_FRONT_PAGE_NOID = "front_page_";

    // Helpful classes
    class DBinder extends Binder { DSync getDataSyncService(){ return DSync.this; } }

    private static boolean running = false;
    DBinder d = new DBinder();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        running = true;
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        running = false;
        super.onDestroy();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return d;
    }
    public static boolean isRunning(){ return running; }

    //TODO: Not this
    public String loadFrontPage() throws IOException { return null; }
}
