package edu.seg2105.client.backend;

import edu.seg2105.client.common.ChatIF;
import ocsf.client.AbstractClient;

import java.io.IOException;


public class ChatClient extends AbstractClient {
    ChatIF clientUI;

    public ChatClient(String host, int port, ChatIF clientUI) throws IOException {
        super(host, port);
        this.clientUI = clientUI;
        this.openConnection();
    }

    public void handleMessageFromServer(Object msg) {
        this.clientUI.display(msg.toString());
    }

    public void handleMessageFromClientUI(String message) {
        try {
            this.sendToServer(message);
        } catch (IOException var3) {
            this.clientUI.display("Could not send message to server.  Terminating client.");
            this.quit();
        }
    }

    @Override
    protected void connectionClosed() {
        clientUI.display("Connection closed.");
    }

    @Override
    protected void connectionException(Exception exception) {
        clientUI.display("Server has been shut down.");
        this.quit();
    }

    public void quit() {
        try {
            this.closeConnection();
        } catch (IOException var2) {
        }

        System.exit(0);
    }
}
