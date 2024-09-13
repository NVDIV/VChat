package org.example.vchat.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.vchat.model.ChatClient;

public class VChatController {
    @FXML
    private TextArea chatArea;

    @FXML
    private TextField messageField;

    @FXML
    private ListView<String> onlineUsersList;

    @FXML
    private Button sendButton;

    private ChatClient chatClient;

    @FXML
    private void initialize() {
        // Initialize the chat client and define the behavior for incoming messages
        chatClient = new ChatClient(this::updateChatArea);

        // Start Client in a new Thread
        Thread clientThread = new Thread(chatClient);
        clientThread.start();

        // Add action listener to the send button
        sendButton.setOnAction(event -> sendMessage());

        // Handle Enter key press in the message field
        messageField.setOnAction(event -> sendMessage());
    }

    // Method to send a message to the server
    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            chatClient.sendMessage(message);
            messageField.clear();
        }
    }

    private void updateChatArea(String s) {
        chatArea.appendText(s + "\n");
    }

    // Cleanup when the application is closed
    public void shutdown() {
        chatClient.shutdown();
    }
}