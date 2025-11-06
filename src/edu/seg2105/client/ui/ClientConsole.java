package edu.seg2105.client.ui;

import edu.seg2105.client.backend.ChatClient;
import edu.seg2105.client.common.ChatIF;

import java.io.IOException;
import java.util.Scanner;

public class ClientConsole implements ChatIF {
    public static final int DEFAULT_PORT = 5555;
    ChatClient client;
    Scanner fromConsole;

    public ClientConsole(int loginId, String host, int port) {
        try {
            this.client = new ChatClient(loginId, host, port, this);
        } catch (IOException exception) {
            System.out.println("Error: Can't setup connection! Terminating client.");
            System.exit(1);
        }
        this.fromConsole = new Scanner(System.in);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("login id must be specified as the first argument.");
            System.exit(1);
        }

        int loginId = 0;
        String host = "localhost";
        int port = DEFAULT_PORT;

        try {
            loginId = Integer.parseInt(args[0]);
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