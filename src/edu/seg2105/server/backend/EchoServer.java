package edu.seg2105.server.backend;

import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

import java.io.PrintStream;

public class EchoServer extends AbstractServer {
    public static final int DEFAULT_PORT = 5555;

    public EchoServer(int port) {
        super(port);
    }

    public static void main(String[] args) {
        int port;
        try {
            port = Integer.parseInt(args[0]);
        } catch (Throwable var5) {
            port = 5555;
        }

        EchoServer sv = new EchoServer(port);

        try {
            sv.listen();
        } catch (Exception var4) {
            System.out.println("ERROR - Could not listen for clients!");
        }

    }

    public void handleMessageFromClient(Object msg, ConnectionToClient client) {
        PrintStream var10000 = System.out;
        String var10001 = String.valueOf(msg);
        var10000.println("Message received: " + var10001 + " from " + client);
        this.sendToAllClients(msg);
    }

    protected void serverStarted() {
        System.out.println("Server listening for connections on port " + this.getPort());
    }

    protected void serverStopped() {
        System.out.println("Server has stopped listening for connections.");
    }
}