package org.example.foodapp;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class AdminHomePage {

    @FXML
    private Label admin_name;

    @FXML
    private AnchorPane main_pane_adminPage;

    @FXML
    private Button manage_resturant_button;

    @FXML
    private Button manage_user_button;

    @FXML
    private AnchorPane ordering_pane_admin;

    @FXML
    private AnchorPane profile_pane_admin;

    @FXML
    private AnchorPane scroll_pane_admin_homePage;

    @FXML
    private AnchorPane user_manage_pane;
    @FXML
    private AnchorPane user_manage_pane1;

    @FXML
    private AnchorPane restaurant_manage_pane;

    @FXML
    private VBox vbox_scroll_pane_admin_homePage;


    @FXML
    public void initialize() {
        // Set the admin name dynamically
        admin_name.setText("John Doe"); // Replace with actual admin name from the backend

        // Load the initial content (profile view)
        showProfilePane();

        // Set up event handlers for buttons
        manage_resturant_button.setOnAction(event -> toggleManageRestaurants());
        manage_user_button.setOnAction(event -> toggleManageUsers());
    }

    /**
     * Shows the profile pane and resets buttons to their default states.
     */
    @FXML
    private void showProfilePane() {
        profile_pane_admin.setVisible(true);
        ordering_pane_admin.setVisible(true);
        scroll_pane_admin_homePage.setVisible(true);
        user_manage_pane.setVisible(false);
        restaurant_manage_pane.setVisible(false);


        // Reset button text
        manage_resturant_button.setText("Manage Restaurants");
        manage_user_button.setText("Manage Users");

                    // Load restaurant management content
            vbox_scroll_pane_admin_homePage.getChildren().clear(); // Clear previous content
            for (int i = 1; i <= 5; i++) {
                Label restaurantLabel = new Label("Restaurant " + i);
                restaurantLabel.setStyle("-fx-font-size: 14px; -fx-padding: 5;");
                vbox_scroll_pane_admin_homePage.getChildren().add(restaurantLabel);
            }
    }

    /**
     * Toggles between the Manage Restaurants view and the Home Page.
     */
    @FXML
    private void toggleManageRestaurants() {
        if ("Home Page".equals(manage_resturant_button.getText())) {
            showProfilePane(); // Go back to the home page
        } else {
            // Show the Manage Restaurants view
            profile_pane_admin.setVisible(true);
            ordering_pane_admin.setVisible(false);
            scroll_pane_admin_homePage.setVisible(false);
            user_manage_pane.setVisible(false);
            restaurant_manage_pane.setVisible(true);


            // Update button text
            manage_resturant_button.setText("Home Page");
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
            // Show the Manage Users view
            profile_pane_admin.setVisible(true);
            ordering_pane_admin.setVisible(false);
            scroll_pane_admin_homePage.setVisible(false);
            user_manage_pane.setVisible(true);
            restaurant_manage_pane.setVisible(false);


            // Update button text
            manage_user_button.setText("Home Page");
            manage_resturant_button.setText("Manage Restaurants");

        }
    }

}
