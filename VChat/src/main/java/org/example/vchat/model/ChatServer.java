package org.example.vchat.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    private static final int PORT = 3000;
    private ServerSocket serverSocket;
    private List<ClientHandler> clients;
    private ExecutorService pool;
    private boolean running;

    public ChatServer() {
        clients = new ArrayList<>();
        pool = Executors.newCachedThreadPool();
    }

    // Method to start the server
    public void start() {
        running = true;
        new Thread(this::run).start();
    }

    // Main server loop
    private void run() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server is up and running on port " + PORT);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                pool.execute(clientHandler);
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            shutdown();
        }
    }

    // Method to stop the server
    public void shutdown() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            for (ClientHandler client : clients) {
                client.shutdown();
            }
            pool.shutdown();
        } catch (IOException e) {
            System.err.println("Error during server shutdown: " + e.getMessage());
        }
    }

    // Inner class for handling individual client connections
    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;
        private String nickname;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Request and set nickname
                out.println("Please enter your nickname: ");
                nickname = in.readLine();
                broadcastMessage(nickname + " joined the chat!");

                // Read messages from client
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("/quit")) {
                        broadcastMessage(nickname + " left the chat.");
                        break;
                    }
                    broadcastMessage(nickname + ": " + message);
                }
            } catch (IOException e) {
                System.err.println("Client error: " + e.getMessage());
            } finally {
                shutdown();
            }
        }

        // Method to broadcast messages to all connected clients
        private void broadcastMessage(String message) {
            for (ClientHandler client : clients) {
                client.out.println(message);
            }
        }

        // Method to clean up client resources
        public void shutdown() {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error during client shutdown: " + e.getMessage());
            }
        }
    }

    // Main method to start the server
    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.start();
    }
}
