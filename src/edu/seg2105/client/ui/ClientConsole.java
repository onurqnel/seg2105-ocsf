package edu.seg2105.client.ui;

import edu.seg2105.client.backend.ChatClient;
import edu.seg2105.client.common.ChatIF;

import java.io.IOException;
import java.util.Scanner;

public class ClientConsole implements ChatIF {
    public static final int DEFAULT_PORT = 5555;
    ChatClient client;
    Scanner fromConsole;

    public ClientConsole(String host, int port) {
        try {
            this.client = new ChatClient(host, port, this);
        } catch (IOException var4) {
            System.out.println("Error: Can't setup connection! Terminating client.");
            System.exit(1);
        }

        this.fromConsole = new Scanner(System.in);
    }

    public static void main(String[] args) {
        String host = "";
        int port = 0;

        try {
            host = args[0];
            port = Integer.parseInt(args[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            host = "localhost";
            port = DEFAULT_PORT;
        } catch (NumberFormatException e) {
            port = DEFAULT_PORT;
        }

        ClientConsole chat = new ClientConsole(host, port);
        chat.accept();
    }

    public void accept() {
        try {
            while (true) {
                String message = this.fromConsole.nextLine();
                this.client.handleMessageFromClientUI(message);
            }
        } catch (Exception var2) {
            System.out.println("Unexpected error while reading from console!");
        }
    }

    public void display(String message) {
        System.out.println("> " + message);
    }
}