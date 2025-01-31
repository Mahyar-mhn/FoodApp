package org.example.foodapp;

import Classes.Item;
import Classes.Restaurant;
import DaoClasses.ItemDAO;
import DaoClasses.RestaurantDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class ManagerRestaurantEdit {

    @FXML
    private TextField restaurant_name_txtfield;

    @FXML
    private TextField restaurant_city_txtfield;

    @FXML
    private TextField restaurant_address_txtfield;

    private RestaurantDAO restaurantDAO = new RestaurantDAO();
    private Restaurant currentRestaurant; // To track the restaurant being edited

    @FXML
    private HBox hbox_item_scrol_pane;

    @FXML
    private TextField item_name_txtfield;

    @FXML
    private TextField item_price_txtfield;

    @FXML
    private TextField item_photo_txtfield;

    @FXML
    private TextField item_detail_txtfield;

    @FXML
    private TextField new_item_name_txtfield;

    @FXML
    private TextField new_item_price_txtfield;

    @FXML
    private TextField new_item_photo_txtfield;

    @FXML
    private TextField new_item_detail_txtfield;

    @FXML
    private Button delete_item_button;

    @FXML
    private Button edit_item_button;

    @FXML
    private Button add_item_button;

    private ItemDAO itemDAO = new ItemDAO();
    private int selectedItemId = -1; // To track the selected item for editing or deletion


    /**
     * Loads the details of the restaurant and its items into the UI.
     *
     * @param restaurant The restaurant object to load.
     */
    public void loadRestaurantDetails(Restaurant restaurant) {
        currentRestaurant = restaurant;

        // Set restaurant details
        restaurant_name_txtfield.setText(restaurant.getName());
        restaurant_city_txtfield.setText(restaurant.getCity());
        restaurant_address_txtfield.setText(restaurant.getAddress());

        // Load items into the HBox
        loadItems();
    }

    /**
     * Loads all items of the current restaurant into the HBox.
     */
    private void loadItems() {
        hbox_item_scrol_pane.getChildren().clear(); // Clear the HBox

        if (currentRestaurant == null) {
            showAlert("Error", "No restaurant selected.");
            return;
        }

        List<Item> items = itemDAO.getItemsByRestaurantId(currentRestaurant.getRestaurantId());

        for (Item item : items) {
            VBox itemBox = new VBox();
            itemBox.setSpacing(10);
            itemBox.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 5px; -fx-background-color: #f9f9f9;");

            Label itemName = new Label("Name: " + item.getName());
            Label itemPrice = new Label("Price: $" + item.getPrice());
            Label itemPhoto = new Label("Photo: " + item.getPhoto());
            Label itemDetail = new Label("Detail: " + item.getFoodDetail());

            itemBox.getChildren().addAll(itemName, itemPrice, itemPhoto, itemDetail);

            // Add click event to select an item
            itemBox.setOnMouseClicked(event -> {
                selectedItemId = item.getItemId();
                item_name_txtfield.setText(item.getName());
                item_price_txtfield.setText(String.valueOf(item.getPrice()));
                item_photo_txtfield.setText(item.getPhoto());
                item_detail_txtfield.setText(item.getFoodDetail());
            });

            hbox_item_scrol_pane.getChildren().add(itemBox);
        }
    }

    @FXML
    private void handleEditItem() {
        if (selectedItemId == -1) {
            showAlert("Error", "No item selected for editing.");
            return;
        }

        String newName = item_name_txtfield.getText().trim();
        String newPrice = item_price_txtfield.getText().trim();
        String newPhoto = item_photo_txtfield.getText().trim();
        String newDetail = item_detail_txtfield.getText().trim();

        if (newName.isEmpty() || newPrice.isEmpty() || newPhoto.isEmpty() || newDetail.isEmpty()) {
            showAlert("Error", "All fields must be filled.");
            return;
        }

        boolean updatedName = itemDAO.updateMenuItem(selectedItemId, "Name", newName);
        boolean updatedPrice = itemDAO.updateMenuItem(selectedItemId, "Price", newPrice);
        boolean updatedPhoto = itemDAO.updateMenuItem(selectedItemId, "Photo", newPhoto);
        boolean updatedDetail = itemDAO.updateMenuItem(selectedItemId, "FoodDetail", newDetail);

        if (updatedName && updatedPrice && updatedPhoto && updatedDetail) {
            showAlert("Success", "Item updated successfully.");
            loadItems(); // Refresh the item list
        } else {
            showAlert("Error", "Failed to update item.");
        }
    }
    /**
     * Handles deleting the selected item.
     */
    @FXML
    private void handleDeleteItem() {
        if (selectedItemId == -1) {
            showAlert("Error", "No item selected for deletion.");
            return;
        }

        boolean deleted = itemDAO.deleteMenuItem(selectedItemId);

        if (deleted) {
            showAlert("Success", "Item deleted successfully.");
            loadItems(); // Refresh the item list
            clearItemFields(); // Clear input fields
            selectedItemId = -1; // Reset selected item
        } else {
            showAlert("Error", "Failed to delete item.");
        }
    }

    /**
     * Handles adding a new item.
     */
    @FXML
    private void handleAddItem() {
        // Retrieve input values from the text fields
        String name = new_item_name_txtfield.getText().trim();
        String price = new_item_price_txtfield.getText().trim();
        String photo = new_item_photo_txtfield.getText().trim();
        String detail = new_item_detail_txtfield.getText().trim();

        // Validate that no field is left empty
        if (name.isEmpty() || price.isEmpty() || photo.isEmpty() || detail.isEmpty()) {
            showAlert("Error", "All fields must be filled.");
            return;
        }

        try {
            // Parse the price to double and create an Item object
            double parsedPrice = Double.parseDouble(price);
            Item newItem = new Item(
                    0, // Item ID will be auto-generated
                    currentRestaurant.getRestaurantId(),
                    name,
                    parsedPrice,
                    photo,
                    detail,
                    false // IsDeleted is false for a new item
            );

            // Add the new item to the database using addMenuItem
            boolean added = itemDAO.addMenuItem(newItem);

            if (added) {
                showAlert("Success", "Item added successfully.");
                loadItems(); // Refresh the item list
                clearNewItemFields(); // Clear the input fields
            } else {
                showAlert("Error", "Failed to add item.");
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Price must be a valid number.");
        }
    }

    /**
     * Clears the fields in the item editing section.
     */
    private void clearItemFields() {
        item_name_txtfield.clear();
        item_price_txtfield.clear();
        item_photo_txtfield.clear();
        item_detail_txtfield.clear();
    }

    /**
     * Clears the fields in the new item adding section.
     */
    private void clearNewItemFields() {
        new_item_name_txtfield.clear();
        new_item_price_txtfield.clear();
        new_item_photo_txtfield.clear();
        new_item_detail_txtfield.clear();
    }

    /**
     * Handles saving changes to the restaurant's details.
     */
    @FXML
    private void handleSaveChanges() {
        if (currentRestaurant == null) {
            showAlert("Error", "No restaurant loaded for editing.");
            return;
        }

        // Retrieve the updated values from the text fields
        String newName = restaurant_name_txtfield.getText().trim();
        String newCity = restaurant_city_txtfield.getText().trim();
        String newAddress = restaurant_address_txtfield.getText().trim();

        // Validate that no field is left empty
        if (newName.isEmpty() || newCity.isEmpty() || newAddress.isEmpty()) {
            showAlert("Error", "All fields must be filled.");
            return;
        }

        // Begin saving changes to the database
        boolean isUpdated = false;
        try {
            // Update only fields that have changed
            if (!newName.equals(currentRestaurant.getName())) {
                isUpdated |= restaurantDAO.updateRestaurant(currentRestaurant.getName(), "Name", newName);
            }

            if (!newCity.equals(currentRestaurant.getCity())) {
                isUpdated |= restaurantDAO.updateRestaurant(currentRestaurant.getName(), "City", newCity);
            }

            if (!newAddress.equals(currentRestaurant.getAddress())) {
                isUpdated |= restaurantDAO.updateRestaurant(currentRestaurant.getName(), "Address", newAddress);
            }

            // If updates were successful
            if (isUpdated) {
                showAlert("Success", "Restaurant details updated successfully.");
                // Update the current restaurant object
                currentRestaurant.setName(newName);
                currentRestaurant.setCity(newCity);
                currentRestaurant.setAddress(newAddress);
            } else {
                showAlert("Info", "No changes were made to the restaurant details.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while updating the restaurant details.");
        }
    }

    /**
     * Handles cancelling the editing and navigating back to the manager page.
     */
    @FXML
    private void handleCancel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RestaurantManagerHomePage.fxml"));
            AnchorPane managerHomePage = loader.load();
            RestaurantManagerHomePage controller = loader.getController();
            // Reload the manager's restaurants when returning
            controller.setManagerId(currentRestaurant.getRestaurantId()); // Ensure restaurants are reloaded
            restaurant_name_txtfield.getScene().setRoot(managerHomePage);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to navigate back to the manager home page.");
        }
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
}
