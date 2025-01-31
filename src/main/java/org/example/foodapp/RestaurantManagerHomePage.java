package org.example.foodapp;

import Classes.Restaurant;
import DaoClasses.RestaurantDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class RestaurantManagerHomePage {

    @FXML
    private AnchorPane main_pane_managerPage;
    @FXML
    private Button edit_manager_restaurant_items;

    @FXML
    private Label manager_name;

    @FXML
    private Button remove_restaurant_button;

    @FXML
    private ScrollPane remove_restaurant_scroll_pane;

    @FXML
    private VBox vbox_manager_restarurants;

    @FXML
    private VBox vbox_scroll_pane_manager_homePage;

    private int managerId; // ID of the logged-in manager
    private RestaurantDAO restaurantDAO = new RestaurantDAO(); // DAO instance
    private int selectedRestaurantId = -1; // To track the selected restaurant for deletion




    /**
     * Sets the customer name into the label.
     *
     * @param name The name of the customer.
     */
    public void loadManagerName(String name) {
        manager_name.setText(name);
    }
    public void setManagerId(int managerId) {
        this.managerId = managerId;
        loadManagerRestaurants();
    }
    @FXML
    public void initialize() {
        showProfilePane();
    }

    @FXML
    public void showProfilePane() {
        // Clear existing content in the VBox
        vbox_scroll_pane_manager_homePage.getChildren().clear();

        // Fetch restaurant data from the database
        RestaurantDAO restaurantDAO = new RestaurantDAO();
        List<Restaurant> restaurants = restaurantDAO.getAllRestaurants();

        // Load only restaurants where isDeleted == 0
        for (Restaurant restaurant : restaurants) {
            if (!restaurant.isDeleted()) { // Check the isDeleted flag
                try {
                    // Load the RestaurantCart.fxml file
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("RestaurantCart.fxml"));
                    AnchorPane restaurantCart = loader.load();

                    // Get the controller for the cart
                    RestaurantCart controller = loader.getController();
                    controller.setRestaurantData(
                            restaurant.getName(),
                            "file:src/main/resources/images/" + restaurant.getPhoto()
                    );

                    // Add hover effects
                    restaurantCart.setOnMouseEntered(event -> {
                        restaurantCart.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #ccc; -fx-border-radius: 8px;");
                    });

                    restaurantCart.setOnMouseExited(event -> {
                        restaurantCart.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ccc; -fx-border-radius: 8px;");
                    });

                    // Add click event to load selected restaurant's detailed page
                    restaurantCart.setOnMouseClicked(event -> {
                        System.out.println("Clicked on: " + restaurant.getName());
                        loadSelectedRestaurantPage(restaurant.getRestaurantId());
                    });

                    // Add the cart to the VBox
                    vbox_scroll_pane_manager_homePage.getChildren().add(restaurantCart);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Loads the selected restaurant's page or detailed view.
     *
     * @param restaurantId The ID of the selected restaurant.
     */
    private void loadSelectedRestaurantPage(int restaurantId) {
        try {
            // Fetch restaurant details from the database using the RestaurantDAO
            RestaurantDAO restaurantDAO = new RestaurantDAO();
            Restaurant restaurant = restaurantDAO.getRestaurantById(restaurantId);

            if (restaurant != null) {
                System.out.println("Loading details for: " + restaurant.getName());
                FXMLLoader loader = new FXMLLoader(getClass().getResource("RestaurantPage.fxml"));
                AnchorPane restaurantDetailsPane = loader.load();

                // Pass restaurant details to the controller
                RestaurantPage controller = loader.getController();
                controller.loadRestaurantPage(restaurant, 1);

                // Switch the main pane to the restaurant details pane
                main_pane_managerPage.getChildren().clear();
                main_pane_managerPage.getChildren().add(restaurantDetailsPane);
            } else {
                System.out.println("Restaurant not found!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadManagerRestaurants() {
        vbox_manager_restarurants.getChildren().clear(); // Clear existing content

        List<Restaurant> restaurants = restaurantDAO.getRestaurantsByManagerId(managerId);

        for (Restaurant restaurant : restaurants) {
            try {
                // Load the RestaurantCart.fxml file
                FXMLLoader loader = new FXMLLoader(getClass().getResource("RestaurantCart.fxml"));
                AnchorPane restaurantCart = loader.load();

                // Get the controller for the cart
                RestaurantCart controller = loader.getController();
                controller.setRestaurantData(
                        restaurant.getName(),
                        "file:src/main/resources/images/" + restaurant.getPhoto()
                );

                // Add hover effects
                restaurantCart.setOnMouseEntered(event -> {
                    restaurantCart.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #ccc; -fx-border-radius: 8px;");
                });

                restaurantCart.setOnMouseExited(event -> {
                    restaurantCart.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ccc; -fx-border-radius: 8px;");
                });

                // Add click event to track the selected restaurant
                restaurantCart.setOnMouseClicked(event -> {
                    System.out.println("Clicked on: " + restaurant.getName());
                    selectedRestaurantId = restaurant.getRestaurantId(); // Set selected restaurant ID
                    highlightSelectedRestaurant(restaurantCart); // Highlight the selected restaurant
                });

                // Add the cart to the VBox
                vbox_manager_restarurants.getChildren().add(restaurantCart);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleDeleteRestaurant() {
        if (selectedRestaurantId == -1) {
            showAlert("Error", "No restaurant selected for deletion.");
            return;
        }

        boolean success = restaurantDAO.removeRestaurant(selectedRestaurantId); // Perform the delete operation

        if (success) {
            showAlert("Success", "Restaurant deleted successfully.");
            loadManagerRestaurants(); // Refresh the list of restaurants
            showProfilePane();
            selectedRestaurantId = -1; // Reset the selected ID
        } else {
            showAlert("Error", "Failed to delete restaurant.");
        }
    }
    private void highlightSelectedRestaurant(AnchorPane selectedCart) {
        // Reset styles for all restaurant carts
        for (var child : vbox_manager_restarurants.getChildren()) {
            if (child instanceof AnchorPane) {
                child.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ccc; -fx-border-radius: 8px;");
            }
        }

        // Highlight the selected cart
        selectedCart.setStyle("-fx-background-color: #c40000; -fx-border-color: #900; -fx-border-radius: 8px;");
    }

    /**
     * Displays an alert dialog.
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


    @FXML
    private void handleEditManagerRestaurant() {
        if (selectedRestaurantId == -1) {
            showAlert("Error", "No restaurant selected to edit.");
            return;
        }

        try {
            // Load the restaurant editing page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ManagerRestaurantEdit.fxml"));
            AnchorPane restaurantEditPane = loader.load();

            // Get the controller for the restaurant editing page
            ManagerRestaurantEdit controller = loader.getController();

            // Pass the selected restaurant details to the controller
            RestaurantDAO restaurantDAO = new RestaurantDAO();
            Restaurant selectedRestaurant = restaurantDAO.getRestaurantById(selectedRestaurantId);

            if (selectedRestaurant != null) {
                controller.loadRestaurantDetails(selectedRestaurant);
            } else {
                showAlert("Error", "Failed to load restaurant details.");
                return;
            }

            // Replace the current pane with the restaurant editing page
            main_pane_managerPage.getChildren().clear();
            main_pane_managerPage.getChildren().add(restaurantEditPane);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the restaurant editing page.");
        }
    }

}
