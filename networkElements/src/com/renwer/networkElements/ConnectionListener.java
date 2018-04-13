package com.renwer.networkElements;

import java.net.Socket;

public interface ConnectionListener {
    void onConnectionReady(Connection connection);
    void onStringReceived(Connection connection, String string);
    void onAbortConnection(Connection connection);
    void onException(Connection connection, Exception e);
}
