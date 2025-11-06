package edu.seg2105.client.ui;

import edu.seg2105.client.backend.ChatClient;
import edu.seg2105.client.common.ChatIF;

import java.io.IOException;
import java.util.Scanner;

/**
 * The class provides a simple text-based client interface for communicating with a chat server.
 * It connects to the server via a ChatClient instance and allows the user to send and receive messages through the console.
 * This class implements the ChatIF interface to display messages received from the server and handle user input in real-time.
 *
 * @author Onur Onel
 * oonel101@uottawa.ca
 */
public class ClientConsole implements ChatIF {
    public static final int DEFAULT_PORT = 5555;
    ChatClient client;
    Scanner fromConsole;

    /**
     * Constructs a ClientConsole object and attempts to establish a connection
     * to the specified chat server using the given login ID, host, and port number.
     *
     * @param loginId The user's login ID for identification with the server.
     * @param host The server host name or IP address.
     * @param port The port number on which the server is listening.
     */
    public ClientConsole(String loginId, String host, int port) {
        try {
            this.client = new ChatClient(loginId, host, port, this);
        } catch (IOException exception) {
            System.out.println("Can't setup connection! Terminating client.");
            System.exit(1);
        }
        this.fromConsole = new Scanner(System.in);
    }

    /**
     * The entry point for the chat client application.
     * Expects at least one argument for the login ID, followed optionally by
     * the host and port number. If not provided, default values are used.
     *
     * @param args Command-line arguments: [loginId] [host] [port].
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("login id must be specified as the first argument.");
            System.exit(1);
        }

        String loginId = "";
        String host = "localhost";
        int port = DEFAULT_PORT;

        try {
            loginId = args[0];
        } catch (NumberFormatException e) {
            System.out.println("Login id must be a valid integer.");
            System.exit(1);
            return;
        }
        if (args.length >= 2) {
            host = args[1];
        }

        if (args.length >= 3) {
            try {
                port = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number. Using default port " + DEFAULT_PORT + ".");
                port = DEFAULT_PORT;
            }
        }
        ClientConsole chat = new ClientConsole(loginId, host, port);
        chat.accept();
    }

    /**
     * Starts an infinite loop that continuously reads user input from the console
     * and forwards it to the ChatClient for transmission to the server.
     * This method blocks indefinitely until the user terminates the program.
     */
    public void accept() {
        try {
            while (true) {
                String message = this.fromConsole.nextLine();
                this.client.handleMessageFromClientUI(message);
            }
        } catch (Exception exception) {
            System.out.println("Unexpected error while reading from console!");
        }
    }

    public void display(String message) {
        System.out.println("> " + message);
    }
}