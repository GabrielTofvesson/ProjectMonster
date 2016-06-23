package com.gabriel.projectmonster;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class NetworkConnection {

    private static volatile boolean callbackInMain = true;
    private static volatile Document data;
    private static volatile String page = "";
    private static volatile INetworkCallback callback = null;
    private static Thread t;
    private static final Runnable grabData = new Runnable(){
        @Override
        public void run() {
            // Local variables to make thread functionally compatible with static methods
            boolean call = callbackInMain;
            final INetworkCallback c = callback;
            try { data = Jsoup.connect(page).userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36").get(); }
            catch (IOException e) { e.printStackTrace(); }
            final Document dat = data;
            Runnable r = c!=null ? () -> c.onDataReceived(dat) : null;
            if(c != null)
                if (call) new Handler(Looper.getMainLooper()).post(r);
                else r.run();
        }
    };

    public static  Document getData(String page){
        NetworkConnection.page = page;
        (t = new Thread(grabData)).start();
        try { t.join(); } catch (InterruptedException e) { e.printStackTrace(); }
        return data;
    }

    public static void getDataAsync(@NonNull String page, @Nullable INetworkCallback callback, boolean callbackInMainThread){
        callbackInMain = callbackInMainThread;
        NetworkConnection.page = page;
        NetworkConnection.callback = callback;
        (t = new Thread(grabData)).start();
        NetworkConnection.callback = null;
    }
}
