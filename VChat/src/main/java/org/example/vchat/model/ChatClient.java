package org.example.vchat.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class ChatClient implements Runnable{
    private static final int PORT = 3000;
    private static final String HOST = "192.168.56.1";
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean running = true;
    private Consumer<String> messageConsumer; // // Function to send messages to the controller

    public ChatClient(Consumer<String> messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    @Override
    public void run() {
        try {
            // Set up the client
            connect();

            // Handle server output
            String inMessage;
            while ((inMessage = in.readLine()) != null) {
                messageConsumer.accept(inMessage); // Send received message to the controller
            }
        } catch (IOException e) {
            shutdown();
        }
    }

    // Method to connect to the server
    public void connect() throws IOException {
        clientSocket = new Socket(HOST, PORT);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        running = true;
        System.out.println("Connected to server at " + HOST + ":" + PORT);
    }

    // Method to send a message to the server
    public void sendMessage(String message) {
        if (out != null && running) {
            out.println(message);
        }
    }

    // Method to disconnect from the server
    public void shutdown() {
        running = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
            System.out.println("Disconnected from the server.");
        } catch (IOException e) {
            System.err.println("Error during shutdown: " + e.getMessage());
        }
    }
}
