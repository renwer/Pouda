package com.renwer.server;
import com.renwer.networkelements.Connection;
import com.renwer.networkelements.ConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.StringJoiner;

public class Server implements ConnectionListener {

    private final static String DEFAULT_SERVER_NAME = "Simple Chat Server";
    private String serverName;

    public static void main(String[] args) {
        new Server();
    }

    /** All connections */
    private static ArrayList<Connection> connections = new ArrayList<>();

    private Server(String serverName){
        System.out.println("Server up and running");
        this.serverName = serverName;

        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            while (true) {
                try {
                    Connection connection = new Connection(this, serverSocket.accept());
                    connection.init();
                } catch (IOException e) {
                    System.out.println("IOException caught: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Server() {
        this(DEFAULT_SERVER_NAME);
    }

    @Override
    public synchronized void onConnectionReady(Connection connection) {
        connections.add(connection);
    }

    @Override
    public synchronized void onStringReceived(Connection connection, String string) {
        if(string.startsWith("TYPE:REGISTER")){
            String userName = string.substring(string.indexOf(':', string.indexOf("USERNAME:")) + 1);
            registerNewUser(connection, userName);
        }else {
            sendToAllConnections(connection.getUserName() + ": " + string);
        }
    }

    @Override
    public synchronized void onAbortConnection(Connection connection) {
        connections.remove(connection);
        sendToAllConnections(connection.getUserName() + " has left");
    }

    @Override
    public synchronized void onException(Connection connection, Exception e) {
        connection.abortConnection();
        System.out.println("Exception caught " + e);
    }

    private synchronized void registerNewUser(Connection connection, String userName){
        if (!isUniqueUserName(userName)) {
            userName = generateUniqueUserName(userName);
        }

        connection.setUserName(userName);
        connection.sendString("TYPE:REGISTER SERVERNAME:" + serverName + " USERNAME:" + userName);

        StringJoiner allUsersStringJoiner = new StringJoiner(",");
        for(Connection c : connections){
            allUsersStringJoiner.add(c.getUserName());
        }

        sendToAllConnections("TYPE:USER_LIST USERS:" + allUsersStringJoiner.toString());
        sendToAllConnections(userName + " joined the chat room");
    }

    private synchronized void sendToAllConnections(String message) {
        System.out.println(message);
        for (Connection i : connections) {
            i.sendString(message);
        }
    }

    private boolean isUniqueUserName(String userName){
        for(Connection c : connections){
            if(userName.equals(c.getUserName())){
                return false;
            }
        }
        return true;
    }

    private String generateUniqueUserName(String userName){
        while(!isUniqueUserName(userName)){
            userName += (int)(Math.random() * 100);
        }
        return userName;
    }
}
