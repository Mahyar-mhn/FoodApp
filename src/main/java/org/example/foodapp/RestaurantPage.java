package org.example.foodapp;

import Classes.Restaurant;
import Classes.Item;
import Classes.WorkingTime;
import DaoClasses.RestaurantDAO;
import DaoClasses.ItemDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.util.List;

public class RestaurantPage {

    @FXML
    private HBox Hbox_scrollPane_RestaurantPage; // Container for menu items

    @FXML
    private Label Restaurant_name_label; // Label to display restaurant name

    @FXML
    private TableView<WorkingTime> workingtime_table_restaurantPage; // Table for working times

    @FXML
    private TableColumn<WorkingTime, String> Day_column;

    @FXML
    private TableColumn<WorkingTime, String> OpenAt_column;

    @FXML
    private TableColumn<WorkingTime, String> CloseAt_column;

    @FXML
    private AnchorPane scrolPane_RestaurantPage; // Main container for the page

    /**
     * Loads the restaurant details into the page, including menu items and working time.
     *
     * @param restaurant The restaurant object containing its details.
     */
    public void loadRestaurantPage(Restaurant restaurant) {
        // Set restaurant name
        Restaurant_name_label.setText(restaurant.getName());

        // Clear existing items in the HBox
        Hbox_scrollPane_RestaurantPage.getChildren().clear();

        // Configure the working time table columns
        Day_column.setCellValueFactory(new PropertyValueFactory<>("day"));
        OpenAt_column.setCellValueFactory(new PropertyValueFactory<>("openAt"));
        CloseAt_column.setCellValueFactory(new PropertyValueFactory<>("closeAt"));

        // Fetch and display working times
        RestaurantDAO restaurantDAO = new RestaurantDAO();
        List<WorkingTime> workingTimes = restaurantDAO.getRestaurantWorkingTime(restaurant.getRestaurantId());

        // Populate the working time table
        workingtime_table_restaurantPage.getItems().clear();
        if (!workingTimes.isEmpty()) {
            workingtime_table_restaurantPage.getItems().addAll(workingTimes);
        } else {
            System.out.println("No working time available for the selected restaurant.");
        }

        // Fetch the menu items for the restaurant
        ItemDAO itemDAO = new ItemDAO();
        List<Item> menuItems = itemDAO.getItemsByRestaurantId(restaurant.getRestaurantId());

        // Populate the HBox with menu items
        for (Item item : menuItems) {
            Label itemLabel = new Label(item.getName() + " - $" + item.getPrice());
            itemLabel.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-border-color: #ccc; -fx-background-color: #f9f9f9; -fx-border-radius: 5px;");
            itemLabel.setOnMouseEntered(event -> itemLabel.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-border-color: #999; -fx-background-color: #e0e0e0; -fx-border-radius: 5px;"));
            itemLabel.setOnMouseExited(event -> itemLabel.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-border-color: #ccc; -fx-background-color: #f9f9f9; -fx-border-radius: 5px;"));

            // Add click event for item details
            itemLabel.setOnMouseClicked(event -> {
                System.out.println("Clicked on: " + item.getName());
                // Add logic for showing item details or adding to cart
            });

            Hbox_scrollPane_RestaurantPage.getChildren().add(itemLabel);
        }

        // If no menu items are available
        if (menuItems.isEmpty()) {
            Label noItemsLabel = new Label("No menu items available.");
            noItemsLabel.setStyle("-fx-font-size: 14px; -fx-padding: 5;");
            Hbox_scrollPane_RestaurantPage.getChildren().add(noItemsLabel);
        }
    }
}
