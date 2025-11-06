package edu.seg2105.server.ui;

import edu.seg2105.client.common.ChatIF;
import edu.seg2105.server.backend.EchoServer;

import java.util.Scanner;

/**
 * The ServerConsole class provides a console-based user interface for the EchoServer.
 * It allows the server administrator to enter commands and messages through the console
 * This class implements the ChatIF interface to display messages received from the server or system.
 *
 * @author Onur Onel
 * oonel101@uottawa.ca
 */
public class ServerConsole implements ChatIF {

    public static final int DEFAULT_PORT = 5555;
    EchoServer server;
    Scanner fromConsole;

    /**
     * Constructs a ServerConsole with a reference to an EchoServer
     *
     * @param server      The server instance to be controlled.
     * @param fromConsole The scanner used to read input from the console.
     */
    public ServerConsole(EchoServer server, Scanner fromConsole) {
        this.server = server;
        this.fromConsole = fromConsole;
        this.server.setServerConsole(this);
    }

    /**
     * The entry point of the server application. Initializes and starts the EchoServer instance.
     *
     * @param args Optional command-line arguments (port number).
     */
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

    /**
     * Continuously listens for input from the console and forwards it to the EchoServer for processing.
     * This includes server commands (prefixed with '#') and regular messages.
     */
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

    /**
     * Displays a message to the console.
     *
     * @param message The message text to display.
     */
    public void display(String message) {
        System.out.println("> " + message);
    }
}