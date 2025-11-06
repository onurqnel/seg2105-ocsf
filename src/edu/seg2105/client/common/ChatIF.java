package edu.seg2105.client.common;

/**
 * This interface serves as a communication bridge between the client-side logic,
 * such as ChatClient and the user interface component ClientConsole.
 * This interface is responsible for presenting messages to the user,
 *
 * @author Onur Onel
 * oonel101@uottawa.ca
 */
public interface ChatIF {
    void display(String var1);
}
