package org.example.foodapp;

import Classes.Restaurant;
import Classes.User;
import DaoClasses.RestaurantDAO;
import DaoClasses.UserDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class AdminHomePage {

    @FXML
    private Label admin_name;

    @FXML
    private Button edit_submission_button;

    @FXML
    private TextField edit_user_name_txtfield;

    @FXML
    private TextField edit_user_phone_txtfield;

    @FXML
    private ChoiceBox<String> edit_user_role_choicebox;

    @FXML
    private AnchorPane edit_user_scroll_pane;

    @FXML
    private VBox edit_user_scroll_vbox;

    @FXML
    private AnchorPane main_pane_adminPage;

    @FXML
    private Button manage_restaurant_button;

    @FXML
    private Button manage_user_button;

    @FXML
    private AnchorPane ordering_pane_admin;

    @FXML
    private AnchorPane profile_pane_admin;

    @FXML
    private AnchorPane restaurant_manage_pane;

    @FXML
    private AnchorPane scroll_pane_admin_homePage;

    @FXML
    private AnchorPane user_manage_pane;

    @FXML
    private VBox vbox_scroll_pane_admin_homePage;


    @FXML
    private AnchorPane remove_user_scrolpane;

    @FXML
    private VBox remove_user_vbox;

    @FXML
    private Button remove_user_button;

    @FXML
    private Button add_user_button;

    @FXML
    private TextField add_user_name_txtfield;

    @FXML
    private TextField add_user_phone_txtfield;

    @FXML
    private TextField add_user_password_txtfield;

    @FXML
    private ChoiceBox<String> add_user_role_choicebox;

    private UserDAO userDAO = new UserDAO(); // DAO for database operations
    private int selectedUserId = -1; // Track selected user ID for editing
    private int selectedUserIdToRemove = -1; // Track the user ID for removal


    @FXML
    public void initialize() {
        // Set the admin name dynamically
        admin_name.setText("John Doe"); // Replace with actual admin name from the backend

        // Load the initial content (profile view)
        showProfilePane();

        // Populate the role ChoiceBox with values
        populateRoleChoiceBox(edit_user_role_choicebox);
        populateRoleChoiceBox(add_user_role_choicebox);




        // Set up event handlers for buttons
        manage_restaurant_button.setOnAction(event -> toggleManageRestaurants());
        manage_user_button.setOnAction(event -> toggleManageUsers());
        remove_user_button.setOnAction(event -> handleRemoveUser());

    }

    private void loadUsersIntoRemoveScrollPane() {
        List<User> users = userDAO.getAllUsers();
        remove_user_vbox.getChildren().clear();

        for (User user : users) {
            if (!user.isDeleted()) { // Only show users who are not soft-deleted
                Label userLabel = new Label(user.getName() + " (" + user.getPhone() + ") - Role: " + user.getRole());
                userLabel.setStyle("-fx-font-size: 14px; -fx-padding: 5; -fx-background-color: transparent; -fx-cursor: hand;");

                userLabel.setOnMouseEntered(event -> userLabel.setStyle("-fx-font-size: 14px; -fx-padding: 5; -fx-background-color: #c40000;"));
                userLabel.setOnMouseExited(event -> userLabel.setStyle("-fx-font-size: 14px; -fx-padding: 5; -fx-background-color: transparent;"));

                userLabel.setOnMouseClicked(event -> {
                    selectedUserIdToRemove = user.getUserId();
                    highlightSelectedUser(userLabel);
                });

                remove_user_vbox.getChildren().add(userLabel);
            }
        }
    }

    /**
     * Handles the removal of the selected user.
     */
    private void handleRemoveUser() {
        if (selectedUserIdToRemove == -1) {
            showAlert("Error", "No user selected for removal.");
            return;
        }

        // Perform the user removal
        boolean success = userDAO.removeUser(selectedUserIdToRemove);

        if (success) {
            showAlert("Success", "User removed successfully.");
            loadUsersIntoRemoveScrollPane(); // Refresh the list
            loadUsersIntoScrollPane();
            selectedUserIdToRemove = -1; // Reset the selected user ID
        } else {
            showAlert("Error", "Failed to remove user.");
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
    /**
     * Populates the role ChoiceBox with the values: Admin, Manager, and User.
     */
    private void populateRoleChoiceBox(ChoiceBox<String> choiceBox) {
        ObservableList<String> roles = FXCollections.observableArrayList("Admin", "Manager", "User");
        choiceBox.setItems(roles);
        choiceBox.setValue("User"); // Set default value
    }

    /**
     * Shows the profile pane and resets buttons to their default states.
     */
    @FXML
    private void showProfilePane() {
        profile_pane_admin.setVisible(true);
        scroll_pane_admin_homePage.setVisible(true);
        ordering_pane_admin.setVisible(true);
        user_manage_pane.setVisible(false);
        restaurant_manage_pane.setVisible(false);

        // Update button text
        manage_restaurant_button.setText("Manage Restaurants");
        manage_user_button.setText("Manage Users");

        // Clear existing content in the VBox
        vbox_scroll_pane_admin_homePage.getChildren().clear();

        // Fetch restaurant data from the database
        RestaurantDAO restaurantDAO = new RestaurantDAO();
        List<Restaurant> restaurants = restaurantDAO.getAllRestaurants();

        // Load each restaurant as a cart
        for (Restaurant restaurant : restaurants) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("RestaurantCart.fxml"));
                AnchorPane restaurantCart = loader.load();

                RestaurantCart controller = loader.getController();
                controller.setRestaurantData(
                        restaurant.getName(),
                        "file:src/main/resources/images/" + restaurant.getPhoto()
                );

                restaurantCart.setOnMouseEntered(event -> restaurantCart.setStyle(
                        "-fx-background-color: #e0e0e0; -fx-border-color: #ccc; -fx-border-radius: 8px;"));
                restaurantCart.setOnMouseExited(event -> restaurantCart.setStyle(
                        "-fx-background-color: #ffffff; -fx-border-color: #ccc; -fx-border-radius: 8px;"));

                restaurantCart.setOnMouseClicked(event -> System.out.println("Clicked on: " + restaurant.getName()));

                vbox_scroll_pane_admin_homePage.getChildren().add(restaurantCart);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Toggles between the Manage Restaurants view and the Home Page.
     */
    @FXML
    private void toggleManageRestaurants() {
        if ("Home Page".equals(manage_restaurant_button.getText())) {
            showProfilePane(); // Go back to the home page
        } else {
            profile_pane_admin.setVisible(true);
            ordering_pane_admin.setVisible(false);
            scroll_pane_admin_homePage.setVisible(false);
            user_manage_pane.setVisible(false);
            restaurant_manage_pane.setVisible(true);

            manage_restaurant_button.setText("Home Page");
            manage_user_button.setText("Manage Users");
        }
    }

    /**
     * Toggles between the Manage Users view and the Home Page.
     */
    @FXML
    private void toggleManageUsers() {
        if ("Home Page".equals(manage_user_button.getText())) {
            showProfilePane(); // Go back to the home page
        } else {
            profile_pane_admin.setVisible(true);
            ordering_pane_admin.setVisible(false);
            scroll_pane_admin_homePage.setVisible(false);
            user_manage_pane.setVisible(true);
            restaurant_manage_pane.setVisible(false);

            manage_user_button.setText("Home Page");
            manage_restaurant_button.setText("Manage Restaurants");

            loadUsersIntoScrollPane();
            loadUsersIntoRemoveScrollPane();
        }
    }

    /**
     * Loads all users into the scroll pane for selection.
     */
    private void loadUsersIntoScrollPane() {
        List<User> users = userDAO.getAllUsers();
        edit_user_scroll_vbox.getChildren().clear();

        for (User user : users) {
            if (!user.isDeleted()) { // Only show users who are not soft-deleted
                Label userLabel = new Label(user.getName() + " (" + user.getPhone() + ") - Role: " + user.getRole());
                userLabel.setStyle("-fx-font-size: 14px; -fx-padding: 5; -fx-background-color: transparent; -fx-cursor: hand;");

                userLabel.setOnMouseEntered(event -> userLabel.setStyle("-fx-font-size: 14px; -fx-padding: 5; -fx-background-color: #c40000;"));
                userLabel.setOnMouseExited(event -> userLabel.setStyle("-fx-font-size: 14px; -fx-padding: 5; -fx-background-color: transparent;"));

                userLabel.setOnMouseClicked(event -> {
                    selectedUserId = user.getUserId();
                    loadUserDetails(user.getUserId());
                });

                edit_user_scroll_vbox.getChildren().add(userLabel);
            }
        }
    }

    /**
     * Loads the details of the selected user into the text fields and choice box.
     *
     * @param userId The ID of the user to fetch.
     */
    private void loadUserDetails(int userId) {
        User user = userDAO.getUserById(userId);
        if (user != null) {
            edit_user_name_txtfield.setText(user.getName());
            edit_user_phone_txtfield.setText(user.getPhone());
            edit_user_role_choicebox.setValue(user.getRole()); // Set the role in the ChoiceBox
        } else {
            edit_user_name_txtfield.setText("");
            edit_user_phone_txtfield.setText("");
            edit_user_role_choicebox.setValue("User"); // Default value
        }
    }

    /**
     * Updates the details of the selected user.
     */
    @FXML
    private void updateUserDetails() {
        if (selectedUserId == -1) {
            System.out.println("No user selected for editing.");
            return;
        }

        String newName = edit_user_name_txtfield.getText().trim();
        String newPhone = edit_user_phone_txtfield.getText().trim();
        String newRole = edit_user_role_choicebox.getValue();

        if (newName.isEmpty() || newPhone.isEmpty() || newRole == null) {
            System.out.println("Name, phone, and role fields cannot be empty.");
            return;
        }

        boolean nameUpdated = userDAO.updateUser(selectedUserId, "Name", newName);
        boolean phoneUpdated = userDAO.updateUser(selectedUserId, "Phone", newPhone);
        boolean roleUpdated = userDAO.updateUser(selectedUserId, "Role", newRole);

        if (nameUpdated && phoneUpdated && roleUpdated) {
            System.out.println("User updated successfully!");
            loadUsersIntoScrollPane();
            loadUsersIntoRemoveScrollPane();
        } else {
            System.out.println("Failed to update user.");
        }
    }

    @FXML
    private void handleAddUser() {
        String name = add_user_name_txtfield.getText().trim();
        String phone = add_user_phone_txtfield.getText().trim();
        String password = add_user_password_txtfield.getText().trim();
        String role = add_user_role_choicebox.getValue();

        // Validate input fields
        if (name.isEmpty() || phone.isEmpty() || password.isEmpty() || role == null) {
            System.out.println("Error: All fields must be filled.");
            return;
        }

        // Create a new User object
        User newUser = new User(0, name, phone, password, role, false);

        // Attempt to add the user to the database
        boolean success = userDAO.addUser(newUser);

        if (success) {
            System.out.println("User added successfully!");
            clearAddUserFields(); // Clear the input fields
            loadUsersIntoScrollPane(); // Reload the user list
            loadUsersIntoRemoveScrollPane();
        } else {
            System.out.println("Failed to add user.");
        }
    }

    /**
     * Clears the input fields for adding a new user.
     */
    private void clearAddUserFields() {
        add_user_name_txtfield.clear();
        add_user_phone_txtfield.clear();
        add_user_password_txtfield.clear();
        add_user_role_choicebox.setValue("User");
    }
    private void highlightSelectedUser(Label userLabel) {
        // Reset styles for all labels
        for (var child : remove_user_vbox.getChildren()) {
            if (child instanceof Label) {
                child.setStyle("-fx-font-size: 14px; -fx-padding: 5; -fx-background-color: transparent;");
            }
        }

        // Highlight the selected label
        userLabel.setStyle("-fx-font-size: 14px; -fx-padding: 5; -fx-background-color: #e0e0e0;");
    }
}
