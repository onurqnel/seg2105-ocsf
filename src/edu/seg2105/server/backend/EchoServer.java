package edu.seg2105.server.backend;

import edu.seg2105.server.ui.ServerConsole;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class EchoServer extends AbstractServer {
    ServerConsole serverConsole;

    public EchoServer(int port) {
        super(port);
    }

    public void setServerConsole(ServerConsole console) {
        this.serverConsole = console;
    }


    @Override
    public void handleMessageFromClient(Object msg, ConnectionToClient client) {
        PrintStream var10000 = System.out;
        String var10001 = String.valueOf(msg);
        var10000.println("Message received: " + var10001 + " from " + client);
        this.sendToAllClients(msg);
    }

    public void handleMessageFromServer(String message) throws IOException {
        try {
            if (message.startsWith("#")) {
                handleCommand(message);
            } else {
                serverConsole.display(message);
            }
        } catch (IOException exception) {
            serverConsole.display("Error sending message: " + exception.getMessage());
            close();
        }
    }

    private void handleCommand(String command) throws IOException {
        Scanner input = new Scanner(command.trim());
        String cmd = input.next();

        if (cmd.equals("#quit")) {
            System.exit(0);

        } else if (cmd.equals("#stop")) {
            stopListening();
            serverConsole.display("Server stopped listening for new connections.");

        } else if (cmd.equals("#close")) {
            close();
            serverConsole.display("Server closed.");

        } else if (cmd.equals("#setport")) {
            if (this.isListening()) {
                serverConsole.display("Server must be closed before changing port.");
                return;
            }
            if (!input.hasNext()) {
                serverConsole.display("Usage: #setport <port>");
                return;
            }
            String portStr = input.next();
            try {
                int port = Integer.parseInt(portStr);
                setPort(port);
                serverConsole.display("Port set to: " + getPort());
            } catch (NumberFormatException e) {
                serverConsole.display("Port must be a number. Usage: #setport <port>");
            }

        } else if (cmd.equals("#start")) {
            if (!isListening()) {
                listen();
            } else {
                serverConsole.display("Server is already listening.");
            }

        } else if (cmd.equals("#getport")) {
            serverConsole.display("Current port: " + getPort());

        } else {
            serverConsole.display("Unknown command: " + command);
        }
    }


    @Override
    protected void clientConnected(ConnectionToClient client) {
        System.out.println("Client connected.");
    }

    @Override
    protected synchronized void clientDisconnected(ConnectionToClient client) {
        System.out.println("Client disconnected.");
    }

    @Override
    protected void serverStarted() {
        System.out.println("Server listening for connections on port " + this.getPort());
    }

    @Override
    protected void serverStopped() {
        System.out.println("Server has stopped listening for connections.");
    }
}