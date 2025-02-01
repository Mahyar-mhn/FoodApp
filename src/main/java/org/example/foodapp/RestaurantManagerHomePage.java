package org.example.foodapp;

import Classes.OrderDetail;
import Classes.Restaurant;
import DaoClasses.OrderDAO;
import DaoClasses.OrderDetailDAO;
import DaoClasses.RestaurantDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @FXML
    private VBox vbox_list_manager_addresses;

    @FXML
    private Button set_manager_default_address;

    private int managerId; // ID of the logged-in manager
    private String managerName;
    private RestaurantDAO restaurantDAO = new RestaurantDAO(); // DAO instance
    private int selectedRestaurantId = -1; // To track the selected restaurant for deletion


    @FXML
    private HBox hbox_manager_history_orders;
    private OrderDAO orderDAO = new OrderDAO();


    @FXML
    private TextField city_txtfield_newAddress;

    @FXML
    private TextArea detail_txtArea_newAddress;

    @FXML
    private TextField coordinate_txtfielad_newAddress;
    @FXML
    private void handleAddManagerAddress() {
        // Get input values from the text fields
        String city = city_txtfield_newAddress.getText().trim();
        String addressDetail = detail_txtArea_newAddress.getText().trim();
        String coordinate = coordinate_txtfielad_newAddress.getText().trim();

        // Validate inputs
        if (city.isEmpty() || addressDetail.isEmpty() || coordinate.isEmpty()) {
            showAlert("Error", "All fields (City, Address, and Coordinate) must be filled.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall("{CALL AddAddress(?, ?, ?, ?, ?)}")) {

            // Set parameters for the stored procedure
            callableStatement.setInt(1, managerId); // Set user ID (manager ID)
            callableStatement.setBoolean(2, false); // DefaultAddress = false (not default initially)
            callableStatement.setString(3, city); // City
            callableStatement.setString(4, addressDetail); // Address detail
            callableStatement.setString(5, coordinate); // Coordinate

            // Execute the procedure
            callableStatement.execute();

            // Show success message and clear fields
            showAlert("Success", "New address added successfully.");
            clearNewAddressFields();
            loadManagerAddresses(); // Reload addresses to display the new one

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while adding the new address.");
        }
    }
    private void clearNewAddressFields() {
        city_txtfield_newAddress.clear();
        detail_txtArea_newAddress.clear();
        coordinate_txtfielad_newAddress.clear();
    }

    private OrderDetailDAO orderDetailDAO = new OrderDetailDAO();

    @FXML
    private void loadManagerAddresses() {
        if (managerId <= 0) { // Ensure managerId is set
            showAlert("Error", "Manager ID is not valid.");
            return;
        }

        vbox_list_manager_addresses.getChildren().clear(); // Clear existing content in the VBox

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall("{CALL GetUserAddresses(?)}")) {

            // Set the manager ID as input for the stored procedure
            callableStatement.setInt(1, managerId);

            // Execute the stored procedure and get the result set
            try (ResultSet resultSet = callableStatement.executeQuery()) {
                if (!resultSet.isBeforeFirst()) {
                    // If no addresses are found
                    Label noAddressesLabel = new Label("No addresses found for this manager.");
                    noAddressesLabel.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
                    vbox_list_manager_addresses.getChildren().add(noAddressesLabel);
                    return;
                }

                // Loop through the result set and add addresses to the VBox
                while (resultSet.next()) {
                    int addressId = resultSet.getInt("AddId");
                    String city = resultSet.getString("City");
                    String addressDetail = resultSet.getString("AddressDetail");
                    boolean isDefault = resultSet.getBoolean("DefaultAddress");

                    // Create a VBox for each address
                    VBox addressBox = new VBox();
                    addressBox.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 5px; -fx-background-color: #f9f9f9; -fx-spacing: 5;");
                    addressBox.setPrefWidth(300); // Optional: Set fixed width for each address box

                    // Add address details
                    Label cityLabel = new Label("City: " + city);
                    cityLabel.setStyle("-fx-font-size: 14px;");

                    Label addressDetailLabel = new Label("Detail: " + addressDetail);
                    addressDetailLabel.setStyle("-fx-font-size: 14px;");

                    Label defaultLabel = new Label(isDefault ? "Default: Yes" : "Default: No");
                    defaultLabel.setStyle(isDefault
                            ? "-fx-font-size: 14px; -fx-text-fill: green;"
                            : "-fx-font-size: 14px; -fx-text-fill: red;");

                    Button setDefaultButton = new Button("Set as Default");
                    setDefaultButton.setOnAction(event -> handleSetDefaultAddress(addressId));

                    // Add elements to the VBox
                    addressBox.getChildren().addAll(cityLabel, addressDetailLabel, defaultLabel, setDefaultButton);

                    // Add the address VBox to the main VBox
                    vbox_list_manager_addresses.getChildren().add(addressBox);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while loading manager addresses.");
        }
    }

    @FXML
    private void handleSetDefaultAddress(int addressId) {
        if (managerId <= 0) {
            showAlert("Error", "Manager ID is not valid.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall("{CALL SetDefaultAddress(?, ?)}")) {

            // Set the input parameters for the stored procedure
            callableStatement.setInt(1, managerId);
            callableStatement.setInt(2, addressId);

            // Execute the procedure
            callableStatement.execute();

            // Show success message and reload the addresses
            showAlert("Success", "Default address updated successfully.");
            loadManagerAddresses();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while setting the default address.");
        }
    }


    /**
     * Sets the customer name into the label.
     *
     * @param name The name of the customer.
     */
    public void loadManagerName(String name) {
        manager_name.setText(name);
    }
//    public void setManagerData(int managerId, String managerName) {
//        this.managerId = managerId; // Set the manager's ID
//        loadManagerName(managerName); // Set the manager's name in the label
//        this.managerName = managerName;
//
//
//        // Load the manager's restaurants and display them
//        loadManagerRestaurants();
//
//        loadManagerOrders();
//
//        // Show the manager's profile pane
//        showProfilePane();
//    }
    public void setManagerId(int managerId) {
        this.managerId = managerId;
        setManagerData(managerId,managerName); // Set the manager's name in the label
    }

    public void setManagerData(int managerId, String managerName) {
        this.managerId = managerId;
        this.managerName = managerName;
        loadManagerName(managerName);

        int addressId = getManagerDefaultAddressId();

        if (addressId != -1) {
            System.out.println("Manager's Default Address ID: " + addressId);
        } else {
            System.out.println("No default address found for the manager.");
        }

        loadManagerRestaurants();
        loadManagerOrders();
        showProfilePane();
        loadManagerAddresses();
    }

    @FXML
    public void showProfilePane() {
        // Clear existing content in the VBox
        vbox_scroll_pane_manager_homePage.getChildren().clear();

        // Fetch restaurant data from the database
        RestaurantDAO restaurantDAO = new RestaurantDAO();
        List<Restaurant> restaurants = restaurantDAO.getAllRestaurants();

        // Load only restaurants where isDeleted == false
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

                    // Apply the new color scheme
                    restaurantCart.setStyle("-fx-background-color: #638C6D; " +  // Dark Green
                            "-fx-border-color: #C84C05; " +  // Deep Orange
                            "-fx-border-width: 3; " +
                            "-fx-border-radius: 15; " +
                            "-fx-background-radius: 15; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.25), 10, 0.4, 0, 6);");

                    // Add hover effects
                    restaurantCart.setOnMouseEntered(event -> {
                        restaurantCart.setStyle("-fx-background-color: #E7FBB4; " + // Light Green on hover
                                "-fx-border-color: #C84C05; " +
                                "-fx-border-radius: 15px;");
                    });

                    restaurantCart.setOnMouseExited(event -> {
                        restaurantCart.setStyle("-fx-background-color: #638C6D; " + // Dark Green default
                                "-fx-border-color: #C84C05; " +
                                "-fx-border-radius: 15px;");
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


    private int getManagerDefaultAddressId() {
        String procedureCall = "{CALL GetDefaultAddressByUserId(?)}";
        int addressId = -1; // Default value indicating no address found

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall)) {

            // Set the manager's ID as input
            callableStatement.setInt(1, managerId);

            try (ResultSet resultSet = callableStatement.executeQuery()) {
                if (resultSet.next()) {
                    addressId = resultSet.getInt("AddId"); // Fetch the address ID
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the error
        }

        return addressId;
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
                controller.loadRestaurantPage(restaurant, getManagerDefaultAddressId());

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
            controller.setCurrentRestaurantId(selectedRestaurantId);
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

    public void loadManagerOrders() {
        // Clear existing orders in the HBox
        hbox_manager_history_orders.getChildren().clear();

        try {
            // Fetch the manager's order details from the database
            List<OrderDetail> managerOrderDetails = orderDAO.getUserOrderHistory(managerId); // Ensure this DAO method exists

            if (managerOrderDetails.isEmpty()) {
                Label noOrdersLabel = new Label("No orders available.");
                noOrdersLabel.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
                hbox_manager_history_orders.getChildren().add(noOrdersLabel);
                return;
            }

            // Group order details by OrderId
            Map<Integer, List<OrderDetail>> ordersGroupedByOrderId = managerOrderDetails.stream()
                    .collect(Collectors.groupingBy(OrderDetail::getOrderId));

            // Display grouped orders in the HBox
            for (Map.Entry<Integer, List<OrderDetail>> entry : ordersGroupedByOrderId.entrySet()) {
                int orderId = entry.getKey();
                List<OrderDetail> orderDetails = entry.getValue();

                VBox orderBox = new VBox();
                orderBox.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 5px; -fx-background-color: #f9f9f9; -fx-spacing: 5;");
                orderBox.setPrefWidth(200); // Set a fixed width for each order box

                // Add order details (Order ID)
                Label orderLabel = new Label("Order ID: " + orderId);
                orderLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                VBox detailsBox = new VBox();
                detailsBox.setStyle("-fx-spacing: 5; -fx-padding: 5;");

                for (OrderDetail detail : orderDetails) {
                    Label detailLabel = new Label(
                            "Item ID: " + detail.getItemId() +
                                    ", Quantity: " + detail.getCount() +
                                    ", Price: $" + detail.getPrice() +
                                    ", Total: $" + detail.calculateTotal()
                    );
                    detailLabel.setStyle("-fx-font-size: 14px;");
                    detailsBox.getChildren().add(detailLabel);
                }

                // Add button to display an alert for the order
                Button alertButton = new Button("Show Total Price");
                alertButton.setStyle("-fx-padding: 5; -fx-background-color: #007bff; -fx-text-fill: white;");
                alertButton.setOnAction(event -> {
                    handleCalculateTotalPrice(orderId);
                });

                // Add click event for the order box to reorder
                orderBox.setOnMouseClicked(event -> {
                    reorderSelectedOrder(orderId); // Call the reorder method when clicked
                });

                // Add components to the order box
                orderBox.getChildren().addAll(orderLabel, detailsBox, alertButton);

                // Add the order box to the HBox
                hbox_manager_history_orders.getChildren().add(orderBox);
            }
        } catch (Exception e) {
            Label errorLabel = new Label("Failed to load orders. Please try again.");
            errorLabel.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-text-fill: red;");
            hbox_manager_history_orders.getChildren().add(errorLabel);
            e.printStackTrace();
        }
    }
    @FXML
    private void handleCalculateTotalPrice(int orderId) {
        // Validate the orderId
        if (orderId <= 0) {
            showAlert("Error", "Invalid order selected. Please select a valid order.");
            return;
        }

        // Variable to store the total price
        BigDecimal totalPrice = BigDecimal.ZERO;

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall("{CALL CalculateTotalPrice(?, ?)}")) {

            // Set the input parameter (OrderId) and register the output parameter (TotalPrice)
            callableStatement.setInt(1, orderId);
            callableStatement.registerOutParameter(2, java.sql.Types.DECIMAL);

            // Execute the stored procedure
            callableStatement.execute();

            // Retrieve the calculated total price
            totalPrice = callableStatement.getBigDecimal(2);

            // Show the calculated total price in an alert
            showAlert("Total Price", "The total price for Order ID " + orderId + " is: $" + totalPrice);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while calculating the total price.");
        }
    }

    @FXML
    private void reorderSelectedOrder(int selectedOrderId) {
        if (selectedOrderId <= 0) {
            showAlert("Error", "Invalid order selected. Please select a valid order to reorder.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableCreateOrder = connection.prepareCall("{CALL NewOrder(?)}");
             CallableStatement callableAddOrderDetail = connection.prepareCall("{CALL AddOrderDetail(?, ?, ?)}");
             CallableStatement callableGetOrderDetails = connection.prepareCall("{CALL GetOrderDetailsByOrderId(?)}")) {

            // Step 1: Create a new order
            callableCreateOrder.setInt(1, getManagerDefaultAddressId());
            callableCreateOrder.execute();

            // Retrieve the newly created order ID
            int newOrderId = -1;
            try (ResultSet resultSet = connection.createStatement().executeQuery("SELECT LAST_INSERT_ID() AS OrderId")) {
                if (resultSet.next()) {
                    newOrderId = resultSet.getInt("OrderId");
                }
            }

            if (newOrderId == -1) {
                showAlert("Error", "Failed to create a new order.");
                return;
            }

            // Step 2: Fetch the items from the selected order
            callableGetOrderDetails.setInt(1, selectedOrderId);
            try (ResultSet orderDetailsResult = callableGetOrderDetails.executeQuery()) {
                while (orderDetailsResult.next()) {
                    int itemId = orderDetailsResult.getInt("ItemId");
                    int quantity = orderDetailsResult.getInt("Quantity");

                    // Step 3: Add each item to the new order
                    callableAddOrderDetail.setInt(1, newOrderId);
                    callableAddOrderDetail.setInt(2, itemId);
                    callableAddOrderDetail.setInt(3, quantity);
                    callableAddOrderDetail.execute();
                }
            }

            showAlert("Success", "Reorder successful! Your new Order ID is: " + newOrderId);
            loadManagerOrders();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while reordering.");
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            // Load the Login.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            AnchorPane loginPage = loader.load();

            // Set the Login scene
            Stage stage = (Stage) manager_name.getScene().getWindow();
            stage.setScene(new Scene(loginPage));
            stage.setTitle("Login Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "An error occurred while navigating back to the login page.");
        }
    }
}
