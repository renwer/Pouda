package com.renwer.networkElements;

//an interface to listen for events which may occur within connections

public interface ConnectionListener {

    void onConnectionReady(Connection connection);
    void onStringReceived(Connection connection, String string);
    void onAbortConnection(Connection connection);
    void onException(Connection connection, Exception e);
}
