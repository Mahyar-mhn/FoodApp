package org.example.foodapp;

import Classes.User;
import DaoClasses.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SignUp {

    @FXML
    private TextField name_txtfield;

    @FXML
    private TextField phone_txtfield;

    @FXML
    private PasswordField password_txtfield;

    @FXML
    private PasswordField confirm_password_txtfield;

    @FXML
    private Button signup_button;

    private UserDAO userDAO = new UserDAO(); // DAO for database operations

    @FXML
    public void initialize() {
        signup_button.setOnAction(event -> handleSignup());
    }

    /**
     * Handles the signup process.
     */
    private void handleSignup() {
        String name = name_txtfield.getText();
        String phone = phone_txtfield.getText();
        String password = password_txtfield.getText();
        String confirmPassword = confirm_password_txtfield.getText();

        // Validate input fields
        if (name.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Error", "All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match.");
            return;
        }

        // Create a new user object
        User newUser = new User(0, name, phone, password);

        // Add the user to the database
        boolean success = userDAO.addUser(newUser);

        if (success) {
            showAlert("Success", "User registered successfully.");
            // Navigate to the login page
            Stage stage = (Stage) signup_button.getScene().getWindow();
            SceneChanger.changeScene(stage, "Login.fxml", "Login Page");
        } else {
            showAlert("Error", "Failed to register user. Please try again.");
        }
    }

    /**
     * Displays an alert dialog to the user.
     *
     * @param title   The title of the alert.
     * @param message The message to display.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
