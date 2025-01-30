package org.example.foodapp;

import Classes.User;
import DaoClasses.UserDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.example.foodapp.SceneChanger.changeScene;

public class HelloApplication extends Application {
    private static Stage primaryStage;

    // Declare admin credentials as a static Map

    @Override
    public void start(Stage stage) throws IOException {
        // Set the primary stage for global access
        primaryStage = stage;

        // Load the initial scene
        changeScene(stage, "Login.fxml", "Admin Page");
    }

    public static void main(String[] args) {
        launch(args);
    }

}
