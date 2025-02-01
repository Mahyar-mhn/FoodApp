package org.example.foodapp;

import Classes.Item;
import Classes.Restaurant;
import DaoClasses.ItemDAO;
import DaoClasses.RestaurantDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.*;
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
    private TextField deliverydee_txtfield;

    @FXML
    private Button delete_item_button;

    @FXML
    private Button edit_item_button;

    @FXML
    private Button add_item_button;

    @FXML
    private Button update_fee;


    private ItemDAO itemDAO = new ItemDAO();
    private int selectedItemId = -1; // To track the selected item for editing or deletion

    @FXML
    private ChoiceBox<String> working_time_day_combobox;
    @FXML
    private ComboBox<String> working_time_open_hour_combobox;
    @FXML
    private ComboBox<String> working_time_open_minute_combobox;
    @FXML
    private ComboBox<String> working_time_close_hour_combobox;
    @FXML
    private ComboBox<String> working_time_close_minute_combobox;
    @FXML
    private Button edit_restaurant_time;


    @FXML
    private HBox hbox_pendingOrders;
    @FXML
    private HBox hbox_manager_order_history;

    @FXML
    private ChoiceBox<String> order_status_choicebox;

    @FXML
    private Button update_order_status_button;

    private int selectedOrderId = -1; // Stores the selected order ID

    @FXML
    private void loadRestaurantOrderHistory() {
        if (currentRestaurant == null) {
            showAlert("Error", "No restaurant selected.");
            return;
        }

        hbox_manager_order_history.getChildren().clear(); // Clear existing orders in the HBox

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall("{CALL GetRestaurantOrders(?)}")) {

            // Set the restaurant ID for the stored procedure
            callableStatement.setInt(1, currentRestaurant.getRestaurantId());

            // Execute the stored procedure and retrieve the result set
            try (ResultSet resultSet = callableStatement.executeQuery()) {
                if (!resultSet.isBeforeFirst()) {
                    // If no orders are found
                    Label noOrdersLabel = new Label("No orders found for this restaurant.");
                    noOrdersLabel.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
                    hbox_manager_order_history.getChildren().add(noOrdersLabel);
                    return;
                }

                // Loop through the result set and create order history cards
                while (resultSet.next()) {
                    int orderId = resultSet.getInt("OrderId");
                    String orderStatus = resultSet.getString("OrderStatus");
                    String itemName = resultSet.getString("ItemName");
                    double itemPrice = resultSet.getDouble("ItemPrice");
                    int quantity = resultSet.getInt("Quantity");
                    String reviewRating = resultSet.getString("ReviewRating");
                    String reviewComment = resultSet.getString("ReviewComment");
                    boolean isItemDeleted = resultSet.getBoolean("IsItemDeleted");

                    // Create a VBox for each order history card
                    VBox orderBox = new VBox();
                    orderBox.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 5px; -fx-background-color: #f9f9f9; -fx-spacing: 5;");
                    orderBox.setPrefWidth(250); // Optional: Set fixed width for each order box

                    // Add order details
                    Label orderIdLabel = new Label("Order ID: " + orderId);
                    orderIdLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

                    Label orderStatusLabel = new Label("Status: " + orderStatus);
                    orderStatusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2a9df4;");

                    Label itemDetailsLabel = new Label("Item: " + itemName + " (x" + quantity + ")");
                    itemDetailsLabel.setStyle("-fx-font-size: 14px;");

                    Label itemPriceLabel = new Label("Price: $" + itemPrice);
                    itemPriceLabel.setStyle("-fx-font-size: 14px;");

                    Label reviewRatingLabel = new Label("Rating: " + (reviewRating != null ? reviewRating : "No Rating"));
                    reviewRatingLabel.setStyle("-fx-font-size: 14px;");

                    Label reviewCommentLabel = new Label("Comment: " + (reviewComment != null ? reviewComment : "No Comment"));
                    reviewCommentLabel.setStyle("-fx-font-size: 14px;");

                    Label itemDeletedLabel = new Label(isItemDeleted ? "Item Deleted" : "Item Active");
                    itemDeletedLabel.setStyle(isItemDeleted
                            ? "-fx-font-size: 14px; -fx-text-fill: red;"
                            : "-fx-font-size: 14px; -fx-text-fill: green;");

                    // Add all details to the VBox
                    orderBox.getChildren().addAll(
                            orderIdLabel,
                            orderStatusLabel,
                            itemDetailsLabel,
                            itemPriceLabel,
                            reviewRatingLabel,
                            reviewCommentLabel,
                            itemDeletedLabel
                    );

                    // Add the VBox to the HBox
                    hbox_manager_order_history.getChildren().add(orderBox);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while loading the order history.");
        }
    }


    /**
     * Method to handle updating the order status for the selected order.
     */
    @FXML
    private void handleUpdateOrderStatus() {
        if (selectedOrderId == -1) {
            showAlert("Error", "No order selected. Please select an order from the pending orders list.");
            return;
        }

        String newStatus = order_status_choicebox.getValue();

        // Validate the selected status
        if (newStatus == null || newStatus.trim().isEmpty()) {
            showAlert("Error", "Please select a status to update.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall("{CALL setOrderStatus(?, ?)}")) {

            // Set parameters for the stored procedure
            callableStatement.setInt(1, selectedOrderId);
            callableStatement.setString(2, newStatus);

            // Execute the procedure
            callableStatement.execute();

            // Success message
            showAlert("Success", "Order status updated successfully to: " + newStatus);

            // Refresh the pending orders list after update
            loadPendingOrders();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while updating the order status.");
        }
    }

    @FXML
    private void handleRejectOrder() {
        if (selectedOrderId == -1) {
            showAlert("Error", "No order selected. Please select an order from the pending orders list.");
            return;
        }

        String newStatus = "Reject"; // Always set the status to "Reject"

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall("{CALL setOrderStatus(?, ?)}")) {

            // Set parameters for the stored procedure
            callableStatement.setInt(1, selectedOrderId); // Pass the selected order ID
            callableStatement.setString(2, newStatus); // Set the status to "Reject"

            // Execute the procedure
            callableStatement.execute();

            // Success message
            showAlert("Success", "Order status updated to: " + newStatus);

            // Refresh the pending orders list after update
            loadPendingOrders();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while rejecting the order.");
        }
    }


    /**
     * Method to handle order selection from the pending orders HBox.
     *
     * @param orderId The selected order's ID.
     */
    private void handleOrderSelection(int orderId) {
        this.selectedOrderId = orderId; // Set the selected order ID
        showAlert("Order Selected", "Order ID " + orderId + " is selected. You can now update its status.");
    }



    private void loadPendingOrders() {
        if (currentRestaurant == null) {
            showAlert("Error", "No restaurant selected.");
            return;
        }

        hbox_pendingOrders.getChildren().clear(); // Clear the existing orders in the HBox

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall("{CALL GetPendingOrdersByRestaurantId(?)}")) {

            // Set the restaurant ID for the stored procedure
            callableStatement.setInt(1, currentRestaurant.getRestaurantId());

            // Execute the stored procedure and get the result set
            try (ResultSet resultSet = callableStatement.executeQuery()) {
                if (!resultSet.isBeforeFirst()) {
                    // If no pending orders are found
                    Label noOrdersLabel = new Label("No pending orders.");
                    noOrdersLabel.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-text-fill: gray;");
                    hbox_pendingOrders.getChildren().add(noOrdersLabel);
                    return;
                }

                // Loop through the result set and create order boxes
                while (resultSet.next()) {
                    int orderId = resultSet.getInt("OrderId");
                    int addressId = resultSet.getInt("AddressId");
                    String status = resultSet.getString("Status");

                    // Create a VBox for each order
                    VBox orderBox = new VBox();
                    orderBox.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 5px; -fx-background-color: #f0f8ff; -fx-spacing: 5;");
                    orderBox.setPrefWidth(250); // Set fixed width for each order box

                    // Add order details
                    Label orderIdLabel = new Label("Order ID: " + orderId);
                    orderIdLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

                    Label addressIdLabel = new Label("Address ID: " + addressId);
                    addressIdLabel.setStyle("-fx-font-size: 14px;");

                    Label statusLabel = new Label("Status: " + status);
                    statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #ff4500;");

                    // Add labels to the VBox
                    orderBox.getChildren().addAll(orderIdLabel, addressIdLabel, statusLabel);

                    // Add a click event for selecting the order
                    orderBox.setOnMouseClicked(event -> {
                        selectedOrderId = orderId; // Update the selectedOrderId field
                        showAlert("Order Selected", "You selected Order ID: " + orderId);
                    });

                    // Add hover effects for better UI experience
                    orderBox.setOnMouseEntered(event -> orderBox.setStyle("-fx-padding: 10; -fx-border-color: #00aced; -fx-border-radius: 5px; -fx-background-color: #e6f7ff; -fx-spacing: 5;"));
                    orderBox.setOnMouseExited(event -> orderBox.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 5px; -fx-background-color: #f0f8ff; -fx-spacing: 5;"));

                    // Add the VBox to the HBox
                    hbox_pendingOrders.getChildren().add(orderBox);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while loading pending orders.");
        }
    }

    @FXML
    private void handleUpdateDeliveryFee() {
        if (currentRestaurant == null) {
            showAlert("Error", "No restaurant selected.");
            return;
        }

        String newFeeValue = deliverydee_txtfield.getText().trim();

        // Validate input
        if (newFeeValue.isEmpty()) {
            showAlert("Error", "Delivery fee cannot be empty.");
            return;
        }

        double parsedFee;
        try {
            parsedFee = Double.parseDouble(newFeeValue); // Validate if the fee is a valid number
            if (parsedFee < 0) {
                showAlert("Error", "Delivery fee must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Delivery fee must be a valid number.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall("{CALL EditDeliveryFee(?, ?, ?)}")) {

            // Retrieve FeeId using the updated method
            int feeId = getFeeIdByRestaurantId(currentRestaurant.getRestaurantId());
            if (feeId == -1) {
                showAlert("Error", "No delivery fee entry found for this restaurant.");
                return;
            }

            // Set parameters for the stored procedure
            callableStatement.setInt(1, feeId);              // FeeId
            callableStatement.setString(2, "Price");         // Column to update
            callableStatement.setString(3, String.valueOf(parsedFee)); // New value

            // Execute the procedure
            callableStatement.execute();

            // Show success message
            showAlert("Success", "Delivery fee updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while updating the delivery fee.");
        }
    }


    /**
     * Helper method to fetch the FeeId for the current restaurant using a stored procedure.
     *
     * @param restaurantId The ID of the restaurant.
     * @return The FeeId if found, or -1 if not.
     */
    private int getFeeIdByRestaurantId(int restaurantId) {
        int feeId = -1; // Default value indicating no FeeId found

        String procedureCall = "{CALL GetDeliveryFeeByRestaurantId(?)}";

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall)) {

            // Set the input parameter for the procedure
            callableStatement.setInt(1, restaurantId);

            try (ResultSet resultSet = callableStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Fetch the FeeId from the result set
                    feeId = resultSet.getInt("FeeId");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while fetching the FeeId using the stored procedure.");
        }

        return feeId; // Return the FeeId or -1 if not found
    }



    private void loadDeliveryFee() {
        if (currentRestaurant == null) {
            showAlert("Error", "No restaurant selected.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall("{CALL GetDeliveryFeeByRestaurantId(?)}")) {

            // Set the restaurant ID parameter
            callableStatement.setInt(1, currentRestaurant.getRestaurantId());

            try (ResultSet resultSet = callableStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Retrieve the delivery fee from the result set
                    double deliveryFee = resultSet.getDouble("Price");

                    // Display the delivery fee in the text field
                    deliverydee_txtfield.setText(String.format("%.2f", deliveryFee));
                } else {
                    // No delivery fee found for the restaurant
                    deliverydee_txtfield.setText("N/A");
                    showAlert("Info", "No delivery fee found for the selected restaurant.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while loading the delivery fee.");
        }
    }


    @FXML
    private void handleEditWorkingTime() {
        // Get selected values from the ComboBox and ChoiceBox
        String dayName = working_time_day_combobox.getValue();
        String openHour = working_time_open_hour_combobox.getValue();
        String openMinute = working_time_open_minute_combobox.getValue();
        String closeHour = working_time_close_hour_combobox.getValue();
        String closeMinute = working_time_close_minute_combobox.getValue();

//        System.out.println(openHour + ":" + openMinute);
//        System.out.println(closeHour + ":" + closeMinute);
//        System.out.println(dayName);
        // Validate inputs
        if (dayName == null || openHour == null || openMinute == null || closeHour == null || closeMinute == null) {
            showAlert("Error", "All fields must be selected to update the working time.");
            return;
        }
//
        // Construct time strings in HH:mm:00 format for SQL Time
        String openAt = openHour + ":" + openMinute + ":00";
        String closeAt = closeHour + ":" + closeMinute + ":00";
//        System.out.println(openAt);
//        System.out.println(closeAt);

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall("{CALL EditWorkingTime(?, ?, ?, ?)}")) {

            // Set input parameters for the stored procedure
            callableStatement.setInt(1, currentRestaurant.getRestaurantId()); // Replace with the current restaurant ID
            callableStatement.setString(2, dayName);
            callableStatement.setTime(3, java.sql.Time.valueOf(openAt));
            callableStatement.setTime(4, java.sql.Time.valueOf(closeAt));
            System.out.println(currentRestaurant.getRestaurantId());
            System.out.println(java.sql.Time.valueOf(openAt));
            System.out.println(java.sql.Time.valueOf(closeAt));

            // Execute the stored procedure
            callableStatement.execute();

            // Success alert
            showAlert("Success", "Working time updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while updating the working time.");
        } catch (IllegalArgumentException e) {
            showAlert("Error", "Invalid time format. Ensure you select valid hours and minutes.");
        }
    }

    private int currentRestaurantId; // ID of the current restaurant being edited
    /**
     * Sets the restaurant ID for which the working time will be edited.
     *
     * @param restaurantId The restaurant ID.
     */
    public void setCurrentRestaurantId(int restaurantId) {
        this.currentRestaurantId = restaurantId;
    }


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
        loadDeliveryFee();
        loadPendingOrders();
        loadRestaurantOrderHistory();
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
