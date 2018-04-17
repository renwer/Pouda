package com.renwer.networkelements;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class Connection {
    /** Connected server socket */
    private final Socket socket;
    /** Associated worker tread */
    private final Thread thread;

    /** Input data stream. Automates work with line end. */
    //private final DataInputStream input;
    /** Output data stream. Automates work with line end. */
    //private final DataOutputStream output;
    private final BufferedReader input;
    private final BufferedWriter output;

    /** Interface to listen for events */
    private final ConnectionListener listener;

    /**
     * Creates connection by IP address and port
     * @param listener
     * @param ipAddress
     * @param port
     * @throws IOException
     */
    public Connection(ConnectionListener listener, String ipAddress, int port) throws IOException {
        this(listener, new Socket(ipAddress, port));
    }

    /**
     * Creates connection by existing socket
     * @param listener
     * @param socket
     * @throws IOException
     */
    public Connection(ConnectionListener listener, Socket socket) throws IOException {
        this.socket = socket;
        this.listener = listener;
        //this.input = new DataInputStream(socket.getInputStream());
        //this.output = new DataOutputStream(socket.getOutputStream());
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        this.output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        this.thread = new Thread(new ConnectionThread());
        thread.start();
    }

    /** Implementation of the worker thread */
    class ConnectionThread implements Runnable {
        @Override
        public void run() {
            try {
                listener.onConnectionReady(Connection.this);

                while (!thread.isInterrupted()) {
                    //listener.onStringReceived(Connection.this, input.readUTF());
                    listener.onStringReceived(Connection.this, input.readLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
                listener.onException(Connection.this, e);
            } finally {
                listener.onAbortConnection(Connection.this);
            }
        }
    }

    /**
     * Send string to the current connection
     * @param string
     */
    public synchronized void sendString(String string) {
        try {
            //output.writeUTF(string);
            output.write(string + "\r\n");
            output.flush();
        } catch (IOException e) {
            listener.onException(this, e);
            abortConnection();
        }
    }

    /**
     * Aborts current connection
     */
    public synchronized void abortConnection() {
        thread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            listener.onException(this, e);
        }
    }

    /**
     * Puts connection data to string
     * @return
     */
    @Override
    public String toString () {
        return "Connection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
