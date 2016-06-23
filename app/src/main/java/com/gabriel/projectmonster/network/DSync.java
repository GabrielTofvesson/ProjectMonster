package com.gabriel.projectmonster.network;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.tofvesson.Cacher;

import org.jsoup.Jsoup;

import java.io.IOException;

public class DSync extends Service {

    // Helpful classes
    public class DBinder extends Binder { public DSync getDataSyncService(){ return DSync.this; } }

    // Public values
    public static int cacheTimeoutSeconds = 3600;
    public static final String url = "www.luckyprison.com/";
    public static final String store = "store.luckyprison.com";
    public static final String[] pages = {"?page=%p", "forums/", "members/"}; //Simple reference to pages on site
    public static final String pageID = "%p";
    public static final String[] memberType = {"", "positive_ratings", "points", "staff"};
    public static final String CACHE_FRONT_PAGE_NOID = "front_page_";

    private Cacher cache;

    // Defines if service is running
    private static boolean running = false;

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
        return new DBinder();
    }
    public static boolean isRunning(){ return running; }

    //TODO: Add code to load front page
    public void loadFrontPage(INetworkCallback onLoad, Looper whereToExecute) throws IOException {
        if(cache.containsKey(CACHE_FRONT_PAGE_NOID+"COUNT"))
            if((System.currentTimeMillis()/1000-cache.load(CACHE_FRONT_PAGE_NOID+0).unixTimeCached)<cacheTimeoutSeconds) onLoad.onDataReceived(Jsoup.parse(cache.load(CACHE_FRONT_PAGE_NOID+0).data));
            else cache.remove(CACHE_FRONT_PAGE_NOID+0);
        else{
            NetworkConnection.getDataAsync(url+pages[0].replace(pageID, "0"), d ->
            {
                String s;
                cache.store(CACHE_FRONT_PAGE_NOID+0, d.toString());
                cache.store(CACHE_FRONT_PAGE_NOID, (s = (s = d.getElementsByClass("pageNavHeader").get(0).toString()).substring(s.indexOf(">")+1, s.substring(s.indexOf(">")).indexOf("<")+s.indexOf(">")))
                        .substring(s.lastIndexOf(" ")+1, s.length()) );
                onLoad.onDataReceived(d);
            }, whereToExecute);
        }
    }
    public void loadFrontPage(INetworkCallback onLoad){ try { loadFrontPage(onLoad, Looper.getMainLooper()); } catch (IOException e) { e.printStackTrace(); } }
}
