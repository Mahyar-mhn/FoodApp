package org.example.foodapp;

import Classes.Order;
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

public class CustomerHomePage {
    @FXML
    private Label customer_name;

    @FXML
    private AnchorPane main_pane_customerPage;

    @FXML
    private AnchorPane Customer_homePage_pane;
    @FXML
    private AnchorPane Customer_addresses_pane;

    @FXML
    private AnchorPane scroll_pane_customer_homePage;

    @FXML
    private VBox vbox_scroll_pane_customer_homePage;

    @FXML
    private VBox vbox_customer_orders;

    @FXML
    private VBox vbox_customer_addresses;

    @FXML
    private TextField city_txtfield_newAddress;

    @FXML
    private TextArea detail_txtArea_newAddress;

    @FXML
    private TextField coordinate_txtfielad_newAddress;

    @FXML
    private Button customer_backtologin_button;

    @FXML
    private void handleAddCustomerAddress() {
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
            callableStatement.setInt(1, customerId); // Set user ID (customer ID)
            callableStatement.setBoolean(2, false); // DefaultAddress = false (not default initially)
            callableStatement.setString(3, city); // City
            callableStatement.setString(4, addressDetail); // Address detail
            callableStatement.setString(5, coordinate); // Coordinate

            // Execute the procedure
            callableStatement.execute();

            // Show success message and clear fields
            showAlert("Success", "New address added successfully.");
            clearNewAddressFields();
            loadCustomerAddresses(); // Reload addresses to display the new one

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


    @FXML
    private void loadCustomerAddresses() {
        if (customerId <= 0) { // Ensure customerId is set
            showAlert("Error", "Customer ID is not valid.");
            return;
        }

        vbox_customer_addresses.getChildren().clear(); // Clear existing content in the VBox

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall("{CALL GetUserAddresses(?)}")) {

            // Set the customer ID as input for the stored procedure
            callableStatement.setInt(1, customerId);

            // Execute the stored procedure and get the result set
            try (ResultSet resultSet = callableStatement.executeQuery()) {
                if (!resultSet.isBeforeFirst()) {
                    // If no addresses are found
                    Label noAddressesLabel = new Label("No addresses found for this customer.");
                    noAddressesLabel.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
                    vbox_customer_addresses.getChildren().add(noAddressesLabel);
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
                    setDefaultButton.setOnAction(event -> handleSetCustomerDefaultAddress(addressId));

                    // Add elements to the VBox
                    addressBox.getChildren().addAll(cityLabel, addressDetailLabel, defaultLabel, setDefaultButton);

                    // Add the address VBox to the main VBox
                    vbox_customer_addresses.getChildren().add(addressBox);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while loading customer addresses.");
        }
    }
    @FXML
    private void handleSetCustomerDefaultAddress(int addressId) {
        if (customerId <= 0) {
            showAlert("Error", "Customer ID is not valid.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall("{CALL SetDefaultAddress(?, ?)}")) {

            // Set the input parameters for the stored procedure
            callableStatement.setInt(1, customerId);
            callableStatement.setInt(2, addressId);

            // Execute the procedure
            callableStatement.execute();

            // Show success message and reload the addresses
            showAlert("Success", "Default address updated successfully.");
            loadCustomerAddresses();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while setting the default address.");
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    private Button customer_show_addresses_button;


    private OrderDAO orderDAO = new OrderDAO();
    private OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
    private int customerId; // ID of the logged-in customer

    @FXML
    private void toggleShowCustomerButton() {
        if ("Home Page".equals(customer_show_addresses_button.getText())) {
            showProfilePane(); // Go back to the home page
        } else {
            Customer_homePage_pane.setVisible(false);

            Customer_addresses_pane.setVisible(true);
            loadCustomerAddresses();

            customer_show_addresses_button.setText("Home Page");

        }
    }

    public void setCustomerData(int customerId, String customerName) {
        this.customerId = customerId;
        loadCustomerName(customerName);
        int defaultAddressId = getCustomerDefaultAddressId();

        if (defaultAddressId != -1) {
            System.out.println("Default Address ID: " + defaultAddressId);
        } else {
            System.out.println("No default address found for the user.");
        }

        showProfilePane();
        loadCustomerOrders();
    }


    /**
     * Sets the customer name into the label.
     *
     * @param name The name of the customer.
     */
    public void loadCustomerName(String name) {
        customer_name.setText(name);
    }

    private int getCustomerDefaultAddressId() {
        String procedureCall = "{CALL GetDefaultAddressByUserId(?)}";
        int addressId = -1; // Default value indicating no address found

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall)) {

            callableStatement.setInt(1, customerId); // Set the user ID

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

    @FXML
    public void showProfilePane() {
        Customer_homePage_pane.setVisible(true);
        Customer_addresses_pane.setVisible(false);

        customer_show_addresses_button.setText("Show My address");

        // Clear existing content in the VBox
        vbox_scroll_pane_customer_homePage.getChildren().clear();

        // Fetch restaurant data from the database
        RestaurantDAO restaurantDAO = new RestaurantDAO();
        List<Restaurant> restaurants = restaurantDAO.getAllRestaurants();

        // Load only non-deleted restaurants as a cart
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

                    // Apply the new styling
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
                    vbox_scroll_pane_customer_homePage.getChildren().add(restaurantCart);

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
                controller.loadRestaurantPage(restaurant, getCustomerDefaultAddressId());

                // Switch the main pane to the restaurant details pane
                main_pane_customerPage.getChildren().clear();
                main_pane_customerPage.getChildren().add(restaurantDetailsPane);
            } else {
                System.out.println("Restaurant not found!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadCustomerOrders() {
    // Clear existing VBox orders
        vbox_customer_orders.getChildren().clear();

        try {
            // Fetch the customer's order details from the database
            List<OrderDetail> customerOrderDetails = orderDAO.getUserOrderHistory(customerId);

            if (customerOrderDetails.isEmpty()) {
                Label noOrdersLabel = new Label("No orders available.");
                noOrdersLabel.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
                vbox_customer_orders.getChildren().add(noOrdersLabel);
                return;
            }

            // Group order details by OrderId
            Map<Integer, List<OrderDetail>> ordersGroupedByOrderId = customerOrderDetails.stream()
                    .collect(Collectors.groupingBy(OrderDetail::getOrderId));

            // Display grouped orders in the VBox
            for (Map.Entry<Integer, List<OrderDetail>> entry : ordersGroupedByOrderId.entrySet()) {
                int orderId = entry.getKey();
                List<OrderDetail> orderDetails = entry.getValue();

                VBox orderBox = new VBox();
                orderBox.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 5px; -fx-background-color: #f9f9f9; -fx-spacing: 5;");

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
                orderBox.getChildren().addAll(orderLabel, detailsBox, alertButton);


                // Add click event for the order box to reorder
                orderBox.setOnMouseClicked(event -> {
                    reorderSelectedOrder(orderId); // Call the reorder method when clicked
                });

                vbox_customer_orders.getChildren().add(orderBox);
            }
        } catch (Exception e) {
            Label errorLabel = new Label("Failed to load orders. Please try again.");
            errorLabel.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-text-fill: red;");
            vbox_customer_orders.getChildren().add(errorLabel);
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
            callableCreateOrder.setInt(1, getCustomerDefaultAddressId());
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
            loadCustomerOrders();
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
            Stage stage = (Stage) customer_backtologin_button.getScene().getWindow();
            stage.setScene(new Scene(loginPage));
            stage.setTitle("Login Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "An error occurred while navigating back to the login page.");
        }
    }

}
