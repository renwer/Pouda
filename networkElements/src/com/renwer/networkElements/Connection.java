package com.renwer.networkElements;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class Connection {

    private final Socket socket;
    private final Thread receivingThread;
    private final ConnectionListener listener;

    private final BufferedReader input;
    private final BufferedWriter output;


    public Connection(ConnectionListener listener, String ipAddress, int port) throws IOException {
        this(listener, new Socket(ipAddress, port));
    }

    public Connection(ConnectionListener listener, Socket socket) throws IOException {
        this.socket = socket;
        this.listener = listener;
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        this.output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        this.receivingThread = new Thread(new ListenerThread());
        receivingThread.run();
    }

    class ListenerThread implements Runnable {
        @Override
        public void run() {
            try {
                listener.onConnectionReady(Connection.this);

                while (!receivingThread.isInterrupted()) {
                    String msg = input.readLine();
                    listener.onStringReceived(Connection.this, msg);
                }
            } catch (IOException e) {
                    listener.onException(Connection.this, e);
            } finally {
                listener.onAbortConnection(Connection.this);
            }
        }
    }

    public synchronized void sendString(String message) {
        try {
            output.write(message + "\r\n");
            output.flush();
        } catch (IOException e) {
            listener.onException(this, e);
            abortConnection();
        }
    }

    public synchronized void abortConnection() {
        receivingThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            listener.onException(this, e);
        }
    }

    @Override
        public String toString () {
            return "Connection: " + socket.getInetAddress() + ": " + socket.getPort();
        }
}
