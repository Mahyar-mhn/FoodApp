package org.example.foodapp;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneChanger {

    /**
     * Changes the current scene to a new scene based on the provided FXML file.
     *
     * @param stage    The current stage where the scene should be displayed.
     * @param fxmlFile The path to the FXML file for the new scene (e.g., "AdminHomePage.fxml").
     * @param title    The title to set for the stage (window).
     */
    public static void changeScene(Stage stage, String fxmlFile, String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SceneChanger.class.getResource(fxmlFile));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load scene", "Error loading the scene: " + fxmlFile);
        }
    }

    /**
     * Displays an error alert dialog to the user.
     *
     * @param header The header text of the error dialog.
     * @param content The detailed error message.
     */
    private static void showError(String header, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
