package edu.seg2105.client.backend;

import edu.seg2105.client.common.ChatIF;
import ocsf.client.AbstractClient;

import java.io.IOException;
import java.util.Scanner;


public class ChatClient extends AbstractClient {
    ChatIF clientUI;

    public ChatClient(String host, int port, ChatIF clientUI) throws IOException {
        super(host, port);
        this.clientUI = clientUI;
        this.openConnection();
    }

    public void handleMessageFromServer(Object message) {
        clientUI.display(message.toString());
    }

    public void handleMessageFromClientUI(String message) {
        try {
            if (message.startsWith("#")) {
                handleCommand(message);
            } else {
                if (!isConnected()) {
                    clientUI.display("Not connected. Use #login first.");
                    return;
                }
                sendToServer(message);
            }
        } catch (IOException exception) {
            clientUI.display("Error sending message: " + exception.getMessage());
            quit();
        }
    }

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
                    } catch (NumberFormatException e) {
                        clientUI.display("Port must be a number. Usage: #setport <port>");
                    }
                }
            }
        } else if (cmd.equals("#login")) {
            if (isConnected()) {
                clientUI.display("Already connected to " + getHost() + " and port: " + getPort());
            } else {
                openConnection();
                clientUI.display("Logged in to " + getHost() + ":" + getPort());
            }

        } else if (cmd.equals("#gethost")) {
            clientUI.display("Current host: " + getHost());

        } else if (cmd.equals("#getport")) {
            clientUI.display("Current port: " + getPort());

        } else {
            clientUI.display("Unknown command: " + command);
        }
    }


    @Override
    protected void connectionClosed() {
        clientUI.display("Connection closed.");
    }

    @Override
    protected void connectionException(Exception exception) {
        clientUI.display("Server has been shut down.");
        quit();
    }

    public void quit() {
        try {
            closeConnection();
        } catch (IOException exception) {
        }
        System.exit(0);
    }
}
