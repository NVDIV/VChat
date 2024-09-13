package org.example.vchat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class VChatApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Load FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(VChatApplication.class.getResource("chat_layout.fxml"));

        //Set the scene using fxml file
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);

        // Set up stage
        stage.setTitle("VChat");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}