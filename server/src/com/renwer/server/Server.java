package com.renwer.server;
import com.renwer.networkElements.Connection;
import com.renwer.networkElements.ConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server implements ConnectionListener {


    public static void main(String[] args) {
        new Server();
    }

    private ArrayList<Connection> connections = new ArrayList<>();


    private Server() {
        System.out.println("Server up and running");
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            while (true) {
                try {
                    new Connection(this, serverSocket.accept());
                } catch (IOException e) {
                    System.out.println("IOException caught: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public synchronized void onConnectionReady(Connection connection) {
        connections.add(connection);
        sendToAllConnections(connection + " joined the chat room");
    }

    @Override
    public synchronized void onStringReceived(Connection connection, String string) {
        sendToAllConnections(string);
    }

    @Override
    public synchronized void onAbortConnection(Connection connection) {
        connections.remove(connection);
        sendToAllConnections(connection + " has left");
    }

    @Override
    public synchronized void onException(Connection connection, Exception e) {
        connection.abortConnection();
        System.out.println("Exception caught " + e);
    }


    private synchronized void sendToAllConnections(String message) {
        System.out.println(message);

        for (int i = 0; i < connections.size(); i++) {
            connections.get(i).sendString(message);
        }
    }
}
