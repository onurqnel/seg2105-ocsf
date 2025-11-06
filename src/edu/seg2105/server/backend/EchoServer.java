package edu.seg2105.server.backend;

import edu.seg2105.server.ui.ServerConsole;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

import java.io.IOException;
import java.util.Scanner;

/**
 * The EchoServer class extends AbstractServer to implement a simple echo server that manages multiple client connections.
 * It supports server commands prefixed with #. Messages sent by clients are broadcast to all connected clients.
 * The server enforces a login protocol requiring clients to issue the command #login upon connection before sending messages.
 *
 * @author Onur Onel
 * oonel101@uottawa.ca
 */
public class EchoServer extends AbstractServer {
    ServerConsole serverConsole;

    /**
     * Constructs an EchoServer instance that listens on the specified port.
     *
     * @param port The port number on which the server will listen for client connections.
     */
    public EchoServer(int port) {

        super(port);
    }

    /**
     * Sets the server console used for displaying messages and server output.
     *
     * @param console The ServerConsole instance associated with this server.
     */
    public void setServerConsole(ServerConsole console) {

        this.serverConsole = console;
    }

    /**
     * Handles messages received from a connected client.
     * This method processes login commands and broadcasts valid messages to all other clients.
     * It also enforces the rule that clients must log in before sending messages.
     *
     * @param msg    The message received from the client.
     * @param client The connection object representing the client.
     */
    @Override
    public void handleMessageFromClient(Object msg, ConnectionToClient client) {
        String s = String.valueOf(msg).trim();
        String loginId = (String) client.getInfo("loginId");

        Scanner scanner = new Scanner(s);
        String command;

        if (scanner.hasNext()) {
            command = scanner.next();
        } else {
            command = "";
        }

        if (loginId == null) {
            if (command.equals("#login") && scanner.hasNext()) {
                String id = scanner.next().trim();
                client.setInfo("loginId", id);
                return;
            }

            try {
                client.sendToClient("You must login first using '#login <loginId>'");
                client.close();
            } catch (IOException e) {
                System.err.println("Connection error: " + e.getMessage());
            }
            return;
        }

        if (command.equals("#login")) {
            try {
                client.sendToClient("#login only allowed at initial connection");
                client.close();
            } catch (IOException e) {
                System.err.println("Connection error: " + e.getMessage());
            }
            return;
        }

        sendToAllClients(loginId + ": " + s);
    }

    /**
     * Handles messages entered by the server via the ServerConsole.
     * Commands prefixed with # are interpreted as server control commands,
     * while all other messages are broadcast to all connected clients.
     *
     * @param message The input message from the server console.
     * @throws IOException If an I/O error occurs while sending messages.
     */
    public void handleMessageFromServer(String message) throws IOException {
        try {
            if (message.startsWith("#")) {
                handleCommand(message);
            } else {
                String msg = "SERVER MSG> " + message;
                serverConsole.display(msg);
                sendToAllClients(msg);
            }
        } catch (IOException exception) {
            serverConsole.display("Error sending message: " + exception.getMessage());
            close();
        }
    }

    /**
     * Processes server commands entered via the console. Supported commands include:
     * - #quit: Terminates the server process.
     * - #stop: Stops listening for new client connections.
     * - #close: Closes all client connections and shuts down the server.
     * - #setport [port]: Sets a new port (server must be closed first).
     * - #start: Starts listening for new connections.
     * - #getport: Displays the current port number.
     *
     * @param command The command string entered by the administrator.
     * @throws IOException If an error occurs while executing the command.
     */
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

    /**
     * Invoked when a client successfully connects to the server.
     *
     * @param client The connection object representing the connected client.
     */
    @Override
    protected void clientConnected(ConnectionToClient client) {
        System.out.println("Client connected.");
    }

    /**
     * Invoked when a client disconnects from the server.
     *
     * @param client The connection object representing the disconnected client.
     */
    @Override
    protected synchronized void clientDisconnected(ConnectionToClient client) {
        System.out.println("Client disconnected.");
    }

    /**
     * Invoked when the server begins listening for client connections.
     */
    @Override
    protected void serverStarted() {
        System.out.println("Server listening for connections on port " + this.getPort());
    }

    /**
     * Invoked when the server stops listening for client connections.
     */
    @Override
    protected void serverStopped() {
        System.out.println("Server has stopped listening for connections.");
    }
}