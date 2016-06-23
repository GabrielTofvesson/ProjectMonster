package com.gabriel.projectmonster;

import org.jsoup.nodes.Document;

public interface INetworkCallback {
    public void onDataReceived(Document data);
}
