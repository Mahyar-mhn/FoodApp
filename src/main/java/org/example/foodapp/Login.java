package org.example.foodapp;

import Classes.User;
import DaoClasses.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Login {

    @FXML
    private Button login_submit_button;

    @FXML
    private Button signup_link_button;

    @FXML
    private TextField pass_textfield;

    @FXML
    private TextField username_txtfield;

    private UserDAO userDAO = new UserDAO(); // DAO instance for database operations

    @FXML
    public void initialize() {
        login_submit_button.setOnAction(event -> handleLogin());
        signup_link_button.setOnAction(event -> navigateToSignup());

    }

    @FXML
    private void navigateToSignup() {
        Stage stage = (Stage) signup_link_button.getScene().getWindow();
        SceneChanger.changeScene(stage, "Signup.fxml", "Signup Page");
    }


    @FXML
    private void handleLogin() {
        String username = username_txtfield.getText();
        String password = pass_textfield.getText();

        // Validate input fields
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Username and password cannot be empty.");
            return;
        }

        try {
            // Authenticate the user using the UserDAO
            User user = userDAO.loginUser(username, password);

            if (user == null) {
                // Invalid credentials
                showAlert("Login Failed", "Invalid username or password.");
            } else {
                // Successful login
                showAlert("Login Successful", "Welcome, " + user.getName() + "!");

                // Get the current stage and navigate to the appropriate page
                Stage stage = (Stage) login_submit_button.getScene().getWindow();
                navigateToHomePage(user, stage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred during login.");
        }
    }

    /**
     * Navigates to the appropriate home page based on the user's role or data.
     *
     * @param user  The authenticated user.
     * @param stage The current stage for scene transition.
     */
    private void navigateToHomePage(User user, Stage stage) {
        // Example: Always navigate to the Admin page (can be extended for roles)
        SceneChanger.changeScene(stage, "CustomerHomePage.fxml", "Admin Page");
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
