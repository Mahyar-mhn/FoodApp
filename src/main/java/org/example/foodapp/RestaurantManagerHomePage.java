package org.example.foodapp;

import Classes.Restaurant;
import DaoClasses.RestaurantDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class RestaurantManagerHomePage {

    @FXML
    private AnchorPane main_pane_adminPage;
    @FXML
    private Button add_restaurant_button;

    @FXML
    private Label manager_name;

    @FXML
    private Button remove_restaurant_button;

    @FXML
    private ScrollPane remove_restaurant_scroll_pane;

    @FXML
    private VBox remove_restaurant_vbox;

    @FXML
    private VBox vbox_scroll_pane_manager_homePage;

    @FXML
    public void initialize() {
        showProfilePane();
    }

    @FXML
    public void showProfilePane(){
        // Clear existing content in the VBox
        vbox_scroll_pane_manager_homePage.getChildren().clear();

        // Fetch restaurant data from the database
        RestaurantDAO restaurantDAO = new RestaurantDAO();
        List<Restaurant> restaurants = restaurantDAO.getAllRestaurants();

        // Load each restaurant as a cart
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
                controller.loadRestaurantPage(restaurant);

                // Switch the main pane to the restaurant details pane
                main_pane_adminPage.getChildren().clear();
                main_pane_adminPage.getChildren().add(restaurantDetailsPane);
            } else {
                System.out.println("Restaurant not found!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
