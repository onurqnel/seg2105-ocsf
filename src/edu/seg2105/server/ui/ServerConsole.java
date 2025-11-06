package edu.seg2105.server.ui;

import edu.seg2105.client.common.ChatIF;
import edu.seg2105.server.backend.EchoServer;

import java.util.Scanner;

public class ServerConsole implements ChatIF {

    public static final int DEFAULT_PORT = 5555;
    EchoServer server;
    Scanner fromConsole;

    public ServerConsole(EchoServer server, Scanner fromConsole) {
        this.server = server;
        this.fromConsole = fromConsole;
        this.server.setServerConsole(this);
    }

    public static void main(String[] args) {
        int port;
        try {
            port = Integer.parseInt(args[0]);
        } catch (ArrayIndexOutOfBoundsException exception) {
            port = DEFAULT_PORT;
        }
        EchoServer server = new EchoServer(port);
        try {
            server.listen();
        } catch (Exception exception) {
            System.out.println("ERROR - Could not listen for clients!");
        }
        ServerConsole console = new ServerConsole(server, new Scanner(System.in));
        server.setServerConsole(console);
        console.accept();
    }

    public void accept() {
        try {
            while (true) {
                String message = this.fromConsole.nextLine();
                this.server.handleMessageFromServer(message);
            }
        } catch (Exception exception) {
            System.out.println("Unexpected error while reading from console!");
        }
    }

    public void display(String message) {
        System.out.println("> " + message);
    }
}