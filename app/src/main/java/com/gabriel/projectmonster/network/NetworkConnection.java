package com.gabriel.projectmonster.network;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class NetworkConnection {

    static volatile Looper whereToRun;
    static volatile Document data;
    static volatile String page = "";
    static volatile INetworkCallback callback = null;
    static Thread t;
    static final Runnable grabData = new Runnable(){
        @Override
        public void run() {
            Looper runIn = whereToRun;
            final INetworkCallback c = callback;
            try { data = Jsoup.connect(page).get(); }
            catch (IOException e) { e.printStackTrace(); }
            final Document dat = data;
            Runnable r = c!=null ? () -> c.onDataReceived(dat) : null;
            if(c != null) new Handler(runIn).post(r);
        }
    };

    public static Document getData(String page){
        NetworkConnection.callback = null;
        NetworkConnection.page = page;
        (t = new Thread(grabData)).start();
        try { t.join(); } catch (InterruptedException e) { e.printStackTrace(); }
        return data;
    }

    public static void getDataAsync(@NonNull String page, @Nullable INetworkCallback callback, @NonNull Looper runIn){
        runIn = whereToRun;
        NetworkConnection.page = page;
        NetworkConnection.callback = callback;
        (t = new Thread(grabData)).start();
    }
}
