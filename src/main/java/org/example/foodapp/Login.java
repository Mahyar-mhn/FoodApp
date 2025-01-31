package org.example.foodapp;

import Classes.User;
import DaoClasses.UserDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Login {

    @FXML
    private Button login_submit_button;

    @FXML
    private Button signup_link_button;

    @FXML
    private TextField pass_textfield;

    @FXML
    private TextField username_txtfield;

    private final UserDAO userDAO = new UserDAO(); // DAO instance for database operations

    @FXML
    public void initialize() {
        // Set up button actions
        login_submit_button.setOnAction(event -> handleLogin());
        signup_link_button.setOnAction(event -> navigateToSignup());
    }

    /**
     * Navigates to the signup page.
     */
    @FXML
    private void navigateToSignup() {
        Stage stage = (Stage) signup_link_button.getScene().getWindow();
        SceneChanger.changeScene(stage, "Signup.fxml", "Signup Page");
    }

    /**
     * Handles the login process by validating credentials and navigating based on user role.
     */
    @FXML
    private void handleLogin() {
        String username = username_txtfield.getText().trim();
        String password = pass_textfield.getText().trim();

        // Validate input fields
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Username and password cannot be empty.");
            return;
        }

        try {
            // Attempt login using UserDAO
            User user = userDAO.loginUser(username, password);

            if (user == null) {
                // Invalid credentials
                showAlert("Login Failed", "Invalid username or password.");
            } else if (user.isDeleted()) {
                // User account is deactivated
                showAlert("Login Failed", "Your account has been deactivated. Please contact support.");
            } else {
                // Successful login - Navigate based on role
                navigateByRole(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred during login. Please try again later.");
        }
    }

    /**
     * Navigates the user to the appropriate page based on their role.
     *
     * @param user The authenticated user object.
     */
    private void navigateByRole(User user) {
        String role = user.getRole();
        int userId = user.getUserId();

        // Debugging: Log role to the console
        System.out.println("Logged-in user's role: " + role);

        Stage stage = (Stage) login_submit_button.getScene().getWindow();

        try {
            FXMLLoader loader;
            if ("Admin".equalsIgnoreCase(role)) {
                showAlert("Login Successful", "Welcome Admin: " + user.getName() + "!");
                loader = new FXMLLoader(getClass().getResource("AdminHomePage.fxml"));
                AnchorPane adminPage = loader.load();

                // Pass the user name to the AdminHomePage controller
                AdminHomePage adminController = loader.getController();
                adminController.loadAdminName(user.getName());

                // Set the scene
                Scene adminScene = new Scene(adminPage);
                stage.setScene(adminScene);
                stage.setTitle("Admin Page");

            } else if ("Manager".equalsIgnoreCase(role)) {
                showAlert("Login Successful", "Welcome Manager: " + user.getName() + "!");
                loader = new FXMLLoader(getClass().getResource("RestaurantManagerHomePage.fxml"));
                AnchorPane managerPage = loader.load();

                // Pass the user name to the RestaurantManagerHomePage controller
                RestaurantManagerHomePage managerController = loader.getController();
                managerController.loadManagerName(user.getName());
                managerController.setManagerId(user.getUserId());

                // Set the scene
                Scene managerScene = new Scene(managerPage);
                stage.setScene(managerScene);
                stage.setTitle("Manager Page");

            } else {
                showAlert("Login Successful", "Welcome, " + user.getName() + "!");
                loader = new FXMLLoader(getClass().getResource("CustomerHomePage.fxml"));
                AnchorPane customerPage = loader.load();

                // Get the controller instance
                CustomerHomePage customerController = loader.getController();
                customerController.setCustomerData(userId, user.getName());

                // Set the scene
                Scene customerScene = new Scene(customerPage);
                stage.setScene(customerScene);
                stage.setTitle("Customer Page");
            }

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Failed to navigate to the appropriate page.");
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
