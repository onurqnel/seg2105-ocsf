package edu.seg2105.client.backend;

import edu.seg2105.client.common.ChatIF;
import ocsf.client.AbstractClient;

import java.io.IOException;
import java.util.Scanner;

/**
 * The ChatClient class extends AbstractClient to provide the client-side logic of a chat application.
 * It handles communication with the server, interprets user commands, and relays messages to and from the user interface.
 * Clients must log in with a unique login ID upon connecting to the server.
 * The client supports various commands (prefixed with '#') that allow users to manage their connection and settings.
 * Supported commands include:
 * - #quit: Disconnects and exits the program
 * - #logoff: Logs off from the server without exiting
 * - #sethost [host]: Changes the host (only when logged off)
 * - #setport [port]: Changes the port (only when logged off)
 * - #login: Connects to the server
 * - #gethost: Displays the current host
 * - #getport: Displays the current port
 *
 * @author Onur Onel
 * oonel101@uottawa.ca
 */
public class ChatClient extends AbstractClient {

    ChatIF clientUI;
    String loginId;

    /**
     * Constructs a new ChatClient that connects to a specified host and port.
     *
     * @param loginId  The user's login ID.
     * @param host     The hostname or IP address of the chat server.
     * @param port     The port number on which the server is listening.
     * @param clientUI The user interface object implementing {@link ChatIF}.
     * @throws IOException If an I/O error occurs during initialization.
     */
    public ChatClient(String loginId, String host, int port, ChatIF clientUI) throws IOException {
        super(host, port);
        this.loginId = loginId;
        this.clientUI = clientUI;
    }

    /**
     * Handles messages received from the server and displays them on the client UI.
     *
     * @param message The message received from the server.
     */
    public void handleMessageFromServer(Object message) {
        clientUI.display(message.toString());
    }

    /**
     * Handles input messages received from the client user interface.
     * If the message starts with '#', it is treated as a command; otherwise, it is sent to the server.
     *
     * @param message The message input by the user.
     */
    public void handleMessageFromClientUI(String message) {
        try {
            if (message.startsWith("#")) {
                handleCommand(message);
            } else {
                if (!isConnected()) {
                    openConnection();
                }
                sendToServer(message);
            }
        } catch (IOException exception) {
            clientUI.display("Error sending message: " + exception.getMessage());
            quit();
        }
    }

    /**
     * Processes administrative commands entered by the client user.
     *
     * @param command The command string entered by the user.
     * @throws IOException If an I/O error occurs while executing a command.
     */
    private void handleCommand(String command) throws IOException {
        Scanner input = new Scanner(command.trim());
        String cmd = input.next();

        if (cmd.equals("#quit")) {
            quit();
            clientUI.display("Client logged off.");
        } else if (cmd.equals("#logoff")) {
            if (isConnected()) {
                closeConnection();
            } else {
                clientUI.display("Already logged off.");
            }

        } else if (cmd.equals("#sethost")) {
            if (isConnected()) {
                clientUI.display("You must log off before changing host.");
            } else {
                if (!input.hasNext()) {
                    clientUI.display("Usage: #sethost <host>");
                } else {
                    String host = input.next();
                    setHost(host);
                    clientUI.display("Host set to: " + getHost());
                }
            }

        } else if (cmd.equals("#setport")) {
            if (isConnected()) {
                clientUI.display("You must log off before changing port.");
            } else {
                if (!input.hasNext()) {
                    clientUI.display("Usage: #setport <port>");
                } else {
                    String portStr = input.next();
                    try {
                        int port = Integer.parseInt(portStr);
                        setPort(port);
                        clientUI.display("Port set to: " + getPort());
                    } catch (NumberFormatException exception) {
                        clientUI.display("Port must be a number. Usage: #setport <port>");
                    }
                }
            }
        } else if (cmd.equals("#login")) {
            if (isConnected()) {
                clientUI.display("Already connected. Use #logoff to disconnect first.");
            } else {
                try {
                    openConnection();
                    clientUI.display("Connected to server.");
                } catch (IOException exception) {
                    clientUI.display("Failed to connect: " + exception.getMessage());
                }
            }
        } else if (cmd.equals("#gethost")) {
            clientUI.display("Current host: " + getHost());

        } else if (cmd.equals("#getport")) {
            clientUI.display("Current port: " + getPort());

        } else {
            clientUI.display("Unknown command: " + command);
        }
    }

    /**
     * Invoked automatically when a connection to the server is successfully established.
     * Sends the login ID to the server for registration.
     */
    @Override
    protected void connectionEstablished() {
        try {
            sendToServer("#login " + loginId);
        } catch (IOException exception) {
            clientUI.display("Failed to send login id to server.");
            quit();
        }
    }

    /**
     * Invoked when the connection to the server is closed.
     * Displays a notification message on the client UI.
     */
    @Override
    protected void connectionClosed() {
        clientUI.display("Connection closed.");
    }

    /**
     * Invoked when an unexpected connection exception occurs.
     * Displays an error message and terminates the client.
     *
     * @param exception The exception that caused the disconnection.
     */
    @Override
    protected void connectionException(Exception exception) {
        clientUI.display("Server has been shut down.");
        quit();
    }

    /**
     * Terminates the client program gracefully by closing the connection and exiting.
     */
    public void quit() {
        try {
            closeConnection();
        } catch (IOException exception) {
        }
        System.exit(0);
    }
}
