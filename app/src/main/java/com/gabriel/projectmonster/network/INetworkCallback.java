package com.gabriel.projectmonster.network;

import org.jsoup.nodes.Document;

public interface INetworkCallback {
    void onDataReceived(Document data);
}
